package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;
import java.util.EnumMap;
import static org.frogforce503.FRCSIM.Main.in;

/**
 *
 * @author Bryce
 */
public class SwerveDrivetrain extends AbstractDrivetrain{
    private final Node chassisNode;
    private Alliance alliance;
    private final Node vehicleNode;
    private final VehicleControl vehicle;
    private final float frictionForce = 300f;
    private float maxTurn = 5;
    private float turnForce = 500;
    private float maxSpeed = 5;
    private float speedForce = 1000;
    private final Bumpers bumpers;
    private Robot robot;
    private final boolean isPlayer;
    public SwerveDrivetrain(final ArrayList<AbstractSubsystem> subsystems, final PhysicsSpace space, final boolean isPlayer){
        this.isPlayer = isPlayer;
        chassisNode = new Node("chassis Node");
        Geometry chassisGeometry = new Geometry("Chassis", new Box(new Vector3f(0, in(3), 0), in(14), in(2.5f), in(14)));
        chassisGeometry.setMaterial(Main.chassis);
        chassisGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        chassisNode.attachChild(chassisGeometry);
        
        bumpers = new Bumpers(chassisNode, Main.in(28), Main.in(28), Main.in(2));
        for(final AbstractSubsystem subsystem : subsystems){
            subsystem.registerPhysics(chassisNode, space, alliance);
        }
        
        vehicleNode = new Node("vehicleNode");
        vehicle = new VehicleControl(CollisionShapeFactory.createDynamicMeshShape(chassisNode), 400);
        vehicleNode.attachChild(chassisNode);
        vehicleNode.addControl(vehicle);
        
        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        final float stiffness = 60;//200=f1 car
        final float compValue = .3f; //(should be lower than damp)
        final float dampValue = .4f;
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setFrictionSlip(1.5f);
        vehicle.setMass(30);
        
        //Create four wheels and add them at their locations
        final Vector3f wheelDirection = new Vector3f(0, -1, 0);
        final Vector3f wheelAxle = new Vector3f(-1, 0, 0);
        final float radius = in(2);
        final float width = in(1);
        final float restLength = in(2);
        final float yOff = in(3);
        final float xOff = in(11.25f);
        final float zOff = in(11.25f);
        

        final Cylinder wheelMesh = new Cylinder(16, 16, radius, width, true);
        
        for(int i = 0; i < 4; i++){            
            Node node = new Node("wheel node");
            Geometry wheel = new Geometry("wheel", wheelMesh);
            node.attachChild(wheel);
            wheel.rotate(0, FastMath.HALF_PI, 0);
            wheel.setMaterial(Main.blue);
            
            vehicle.addWheel(node, new Vector3f((i<=1?xOff:-xOff), yOff, (i%2 == 0? zOff : -zOff)),
                    wheelDirection, wheelAxle, restLength, radius, false);
            vehicleNode.attachChild(node);
        }
        
        vehicle.setFriction(.1f);
        vehicle.setDamping(.5f, .5f);
    }
    
    @Override
    public void registerPhysics(final Node rootNode, final PhysicsSpace space, final Alliance alliance){
        rootNode.attachChild(vehicleNode);
        space.add(vehicle);
        this.alliance = alliance;
        bumpers.registerAlliance(alliance, isPlayer);
    }
    
    protected void updateRC(float FWR, float STR, float omega){
        FWR = (FWR>1? 1 : (FWR<-1? -1 : FWR));
        STR = (STR>1? 1 : (STR<-1? -1 : STR));
        omega = (omega>1? 1 : (omega<-1? -1 : omega));
        
        final Vector2f V = new Vector2f(STR, FWR);
        float l2 = Main.in(11.25f), speedFactor = speedForce, turnFactor = turnForce;
        
        final float angleFromX = (new Vector2f(vehicle.getForwardVector(null).x, vehicle.getForwardVector(null).z)).getAngle();
        Vector2f command = new Vector2f(FWR, STR);
        command.rotateAroundOrigin(angleFromX+FastMath.HALF_PI, false);
        
        if(command.dot(new Vector2f(vehicle.getLinearVelocity().z, -vehicle.getLinearVelocity().x))<0){
            speedFactor *= (maxSpeed-vehicle.getLinearVelocity().length())/maxSpeed;
        } 
        
        if(vehicle.getAngularVelocity().dot(Vector3f.UNIT_Y) * omega < 0){
            turnFactor *= (maxTurn-Math.abs(vehicle.getAngularVelocity().dot(Vector3f.UNIT_Y)))/maxTurn;
        }
        
        V.mult(speedFactor, V);
        l2 *= turnFactor;
        
        final float[] ABCD = new float[]{V.x-omega*l2, V.x + omega*l2,
                                   V.y-omega*l2, V.y + omega*l2};
        final float[][] FSW = new float[4][];
        for(int i = 0; i < 4; i++){
            float ABCD1 = ABCD[1-i%2], ABCD2 = ABCD[3-i/2];
            FSW[i] = new float[]{ (float)Math.sqrt(ABCD1*ABCD1 + ABCD2*ABCD2),
                                    (float)Math.atan2(ABCD1, ABCD2)};
        }        
        
        for(int i = 0; i < 4; i++){
            vehicle.steer(i, -FSW[i][1]);
            vehicle.accelerate(i, FSW[i][0]);
        }
        
        vehicle.brake(frictionForce * 2f / (Math.abs(FWR)+Math.abs(STR)));
        applyDownforce();
    }
    
    void updateFCSC(float FWR, float STR, float omega) {
        FWR = (FWR>1? 1 : (FWR<-1? -1 : FWR));
        STR = (STR>1? 1 : (STR<-1? -1 : STR));
        
        float angleFromX = (new Vector2f(vehicle.getForwardVector(null).x, vehicle.getForwardVector(null).z)).getAngle();
        Vector2f command = new Vector2f(FWR, STR);
        command.rotateAroundOrigin(angleFromX+FastMath.HALF_PI, true);
        updateRC(command.x, command.y, omega);
    }
    
    void updateFCRDC(float FWR, float STR, float omega) {
        updateFCBDC(-FWR, -STR, omega);
    }
    
    void updateFCBDC(float FWR, float STR, float omega) {
        FWR = (FWR>1? 1 : (FWR<-1? -1 : FWR));
        STR = (STR>1? 1 : (STR<-1? -1 : STR));
        
        float angleFromX = (new Vector2f(vehicle.getForwardVector(null).x, vehicle.getForwardVector(null).z)).getAngle();
        Vector2f command = new Vector2f(FWR, STR);
        command.rotateAroundOrigin(angleFromX-FastMath.PI, true);
        updateRC(command.x, command.y, omega);
    }

    @Override
    public VehicleControl getVehicleControl() {
        return vehicle;
    }

    @Override
    public Node getVehicleNode() {
        return vehicleNode;
    }

    @Override
    public void update() {}

    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        this.robot = robot;
    }
    
    @Override
    public void driveToPoint(final Vector3f point, final DriveDirection direction){
        Vector3f curPos = vehicle.getPhysicsLocation(); 
        float z = (curPos.z-point.z), x = (point.x - curPos.x);
        Vector3f vectorToPoint = point.subtract(curPos), vehicleVector = vehicle.getForwardVector(null);
        float angle = FastMath.atan2(vehicleVector.cross(vectorToPoint).length(), vehicleVector.dot(vectorToPoint)) * FastMath.sign(vectorToPoint.cross(vehicleVector).dot(Vector3f.UNIT_Y)) / 3 * (direction == DriveDirection.Towards? 1 : (direction == DriveDirection.Away? -1 : 0));
        if(FastMath.abs(z)+FastMath.abs(x) < FastMath.abs(angle) && robot.isTouchingWall()){
            z = curPos.z;
            x = -curPos.x;
        }
        updateFCSC(z, x, angle);        
    }
    
    public void turnTowardsPoint(final Vector3f point){
        final Vector3f curPos = vehicle.getPhysicsLocation(); 
        final Vector3f vectorToPoint = point.subtract(curPos), vehicleVector = vehicle.getForwardVector(null);
        float angle = FastMath.atan2(vehicleVector.cross(vectorToPoint).length(), vehicleVector.dot(vectorToPoint)) * FastMath.sign(vectorToPoint.cross(vehicleVector).dot(Vector3f.UNIT_Y));
        if(robot.isTouchingWall()){
            updateFCSC(curPos.z, -curPos.x, angle/3);
        } else {
            updateFCSC(0, 0, angle/3);
        }
    }

    @Override
    public String toString(){
        return "SwerveDrivetrain";
    }
    
    @Override
    public String detailedToString(String offset) {
        return offset + "SwerveDrivetrain{\n"+offset+"    isPlayer: " + isPlayer +"\n"+offset+"}";
    }
}

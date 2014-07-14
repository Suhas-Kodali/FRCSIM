package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import java.util.EnumMap;
import static org.frogforce503.FRCSIM.Main.in;

/**
 *
 * @author Bryce Paputa
 */
public class TankDrivetrain extends AbstractDrivetrain{
    protected VehicleControl vehicle;
    private final float accelerationForce = 175f;
    private final float turningForce = 100f;
    private final float frictionForce = 2f;
    private final float maxSpeed = 17;
    protected Alliance alliance;
    private Node chassisNode, vehicleNode;
    private CollisionShape collisionShape;
    protected AbstractIntake intake;
    protected AbstractShooter shooter;
    private Bumpers bumpers;
    
    public TankDrivetrain() {
        chassisNode = new Node("chassis Node");
        Box chassis = new Box(new Vector3f(0, in(3), 0), in(14), in(2.5f), in(14));
        Geometry chassisGeometry = new Geometry("Chassis", chassis);
        chassisGeometry.setMaterial(Main.cage);
        chassisGeometry.setQueueBucket(Bucket.Transparent);
        chassisNode.attachChild(chassisGeometry);
        
        
        bumpers = new Bumpers(chassisNode, Main.in(28), Main.in(28), Main.in(2));
        
        collisionShape = CollisionShapeFactory.createDynamicMeshShape(chassisNode);
        //create vehicle node
        vehicleNode=new Node("vehicleNode");
        vehicleNode.attachChild(chassisNode);
        vehicle = new VehicleControl(collisionShape, 400);
        vehicleNode.addControl(vehicle);
        
        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        float stiffness = 60;//200=f1 car
        float compValue = .3f; //(should be lower than damp)
        float dampValue = .4f;
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setFrictionSlip(1.5f);
        vehicle.setMass(30);
        //Create four wheels and add them at their locations
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);
        float radius = in(2);
        float width = in(1);
        float restLength = in(2);
        float yOff = in(3);
        float xOff = in(14);
        float zOff = in(11.25f);
        float rocker = in(.5f);

        Cylinder wheelMesh = new Cylinder(16, 16, radius, width, true);

        float[][] pos = new float[8][3];
        
        for(int i = 0; i < 8; i++){
            if(i<=3){
                pos[i][0]=-xOff+width/2+Main.in(1);
            } else {
                pos[i][0]=xOff-width/2-Main.in(1);
            }
            if(i%4 == 0){
                pos[i][1]=zOff;
            } else if (i%4 == 1){
                pos[i][1]=zOff/3;
            } else if (i%4 == 2){
                pos[i][1]=-zOff/3;
            } else {
                pos[i][1]=-zOff;
            }
            if(i%4 % 3 ==0){
                pos[i][2]=yOff;
            } else {
                pos[i][2]=yOff-rocker;
            }
        }
        
        for(int i = 0; i < 8; i++){
            Node node = new Node("wheel node");
            Geometry wheel = new Geometry("wheel", wheelMesh);
            node.attachChild(wheel);
            wheel.rotate(0, FastMath.HALF_PI, 0);
            wheel.setMaterial(Main.black);
            
            vehicle.addWheel(node, new Vector3f(pos[i][0], pos[i][2], pos[i][1]),
                    wheelDirection, wheelAxle, restLength, radius, false);
            vehicleNode.attachChild(node);
            vehicle.setFriction(.1f);   
        }            
    }
    
    @Override
    public void registerPhysics(Node rootNode, PhysicsSpace space, Alliance alliance){
        rootNode.attachChild(vehicleNode);
        space.add(vehicle);
        this.alliance = alliance;
        bumpers.registerAlliance(alliance);
    }
    
    private float lastTurn = 0;
    protected void update(float cup, float cdown, float cleft, float cright){
        float left = 0;
        float right = 0;
        float curTurn = cleft-cright;
        float curPow = cup-cdown, accelerationFactor;
        
        float curSpeed = vehicle.getCurrentVehicleSpeedKmHour();
        if(curPow*curSpeed > 0){
            accelerationFactor = (maxSpeed-Math.abs(curSpeed))/maxSpeed * accelerationForce;
        } else {
            accelerationFactor = accelerationForce;
        }
        left  += accelerationFactor * (curPow) + turningForce * curTurn;
        right += accelerationFactor * (curPow) - turningForce * curTurn;
        
        if(Math.abs(lastTurn)>.1 && Math.abs(curTurn)<.1){
            vehicle.setAngularVelocity(vehicle.getAngularVelocity().divide(4));
        }
        lastTurn = curTurn;
        
        for(int i = 0; i < 4; i++){
            vehicle.accelerate(i, left);
        }
        for(int i = 4; i < 8; i++){
            vehicle.accelerate(i, right);
        }
        
        vehicle.brake(frictionForce * 1f / (1-Math.abs(curPow)));
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
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {}

    
    @Override
    public void driveTowardsPoint(Vector3f point) {
        float turn = 1, pow = 1;
        Vector3f vehicleVector = vehicle.getForwardVector(null), vectorToPoint = point.subtract(vehicle.getPhysicsLocation());
        float s = vehicleVector.cross(vectorToPoint).length(), c = vehicleVector.dot(vectorToPoint), angle = FastMath.atan2(s, c);
        if(FastMath.abs(angle)>FastMath.HALF_PI){
            vehicleVector = vehicle.getForwardVector(null).negate();
            angle = FastMath.PI-angle;   
            pow = -1;
            if(FastMath.abs(angle)>FastMath.PI/6){
                angle = 3;
                pow = 0;                        
            }
        }
        
        pow *= vectorToPoint.dot(vehicleVector);
        if(vectorToPoint.dot(vehicleVector) < 0){
            pow *= 1.5;
        }
        turn *= angle * vectorToPoint.length() / 10; 
        update(pow, 0, turn, 0);
    }
}

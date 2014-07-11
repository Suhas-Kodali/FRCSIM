package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
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
import java.util.EnumMap;
import static org.frogforce503.FRCSIM.Main.in;

/**
 *
 * @author Bryce
 */
public class SwerveDrivetrain extends AbstractDrivetrain{
    private final Node chassisNode;
    private final CollisionShape collisionShape;
    private Alliance alliance;
    private final Node vehicleNode;
    private final VehicleControl vehicle;
    private final float frictionForce = 20f;
    private float maxTurn=7;
    private float turnForce=500;
    private float maxSpeed=7;
    private float speedForce=500;
    private Bumpers bumpers;
    public SwerveDrivetrain(){
        chassisNode = new Node("chassis Node");
        Box chassis = new Box(new Vector3f(0, in(3), 0), in(14), in(2.5f), in(14));
        Geometry chassisGeometry = new Geometry("Chassis", chassis);
        chassisGeometry.setMaterial(Main.cage);
        chassisGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        chassisNode.attachChild(chassisGeometry);
        
        bumpers = new Bumpers(chassisNode, Main.in(28), Main.in(28), Main.in(2));
        
        collisionShape = CollisionShapeFactory.createDynamicMeshShape(chassisNode);
        
        //create vehicle node
        vehicleNode=new Node("vehicleNode");
        vehicle = new VehicleControl(collisionShape, 400);
        vehicleNode.attachChild(chassisNode);
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
        float xOff = in(11.25f);
        float zOff = in(11.25f);
        

        Cylinder wheelMesh = new Cylinder(16, 16, radius, width, true);

        float[][] pos = new float[4][3];
        
        for(int i = 0; i < 4; i++){
            if(i<=1){
                pos[i][0]=+xOff;
            } else {
                pos[i][0]=-xOff;
            }
            if(i%2 == 0){
                pos[i][1]=zOff;
            } else {
                pos[i][1]=-zOff;
            }
            pos[i][2]=yOff;
        }
        for(int i = 0; i < 4; i++){
            Node node = new Node("wheel node");
            Geometry wheel = new Geometry("wheel", wheelMesh);
            node.attachChild(wheel);
            wheel.rotate(0, FastMath.HALF_PI, 0);
            wheel.setMaterial(Main.blue);
            
            vehicle.addWheel(node, new Vector3f(pos[i][0], pos[i][2], pos[i][1]),
                    wheelDirection, wheelAxle, restLength, radius, false);
            vehicleNode.attachChild(node);
        }
    }
    
    @Override
    public void registerPhysics(Node rootNode, PhysicsSpace space, Alliance alliance){
        rootNode.attachChild(vehicleNode);
        space.add(vehicle);
        this.alliance = alliance;
        bumpers.registerAlliance(alliance);
    }
    
    protected void updateRC(float fwr, float str, float omega){
        Vector2f V = new Vector2f(str, fwr);
        float l2 = Main.in(11.25f);
        
        float speedFactor = (maxSpeed-vehicle.getLinearVelocity().length())/maxSpeed * speedForce;
        float turnFactor = (maxTurn-Math.abs(vehicle.getAngularVelocity().dot(Vector3f.UNIT_Y)))/maxTurn * turnForce;
        
        V.mult(speedFactor, V);
        l2 *= turnFactor;
        
        float[] ABCD = new float[]{V.x-omega*l2, V.x + omega*l2,
                                   V.y-omega*l2, V.y + omega*l2};
        float[][] FSW = new float[4][];
        for(int i = 0; i < 4; i++){
            float ABCD1 = ABCD[1-i%2], ABCD2 = ABCD[3-i/2];
            FSW[i] = new float[]{ (float)Math.sqrt(ABCD1*ABCD1 + ABCD2*ABCD2),
                                    (float)Math.atan2(ABCD1, ABCD2)};
        }        
        
        for(int i = 0; i < 4; i++){
            vehicle.steer(i, -FSW[i][1]);
            vehicle.accelerate(i, FSW[i][0]);
        }
        
        vehicle.brake(frictionForce);
        if(vehicle.getLinearVelocity().length()<.1){
            vehicle.brake(frictionForce*5);
        }
    }

    void updateFC(float FWR, float STR, float omega) {
        Vector2f forwardDirectionProjection = new Vector2f(vehicle.getForwardVector(null).x, vehicle.getForwardVector(null).z);
        float angleFromX = forwardDirectionProjection.getAngle();
        Vector2f command = new Vector2f(FWR, STR);
        command.rotateAroundOrigin(angleFromX+FastMath.HALF_PI, true);
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
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {}
}

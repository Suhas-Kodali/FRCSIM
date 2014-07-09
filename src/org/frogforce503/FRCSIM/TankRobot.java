package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import static org.frogforce503.FRCSIM.Main.in;

/**
 *
 * @author Bryce Paputa
 */
public abstract class TankRobot extends AbstractRobot{
    private VehicleControl vehicle;
    private final float accelerationForce = 175f;
    private final float turningForce = 100f;
    private final float frictionForce = 2f;
    private final float maxSpeed = 17;
    private GhostControl pullGhost, holdGhost;
    private final Alliance alliance;
    private Node chassisNode, vehicleNode;
    private Geometry intakeGeometry2, intakeGeometry;
    private CollisionShape collisionShape;
    private Node intakeNode;
    private ArrayList<Ball> pulledBalls = new ArrayList<Ball>(2);
    private Ball heldBall = null;
    public static final ArrayList<TankRobot> robots = new ArrayList<TankRobot>(6);
    
    public TankRobot(Node rootNode, PhysicsSpace space, Alliance alliance) {
        chassisNode = new Node("chassis Node");
        Box chassis = new Box(new Vector3f(0, in(3), 0), in(14), in(2.5f), in(14));
        Geometry chassisGeometry = new Geometry("Chassis", chassis);
        chassisGeometry.setMaterial(Main.cage);
        chassisGeometry.setQueueBucket(Bucket.Transparent);
        chassisNode.attachChild(chassisGeometry);
        Box intakeBox = new Box(in(1), in(12), in(6));
        intakeGeometry = new Geometry("Intake", intakeBox);
        intakeGeometry.setLocalTranslation(in(31)/2, in(3) + in(12)/2 + in(10), in(28)/2 - in(6));
        intakeGeometry.setMaterial(Main.green);
        
        
        //chassisNode.attachChild(intakeGeometry);
        intakeGeometry2 = new Geometry("Intake", intakeBox);
        intakeGeometry2.setLocalTranslation(-in(31)/2, in(3) + in(12)/2 + in(10), in(28)/2 - in(6));
        intakeGeometry2.setMaterial(Main.green);
        //chassisNode.attachChild(intakeGeometry2);
        
        //having incredle trouble changing the size of these ghostcontrols
        
        pullGhost = new GhostControl(new BoxCollisionShape(new Vector3f(in(4),in(3 + 6 + 5)/2,0)));  // a box-shaped ghost
        //Node ghostNode = new Node("a ghost-controlled thing");
        //ghostNode.addControl(ghost);
        intakeNode = new Node("node");
        intakeNode.addControl(pullGhost);
        
        intakeNode.attachChild(intakeGeometry);
        intakeNode.attachChild(intakeGeometry2);
        chassisNode.attachChild(intakeNode);
        space.add(pullGhost);
        
        holdGhost = new GhostControl(new BoxCollisionShape(new Vector3f(in(0f)/2,in(18)/2,in(0f)/2)));  // a box-shaped ghost
        Node holdGhostNode = new Node("a ghost-controlled thing");
        holdGhostNode.addControl(holdGhost);
        holdGhostNode.setLocalTranslation(new Vector3f(0,in(18)/2,0));
        space.add(holdGhost);
        
        chassisNode.attachChild(holdGhostNode);
        
        
        new Bumper(chassisNode, Main.in(28), Main.in(28), Main.in(2), alliance);
        
        collisionShape = CollisionShapeFactory.createDynamicMeshShape(chassisNode);
        
        this.alliance = alliance;

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
        }            
        
        rootNode.attachChild(vehicleNode);

        space.add(vehicle);
        vehicle.setPhysicsLocation(alliance.position[0]);
    }
    
    @Override
    public void setPhysicsLocation(Vector3f pos){
        vehicle.setPhysicsLocation(pos);
    }
    
    public abstract void update();
    
    public static void updateAll(){
        for(TankRobot robot : robots){
            robot.update();
        }
    }
    
    private float lastTurn = 0;
    protected void update(float cup, float cdown, float cleft, float cright){
        float left = 0;
        float right = 0;
        float curSpeed = Math.abs(vehicle.getCurrentVehicleSpeedKmHour());
        float accelerationFactor = (maxSpeed-curSpeed)/maxSpeed * accelerationForce;
        
        float curTurn = cleft-cright;
        left  += accelerationFactor * (cup-cdown) + turningForce * curTurn;
        right += accelerationFactor * (cup-cdown) - turningForce * curTurn;
        
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
        
        vehicle.brake(frictionForce);
        if(vehicle.getLinearVelocity().length()<.1){
            vehicle.brake(frictionForce*5);
        }
        
        if(heldBall == null && !isShooting){
            for(int j = pullGhost.getOverlappingObjects().size()-1; j >=0; j--){
                if(pullGhost.getOverlapping(j).getUserObject() instanceof Ball){
                    Ball ball = ((Ball) pullGhost.getOverlapping(j).getUserObject());
                    if(!pulledBalls.contains(ball)){
                        pulledBalls.add(ball);
                    }
                }
            }
            for(int j = holdGhost.getOverlappingObjects().size()-1; j>=0; j--){
                if(holdGhost.getOverlapping(j).getUserObject() instanceof Ball){
                    heldBall = (Ball) holdGhost.getOverlapping(j).getUserObject();
                    if(pulledBalls.contains(heldBall)){
                        pulledBalls.remove(heldBall);
                    }
                }
            }
        }
        
        for(Ball ball : pulledBalls){
            ball.getRigidBodyControl().applyCentralForce(vehicle.getPhysicsLocation().subtract(ball.getRigidBodyControl().getPhysicsLocation()).normalize().add(new Vector3f(0,.5f,0)).mult(45));
        }
        
        if(heldBall!= null && !isShooting){
            heldBall.getRigidBodyControl().setPhysicsLocation(vehicle.getPhysicsLocation().add(new Vector3f(0, in(18), 0)));
        }
    }
    
    private boolean isShooting = false;
    public static final int shootLength = 1000;
    public static final int shootForce = 12;
    public static final float shootElevation = .6f;
    public Runnable shoot = new Runnable(){
        public void run(){
            shoot();
        }
    };
    private boolean isIntakeDown = false;
    public void lowerIntake(){
        intakeNode.rotate(FastMath.HALF_PI, 0, 0);
        intakeNode.setLocalTranslation(intakeGeometry.getLocalTranslation().add(-in(16), -in(6)/2, in(2.5f)));
        isIntakeDown = true;
    }
    public void retractIntake(){
        intakeNode.rotate(-FastMath.HALF_PI, 0, 0);
        intakeNode.setLocalTranslation(intakeGeometry.getLocalTranslation().add(-in(16), -in(18), in(-6f)));
        isIntakeDown = false;
    }
    public void shoot(){
        if(heldBall != null && ! isShooting){
            heldBall.getRigidBodyControl().setLinearVelocity((vehicle.getForwardVector(null)).add(new Vector3f(0, shootElevation, 0)).mult(shootForce).add(vehicle.getLinearVelocity()));
            heldBall = null;
            if(pulledBalls.contains(heldBall)){
                pulledBalls.remove(heldBall);
            }                    
            isShooting = true;
            (new Timer()).schedule(new TimerTask(){public void run(){isShooting = false;}}, shootLength);
        }
    }
    public Runnable toggleIntake = new Runnable(){
        public void run() {
            if(isIntakeDown){
                retractIntake();
            } else {
                lowerIntake();
            }
        }
    };
    
}

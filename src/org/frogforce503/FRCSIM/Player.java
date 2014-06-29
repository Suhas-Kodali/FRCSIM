package org.frogforce503.FRCSIM;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import static org.frogforce503.FRCSIM.Main.in;

/**
 *
 * @author Bryce Paputa
 */
public class Player {
    private VehicleControl vehicle;
    private final float accelerationForce = 175f;
    private final float turningForce = 100f;
    private final float frictionForce = 2f;
    private final float maxSpeed = 17;
    private GhostControl pullGhost, holdGhost;
    private Vector3f jumpForce = new Vector3f(0, 60, 0);
    private KeyMapping keyMapping = KeyMapping.std;
    private final Alliance alliance;
    private AssetManager assetManager;
    private Node rootNode, chassisNode, vehicleNode;
    private final MotionPath path = new MotionPath();
    private Geometry intakeGeometry2, intakeGeometry;
    private CollisionShape collisionShape;
    private Node intakeNode;
    
    public Player(Node rootNode, PhysicsSpace space, Alliance alliance, AssetManager assetManager) {

        
        this.assetManager = assetManager;
        this.rootNode = rootNode;
        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        
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
        
        
        chassisNode.attachChild(intakeGeometry);
        intakeGeometry2 = new Geometry("Intake", intakeBox);
        intakeGeometry2.setLocalTranslation(-in(31)/2, in(3) + in(12)/2 + in(10), in(28)/2 - in(6));
        intakeGeometry2.setMaterial(Main.green);
        chassisNode.attachChild(intakeGeometry2);
        
        //having incredle trouble changing the size of these ghostcontrols
        
        pullGhost = new GhostControl(new BoxCollisionShape(new Vector3f(in(4),in(3 + 6 + 5)/2,0)));  // a box-shaped ghost
        //Node ghostNode = new Node("a ghost-controlled thing");
        //ghostNode.addControl(ghost);
        intakeNode = new Node("node");
        intakeNode.addControl(pullGhost);
        chassisNode.attachChild(intakeNode);
        
        intakeNode.attachChild(intakeGeometry);
        intakeNode.attachChild(intakeGeometry2);
        space.add(pullGhost);
        
        holdGhost = new GhostControl(new BoxCollisionShape(new Vector3f(in(0f)/2,in(18)/2,in(0f)/2)));  // a box-shaped ghost
        Node holdGhostNode = new Node("a ghost-controlled thing");
        holdGhostNode.addControl(holdGhost);
        chassisNode.attachChild(intakeNode);
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
        Vector3f wheelDirection = new Vector3f(0, -1, 0); // was 0, -1, 0
        Vector3f wheelAxle = new Vector3f(-1, 0, 0); // was -1, 0, 0
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
//        
//        Node node = new Node("wheel node");
//            Geometry wheels = new Geometry("wheel", wheelMesh);
//            node.attachChild(wheels);
//            wheels.rotate(0, 0, 0);
//            wheels.setMaterial(Main.red);
//            vehicle.addWheel(node, new Vector3f(in(28)/2, in(6 + 3 + 3), in(28)/2 + in(2) + in(24)),
//                    wheelDirection, wheelAxle, restLength, radius, false);
//            vehicleNode.attachChild(node);
//            
//            vehicle.setFrictionSlip(8, 100f);
//            
//        Node wheelNode = new Node("wheel node");
//            Geometry wheel2 = new Geometry("wheel", wheelMesh);
//            wheelNode.attachChild(wheel2);
//            wheel2.rotate(0, FastMath.HALF_PI, 0);
//            wheel2.setMaterial(Main.red);
//            vehicle.addWheel(wheelNode, new Vector3f(-in(28)/2, in(6 + 3 + 3), in(28)/2 + in(2) + in(24)),
//                    wheelDirection, wheelAxle, restLength, radius, false);
//            vehicleNode.attachChild(wheelNode);
            
            
            
                
        rootNode.attachChild(vehicleNode);

        space.add(vehicle);
        vehicle.setPhysicsLocation(alliance.position[0]);
    }
    
    public Vector3f getPhysicsLocation(){
        return vehicle.getPhysicsLocation();
    }
    
    public void setPhysicsLocation(Vector3f pos){
        vehicle.setPhysicsLocation(pos);
    }
    
    public int lastTurn = 0, turnCounter = 0;
    public void update(){
        float left = 0;
        float right = 0;
        float curSpeed = Math.abs(vehicle.getCurrentVehicleSpeedKmHour());
        float accelerationFactor = (maxSpeed-curSpeed)/maxSpeed * accelerationForce;
        
        if(Main.InputManager.isPressed(keyMapping.up)){
            left+=1 * accelerationFactor;
            right+=1 * accelerationFactor;
        }
        if(Main.InputManager.isPressed(keyMapping.down)){
            left-=1 * accelerationFactor;
            right-=1 * accelerationFactor;
        }
        int curTurn = 0;
        if(Main.InputManager.isPressed(keyMapping.left)){
            left+=turningForce;
            right-=turningForce;
            curTurn++;
        }
        if(Main.InputManager.isPressed(keyMapping.right)){
            left-=turningForce;
            right+=turningForce;
            curTurn--;
        }
        if(Main.InputManager.isPressed(keyMapping.load)){
            for(int i = 0; i < Ball.balls.size(); i++){
                for(int j = 0; j < pullGhost.getOverlappingObjects().size(); j++){
                    if(pullGhost.getOverlapping(j) == Ball.balls.get(i).getRigidBodyControl()){
                        Ball.balls.get(i).getRigidBodyControl().applyCentralForce(vehicle.getPhysicsLocation().subtract(Ball.balls.get(i).getRigidBodyControl().getPhysicsLocation()).normalize().mult(40));
                    }
                }
                for(int j = 0; j < holdGhost.getOverlappingObjects().size(); j++){
                    if(holdGhost.getOverlapping(j) == Ball.balls.get(i).getRigidBodyControl()){
                        Ball.balls.get(i).getRigidBodyControl().setPhysicsLocation(vehicle.getPhysicsLocation().add(new Vector3f(0, in(18), 0)));
                    }
                }
            }
        }
        
        if(Main.InputManager.isPressed(keyMapping.shoot)){
            for(int i = 0; i < Ball.balls.size(); i++){
                
                for(int j = 0; j < holdGhost.getOverlappingObjects().size(); j++){
                    if(holdGhost.getOverlapping(j) == Ball.balls.get(i).getRigidBodyControl()){
                        Ball.balls.get(i).getRigidBodyControl().applyCentralForce((vehicle.getForwardVector(null).add(0, 0.6f, 0)).mult(new Vector3f(70f,70f,70f)));
                    }
                }
            }
        }else{
            for(int i = 0; i < Ball.balls.size(); i++){
                for(int j = 0; j < holdGhost.getOverlappingObjects().size(); j++){
                    if(holdGhost.getOverlapping(j) == Ball.balls.get(i).getRigidBodyControl()){
                        Ball.balls.get(i).getRigidBodyControl().setPhysicsLocation(vehicle.getPhysicsLocation().add(new Vector3f(0, in(18), 0)));
                    }
                }
            }
        }
        if(lastTurn!=0 && curTurn==0){
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
    }
    
    public void jump(){
        vehicle.applyImpulse(jumpForce, Vector3f.ZERO);
    }
    
    public void reset(){
        System.out.println("Reset");
        vehicle.setPhysicsLocation(alliance.position[0]);
        vehicle.setPhysicsRotation(new Matrix3f());
        vehicle.setLinearVelocity(Vector3f.ZERO);
        vehicle.setAngularVelocity(Vector3f.ZERO);
        vehicle.resetSuspension();
        chassisNode.attachChild(intakeGeometry);
        chassisNode.attachChild(intakeGeometry2);
    }
    public void lowerIntake(){
        intakeNode.rotate(FastMath.HALF_PI, 0, 0);
        intakeNode.setLocalTranslation(intakeGeometry.getLocalTranslation().add(-in(16), -in(6)/2, in(2.5f)));
    }
    public void retractIntake(){
        intakeNode.rotate(-FastMath.HALF_PI, 0, 0);
        intakeNode.setLocalTranslation(intakeGeometry.getLocalTranslation().add(-in(16), -in(18), in(-6f)));
    }
    public static class KeyMapping{
        public final String up, down, left, right, load, shoot;
        public KeyMapping(String up, String down, String left, String right, String load, String shoot){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.load = load;
            this.shoot = shoot;
        }
        public final static KeyMapping std = new KeyMapping("up", "down", "left", "right", "pgdwn", "enter");
        public final static KeyMapping wasd = new KeyMapping("w", "s", "a", "d", "r", "space");
    }
    
    public void setKeyMapping(KeyMapping src){
        keyMapping = src;
    }
}

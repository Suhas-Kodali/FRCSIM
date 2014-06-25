package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
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
    private Vector3f jumpForce = new Vector3f(0, 60, 0);
    private KeyMapping keyMapping = KeyMapping.std;
    private final Alliance alliance;
    
    public Player(Node rootNode, PhysicsSpace space, Alliance alliance) {

        //create a compound shape and attach the BoxCollisionShape for the car body at 0,1,0
        //this shifts the effective center of mass of the BoxCollisionShape to 0,-1,0
        
        Node chassisNode = new Node("chassis Node");
        Box chassis = new Box(new Vector3f(0, in(3), 0), in(14), in(2.5f), in(14));
        Geometry chassisGeometry = new Geometry("Chassis", chassis);
        chassisGeometry.setMaterial(Main.black);
        chassisNode.attachChild(chassisGeometry);
        new Bumper(chassisNode, Main.in(28), Main.in(28), Main.in(2), alliance);
        
        CompoundCollisionShape compoundShape = new CompoundCollisionShape();
        CollisionShape box = new BoxCollisionShape(new Vector3f(in(14), in(2.5f), in(14)));
        compoundShape.addChildShape(box, new Vector3f(0, in(3), 0));
        
        this.alliance = alliance;

        //create vehicle node
        Node vehicleNode=new Node("vehicleNode");
        vehicleNode.attachChild(chassisNode);
        vehicle = new VehicleControl(compoundShape, 400);
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
            Geometry wheels = new Geometry("wheel", wheelMesh);
            node.attachChild(wheels);
            wheels.rotate(0, FastMath.HALF_PI, 0);
            wheels.setMaterial(Main.black);
            
            vehicle.addWheel(node, new Vector3f(pos[i][0], pos[i][2], pos[i][1]),
                    wheelDirection, wheelAxle, restLength, radius, false);
            vehicleNode.attachChild(node);
        }
                
        rootNode.attachChild(vehicleNode);

        space.add(vehicle);
        vehicle.setPhysicsLocation(new Vector3f(0,-5,0));
    }
    
    public Vector3f getPhysicsLocation(){
        return vehicle.getPhysicsLocation();
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
    }
    
    public void jump(){
        vehicle.applyImpulse(jumpForce, Vector3f.ZERO);
    }
    
    public void reset(){
        System.out.println("Reset");
        vehicle.setPhysicsLocation(new Vector3f(0,-5,0));
        vehicle.setPhysicsRotation(new Matrix3f());
        vehicle.setLinearVelocity(Vector3f.ZERO);
        vehicle.setAngularVelocity(Vector3f.ZERO);
        vehicle.resetSuspension();
    }
    
    public static class KeyMapping{
        public final String up, down, left, right;
        public KeyMapping(String up, String down, String left, String right){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
        }
        public final static KeyMapping std = new KeyMapping("up", "down", "left", "right");
        public final static KeyMapping wasd = new KeyMapping("w", "s", "a", "d");
    }
    
    public void setKeyMapping(KeyMapping src){
        keyMapping = src;
    }
}

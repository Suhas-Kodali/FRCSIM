package org.frogforce503.FRCSIM;

import org.frogforce503.FRCSIM.AI.Position;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import static org.frogforce503.FRCSIM.Robot.robots;

/**
 *
 * @author Bryce Paputa
 */
public class Ball extends Position{
    
    public final Alliance alliance;
    public Geometry sphereGeometry;
    private RigidBodyControl sphereControl;
    public static final float drag = 1f/18f;
    public static final ArrayList<Ball> balls = new ArrayList<Ball>(6);
    public final int number;
    private static int count = 0;
    private boolean scored = false;
    private boolean trussed = false;
    private PhysicsSpace space;
    private static int redCount = 0, blueCount = 0;
    private boolean noAssistsLeft = false;
    
    private final ArrayList<Robot> owners = new ArrayList<Robot>(3);
    
    public Ball(Node rootNode, PhysicsSpace space, Alliance alliance){
        this.space = space;
        Sphere sphere = new Sphere(32, 32, Main.in(12.5f));
        sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(alliance.material);
        sphereControl = new RigidBodyControl(.907f);
        sphereGeometry.addControl(sphereControl);
        sphereControl.setUserObject(this);
        sphereControl.setPhysicsLocation(new Vector3f(1, 0, 0));
        lastPos = new Vector3f(1, 0, 0);
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);
        this.alliance = alliance;
        switch(alliance){
            case RED:
                redCount++;
                break;
            case BLUE:
                blueCount++;
        }        
        balls.add(this);
        number = count++;
    }
    
    private Vector3f lastPos;
    public void update(){
        Vector3f curPos = sphereControl.getPhysicsLocation();
        sphereControl.applyCentralForce(sphereControl.getLinearVelocity().normalize().mult(sphereControl.getLinearVelocity().distanceSquared(Vector3f.ZERO)).mult(-drag));
        if(Main.field.isBallOutOfBounds(this) && !isOwned()){
            HumanPlayer.ballExitField(this, getPosition());
        }
        if(!trussed && (lastPos.x * curPos.x <= 0) && (lastPos.x * alliance.side > curPos.x * alliance.side) && (lastPos.y-Main.in(12.5f) > Main.in(74))){
            trussed = true;
            alliance.incrementScore(10);
        }
        lastPos = curPos;
        
        if(owner instanceof Robot && ((Robot) owner).alliance == alliance){
            if(!owners.contains((Robot) owner)){
                owners.add((Robot) owner);
            }
        }
        if(owners.size() == Robot.robots.get(alliance).size()){
            noAssistsLeft = true;
        } else {
            noAssistsLeft = false;
        }
    }
    
    public static void updateAll(){
        for(int i = balls.size()-1; i >= 0; i--){
            balls.get(i).update();
        }
        if(redCount<=0){
            Ball ball = new Ball(Main.getRoot(), Main.bulletAppState.getPhysicsSpace(), Alliance.RED);
            ball.setPosition(Vector3f.UNIT_X.mult(Main.in(-32*12)));
            //ball.setPosition(new Vector3f(((new Random()).nextFloat()-.5f)*Main.in(54*24), ((new Random()).nextFloat()-.5f)*5, ((new Random()).nextFloat()-.5f)*Main.in(25*24)));
        }
        if(blueCount<=0){
            Ball ball = new Ball(Main.getRoot(), Main.bulletAppState.getPhysicsSpace(), Alliance.BLUE);
            ball.setPosition(Vector3f.UNIT_X.mult(Main.in(32*12)));
        }
    }
    
    public static Ball getClosestBall(Vector3f point, Alliance alliance){
        float minDistance = Float.MAX_VALUE;
        Ball ball = null;
        for(Ball curBall : balls){
            if(curBall.alliance == alliance){
                float curDistance = curBall.getPosition().subtract(point).length();
                if(curDistance < minDistance){
                    minDistance = curDistance;
                    ball = curBall;
                }
            }
        }
        return ball;
    }
    public RigidBodyControl getRigidBodyControl(){
        return this.sphereControl;
    }
    
    public Geometry getGeometry(){
        return this.sphereGeometry;
    }
    
    public boolean anyAssistsLeft(){
        return !noAssistsLeft;
    }
    
    @Override
    public boolean equals(Object other){
        if(other instanceof Ball){
            return ((Ball) other).number == this.number;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.number;
        return hash;
    }
    
    public void score(){
        this.scored = true;
    }
    
    public boolean isScored(){
        return scored;
    }
       
    public Object owner = null;
    public void capture(Object newOwner){
        this.owner = newOwner;
    }
    
    public void release(){
        this.owner = null;
    }
    
    public boolean isOwned(){
        return owner != null;
    }
    
    boolean doesExist = true;
    public void destroy(){
        getGeometry().removeFromParent();
        space.remove(getGeometry());
        balls.remove(this);
        doesExist = false;
        switch(alliance){
            case RED:
                redCount--;
                break;
            case BLUE:
                blueCount--;
        }
    }
    
    public Vector3f getPosition(){
        if(doesExist){
            return sphereControl.getPhysicsLocation();
        }
        return null;
    }
    
    public Vector3f getVelocity(){
        return sphereControl.getLinearVelocity();
    }
    
    public void setVelocity(Vector3f v){
        sphereControl.setLinearVelocity(v);
    }
    
    public void setPosition(Vector3f pos){
        sphereControl.setPhysicsLocation(lastPos = pos);
    }
    
    public int getAssistScore(){
        int assistsNum = owners.size();
        return 5 * (assistsNum * (assistsNum - 1));
    }
    
    public boolean hasBeenOwnedBy(Robot obj){
        return owners.contains(obj);
    }
}

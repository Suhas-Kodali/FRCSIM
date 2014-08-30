package org.frogforce503.FRCSIM;

import org.frogforce503.FRCSIM.AI.Position;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;

/**
 *
 * @author Bryce Paputa
 */
public class Ball implements Position{
    
    public final Alliance alliance;
    private final Geometry sphereGeometry;
    private RigidBodyControl sphereControl;
    private static final float drag = 1f/18f;
    public static final ArrayList<Ball> balls = new ArrayList<Ball>(6);
    public final int number = count++;
    private static int count = 1;
    private boolean scored = false;
    private boolean trussed = false;
    private static int redCount = 0, blueCount = 0;
    private boolean noAssistsLeft = false;
    private Vector3f lastPos;    
    private Object owner = null;
    private final ArrayList<Robot> owners = new ArrayList<Robot>(3);
    
    public Ball(final Node rootNode, final PhysicsSpace space, final Alliance alliance){
        Sphere sphere = new Sphere(16, 16, Main.in(12.5f), false, true);
        sphere.setTextureMode(Sphere.TextureMode.Projected);
        sphereGeometry = new Geometry("ball#"+count, sphere);
        sphereGeometry.setMaterial(alliance.material);
        sphereControl = new RigidBodyControl(.907f);
        sphereGeometry.addControl(sphereControl);
        sphereControl.setUserObject(this);
        lastPos = new Vector3f(0, 0, 0);
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
    }
    
    public void reset(final Vector3f pos){
        setPosition(pos);
        setVelocity(Vector3f.ZERO);
        scored = false;
        trussed = false;
        noAssistsLeft = false;
        owner = null;
        owners.clear();
    }
    
    public void update(){
        final Vector3f curPos = sphereControl.getPhysicsLocation();
        sphereControl.applyCentralForce(sphereControl.getLinearVelocity().normalize().mult(sphereControl.getLinearVelocity().distanceSquared(Vector3f.ZERO)).mult(-drag));
        if(Main.field.isBallOutOfBounds(this) && !isOwned()){
            HumanPlayer.ballExitField(this, getPosition());
        }
        
        if(Math.abs(curPos.z) > Field.width/2){
            sphereControl.applyCentralForce(Vector3f.UNIT_Z.mult(-curPos.z));
        }
        
        if(!trussed && (lastPos.x * curPos.x <= 0) && (lastPos.x * alliance.side > curPos.x * alliance.side) && (lastPos.y-Main.in(12.5f) > Main.in(74))){
            trussed = true;
            alliance.incrementScore(10);
        }
        lastPos = curPos;
    }
    
    public static void updateAll(){
        for(int i = balls.size()-1; i >= 0; i--){
            balls.get(i).update();
        }
        if(redCount<=0){
            Ball ball = new Ball(Main.app.getRootNode(), Main.bulletAppState.getPhysicsSpace(), Alliance.RED);
            ball.setPosition(Vector3f.UNIT_X.mult(Main.in(-32*12)));
        }
        if(blueCount<=0){
            Ball ball = new Ball(Main.app.getRootNode(), Main.bulletAppState.getPhysicsSpace(), Alliance.BLUE);
            ball.setPosition(Vector3f.UNIT_X.mult(Main.in(32*12)));
        }             
        
        for(int j = Field.redGoalGhost.getOverlappingObjects().size()-1; j >=0; j--){
            if(Field.redGoalGhost.getOverlapping(j).getUserObject() instanceof Ball){
                Ball ball = (Ball) Field.redGoalGhost.getOverlapping(j).getUserObject();
                if(!ball.isScored() && ball.alliance == Alliance.RED && ball.getPosition().x > Main.in(54*12/2)){
                    Alliance.RED.incrementScore(10 + ball.getAssistScore());
                    ball.score();
                }
            }
        }           
        for(int j = Field.blueGoalGhost.getOverlappingObjects().size()-1; j >=0; j--){
            if(Field.blueGoalGhost.getOverlapping(j).getUserObject() instanceof Ball){
                Ball ball = (Ball) Field.blueGoalGhost.getOverlapping(j).getUserObject();
                if(!ball.isScored() && ball.alliance == Alliance.BLUE && ball.getPosition().x < Main.in(-54*12/2)){
                    Alliance.BLUE.incrementScore(10 + ball.getAssistScore());
                    ball.score();
                }
            }
        }
    }
    
    public static Ball getClosestBall(final Vector3f point, final Alliance alliance){
        float minDistance = Float.MAX_VALUE;
        Ball ball = null;
        for(Ball curBall : balls){
            if(curBall.alliance == alliance){
                float curDistance = curBall.getPosition().subtract(point).lengthSquared();
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
    public boolean equals(final Object other){
        if(other instanceof Ball){
            return ((Ball) other).number == this.number;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.number;
    }
    
    public void score(){
        this.scored = true;
    }
    
    public boolean isScored(){
        return scored;
    }
       
    public void capture(final Object newOwner){
        this.owner = newOwner;
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
    
    public Object getOwner(){
        return owner;
    }
    
    public void release(){
        this.owner = null;
    }
    
    public boolean isOwned(){
        return owner != null;
    }
    
    public boolean isOwnedByRobot(){
        return owner != null && owner instanceof Robot;
    }
    
    public void destroy(){
        if(alliance == Alliance.RED){
            reset(Vector3f.UNIT_X.mult(Main.in(-32*12)));
        } else {
            reset(Vector3f.UNIT_X.mult(Main.in(32*12)));
        }
    }
    
    public Vector3f getPosition(){
        return sphereControl.getPhysicsLocation();
    }
    
    public Vector3f getVelocity(){
        return sphereControl.getLinearVelocity();
    }
    
    public void setVelocity(final Vector3f v){
        sphereControl.setLinearVelocity(v);
    }
    
    public void setPosition(final Vector3f pos){
        sphereControl.setPhysicsLocation(lastPos = pos);
    }
    
    public int getAssistScore(){
        final int assistsNum = owners.size();
        return 5 * (assistsNum * (assistsNum - 1));
    }
    
    public boolean hasBeenOwnedBy(final Robot obj){
        return owners.contains(obj);
    }

    public boolean hasBeenTrussed() {
        return trussed;
    }
}

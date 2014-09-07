package org.frogforce503.FRCSIM;

import org.frogforce503.FRCSIM.AI.Position;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import static org.frogforce503.FRCSIM.Main.in;

/**
 * Class for the game peice.
 * @author Bryce Paputa
 */
public class Ball implements Position, DTSDebuggable{
    /**
     * Which alliance this ball is for.
     */
    public final Alliance alliance;
    
    /**
     * List of all balls.
     */
    public static final ArrayList<Ball> balls = new ArrayList<Ball>(6);
    
    /**
     * Unique ID number.
     */
    public final int number = count++;
    
    private final Geometry sphereGeometry;
    private RigidBodyControl sphereControl;
    private static final float drag = 1f/18f;
    private static int count = 1;
    private boolean scored = false;
    private boolean trussed = false;
    private static int redCount = 0, blueCount = 0;
    private boolean noAssistsLeft = false;
    private Vector3f lastPos;    
    private BallOwner owner = null;
    private final ArrayList<Robot> owners = new ArrayList<Robot>(3);
    
    /**
     * Ball constructor.
     * @param rootNode  Node to add the ball to
     * @param space     PhysicsSpace
     * @param alliance  Alliance that owns the ball
     */
    public Ball(final Node rootNode, final PhysicsSpace space, final Alliance alliance){
        Sphere sphere = new Sphere(16, 16, in(12.5f), false, true);
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
            case Red:
                redCount++;
                break;
            case Blue:
                blueCount++;
        }        
        balls.add(this);
    }
    
    /**
     * Resets the ball back to the "pedestal".
     * @param pos Position of pedestal
     */
    public void reset(final Vector3f pos){
        setPosition(pos);
        setVelocity(Vector3f.ZERO);
        scored = false;
        trussed = false;
        noAssistsLeft = false;
        if(owner!=null){
            owner.releaseBall();
        }
        owner = null;
        owners.clear();
    }
    
    /**
     * Resets the ball back to the correct pedestal.
     */
    public void reset(){
        if(alliance == Alliance.Red){
            reset(Vector3f.UNIT_X.mult(in(-32*12)));
        } else {
            reset(Vector3f.UNIT_X.mult(in(32*12)));
        }
    }
    
    /**
     * Applys drag and checks for trusses.
     */
    public void update(){
        final Vector3f curPos = sphereControl.getPhysicsLocation();
        sphereControl.applyCentralForce(sphereControl.getLinearVelocity().normalize().mult(sphereControl.getLinearVelocity().distanceSquared(Vector3f.ZERO)).mult(-drag));
        if(Main.field.isBallOutOfBounds(this) && !isOwned()){
            HumanPlayer.giveBallToNearestHP(this);
        }
        
        if(Math.abs(curPos.z) > Field.width/2){
            sphereControl.applyCentralForce(Vector3f.UNIT_Z.mult(-curPos.z));
        }
        
        if(!trussed && (lastPos.x * curPos.x <= 0) && (lastPos.x * alliance.side > curPos.x * alliance.side) && (lastPos.y-in(12.5f) > in(74))){
            trussed = true;
            alliance.incrementScore(10);
        }
        lastPos = curPos;
    }
    
    /**
     * Updates all of the balls and checks for scoring.
     */
    public static void updateAll(){
        for(int i = balls.size()-1; i >= 0; i--){
            balls.get(i).update();
        }
        if(redCount < Main.maxBalls){
            Ball ball = new Ball(Main.app.getRootNode(), Main.bulletAppState.getPhysicsSpace(), Alliance.Red);
            ball.setPosition(Vector3f.UNIT_X.mult(in(-32*12)));
        }
        if(blueCount < Main.maxBalls){
            Ball ball = new Ball(Main.app.getRootNode(), Main.bulletAppState.getPhysicsSpace(), Alliance.Blue);
            ball.setPosition(Vector3f.UNIT_X.mult(in(32*12)));
        }             
        
        for(int j = Field.redGoalGhost.getOverlappingObjects().size()-1; j >=0; j--){
            if(Field.redGoalGhost.getOverlapping(j).getUserObject() instanceof Ball){
                Ball ball = (Ball) Field.redGoalGhost.getOverlapping(j).getUserObject();
                if(!ball.isScored() && ball.alliance == Alliance.Red && ball.getPosition().x > in(54*12/2)){
                    Alliance.Red.incrementScore(10 + ball.getAssistScore());
                    ball.score();
                }
            }
        }           
        
        for(int j = Field.blueGoalGhost.getOverlappingObjects().size()-1; j >=0; j--){
            if(Field.blueGoalGhost.getOverlapping(j).getUserObject() instanceof Ball){
                Ball ball = (Ball) Field.blueGoalGhost.getOverlapping(j).getUserObject();
                if(!ball.isScored() && ball.alliance == Alliance.Blue && ball.getPosition().x < in(-54*12/2)){
                    Alliance.Blue.incrementScore(10 + ball.getAssistScore());
                    ball.score();
                }
            }
        }
        
        for(int k = Field.lowGoalGhosts.length - 1; k>=0; k--){
            for(int j = Field.lowGoalGhosts[k].getOverlappingObjects().size() - 1; j >= 0; j--){
                if(Field.lowGoalGhosts[k].getOverlapping(j).getUserObject() instanceof Ball){
                    Ball ball = (Ball) Field.lowGoalGhosts[k].getOverlapping(j).getUserObject();
                    if(!ball.isScored() && ball.alliance.side * ball.getPosition().x < 0){
                        ball.alliance.incrementScore(1 + ball.getAssistScore());
                        ball.score();
                        if(ball.isOwned()){
                            ball.release();
                        }
                        ball.setPosition(ball.getPosition().mult(new Vector3f(1, 1.25f, 1.25f)));
                        HumanPlayer.giveBallToNearestHP(ball);
                    } else if(!ball.isOwned()){
                        ball.setPosition(ball.getPosition().mult(new Vector3f(1, 1.25f, 1)));
                        HumanPlayer.giveBallToNearestHP(ball);
                    }
                }
            }
        }        
    }
    
    /**
     * Gets the closest ball of a specified alliance to a point.
     * @param point     Point to compare distances from
     * @param alliance  Alliance to find
     * @return          Closest ball of specified alliance to the point.
     */
    public static Ball getClosestBall(final Vector3f point, final Alliance alliance){
        float minDistance = Float.MAX_VALUE;
        Ball ball = null;
        for(Ball curBall : balls){
            if(curBall.alliance == alliance || alliance == null){
                float curDistance = curBall.getPosition().subtract(point).lengthSquared();
                if(curDistance < minDistance){
                    minDistance = curDistance;
                    ball = curBall;
                }
            }
        }
        return ball;
    }
    
    /**
     * Returns whether or not there are any aassists left to get on the ball.
     * @return Whether or not there are any aassists left to get on the ball
     */
    public boolean anyAssistsLeft(){
        return !noAssistsLeft;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean equals(final Object other){
        if(other instanceof Ball){
            return ((Ball) other).number == this.number;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public int hashCode() {
        return this.number;
    }
    
    /**
     * Marks the ball as scored.
     */
    public void score(){
        this.scored = true;
    }
    
    /**
     * Checks if the ball has been scored.
     * @return Whether or not this ball been scored
     */
    public boolean isScored(){
        return scored;
    }
       
    /**
     * Changes the owner of the ball.
     * @param newOwner New owner
     */
    public void capture(final BallOwner newOwner){
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
    
    /**
     * Gets the current owner of the ball.
     * @return Current owner of the ball
     */
    public Object getOwner(){
        return owner;
    }
    
    /**
     * Tells the current owner to release the ball and removes it as owner.
     */
    public void release(){
        owner.releaseBall();
        owner = null;
    }
    
    /**
     * Is this ball owned?
     * @return Whether or not this ball is owned.
     */
    public boolean isOwned(){
        return owner != null;
    }
    
    /**
     * Is this ball owned and is that owner a robot?
     * @return Whether or not this ball is owned by a robot
     */
    public boolean isOwnedByRobot(){
        return owner != null && owner instanceof Robot;
    }
    
    /**
     * Gets the position of this ball.
     * @return Position of this ball
     */
    @Override
    public Vector3f getPosition(){
        return sphereControl.getPhysicsLocation();
    }
    
    /**
     * Gets the velocity of this ball.
     * @return Velocity of this ball
     */
    public Vector3f getVelocity(){
        return sphereControl.getLinearVelocity();
    }
    
    /**
     * Sets the velocity of this ball.
     * @param v New velocity of this ball
     */
    public void setVelocity(final Vector3f v){
        sphereControl.setLinearVelocity(v);
    }
    
    /**
     * Sets the position of this ball.
     * @param pos New position of this ball
     */
    public void setPosition(final Vector3f pos){
        sphereControl.setPhysicsLocation(lastPos = pos);
    }
    
    /**
     * Applys a force to this ball.
     * @param force Force to be applied
     */
    public void applyForce(final Vector3f force){
        sphereControl.applyCentralForce(force);
    }
    
    /**
     * Calculates the assist score for this ball.
     * @return Calculated assist score.
     */
    public int getAssistScore(){
        final int assistsNum = owners.size();
        return 5 * (assistsNum * (assistsNum - 1));
    }
    
    /**
     * Has this ball been owned by a specific robot?
     * @param obj Robot to check
     * @return Whether or not the robot has owned this ball
     */
    public boolean hasBeenOwnedBy(final Robot obj){
        return owners.contains(obj);
    }

    /**
     * Has this ball been trussed?
     * @return Whether or not this ball has been trussed.
     */
    public boolean hasBeenTrussed() {
        return trussed;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String detailedToString(String offset) {
        StringBuilder temp = new StringBuilder();
        temp.append(offset).append("Ball(").append(number).append("){\n");
        temp.append(offset).append("    alliance: ").append(alliance.toString()).append(",\n");
        temp.append(offset).append("    trussed: ").append(trussed).append(",\n");
        temp.append(offset).append("    scored: ").append(scored).append(",\n");
        temp.append(offset).append("    position: ").append(getPosition().toString()).append(",\n");
        temp.append(offset).append("    velocity: ").append(getVelocity().toString()).append(",\n");
        temp.append(offset).append("    owners: [ ");
        for(Robot lowner : owners){
            temp.append(offset).append("\n         ").append(lowner.toString()).append(",");
        }
        temp.setLength(temp.length()-1);
        temp.append(offset).append("\n    ],\n");
        temp.append(offset).append("    owner: ").append(owner);
        temp.append(offset).append("\n}");
        return temp.toString();        
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString(){
        return "Ball(" + number + ")";
        
    }

    /**
     * Interface that just has a method to release an owned ball.
     * @see #release()
     */
    public static interface BallOwner {
        /**
         * Release the ball programatically and from any physical constraints.
         */
        public void releaseBall();
    }
}

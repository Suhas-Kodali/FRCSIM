package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import static org.frogforce503.FRCSIM.Main.in;

/**
 * A basic intake.
 * @author Bryce Paputa
 */
public class BasicIntake extends AbstractIntake{
    private final Geometry intakeGeometry;
    private final Geometry intakeGeometry2;
    private final GhostControl pullGhost;
    private final Node intakeNode;
    private final GhostControl holdGhost;
    private Ball heldBall;
    private Ball shootingBall;
    private final ArrayList<Ball> pulledBalls = new ArrayList<Ball>(6);
    private final Node holdGhostNode;
    private boolean isShooting = false;
    private boolean isIntakeExtended = false;
    
    /**
     * Constructor for a basic intake.
     */
    public BasicIntake(){
        final Box intakeBox = new Box(in(1), in(12), in(6));
        intakeGeometry = new Geometry("Intake", intakeBox);
        intakeGeometry.setLocalTranslation(in(31)/2, in(3) + in(12)/2 + in(10), in(28)/2 - in(6));
        intakeGeometry.setMaterial(Main.green);
        
        intakeGeometry2 = new Geometry("Intake", intakeBox);
        intakeGeometry2.setLocalTranslation(-in(31)/2, in(3) + in(12)/2 + in(10), in(28)/2 - in(6));
        intakeGeometry2.setMaterial(Main.green);
        
        pullGhost = new GhostControl(new BoxCollisionShape(new Vector3f(in(4),in(3 + 6 + 5)/2,0)));
        intakeNode = new Node("node");
        intakeNode.addControl(pullGhost);
        
        intakeNode.attachChild(intakeGeometry);
        intakeNode.attachChild(intakeGeometry2);
        
        holdGhost = new GhostControl(new BoxCollisionShape(new Vector3f(in(0f)/2,in(18)/2,in(0f)/2)));  // a box-shaped ghost
        holdGhostNode = new Node("a ghost-controlled thing");
        holdGhostNode.addControl(holdGhost);
        holdGhostNode.setLocalTranslation(new Vector3f(0,in(18)/2,0));
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void registerPhysics(final Node rootNode, final PhysicsSpace space) {
        space.add(intakeNode);
        rootNode.attachChild(intakeNode);
        space.add(pullGhost);
        space.add(holdGhost);
        rootNode.attachChild(holdGhostNode);
        
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void preShot(){
        isShooting = true;
        shootingBall = heldBall;
        heldBall = null;
        shootingBall.release();
        if(pulledBalls.contains(heldBall)){
            pulledBalls.remove(heldBall);
        } 
    }

    /**
     * {@inheritDoc} 
     */    
    @Override
    public void postShot(){
        shootingBall = null;
        isShooting = false;
    }
 
    /**
     * {@inheritDoc} 
     */   
    @Override
    public Ball getShootingBall(){
        return shootingBall;
    }
 
    /**
     * {@inheritDoc} 
     */   
    @Override
    public Ball getHeldBall() {
        return heldBall;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void update() {
        if(heldBall == null && !isShooting){
            for(int j = pullGhost.getOverlappingObjects().size()-1; j >=0; j--){
                if(pullGhost.getOverlapping(j).getUserObject() instanceof Ball){
                    Ball ball = ((Ball) pullGhost.getOverlapping(j).getUserObject());
                    pulledBalls.add(ball);
                }
            }
            for(int j = holdGhost.getOverlappingObjects().size()-1; j>=0; j--){
                if(holdGhost.getOverlapping(j).getUserObject() instanceof Ball && !((Ball) holdGhost.getOverlapping(j).getUserObject()).isOwned() && ((Ball) holdGhost.getOverlapping(j).getUserObject()).getVelocity().length()<7){
                    heldBall = (Ball) holdGhost.getOverlapping(j).getUserObject();
                    heldBall.capture(robot);
                    if(pulledBalls.contains(heldBall)){
                        pulledBalls.remove(heldBall);
                    }
                }
            }
        }
        
        for(Ball ball : pulledBalls){
            ball.applyForce(robot.getPosition().subtract(ball.getPosition()).normalize().add(new Vector3f(0,.5f,0)).mult(45));
        }
        pulledBalls.clear();
        if(heldBall!= null && !isShooting){
            heldBall.setPosition(robot.getPosition().add(new Vector3f(0, in(18), 0)));
        }
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void extend(){
        if(!isIntakeExtended){
            intakeNode.rotate(FastMath.HALF_PI, 0, 0);
            intakeNode.setLocalTranslation(intakeGeometry.getLocalTranslation().add(-in(16), -in(6)/2, in(2.5f)));
            isIntakeExtended = true;
        }
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void retract(){
        if(isIntakeExtended){
            intakeNode.rotate(-FastMath.HALF_PI, 0, 0);
            intakeNode.setLocalTranslation(intakeGeometry.getLocalTranslation().add(-in(16), -in(18), in(-6f)));
            isIntakeExtended = false;
        }
    }    
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isExtended(){
        return isIntakeExtended;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void releaseBall() {
        heldBall = null;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public String detailedToString(String offset){
        StringBuilder temp = new StringBuilder();
        temp.append(offset).append("BasicIntake{\n");
        temp.append(offset).append("    heldBall: ").append(heldBall).append(",\n");
        temp.append(offset).append("    shootingBall: ").append(shootingBall).append(",\n");
        temp.append(offset).append("    pulledBalls: [ ");
        for(Ball ball : pulledBalls){
            temp.append(offset).append("\n         ").append(ball).append(",");
        }
        temp.setLength(temp.length()-1);
        temp.append("\n").append(offset).append("    ]\n");
        temp.append(offset).append("}");
        return temp.toString();
    }
}

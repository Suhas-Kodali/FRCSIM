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
import org.frogforce503.FRCSIM.Ball.BallOwner;
import static org.frogforce503.FRCSIM.Main.in;

/**
 *
 * @author Bryce
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

    @Override
    public void registerPhysics(final Node rootNode, final PhysicsSpace space, final Alliance alliance) {
        space.add(intakeNode);
        rootNode.attachChild(intakeNode);
        space.add(pullGhost);
        space.add(holdGhost);
        rootNode.attachChild(holdGhostNode);
        
    }

    @Override
    public void preShot(){
        super.preShot();
        shootingBall = heldBall;
        heldBall = null;
        shootingBall.release();
        if(pulledBalls.contains(heldBall)){
            pulledBalls.remove(heldBall);
        } 
    }
    
    @Override
    public void postShot(){
        super.postShot();
        shootingBall = null;
    }
    
    @Override
    public Ball getShootingBall(){
        return shootingBall;
    }
    
    @Override
    public Ball getHeldBall() {
        return heldBall;
    }

    @Override
    public void update() {
        if(heldBall == null && !isShooting()){
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
            ball.getRigidBodyControl().applyCentralForce(vehicle.getPhysicsLocation().subtract(ball.getRigidBodyControl().getPhysicsLocation()).normalize().add(new Vector3f(0,.5f,0)).mult(45));
        }
        pulledBalls.clear();
        if(heldBall!= null && !isShooting()){
            heldBall.getRigidBodyControl().setPhysicsLocation(vehicle.getPhysicsLocation().add(new Vector3f(0, in(18), 0)));
        }
    }

    private boolean isIntakeExtended = false;
    
    @Override
    public void extend(){
        if(!isIntakeExtended){
            intakeNode.rotate(FastMath.HALF_PI, 0, 0);
            intakeNode.setLocalTranslation(intakeGeometry.getLocalTranslation().add(-in(16), -in(6)/2, in(2.5f)));
            isIntakeExtended = true;
        }
    }
    
    @Override
    public void retract(){
        if(isIntakeExtended){
            intakeNode.rotate(-FastMath.HALF_PI, 0, 0);
            intakeNode.setLocalTranslation(intakeGeometry.getLocalTranslation().add(-in(16), -in(18), in(-6f)));
            isIntakeExtended = false;
        }
    }    
    
    @Override
    public boolean isExtended(){
        return isIntakeExtended;
    }

    @Override
    public void releaseBall() {
        heldBall = null;
    }
}

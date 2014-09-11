package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import org.frogforce503.FRCSIM.Alliance;
import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Main;
import org.frogforce503.FRCSIM.Robot;

/**
 * Class that finds a position that interferes with offense.
 * @author Bryce Paputa
 */
public class InterferencePosition implements Position{
    private final Robot defender;
    private boolean isPinning = false, isRunningAway = false;
    private long pinBeginTime = 0, runBeginTime = 0;
    
    /**
     * Constructor for an interference position.
     * @param defender Reference to the defending robot
     */
    public InterferencePosition(final Robot defender){
        this.defender = defender;
        if(defender != Main.player){
            defender.setWantsBall(false);
        }
    }
    
    /**
     * {@inheritDoc}
     * Calculates a new interfering position on each call. 
     */
    public Vector3f getPosition() {
        final Robot robotTarget = Robot.getClosestRobot(defender.getPosition(), defender.alliance.invert());
        if(robotTarget == null){
            return Vector3f.ZERO;
        }
        final Vector3f vectorTarget = robotTarget.getPosition();
        
        Vector3f ballTarget, ourBall;
        try{
            ballTarget = Ball.getClosestBall(vectorTarget, (defender.alliance == Alliance.Red? Alliance.Blue : Alliance.Red)).getPosition();
        } catch(NullPointerException e){
            ballTarget = null;
        }
        try{
            ourBall = Ball.getClosestBall(vectorTarget, defender.alliance).getPosition();
            if(ourBall.distance(vectorTarget)<vectorTarget.distance(ballTarget) && vectorTarget.distance(ballTarget)<5){
                return defender.getPosition().add(defender.getPosition().subtract(ourBall));
            }
        } catch(NullPointerException e){ }
        
        if(vectorTarget.distance(defender.getPosition())<1 && robotTarget.isTouchingWall()){
            if(isPinning){
                if(System.nanoTime() - pinBeginTime > 2500000000l){
                    isPinning = false;
                    isRunningAway = true;
                    runBeginTime = System.nanoTime();
                    pinBeginTime = 0;
                }
            } else {
                isPinning = true;
                pinBeginTime = System.nanoTime();
            }
        }
        
        float runFactor = 1;
        
        if(isRunningAway){
            if(System.nanoTime() - runBeginTime > 2500000000l){
                isRunningAway = false;
                runBeginTime = 0;
            } else {
                runFactor = .75f;
            }
        }
                
        if(ballTarget == null || ballTarget.subtract(robotTarget.getPosition()).length() > Main.in(12*27)){
            return robotTarget.getPosition().mult(runFactor);
        }
        return robotTarget.getPosition().add(ballTarget).mult(.5f * runFactor);
    }
}
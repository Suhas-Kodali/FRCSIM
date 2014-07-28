package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import org.frogforce503.FRCSIM.Alliance;
import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Main;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class InterferencePosition extends Position{
    Robot defender;
    public InterferencePosition(Robot defender){
        this.defender = defender;
    }
    private boolean isPinning = false, isRunningAway = false;
    private long pinBeginTime = 0, runBeginTime = 0;
    public Vector3f getPosition() {
        Robot robotTarget = Robot.getClosestRobot(defender.getPosition(), (defender.alliance == Alliance.RED? Alliance.BLUE : Alliance.RED));
        if(robotTarget == null){
            return Vector3f.ZERO;
        }
        Position ballTarget = Ball.getClosestBall(robotTarget.getPosition(), (defender.alliance == Alliance.RED? Alliance.BLUE : Alliance.RED));
        
        if(robotTarget.distanceTo(defender)<1 && robotTarget.isTouchingWall()){
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
        
        if(ballTarget == null || ballTarget.getPosition() == null){
            return robotTarget.getPosition().mult(runFactor);
        }
        if(ballTarget.getPosition().subtract(robotTarget.getPosition()).length() > Main.in(12*27)){
            return robotTarget.getPosition().mult(runFactor);
        }
        return robotTarget.getPosition().add(ballTarget.getPosition()).mult(.5f * runFactor);
    }
}
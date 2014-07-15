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
public class InterferencePosition implements Position{
    Robot defender;
    public InterferencePosition(Robot defender){
        this.defender = defender;
    }
    
    public Vector3f getPosition() {
        Position robotTarget = Robot.getClosestRobot(defender.getPosition(), (defender.alliance == Alliance.RED? Alliance.BLUE : Alliance.RED));
        Position ballTarget = Ball.getClosestBall(robotTarget.getPosition(), (defender.alliance == Alliance.RED? Alliance.BLUE : Alliance.RED));
        if(ballTarget == null || ballTarget.getPosition() == null){
            return robotTarget.getPosition();
        }
        if(ballTarget.getPosition().subtract(robotTarget.getPosition()).length() > Main.in(12*27)){
            return robotTarget.getPosition();
        }
        return robotTarget.getPosition().add(ballTarget.getPosition()).mult(.5f);
    }
}

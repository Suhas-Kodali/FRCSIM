package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class InterferencePosition implements Position{
    Ball ball;
    public InterferencePosition(Ball ball){
        this.ball = ball;
    }
    
    public Vector3f getPosition() {
        if(ball.getPosition() == null){
            return null;
        }
        return Robot.getClosestRobot(ball.getPosition(), ball.alliance).getPosition().add(ball.getPosition()).mult(.5f);
    }
}

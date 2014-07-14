package org.frogforce503.FRCSIM.AI;

import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Robot;
import org.frogforce503.FRCSIM.Zone;

/**
 *
 * @author Bryce
 */
public class TestAI extends AIControl{
    Robot target;
    public TestAI(Robot target){
        this.target = target;
    }
    
    public AbstractProgram chooseNextProgram() {
        return new GoToProgram(new InterferencePosition(Ball.balls.get(0)), -1);
    }
}

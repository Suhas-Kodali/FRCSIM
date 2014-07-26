package org.frogforce503.FRCSIM.AI;

import org.frogforce503.FRCSIM.AI.GoToProgram.Check;

/**
 *
 * @author Bryce
 */
public class TestAI extends AIControl{
    public TestAI(){}
    
    
    public AbstractProgram chooseNextProgram() {
        return new GoToProgram(new InterferencePosition(robot), -1, new Check(){

            @Override
            public boolean shouldExit() {
                return false;
            }
            
        });
    }
}

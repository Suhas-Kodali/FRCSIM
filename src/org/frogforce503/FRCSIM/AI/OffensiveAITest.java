package org.frogforce503.FRCSIM.AI;

/**
 *
 * @author Bryce
 */
public class OffensiveAITest extends AIControl{

    @Override
    public AbstractProgram chooseNextProgram() {
        return (robot.hasBall()? (!robot.getCurrentBall().anyAssistsLeft()? new ScoreBallProgram() : new GetNearestAssistProgram()) : new GetBallProgram());
    }
    
}

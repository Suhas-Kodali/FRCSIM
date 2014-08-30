package org.frogforce503.FRCSIM.AI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.frogforce503.FRCSIM.Alliance;
import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class AISuperCoach {
    private final Alliance alliance;
    
    public AISuperCoach(final Alliance alliance){
        this.alliance = alliance;
    }

    public void update() {
        final ArrayList<Robot> rolelessRobots = new ArrayList<Robot>(Robot.robots.get(alliance));
        final ArrayList<Robot> ourRobots = Robot.robots.get(alliance);
        final ArrayList<Ball> balls = Ball.balls;
        final HashMap<Robot, RobotRole> roles = new HashMap<Robot, RobotRole>(3);
        final HashMap<Robot, Position> targets = new HashMap<Robot, Position>(3);
        for(Ball ball : balls){
            final Robot owner = (ball.getOwner() instanceof Robot) ? ((Robot) ball.getOwner()) : null;
            if(ball.alliance == alliance){
                if(owner == null){
                    Robot pickuper = null;
                    float minDist = Float.MAX_VALUE;
                    for(Robot robot : rolelessRobots){
                        float distance = robot.getPosition().distance(ball.getPosition()) + (ball.hasBeenOwnedBy(robot)? 5 : 0);
                        if(!robot.hasBall() && distance < minDist){
                            minDist = distance;
                            pickuper = robot;
                        }
                    }
                    if(pickuper == null){
                        break;
                    }
                    roles.put(pickuper, RobotRole.Pickup);
                    targets.put(pickuper, ball);
                    rolelessRobots.remove(pickuper);
                } else {
                    if(owner.alliance == alliance){
                        if(ball.anyAssistsLeft()){
                            Robot assistGetter = null;
                            float minScore = Float.MAX_VALUE;
                            for(Robot robot : ourRobots){
                                float score = robot.getPosition().distance(owner.getPosition()) + (robot.hasBall()? 10 : 0) + (ball.hasBeenOwnedBy(robot)? 1000 : 0) + (rolelessRobots.contains(robot)? 0 : (roles.get(robot) == RobotRole.AssistGiver? (targets.get(robot) == owner? -20 : 5) : 10));
                                if(score < minScore){
                                    assistGetter = robot;
                                    minScore = score;
                                }
                            }
                            if(assistGetter != null && ((minScore < 5 && owner.getPosition().x * alliance.side > 0 && !ball.hasBeenTrussed()) || (minScore < 10 && owner.getPosition().x * alliance.side < 0))){
                                rolelessRobots.remove(owner);
                                roles.put(owner, RobotRole.AssistGiver);
                                targets.put(owner, assistGetter);
                                if(rolelessRobots.contains(assistGetter)){
                                    rolelessRobots.remove(assistGetter);
                                    roles.put(assistGetter, RobotRole.AssistGetter);
                                    targets.put(assistGetter, ball);
                                }
                            } else {
                                rolelessRobots.remove(owner);
                                if(owner.getPosition().x * alliance.side > 0 && !ball.hasBeenTrussed()){
                                    roles.put(owner, RobotRole.Trusser);
                                } else {
                                    roles.put(owner, RobotRole.Scorer);
                                }
                            }
                        } else {
                            rolelessRobots.remove(owner);
                            if(owner.getPosition().x * alliance.side > 0 && !ball.hasBeenTrussed()){
                                roles.put(owner, RobotRole.Trusser);
                            } else {
                                roles.put(owner, RobotRole.Scorer);
                            }
                        }
                    }
                }
            } else if(owner != null && owner.alliance==alliance){
                roles.remove(owner);
                roles.put(owner, RobotRole.Eject);
                rolelessRobots.remove(owner);
            }
        }
        for(Robot robot : rolelessRobots){
            if(robot.getAIFollower()!=null){
                roles.put(robot, RobotRole.Defense);
            }
        }
        for(Entry robotRole : roles.entrySet()){
            Robot robot = (Robot) robotRole.getKey();
                switch((RobotRole) robotRole.getValue()){
                    case Pickup:
                    case AssistGetter:
                        robot.getAIFollower().setProgram(new GetBallProgram((Ball) targets.get(robot)));
                        break;
                    case Scorer:
                        robot.getAIFollower().setProgram(new ScoreBallProgram());
                        break;
                    case AssistGiver:
                        robot.getAIFollower().setProgram(new GetAssistProgram((Robot) targets.get(robot)));
                        break;
                    case Trusser:
                        robot.getAIFollower().setProgram(new TrussProgram());
                        break;
                    case Defense:
                        robot.getAIFollower().setProgram(new DefenseProgram());
                        break;
                    case Eject:
                        robot.getAIFollower().setProgram(new EjectProgram());
            }
        }
    }

    void registerFollower(final AIFollowerProgram aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static enum RobotRole{
        Pickup, Scorer, AssistGiver, AssistGetter, Trusser, Defense, Eject;
    }
}

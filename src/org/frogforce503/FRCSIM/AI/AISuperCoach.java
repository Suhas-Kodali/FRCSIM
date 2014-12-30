package org.frogforce503.FRCSIM.AI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.frogforce503.FRCSIM.Alliance;
import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.DTSDebuggable;
import org.frogforce503.FRCSIM.Robot;

/**
 * AI coach that sends commands to the alliance's robots.
 * @author Bryce Paputa
 */
public class AISuperCoach implements DTSDebuggable{
    private final Alliance alliance;
    private final HashMap<Robot, RobotRole> roles = new HashMap<Robot, RobotRole>(3);
    private final HashMap<Robot, Position> targets = new HashMap<Robot, Position>(3);
    
    /**
     * Constructor for an AI coach.
     * @param alliance Alliance to coach
     */
    public AISuperCoach(final Alliance alliance){
        this.alliance = alliance;
    }
    
    /**
     * Runs the algorithm to determine the next commands and sends them to the robots.
     */
    public void update() {
        final ArrayList<Robot> rolelessRobots = new ArrayList<Robot>(Robot.robots.get(alliance));
        final ArrayList<Robot> ourRobots = Robot.robots.get(alliance);
        final ArrayList<Ball> balls = Ball.balls;
        roles.clear();
        targets.clear();
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
    
    /**
     * {@inheritDoc}
     */
    public String detailedToString(String offset) {
        StringBuilder temp = new StringBuilder();
        temp.append(offset).append("AISuperCoach(").append(alliance).append("){\n")
            .append(offset).append("    Last Assignment: { ");
        for(Robot robot : roles.keySet()){
            temp.append(offset).append("\n        ").append(robot).append(": ").append(roles.get(robot));
            if(targets.containsKey(robot)){
                temp.append("\n").append(offset).append("            ").append("Target: ").append(targets.get(robot));
            }
            temp.append(",");
        }
        temp.setLength(temp.length()-1);
        temp.append("\n    }\n}");
        return temp.toString();
    }
    
    /**
     * Enum that represents the different commmands.
     */
    public static enum RobotRole{
        /**
         * Tells the robot to pick up a specific ball.
         */
        Pickup, 
        
        /**
         * Tells the robot to score.
         */
        Scorer, 
        
        /**
         * Tells the robot to give the ball to another robot to get an assist.
         */
        AssistGiver, 
        
        /**
         * Tells the robot to get the ball from another robot to get an assist.
         */
        AssistGetter, 
        
        /**
         * Tells the robot to truss.
         */
        Trusser, 
        
        /**
         * Tells the robot to play defense.
         */
        Defense, 
        
        /**
         * Tells the robot to eject it's ball.
         */
        Eject;
    }
}

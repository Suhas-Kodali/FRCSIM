package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractDrivetrain.DriveDirection;
import org.frogforce503.FRCSIM.AbstractIntake;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class GetBallProgram extends AbstractProgram{
    private AbstractDrivetrain drivetrain;
    private Robot robot;
    private AbstractIntake intake;
    private Position defense;
    private Ball target;
    private String name;
    
    public GetBallProgram(){
        target = null;
        name = "Get Nearest Ball Program";
    }
    
    public GetBallProgram(Ball target){
        this.target = target;
        name = "Get Ball Program, Ball #" + target.number;
    }
    
    @Override
    public void update() {
        if(robot.hasBall()){
            intake.retract();
            return;
        }
        intake.extend();
        robot.setWantsBall(true);
        Ball localTarget = null;
        boolean owned = false;
        if(target==null){
            float minDist = Float.MAX_VALUE;
            for(Ball ball : Ball.balls){
                float distance = ball.quickDistanceTo(robot);
                if(ball.alliance == robot.alliance && !(ball.owner instanceof Robot) && distance < minDist){
                    localTarget = ball;
                    minDist = distance;
                    owned = false;
                }
            }
            if(localTarget == null || localTarget.getPosition() == null){
                for(Ball ball : Ball.balls){
                    float distance = ball.quickDistanceTo(robot);
                    if(ball.alliance == robot.alliance && !ball.hasBeenOwnedBy(robot) && distance < minDist){
                        localTarget = ball;
                        owned = true;
                        minDist = distance;
                    }
                }      
            }
        } else {
            localTarget = target;
            owned = target.isOwned() && target.owner instanceof Robot;
        }
        if(localTarget != null && localTarget.getPosition() != null){
            if(!owned){
                drivetrain.driveToPoint(localTarget.getPosition(), DriveDirection.Towards);
            } else {
                if(((Robot) localTarget.owner).isTouchingWall()){
                    drivetrain.driveToPoint(Vector3f.ZERO, DriveDirection.DontCare);
                } else {
                    drivetrain.driveToPoint(localTarget.getPosition().interpolate(robot.getPosition(), robot.getPosition().subtract(localTarget.getPosition()).length() > 2? 0.6f : 1.5f), DriveDirection.Towards);
                }                
            }
        } else {
            drivetrain.driveToPoint(defense.getPosition(), DriveDirection.DontCare);         
        }
    }

    @Override
    public boolean isFinished() {
        return robot.hasBall();
    }

    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
        this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        this.robot = robot; 
        defense = new InterferencePosition(robot);
    }

    @Override
    public String getName() {
        return name;
    }
    
}

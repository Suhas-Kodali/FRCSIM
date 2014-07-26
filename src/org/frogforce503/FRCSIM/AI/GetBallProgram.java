package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AI.GoToProgram.Check;
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

    @Override
    public void update() {
        if(robot.hasBall()){
            intake.retract();
            return;
        }
        intake.extend();
        robot.setWantsBall(true);
        Ball target = null;
        float minDist = Float.MAX_VALUE;
        for(Ball ball : Ball.balls){
            float distance = ball.quickDistanceTo(robot);
            if(ball.alliance == robot.alliance && !(ball.owner instanceof Robot) && distance < minDist){
                target = ball;
                minDist = distance;
            }
        }
        if(target != null && target.getPosition() != null){
            drivetrain.driveToPoint(target.getPosition(), DriveDirection.Towards);
        } else {
            for(Ball ball : Ball.balls){
                float distance = ball.quickDistanceTo(robot);
                if(ball.alliance == robot.alliance && !ball.hasBeenOwnedBy(robot) && distance < minDist){
                    target = ball;
                    minDist = distance;
                }
            }      
            if(target != null && target.getPosition() != null){
                if(((Robot) target.owner).isTouchingWall()){
                    drivetrain.driveToPoint(Vector3f.ZERO, DriveDirection.DontCare);
                } else {
                    drivetrain.driveToPoint(target.getPosition().interpolate(robot.getPosition(), robot.getPosition().subtract(target.getPosition()).length() > 2? 0.6f : 1.5f), DriveDirection.Towards);
                }
            } else {   
                drivetrain.driveToPoint(defense.getPosition(), DriveDirection.DontCare);
            }
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
    
}

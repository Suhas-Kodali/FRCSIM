package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractIntake;
import org.frogforce503.FRCSIM.AbstractShooter;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Robot;
import org.frogforce503.FRCSIM.Robot.RobotPosition;

/**
 *
 * @author Bryce
 */
public class GetNearestAssistProgram extends AbstractProgram{
    private AbstractDrivetrain drivetrain;
    private Robot robot;
    private AbstractIntake intake;
    private AbstractShooter shooter;
    @Override
    public void update() {
        if(robot.hasBall()){
            Robot target = null;
            float minDist = Float.MAX_VALUE;
            for(Robot other : Robot.robots.get(robot.alliance)){
                float distance = other.quickDistanceTo(robot);
                if(!intake.getHeldBall().hasBeenOwnedBy(other) && distance < minDist && !other.hasBall()){
                    minDist = distance;
                    target = other;
                }
            }
            if(target != null){
                if(drivetrain.driveToPointAndDirection(target.getPosition(), target.getPosition(), Vector3f.UNIT_XYZ.mult(2), 15) && !target.hasBall()){
                    shooter.spit.run();
                }
            } else {
                for(Robot other : Robot.robots.get(robot.alliance)){
                    float distance = other.quickDistanceTo(robot);
                    if(!intake.getHeldBall().hasBeenOwnedBy(other) && distance < minDist){
                        minDist = distance;
                        target = other;
                    }
                }
                if(target != null){
                    if(drivetrain.driveToPointAndDirection(target.getPosition(), target.getPosition(), Vector3f.UNIT_XYZ.mult(2), 15) && !target.hasBall()){
                        shooter.spit.run();
                    }
                }
            }
        }
    }

    @Override
    public boolean isFinished() {
        return !robot.hasBall() || !intake.getHeldBall().anyAssistsLeft();
    }

    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
        this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        this.shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
        this.robot = robot;
    }
    
}

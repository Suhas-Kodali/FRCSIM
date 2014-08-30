package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractIntake;
import org.frogforce503.FRCSIM.AbstractShooter;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class GetAssistProgram extends AbstractProgram{
    private AbstractDrivetrain drivetrain;
    private Robot robot;
    private AbstractIntake intake;
    private AbstractShooter shooter;
    private final Robot target;
    private static int baseID = AbstractProgram.getProgramNum();
    private int uid = -baseID;
    
    public GetAssistProgram(){
        target = null;
    }
    
    public GetAssistProgram(final Robot target){
        this.target = target;
    }
    
    @Override
    public void update() {
        if(robot.hasBall()){
            Robot localTarget = target;
            if(localTarget == null){
                float minDist = Float.MAX_VALUE;
                for(Robot other : Robot.robots.get(robot.alliance)){
                    final float distance = other.getPosition().distance(robot.getPosition());
                    if(!intake.getHeldBall().hasBeenOwnedBy(other) && distance < minDist && !other.hasBall()){
                        minDist = distance;
                        localTarget = other;
                    }
                }        
            }
            if(localTarget != null){
                if(drivetrain.driveToPointAndDirection(localTarget.getPosition(), localTarget.getPosition(), Vector3f.UNIT_XYZ.mult(2), 30) && !localTarget.hasBall()){
                    shooter.spit.run();
                }
            }
        }
    }

    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
        this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        this.shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
        this.robot = robot;
        uid = baseID  + robot.number * AbstractProgram.getMaxProgramNum() + (target == null? 0 : target.number) * AbstractProgram.getMaxProgramNum() * Robot.getMaxRobotNum();
        drivetrain.setOnDefense(false);
    }
    
    @Override 
    public int getUID(){
        return uid;
    }
    
    @Override
    public String getHRName(){
        return "Get Assist!";
    }    
}

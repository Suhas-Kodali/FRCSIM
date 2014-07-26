package org.frogforce503.FRCSIM.AI;

import com.jme3.bullet.control.VehicleControl;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractDrivetrain.DriveDirection;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;
import org.frogforce503.FRCSIM.Zone;

/**
 *
 * @author Bryce
 */
public class GoToProgram extends AbstractProgram{
    AbstractDrivetrain drivetrain;
    Position target;
    float range;
    private final Check checkRunnable;
    
    public GoToProgram(Position target, float range, Check checkRunnable){
        this.target = target;
        this.range = range;
        this.checkRunnable = checkRunnable;
    }
    
    @Override
    public void update() {
        if(target.getPosition() != null){
            drivetrain.driveToPoint(target.getPosition(), DriveDirection.DontCare);
        }
    }

    @Override
    public boolean isFinished() {
        return target.getPosition() == null || target.getPosition().subtract(drivetrain.getPosition()).length() < range || checkRunnable.shouldExit();
    }

    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
    }
    
    public static abstract class Check{
        public abstract boolean shouldExit();
    }
}

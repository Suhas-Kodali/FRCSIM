package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Zone;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class GoToZoneProgram extends AbstractProgram{
    AbstractDrivetrain drivetrain;
    Zone target;
    float range;
    
    public GoToZoneProgram(Zone target, float range){
        this.target = target;
        this.range = range;
    }
    
    @Override
    public void update() {
        drivetrain.driveTowardsPoint(target.getCenter());
    }

    @Override
    public boolean isFinished() {
        return target.getCenter().subtract(drivetrain.getPosition()).length() < range;
    }

    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
    }
    
}

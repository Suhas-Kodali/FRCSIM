package org.frogforce503.FRCSIM.AI;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class DefenseProgram extends AbstractProgram{
    AbstractDrivetrain drivetrain;
    private Position target;
    private String name;
    
    public DefenseProgram(){
        name = "Defense Program";
    }
    
    @Override
    public void update() {
        if(target.getPosition() != null){
            drivetrain.driveToPoint(target.getPosition(), AbstractDrivetrain.DriveDirection.DontCare);
        }
    }

    @Override
    public boolean isFinished() {
        return target.getPosition() == null;
    }

    @Override
    public void registerOtherSubsystems(EnumMap<AbstractSubsystem.SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(AbstractSubsystem.SubsystemType.Drivetrain);
        target = new InterferencePosition(robot);
    }

    @Override
    public String getName() {
        return name;
    }
    
    
}

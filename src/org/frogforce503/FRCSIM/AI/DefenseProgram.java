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
    private static int baseID = AbstractProgram.getProgramNum();
    private int uid = -baseID;
    
    @Override
    public void update() {
        if(target.getPosition() != null){
            drivetrain.driveToPoint(target.getPosition(), AbstractDrivetrain.DriveDirection.DontCare);
        }
    }

    @Override
    public void registerOtherSubsystems(final EnumMap<AbstractSubsystem.SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(AbstractSubsystem.SubsystemType.Drivetrain);
        drivetrain.setOnDefense(true);
        target = new InterferencePosition(robot);
        uid = baseID + robot.number * AbstractProgram.getMaxProgramNum();
    }

    @Override
    public int getUID(){
        return uid;
    }
    
    @Override
    public String getHRName() {
        return "Play Defense!";
    }
    
    
}

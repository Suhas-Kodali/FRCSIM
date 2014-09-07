package org.frogforce503.FRCSIM.AI;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 * Program that plays [illegal] defense.
 * @author Bryce Paputa
 */
public class DefenseProgram extends AbstractProgram{
    private AbstractDrivetrain drivetrain;
    private Position target;
    private static int baseID = AbstractProgram.getProgramBaseID();
    private int uid = -baseID;
    
    /**
     * {@inheritDoc} 
     * Drives the drivetrain to a position that should interfere with offenseive action.
     */
    @Override
    public void update() {
        if(target.getPosition() != null){
            drivetrain.driveToPoint(target.getPosition(), AbstractDrivetrain.DriveDirection.DontCare);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOtherSubsystems(final EnumMap<AbstractSubsystem.SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(AbstractSubsystem.SubsystemType.Drivetrain);
        drivetrain.setOnDefense(true);
        target = new InterferencePosition(robot);
        uid = baseID + robot.number * AbstractProgram.getMaxProgramBaseID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUID(){
        return uid;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getHRName() {
        return "Play Defense!";
    }
}

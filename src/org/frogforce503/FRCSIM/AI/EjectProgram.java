package org.frogforce503.FRCSIM.AI;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractIntake;
import org.frogforce503.FRCSIM.AbstractShooter;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class EjectProgram extends AbstractProgram{
    private AbstractShooter shooter;
    private AbstractIntake intake;
    private static int baseID = AbstractProgram.getProgramNum();
    private int uid = -baseID;
    
    @Override
    public int getUID() {
        return uid;
    }
    
    @Override
    public String getHRName() {
        return "Eject The Ball";
    }

    @Override
    public void update() {
        if(intake.hasBall()){
            shooter.spit.run();
        }
    }

    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
        intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        uid = baseID + AbstractProgram.getMaxProgramNum() * robot.number;
    }
    
}

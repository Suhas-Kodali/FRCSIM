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

    @Override
    public String getName() {
        return "Eject Ball Program";
    }

    @Override
    public void update() {
        shooter.shoot.run();
    }

    @Override
    public boolean isFinished() {
        return !intake.hasBall();
    }

    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
        intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
    }
    
}

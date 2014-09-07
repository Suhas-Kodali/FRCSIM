package org.frogforce503.FRCSIM.AI;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractIntake;
import org.frogforce503.FRCSIM.AbstractShooter;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 * Program that ejects the robot's ball.
 * @author Bryce Paputa
 */
public class EjectProgram extends AbstractProgram{
    private AbstractShooter shooter;
    private AbstractIntake intake;
    private static int baseID = AbstractProgram.getProgramBaseID();
    private int uid = -baseID;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getUID() {
        return uid;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getHRName() {
        return "Eject The Ball";
    }

    /**
     * {@inheritDoc}
     * Ejects the ball.
     */
    @Override
    public void update() {
        if(intake.hasBall()){
            shooter.spit.run();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
        intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        uid = baseID + AbstractProgram.getMaxProgramBaseID() * robot.number;
    }
}

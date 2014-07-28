package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractIntake;
import org.frogforce503.FRCSIM.AbstractShooter;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Main;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
class ScoreBallProgram extends AbstractProgram {
    private AbstractShooter shooter;
    private AbstractIntake intake;
    private AbstractDrivetrain drivetrain;
    private Robot robot;

    @Override
    public void update() {
        if(drivetrain.driveToPointAndDirection(Vector3f.UNIT_X.mult(-robot.alliance.side * Main.in(9*12)), Vector3f.UNIT_X.mult(-robot.alliance.side * Main.in(27*12)), new Vector3f(1.75f, 200, 200), 25)){
            shooter.shoot.run();
        }
    }

    @Override
    public boolean isFinished() {
        return !robot.hasBall();
    }


    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
        this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        this.shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
        this.robot = robot;
    }

    @Override
    public String getName() {
        return "Score Ball Program";
    }
    
}

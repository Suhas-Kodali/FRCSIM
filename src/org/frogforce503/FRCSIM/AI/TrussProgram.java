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
class TrussProgram extends AbstractProgram {
    private AbstractShooter shooter;
    private AbstractDrivetrain drivetrain;
    private Robot robot;
    private static int baseID = AbstractProgram.getProgramNum();
    private int uid = -baseID;

    @Override
    public void update() {
        if(drivetrain.driveToPointAndDirection(Vector3f.UNIT_X.mult(robot.alliance.side * Main.in(9*12)), Vector3f.UNIT_X.mult(-robot.alliance.side * Main.in(27*12)), new Vector3f(1.75f, 200, 200), 25)){
            shooter.shoot.run();
        }
    }

    @Override
    public void registerOtherSubsystems(final EnumMap<AbstractSubsystem.SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(AbstractSubsystem.SubsystemType.Drivetrain);
        this.shooter = (AbstractShooter) subsystems.get(AbstractSubsystem.SubsystemType.Shooter);
        this.robot = robot;
        this.uid = baseID + robot.number * AbstractProgram.getMaxProgramNum();
        drivetrain.setOnDefense(false);
    }

    @Override
    public int getUID() {
        return uid;
    }

    @Override
    public String getHRName() {
        return "Truss The Ball!";
    }
    
}

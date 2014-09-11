package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Main;
import org.frogforce503.FRCSIM.Robot;

/**
 * Program for testing ai drivetrain functions.
 * @author Bryce Paputa
 */
public class TestProgram extends AbstractProgram{
    private AbstractDrivetrain drivetrain;
    private int baseID = AbstractProgram.getProgramBaseID();
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
        return "Test Program";
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void update() {
        if(Main.InputManager.isPressed("g")){
            drivetrain.setOnDefense(true);
            drivetrain.driveToPoint(new Vector3f(0, 0, 0), AbstractDrivetrain.DriveDirection.DontCare);
        } else if(Main.InputManager.isPressed("f")){
            drivetrain.setOnDefense(false);
            drivetrain.driveToPoint(new Vector3f(0, 0, 0), AbstractDrivetrain.DriveDirection.DontCare);
        } else {
            drivetrain.stop();
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(AbstractSubsystem.SubsystemType.Drivetrain);
        uid = baseID + robot.number * AbstractProgram.getMaxProgramBaseID();
    }    
}

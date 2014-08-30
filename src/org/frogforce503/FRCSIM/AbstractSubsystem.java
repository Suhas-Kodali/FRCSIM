package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;
import java.util.EnumMap;

/**
 *
 * @author Bryce
 */
public abstract class AbstractSubsystem {
    public abstract void update();
    public abstract SubsystemType getType();
    public abstract void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot);
    public abstract void registerPhysics(final Node rootNode, final PhysicsSpace space, final Alliance alliance);
    
    public static enum SubsystemType {
        Drivetrain(), Shooter(), Intake(), Controller(), GoaliePole(), Box();
    }
}

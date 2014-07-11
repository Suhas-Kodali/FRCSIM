package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;
import java.util.EnumMap;
import java.util.HashMap;

/**
 *
 * @author Bryce
 */
public abstract class AbstractSubsystem {
    public abstract void update();
    public abstract SubsystemType getType();
    public abstract void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot);
    public abstract void registerPhysics(Node rootNode, PhysicsSpace space, Alliance alliance);
    
    public static enum SubsystemType {
        Drivetrain(), Shooter(), Intake(), Controller(), GoaliePole();
    }
}

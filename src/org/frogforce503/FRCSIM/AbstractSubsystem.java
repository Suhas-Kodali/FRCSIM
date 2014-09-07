package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;
import java.util.EnumMap;

/**
 * Abstract class that represents a subsystem of a robot.
 * @author Bryce Paputa
 */
public abstract class AbstractSubsystem implements DTSDebuggable{
    /**
     * Runs the standard loop based functions of the subsystem.
     */
    public abstract void update();
    
    /**
     * Returns the type of this subsystem, used as a key in HashMaps.
     * @return Type of the subsystem
     */
    public abstract SubsystemType getType();
    
    /**
     * Registers the other subsystems with this one to allow for intersubsystem interaction.
     * @param subsystems    Other subsystems
     * @param robot         Parent robot
     */
    public abstract void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot);
    
    /**
     * Adds this subsystem to the robot and to the physics space.
     * @param rootNode  Root of the chassis to add stuff to
     * @param space     Physics space for physical objects
     */
    public abstract void registerPhysics(final Node rootNode, final PhysicsSpace space);
    
    /**
     * Enum that represents the purpose of a subsystem. Often used as a key in a HashMap.
     */
    public static enum SubsystemType {
        /**
         * Drivetrain subsystems
         */
        Drivetrain(), 
        
        /**
         * Shooter subsystems
         */
        Shooter(), 
        
        /**
         * Intake subsystems
         */
        Intake(), 
        
        /**
         * Control systems
         */
        Controller(), 
        
        /**
         * Box subsystems
         */
        Box();
    }
}

package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;

/**
 * Abstract class representing control systems.
 * @author Bryce Paputa
 */
public abstract class AbstractControl extends AbstractSubsystem{
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SubsystemType getType(){
        return SubsystemType.Controller;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPhysics(final Node rootNode, final PhysicsSpace space){}
}

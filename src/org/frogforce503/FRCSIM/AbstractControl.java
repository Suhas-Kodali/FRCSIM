package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;

/**
 *
 * @author Bryce
 */
public abstract class AbstractControl extends AbstractSubsystem{
    protected Alliance alliance;
    public SubsystemType getType(){
        return SubsystemType.Controller;
    }
    
    public void registerPhysics(Node rootNode, PhysicsSpace space, Alliance alliance){
        this.alliance = alliance;
    }
}

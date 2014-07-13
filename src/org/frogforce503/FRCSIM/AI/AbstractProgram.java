package org.frogforce503.FRCSIM.AI;

import org.frogforce503.FRCSIM.AbstractControl;

/**
 *
 * @author Bryce
 */
public abstract class AbstractProgram extends AbstractControl{
    protected AIControl owner;
    
    public void giveOwner(AIControl owner){
        this.owner = owner;
    }
    
    public abstract void update();
    
    public abstract boolean isFinished();
}

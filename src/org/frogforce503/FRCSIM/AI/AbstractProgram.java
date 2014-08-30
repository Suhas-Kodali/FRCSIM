package org.frogforce503.FRCSIM.AI;

import org.frogforce503.FRCSIM.AbstractControl;

/**
 *
 * @author Bryce
 */
public abstract class AbstractProgram extends AbstractControl{
    
    public abstract String getName();
    public abstract void update();
    
    public abstract boolean isFinished();
}

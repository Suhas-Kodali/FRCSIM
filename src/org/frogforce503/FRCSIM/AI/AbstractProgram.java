package org.frogforce503.FRCSIM.AI;

import org.frogforce503.FRCSIM.AbstractControl;

/**
 *
 * @author Bryce
 */
public abstract class AbstractProgram extends AbstractControl{
    
    public abstract int getUID();
    
    public abstract void update();

    public abstract String getHRName();
    
    private static int progCount = 0;
    public static int getProgramNum(){
        System.out.print(progCount);
        return progCount++;
    }
    
    public static int getMaxProgramNum(){
        return 10;//8
    }
}

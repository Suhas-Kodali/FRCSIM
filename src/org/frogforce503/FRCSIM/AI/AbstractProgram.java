package org.frogforce503.FRCSIM.AI;

import org.frogforce503.FRCSIM.AbstractControl;

/**
 * Abstract class that represents a program that is executed on a robot.
 * @author Bryce Paputa
 */
public abstract class AbstractProgram extends AbstractControl{
    private static int progCount = 0;
    
    /**
     * Unique ID number that differentiates programs. 
     * Iff two programs are to be executed on the same robot and would result in 
     * the same action, they should have the same uid. This must return the 
     * negated base id number until {@code registerOtherSubsystems} is called.
     * @return Unique ID number
     */
    public abstract int getUID();

    /**
     * Gets a human readable name for this program.
     * @return Human readbale name
     */
    public abstract String getHRName();
    
    /**
     * {@inheritDoc}
     */
    public String detailedToString(String offset){
        return offset + "Program("+getUID()+", \"" + getHRName() + "\")";
    }
    
    /**
     * Gets a new base ID number for a program type.
     * @return New base ID number
     */
    public static int getProgramBaseID(){
        return progCount++;
    }
    
    /**
     * Gets the maximum base ID number for use as a radix in a uid.
     * @return A number equal to or higher than the highest base ID number
     */
    public static int getMaxProgramBaseID(){
        return 10;//8
    }
}

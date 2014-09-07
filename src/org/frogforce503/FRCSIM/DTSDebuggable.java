package org.frogforce503.FRCSIM;

/**
 * Interface for a simple debugging serialization method.
 * @author Bryce Paputa
 */
public interface DTSDebuggable {
    /**
     * Compile all of the information stored in this object into string, each line will be started with whatever is given in the offset parameter.
     * @param offset    What to start each line with
     * @return          String representation of the object
     */
    public String detailedToString(String offset);
}

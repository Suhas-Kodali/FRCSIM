package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;

/**
 * Interface that allows an object to have a defined position.
 * @author Bryce Paputa
 */
public interface Position {
    /**
     * Gets this object's current position.
     * @return Current position
     */
    public abstract Vector3f getPosition();
}

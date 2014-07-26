package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;

/**
 *
 * @author Bryce
 */
public abstract class Position implements PositionBase{
    public abstract Vector3f getPosition();
    public float distanceTo(Position other){
        return getPosition().subtract(other.getPosition()).length();
    }
    public float quickDistanceTo(Position other){
        return getPosition().subtract(other.getPosition()).lengthSquared();
    }
}

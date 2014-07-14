package org.frogforce503.FRCSIM;

import org.frogforce503.FRCSIM.AI.Position;
import com.jme3.math.Vector3f;

/**
 *
 * @author Bryce
 */
public enum Zone implements Position{
    Red, White, Blue;

    public static Zone getZone(Vector3f point) {
        if (Math.abs(point.x) <= Main.in(9 * 12)) {
            return White;
        }
        if (point.x > Main.in(9 * 12)) {
            return Red;
        }
        if (point.x < Main.in(-9 * 12)) {
            return Blue;
        }
        throw new Error();
    }

    public Vector3f getPosition() {
        switch (this) {
            case Red:
                return new Vector3f(Main.in(18*12), 0, 0);
            case White:
                return Vector3f.ZERO;
            case Blue:
                return new Vector3f(Main.in(-18*12), 0, 0);
            default:
                return Vector3f.NAN;
        }
    }
    
}

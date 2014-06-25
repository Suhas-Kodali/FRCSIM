package org.frogforce503.FRCSIM;

import com.jme3.material.Material;

/**
 *
 * @author Bryce Paputa
 */
public enum Alliance {
    BLUE(Main.blue), RED(Main.red);
    public final Material material;

    Alliance(Material color) {
        this.material = color;
    }
}

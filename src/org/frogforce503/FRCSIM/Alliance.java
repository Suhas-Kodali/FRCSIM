package org.frogforce503.FRCSIM;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;

/**
 *
 * @author Bryce Paputa
 */
public enum Alliance {
    
    
    BLUE(Main.blue), RED(Main.red);
    public final Material material;
    public Vector3f[] position = new Vector3f[3];
    private float zOff = Main.in(120), xOff = Main.in(60); 
    Alliance(Material color) {
        this.material = color;
        for(int i = 0; i < 3; i++){
            if(material == Main.red){
                position[i] = new Vector3f(Main.field.length/2 - zOff, 0, (Main.field.width - xOff)/2 - (((Main.field.width - xOff)/(2f))*i));
            }else{
                position[i] = new Vector3f(-Main.field.length/2 + zOff, 0, (Main.field.width - xOff)/2 - (((Main.field.width - xOff)/(2f))*i));
                System.out.println(position[i]);
            }
        }
    }
}

package org.frogforce503.FRCSIM;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;

/**
 *
 * @author Bryce Paputa
 */
public enum Alliance {
    
    
    BLUE(Main.blue, new Vector3f(Field.length/2 - Main.in(81), Main.in(42), Field.width/2 + Main.in(28)), new Vector3f(-Field.length/2 + Main.in(216), Main.in(42), Field.width/2 + Main.in(28))), RED(Main.red, new Vector3f(-Field.length/2 + Main.in(81), Main.in(42), Field.width/2 + Main.in(28)), new Vector3f(Field.length/2 - Main.in(216), Main.in(42), Field.width/2 + Main.in(28)));
    public final Vector3f farHumanPlayer;
    public final Vector3f closeHumanPlayer;
    public final Material material;
    public Vector3f[] position = new Vector3f[3];
    private float zOff = Main.in(120), xOff = Main.in(60); 
    
    Alliance(Material color, Vector3f farHumanPlayer, Vector3f closeHumanPlayer) {
        this.material = color;
        this.farHumanPlayer = farHumanPlayer;
        this.closeHumanPlayer = closeHumanPlayer;
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

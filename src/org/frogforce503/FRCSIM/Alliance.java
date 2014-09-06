package org.frogforce503.FRCSIM;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;

/**
 *
 * @author Bryce Paputa
 */
public enum Alliance {
    BLUE(Main.blue, new Vector3f(Field.length/2 - Main.in(81), Main.in(72), Field.width/2 + Main.in(28)), new Vector3f(-Field.length/2 + Main.in(216), Main.in(72), Field.width/2 + Main.in(28))), RED(Main.red, new Vector3f(-Field.length/2 + Main.in(81), Main.in(72), Field.width/2 + Main.in(28)), new Vector3f(Field.length/2 - Main.in(216), Main.in(72), Field.width/2 + Main.in(28)));
    public final Material playermaterial;
    public final Vector3f farHumanPlayer;
    public final Vector3f closeHumanPlayer;
    public final Material material;
    public final int side;
    private int score = 0;
    Alliance(final Material color, final Vector3f farHumanPlayer, final Vector3f closeHumanPlayer) {
        this.material = color;
        if(material == Main.red){
            playermaterial = Main.orange;
            side = -1;
        } else {
            playermaterial = Main.cyan;
            side = +1;
        }
        this.farHumanPlayer = farHumanPlayer.mult(side);
        this.closeHumanPlayer = closeHumanPlayer.mult(side);
    }
    
    public void incrementScore(final int amount){
        score += amount;
        Main.scene.updateScore();
    }
    
    public int getScore(){
        return score;
    }
    
    @Override
    public String toString(){
        return this == Alliance.RED? "red" : (this == Alliance.BLUE? "blue" : "null");
    }
    
    public static Alliance fromString(String str){
        if(str.equals("red")){
            return Alliance.RED;
        }
        if(str.equals("blue")){
            return Alliance.BLUE;
        }
        return null;
    }
}

package org.frogforce503.FRCSIM;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import static org.frogforce503.FRCSIM.Main.in;

/**
 * Enum that represents the different alliances.
 * @author Bryce Paputa
 */
public enum Alliance {
    /**
     * Blue alliance.
     */
    Blue(Main.blue, new Vector3f(Field.length/2 - in(81), in(72), Field.width/2 + in(28)), new Vector3f(-Field.length/2 + in(216), in(72), Field.width/2 + in(28))), 
    
    /**
     * Red alliance.
     */
    Red(Main.red, new Vector3f(-Field.length/2 + in(81), in(72), Field.width/2 + in(28)), new Vector3f(Field.length/2 - in(216), in(72), Field.width/2 + in(28)));
    
    /**
     * Material for player bumpers.
     */
    public final Material playermaterial;
    
    /**
     * Material for AI bumpers.
     */
    public final Material material;
    
    /**
     * Location of a far human player.
     */
    public final Vector3f farHumanPlayer;
    
    /**
     * Location of a close human player
     */
    public final Vector3f closeHumanPlayer;
    
    /**
     * Either -1 or 1, represents the side of the field that has this alliance's goals.
     */
    public final int side;
    
    private int score = 0;
    
    /**
     * Constructor for making alliances.
     * @param color             Color of bumpers
     * @param farHumanPlayer    Far human player location
     * @param closeHumanPlayer  Close human player location
     */
    private Alliance(final Material color, final Vector3f farHumanPlayer, final Vector3f closeHumanPlayer) {
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
    
    /**
     * Increase the score by a given ammount.
     * @param amount Ammount to increase score by
     */
    public void incrementScore(final int amount){
        score += amount;
        Main.scene.updateScore();
    }
    
    /**
     * Gets this alliance's score.
     * @return Score of the alliance
     */
    public int getScore(){
        return score;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString(){
        return this == Alliance.Red? "red" : (this == Alliance.Blue? "blue" : "null");
    }
    
    public Alliance invert(){
        return (this == Red? Blue : Red);
    }
}

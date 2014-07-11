package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;

/**
 *
 * @author Bryce
 */
public class CloseHumanPlayer extends FarHumanPlayer{
    public CloseHumanPlayer(Alliance alliance, HumanPlayerPosition humanPlayerPosition){
        super(alliance, humanPlayerPosition);
    }
    public void switchPosition(){
        holdingPosition = holdingPosition.mult(new Vector3f(1,1,-1));
    }
}

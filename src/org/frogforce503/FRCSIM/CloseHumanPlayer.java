package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;

/**
 *
 * @author Bryce
 */
public class CloseHumanPlayer extends HumanPlayer{
    public CloseHumanPlayer(Alliance alliance, HumanPlayerPosition humanPlayerPosition){
        super(alliance, humanPlayerPosition);
    }
    public void switchPosition(){
        holdingPosition = holdingPosition.mult(new Vector3f(1,1,-1));
    }
    public void sendBallToPedestal(){
        currentBall.setVelocity(currentBall.getVelocity().add(new Vector3f(15,0,0).mult(alliance.side)));
    }
    public void moveBallToOtherSide(){
        isBallHeld = false;
        switchPosition();
        sendBallToPedestal();
    }
}

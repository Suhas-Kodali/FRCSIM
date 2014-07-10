/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author skodali
 */
public class HumanPlayer {
    public Vector3f close;
    public Vector3f far;
    public Alliance alliance;
    public HumanPlayer(Alliance alliance){
        close = alliance.closeHumanPlayer;
        far = alliance.farHumanPlayer;
        this.alliance = alliance;
    }
    private void inbound(){
        for(int j = Main.field.outOfBoundsBalls.size()-1; j >=0; j--){
                    RigidBodyControl ball = Main.field.outOfBoundsBalls.get(j).getRigidBodyControl();
                    if(Main.field.outOfBoundsBalls.get(j).alliance == alliance){
                        Main.field.isInbounding = true;
                        Main.field.outOfBoundsBalls.remove(j);
                        ball.setLinearVelocity(new Vector3f(Vector3f.ZERO.subtract(ball.getPhysicsLocation())));
                        (new Timer()).schedule(new TimerTask(){public void run(){Main.field.isInbounding = false;}}, 1000);
                    }
        }
        }
    public Runnable action = new Runnable(){
        public void run(){
            inbound();
        }
    };
}

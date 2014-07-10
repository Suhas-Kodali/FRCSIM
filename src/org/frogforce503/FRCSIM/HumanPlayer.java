/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import java.util.Timer;
import java.util.TimerTask;
import org.frogforce503.FRCSIM.Robot.RobotPosition;

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
        float distance = 500;
        Vector3f vehiclePosition = new Vector3f(0, 0, 0);
        for(RobotPosition robotPosition : Robot.getRobotPositions()){
            if(robotPosition.getPosition().distance(Main.field.getOutOfBoundsBall(alliance).getRigidBodyControl().getPhysicsLocation()) < distance){
                distance = robotPosition.getPosition().distance(Main.field.getOutOfBoundsBall(alliance).getRigidBodyControl().getPhysicsLocation());
                vehiclePosition = robotPosition.getPosition();
            }
        }
        for(int j = Main.field.outOfBoundsBalls.size()-1; j >=0; j--){
                    RigidBodyControl ball = Main.field.outOfBoundsBalls.get(j).getRigidBodyControl();
                    if(Main.field.outOfBoundsBalls.get(j).alliance == alliance){
                        Main.field.isInbounding = true;
                        Main.field.outOfBoundsBalls.remove(j);
                        ball.setLinearVelocity(new Vector3f(vehiclePosition.subtract(ball.getPhysicsLocation())));
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

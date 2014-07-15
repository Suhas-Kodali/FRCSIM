package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;
import org.frogforce503.FRCSIM.Robot.RobotPosition;

/**
 *
 * @author Bryce
 */
public class HumanPlayer {
    private static final EnumMap<Alliance, EnumMap<HumanPlayerPosition, HumanPlayer>> humanPlayers;
    private static Random rand = new Random();
    static {
        humanPlayers = new EnumMap<Alliance, EnumMap<HumanPlayerPosition, HumanPlayer>>(Alliance.class);
        humanPlayers.put(Alliance.RED, new EnumMap<HumanPlayerPosition, HumanPlayer>(HumanPlayerPosition.class));
        humanPlayers.put(Alliance.BLUE, new EnumMap<HumanPlayerPosition, HumanPlayer>(HumanPlayerPosition.class));
        for(Alliance alliance : Alliance.values()){
            for(HumanPlayerPosition pos : HumanPlayerPosition.values()){
                humanPlayers.get(alliance).put(pos, (pos == HumanPlayerPosition.Close? new CloseHumanPlayer(alliance, pos): new HumanPlayer(alliance, pos)));
            }
        }
    }        
    
    public HumanPlayer(Alliance alliance, HumanPlayerPosition pos){
        humanPlayers.get(alliance).put(pos, this);
        this.alliance = alliance;
        this.holdingPosition = pos.holdingPosition.mult(alliance.side);
    }
    
    protected void giveBall(Ball ball){
        ball.capture(this);
        System.out.println("3");
        System.out.println(currentBall);
        if(currentBall == null){
            currentBall = ball;
            System.out.println("2");
        } else {
        System.out.println("4");
            ballQueue.add(ball);
        }
    }
    
    private final Alliance alliance;
    private Ball currentBall = null;
    private ArrayList<Ball> ballQueue = new ArrayList<Ball>(3);
    protected Vector3f holdingPosition;
    
    public void requestAutoThrow(){
        isAutoThrowing = true;
    }
    public void requestManualThrow(){
        isAutoThrowing = false;
    }
    
    private boolean isAutoThrowing = true;
    private static float autoThrowRadius = 4;
    private static float throwForce = 10;
    private static float holdRange = .05f;
    private static float pullForce = 10;
    private boolean isBallHeld = false;
    public void update(){
        if(isAutoThrowing && currentBall != null && isBallHeld){
            for(RobotPosition pos : Robot.getRobotPositions()){
                if(pos.getAlliance() == alliance && pos.getPosition().subtract(currentBall.getPosition()).length() < autoThrowRadius){
                    doThrow(pos.getPosition());
                    break;
                }
            }
        }
        
        if(currentBall != null && !isBallHeld){
            if(currentBall.getRigidBodyControl().getPhysicsLocation().distance(holdingPosition) < 2){
                currentBall.setVelocity(holdingPosition.subtract(currentBall.getPosition()).mult(pullForce));
            }else{
                currentBall.getRigidBodyControl().applyCentralForce(holdingPosition.subtract(currentBall.getPosition()).mult(3));
            }
            if(holdingPosition.subtract(currentBall.getPosition()).length() < holdRange){
                isBallHeld = true;
            }
        }
        
        if(currentBall != null && isBallHeld){
            currentBall.setPosition(holdingPosition);
        }
        
        for(Ball ball : ballQueue){
            ball.setVelocity(holdingPosition.mult(1.2f).subtract(ball.getPosition()).mult(2));
        }
    }
    
    public static void updateAll(){
        for(EnumMap<HumanPlayerPosition, HumanPlayer> array : humanPlayers.values()){
            for(HumanPlayer player : array.values()){
                player.update();
            }
        }
    }
    
    public void doThrow(Vector3f target){
        target = target.add((rand.nextFloat()-.5f), (rand.nextFloat()-.5f), (rand.nextFloat()-.5f));
        if(currentBall != null){
            currentBall.setVelocity(target.subtract(currentBall.getPosition()).normalize().mult(throwForce));
            currentBall.release();
            currentBall = null;
            isBallHeld = false;
            if(ballQueue.isEmpty() == false){
                currentBall = ballQueue.remove(0);
            }
        }
    }
    
    public static void ballExitField(Ball ball, Vector3f exitPos){
        Alliance alliance = ball.alliance;
        if(ball.isScored()){
            ball.destroy();
        } else if(exitPos.x * alliance.side < 0){
            if(exitPos.z > 0){
                ((HumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.FarPosZ)).giveBall(ball);
                System.out.println("FarPosZ");
            } else {
                ((HumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.FarNegZ)).giveBall(ball);         
                System.out.println("FarNegZ");       
            }
        } else {
            ((HumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.Close)).giveBall(ball);
            System.out.println("Close");
        }
    }
    
    
    public static enum HumanPlayerPosition{
        Close(Alliance.RED.farHumanPlayer), FarPosZ(Alliance.RED.closeHumanPlayer), FarNegZ(Alliance.RED.closeHumanPlayer.mult(new Vector3f(1,1,-1)));
        public final Vector3f holdingPosition;
        private HumanPlayerPosition(Vector3f holdingPosition){
            this.holdingPosition = holdingPosition;
        }
    }
}

package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.Robot.RobotPosition;

/**
 *
 * @author Bryce
 */
public class FarHumanPlayer {
    private static final EnumMap<Alliance, EnumMap<HumanPlayerPosition, FarHumanPlayer>> humanPlayers;
    static {
        humanPlayers = new EnumMap<Alliance, EnumMap<HumanPlayerPosition, FarHumanPlayer>>(Alliance.class);
        humanPlayers.put(Alliance.RED, new EnumMap<HumanPlayerPosition, FarHumanPlayer>(HumanPlayerPosition.class));
        humanPlayers.put(Alliance.BLUE, new EnumMap<HumanPlayerPosition, FarHumanPlayer>(HumanPlayerPosition.class));
    }        
    
    public FarHumanPlayer(Alliance alliance, HumanPlayerPosition pos){
        humanPlayers.get(alliance).put(pos, this);
        this.alliance = alliance;
        this.holdingPosition = pos.holdingPosition.mult(alliance.side);
    }
    
    protected void giveBall(Ball ball){
        if(currentBall == null){
            currentBall = null;
        } else {
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
    private static float autoThrowRadius = 1;
    public void update(){
        if(isAutoThrowing && currentBall != null){
            for(RobotPosition pos : Robot.getRobotPositions()){
                if(pos.getAlliance() == alliance && pos.getPosition().subtract(currentBall.getPosition()).length() < autoThrowRadius){
                    doThrow(pos.getPosition());
                }
            }
        }
        if(currentBall != null){
            currentBall.setVelocity(holdingPosition.subtract(currentBall.getPosition()).mult(2));
        }
    }
    
    public static void updateAll(){
        for(EnumMap<HumanPlayerPosition, FarHumanPlayer> array : humanPlayers.values()){
            for(FarHumanPlayer player : array.values()){
                player.update();
            }
        }
    }
    
    public void doThrow(Vector3f target){
        if(currentBall != null){
            currentBall.setVelocity(target.subtract(currentBall.getPosition()).mult(10));
            currentBall = null;
            if(ballQueue.isEmpty() == false){
                currentBall = ballQueue.remove(0);
            }
        }
    }
    
    public static void ballExitField(Ball ball, Vector3f exitPos){
        Alliance alliance = ball.alliance;
        if(ball.isScored()){
            ball.destroy();
        }
        if(exitPos.x * alliance.side > 0){
            if(exitPos.z > 0){
                ((FarHumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.FarPosZ)).giveBall(ball);
            } else {
                ((FarHumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.FarNegZ)).giveBall(ball);                
            }
        } else {
            ((FarHumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.Close)).giveBall(ball);
        }
    }
    
    
    public static enum HumanPlayerPosition{
        Close(Vector3f.ZERO), FarPosZ(Vector3f.ZERO), FarNegZ(Vector3f.ZERO);
        public final Vector3f holdingPosition;
        private HumanPlayerPosition(Vector3f holdingPosition){
            this.holdingPosition = holdingPosition;
        }
    }
}

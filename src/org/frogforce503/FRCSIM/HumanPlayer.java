package org.frogforce503.FRCSIM;

import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map.Entry;
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
    private long lastThrow = System.nanoTime();
    public void update(){
        if(isAutoThrowing && currentBall != null && isBallHeld && System.nanoTime() - lastThrow > 1 * 1000 * 1000){
            Robot goodRobot = Robot.getClosestRobot(currentBall.getPosition(), alliance);
            if(goodRobot.getPosition().subtract(currentBall.getPosition()).length() < autoThrowRadius 
                    && Math.abs(goodRobot.getVelocity().dot(currentBall.getPosition().subtract(goodRobot.getPosition()).cross(Vector3f.UNIT_Y))) < 3 && !goodRobot.hasBall()){
                Robot badRobot = Robot.getClosestRobot(goodRobot.getPosition(), alliance == Alliance.RED? Alliance.BLUE : Alliance.RED);
                if(badRobot == null){
                    doThrow(goodRobot.getPosition());
                } else if(badRobot.isTall){
                    Plane goodRobotBallPlane = new Plane();
                    goodRobotBallPlane.setPlanePoints(goodRobot.getPosition(), currentBall.getPosition(), goodRobot.getPosition().add(currentBall.getPosition()).scaleAdd(.5f, Vector3f.UNIT_Y));
                    if(Math.abs(goodRobotBallPlane.pseudoDistance(badRobot.getPosition()))< .5f){
                        Plane goodRobotOrthoPlane = new Plane();
                        goodRobotOrthoPlane.setOriginNormal(goodRobot.getPosition(), goodRobot.getPosition().subtract(currentBall.getPosition()));
                        if(goodRobotOrthoPlane.pseudoDistance(badRobot.getPosition()) > 0){
                            doThrow(goodRobot.getPosition());
                        }
                    } else {
                        doThrow(goodRobot.getPosition());
                    }                    
                } else {
                    doThrow(goodRobot.getPosition());
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
            lastThrow = System.nanoTime();
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
    
    public static void requestThrowAt(Vector3f pos, Alliance alliance){
        float minDistance = Float.MAX_VALUE;
        HumanPlayer closest = null;
        for(HumanPlayer hp : humanPlayers.get(alliance).values()){
            if(hp.currentBall != null && hp.holdingPosition.distanceSquared(pos) < minDistance){
                minDistance = hp.holdingPosition.distanceSquared(pos);
                closest = hp;
            }
        }
        if(closest != null){
            closest.doThrow(pos);
        }
    }
    
    public static class ManualInboundRunnable implements Runnable{
        private Robot robot;
        public ManualInboundRunnable(Robot robot){
            this.robot = robot;
        }
        
        public void run() {
            requestThrowAt(robot.getPosition(), robot.alliance);
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

package org.frogforce503.FRCSIM;

import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.Ball.BallOwner;

/**
 *
 * @author Bryce
 */
public class HumanPlayer implements BallOwner, DTSDebuggable{
    private static final EnumMap<Alliance, EnumMap<HumanPlayerPosition, HumanPlayer>> humanPlayers;
    static {
        humanPlayers = new EnumMap<Alliance, EnumMap<HumanPlayerPosition, HumanPlayer>>(Alliance.class);
        humanPlayers.put(Alliance.RED, new EnumMap<HumanPlayerPosition, HumanPlayer>(HumanPlayerPosition.class));
        humanPlayers.put(Alliance.BLUE, new EnumMap<HumanPlayerPosition, HumanPlayer>(HumanPlayerPosition.class));
        for(final HumanPlayerPosition pos : HumanPlayerPosition.values()){
            humanPlayers.get(Alliance.RED).put(pos, (pos == HumanPlayerPosition.Close? new CloseHumanPlayer(Alliance.RED, pos): new HumanPlayer(Alliance.RED, pos)));
            humanPlayers.get(Alliance.BLUE).put(pos, (pos == HumanPlayerPosition.Close? new CloseHumanPlayer(Alliance.BLUE, pos): new HumanPlayer(Alliance.BLUE, pos)));
        }
    }        
    
    public HumanPlayer(final Alliance alliance, final HumanPlayerPosition pos){
        this.alliance = alliance;
        this.holdingPosition = pos.holdingPosition.mult(new Vector3f(alliance.side, -1, -1));
        this.name = "HumanPlayer("+alliance +", "+pos.name()+")";
        humanPlayers.get(alliance).put(pos, this);
    }
    
    protected void giveBall(final Ball ball){
        ball.capture(this);
        if(currentBall == null){
            currentBall = ball;
        } else {
            ballQueue.add(ball);
        }
    }
    
    public final Alliance alliance;
    protected Ball currentBall = null;
    protected final ArrayList<Ball> ballQueue = new ArrayList<Ball>(3);
    protected Vector3f holdingPosition;
    private final String name;
    
    private final static float autoThrowRadius = 4;
    private final static float throwForce = 5;
    private final static float pullForce = 8;
    private final static float maxThrowForce = 15;
    private final static float holdRange = .05f;
    private final static float holdForce = 10;
    protected boolean isBallHeld = false;
    private long lastThrow = System.nanoTime();
    public void update(){
        if(currentBall != null && isBallHeld && System.nanoTime() - lastThrow > 1 * 1000 * 1000 * 1000l){
            final Robot goodRobot = Robot.getClosestRobot(currentBall.getPosition(), alliance);
            if(goodRobot != null && goodRobot.getPosition().subtract(currentBall.getPosition()).length() < autoThrowRadius 
                    && Math.abs(goodRobot.getVelocity().dot(currentBall.getPosition().subtract(goodRobot.getPosition()).cross(Vector3f.UNIT_Y))) < 3 && !goodRobot.hasBall() && goodRobot.wantsBall()){
                final Robot badRobot = Robot.getClosestRobot(goodRobot.getPosition(), alliance == Alliance.RED? Alliance.BLUE : Alliance.RED);
                if(badRobot == null){
                    doThrow(goodRobot.getPosition());
                } else if(badRobot.isTall){
                    final Plane goodRobotBallPlane = new Plane();
                    goodRobotBallPlane.setPlanePoints(goodRobot.getPosition(), currentBall.getPosition(), goodRobot.getPosition().add(currentBall.getPosition()).scaleAdd(.5f, Vector3f.UNIT_Y));
                    if(Math.abs(goodRobotBallPlane.pseudoDistance(badRobot.getPosition()))< .5f){
                        final Plane goodRobotOrthoPlane = new Plane();
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
                currentBall.setVelocity(holdingPosition.subtract(currentBall.getPosition()).mult(holdForce));
            }else{
                currentBall.getRigidBodyControl().applyCentralForce(holdingPosition.subtract(currentBall.getPosition()).mult(pullForce));
            }
            if(holdingPosition.subtract(currentBall.getPosition()).length() < holdRange){
                isBallHeld = true;
            }
        }
        
        if(currentBall != null && isBallHeld){
            currentBall.setPosition(holdingPosition);
        }
        
        for(final Ball ball : ballQueue){
            ball.setVelocity(holdingPosition.mult(1.2f).subtract(ball.getPosition()).mult(holdForce));
        }
    }
    
    public static void updateAll(){
        for(final EnumMap<HumanPlayerPosition, HumanPlayer> array : humanPlayers.values()){
            for(final HumanPlayer player : array.values()){
                player.update();
            }
        }
    }
    
    public void doThrow(final Vector3f target){
        if(currentBall != null){
            final Vector3f force = target.subtract(currentBall.getPosition().add(Vector3f.UNIT_Y.mult(1.5f)));
            currentBall.setVelocity(force.mult(force.length()*throwForce > maxThrowForce? maxThrowForce/force.length() : throwForce));
            currentBall.release();
            currentBall = null;
            isBallHeld = false;
            if(ballQueue.isEmpty() == false){
                currentBall = ballQueue.remove(0);
            }
            lastThrow = System.nanoTime();
        }
    }
    
    public static void ballExitField(final Ball ball, final Vector3f exitPos){
        final Alliance alliance = ball.alliance;
        if(ball.isScored()){
            ball.reset();
        } else if(!ball.isOwned()){
            if(exitPos.x * alliance.side < 0){
                if(exitPos.z > 0){
                    ((HumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.FarPosZ)).giveBall(ball);
                } else {
                    ((HumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.FarNegZ)).giveBall(ball);  
                }
            } else {
                ((HumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.Close)).giveBall(ball);
                if(humanPlayers.get(alliance).get(HumanPlayerPosition.Close).holdingPosition.z * ball.getPosition().z < 0){
                    ((CloseHumanPlayer)humanPlayers.get(alliance).get(HumanPlayerPosition.Close)).switchPosition();
                }
            }
        }
    }
    
    public static void requestThrowAt(final Vector3f pos, final Alliance alliance){
        float minDistance = Float.MAX_VALUE;
        HumanPlayer closest = null;
        for(final HumanPlayer hp : humanPlayers.get(alliance).values()){
            if(hp.currentBall != null && hp.holdingPosition.distanceSquared(pos) < minDistance){
                minDistance = hp.holdingPosition.distanceSquared(pos);
                closest = hp;
            }
        }
        if(closest != null){
            closest.doThrow(pos);
        }
    }

    public void releaseBall() {
        currentBall = null;
        isBallHeld = false;
        if(ballQueue.isEmpty() == false){
            currentBall = ballQueue.remove(0);
        }
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    @Override
    public String detailedToString(String offset){
        StringBuilder temp = new StringBuilder();
        temp.append(offset).append(name).append("{");
        temp.append(offset).append("    currentBall: ").append(currentBall).append(",\n");
        temp.append(offset).append("    ballQueue: [ ");
        for(Ball ball : ballQueue){
            temp.append(offset).append("\n        ").append(ball).append(",");
        }
        temp.setLength(temp.length()-1);
        temp.append(offset).append("\n    holdingPosition: ").append(holdingPosition).append("\n");
        temp.append(offset).append("     isBallHeld: ").append(isBallHeld).append("\n");
        temp.append(offset).append("}");
        return temp.toString();
    }
    
    public static class ManualInboundRunnable implements Runnable{
        private Robot robot;
        public ManualInboundRunnable(final Robot robot){
            this.robot = robot;
        }
        
        public void run() {
            requestThrowAt(robot.getPosition(), robot.alliance);
        }
        
    }
    
    public static class SwitchSidesRunnable implements Runnable{
        private final Alliance alliance;
        public SwitchSidesRunnable(final Alliance alliance){
            this.alliance = alliance;
        }
        
        public void run(){
            if(((CloseHumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.Close)).currentBall != null){
                ((CloseHumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.Close)).moveBallToOtherSide();
            }
        }
    }
    
    public static enum HumanPlayerPosition{
        Close(Alliance.RED.farHumanPlayer, "Close"), FarPosZ(Alliance.RED.closeHumanPlayer, "Far(+Z)"), FarNegZ(Alliance.RED.closeHumanPlayer.mult(new Vector3f(1,1,-1)), "Far(-Z)");
        public final Vector3f holdingPosition;
        public final String name;
        private HumanPlayerPosition(final Vector3f holdingPosition, final String name){
            this.holdingPosition = holdingPosition;
            this.name = name;
        }
    }
    
    public static class CloseHumanPlayer extends HumanPlayer{
        public CloseHumanPlayer(final Alliance alliance, final HumanPlayerPosition humanPlayerPosition){
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
}

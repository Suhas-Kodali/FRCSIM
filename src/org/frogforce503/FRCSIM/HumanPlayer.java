package org.frogforce503.FRCSIM;

import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.Ball.BallOwner;

/**
 * Human player class.
 * @author Bryce Paputa
 */
public class HumanPlayer implements BallOwner, DTSDebuggable{
    private static final EnumMap<Alliance, EnumMap<HumanPlayerPosition, HumanPlayer>> humanPlayers;
    static {
        humanPlayers = new EnumMap<Alliance, EnumMap<HumanPlayerPosition, HumanPlayer>>(Alliance.class);
        humanPlayers.put(Alliance.Red, new EnumMap<HumanPlayerPosition, HumanPlayer>(HumanPlayerPosition.class));
        humanPlayers.put(Alliance.Blue, new EnumMap<HumanPlayerPosition, HumanPlayer>(HumanPlayerPosition.class));
        for(final HumanPlayerPosition pos : HumanPlayerPosition.values()){
            humanPlayers.get(Alliance.Red).put(pos, (pos == HumanPlayerPosition.Close? new CloseHumanPlayer(Alliance.Red, pos): new HumanPlayer(Alliance.Red, pos)));
            humanPlayers.get(Alliance.Blue).put(pos, (pos == HumanPlayerPosition.Close? new CloseHumanPlayer(Alliance.Blue, pos): new HumanPlayer(Alliance.Blue, pos)));
        }
    }     
    
    /**
     * Alliance of this human player.
     */
    public final Alliance alliance;
    
    /**
     * Next ball to be inbounded.
     */
    protected Ball currentBall = null;
    
    /**
     * Balls waiting in queue.
     */
    protected final ArrayList<Ball> ballQueue = new ArrayList<Ball>(3);
    
    /**
     * Position to hold balls in.
     */
    protected Vector3f holdingPosition;
    
    /**
     * Is the ball held in the final position?
     */
    protected boolean isBallHeld = false;
    
    private final String name;
    private final static float autoThrowRadius = 4;
    private final static float throwForce = 5;
    private final static float pullForce = 8;
    private final static float maxThrowForce = 15;
    private final static float holdRange = .05f;
    private final static float holdForce = 10;
    private long lastThrow = System.nanoTime();   
    
    /**
     * Human player constructor.
     * @param alliance  Alliance for this player
     * @param pos       Position this player should hold the ball in
     */
    public HumanPlayer(final Alliance alliance, final HumanPlayerPosition pos){
        this.alliance = alliance;
        this.holdingPosition = pos.holdingPosition.mult(new Vector3f(alliance.side, -1, -1));
        this.name = "HumanPlayer("+alliance +", "+pos.name()+")";
        humanPlayers.get(alliance).put(pos, this);
    }
    
    /**
     * Give this human player a ball.
     * @param ball Ball to give
     */
    protected void giveBall(final Ball ball){
        ball.capture(this);
        if(currentBall == null){
            currentBall = ball;
        } else {
            ballQueue.add(ball);
        }
    }
    
    /**
     * Update this human player. Runs auto throw code, updates ball states, and applys forces to balls.
     */
    public void update(){
        if(currentBall != null && isBallHeld && System.nanoTime() - lastThrow > 1 * 1000 * 1000 * 1000l){
            final Robot goodRobot = Robot.getClosestRobot(currentBall.getPosition(), alliance);
            if(goodRobot != null && goodRobot.getPosition().subtract(currentBall.getPosition()).length() < autoThrowRadius 
                    && Math.abs(goodRobot.getVelocity().dot(currentBall.getPosition().subtract(goodRobot.getPosition()).cross(Vector3f.UNIT_Y))) < 3 && !goodRobot.hasBall() && goodRobot.wantsBall()){
                final Robot badRobot = Robot.getClosestRobot(goodRobot.getPosition(), alliance.invert());
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
            if(currentBall.getPosition().distance(holdingPosition) < 2){
                currentBall.setVelocity(holdingPosition.subtract(currentBall.getPosition()).mult(holdForce));
            }else{
                currentBall.applyForce(holdingPosition.subtract(currentBall.getPosition()).mult(pullForce));
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
    
    /**
     * Update all of the human players.
     */
    public static void updateAll(){
        for(final EnumMap<HumanPlayerPosition, HumanPlayer> array : humanPlayers.values()){
            for(final HumanPlayer player : array.values()){
                player.update();
            }
        }
    }
    
    /**
     * Throw the current ball at a target.
     * @param target Target to throw at
     */
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
    
    /**
     * Give a ball to a human player.
     * @param ball Ball to give
     */
    public static void giveBallToNearestHP(final Ball ball){
        final Alliance alliance = ball.alliance;
        if(ball.isScored()){
            ball.reset();
        } else if(!ball.isOwned()){
            Vector3f exitPos = ball.getPosition();
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
    
    /**
     * Throws the closest ball at a point.
     * @param pos       Point to throw at
     * @param alliance  Alliance of requester
     */
    public static void throwAt(final Vector3f pos, final Alliance alliance){
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

    /**
     * {@inheritDoc}
     */
    public void releaseBall() {
        currentBall = null;
        isBallHeld = false;
        if(ballQueue.isEmpty() == false){
            currentBall = ballQueue.remove(0);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String detailedToString(String offset){
        StringBuilder temp = (new StringBuilder()
            .append(offset).append(name).append("{")
            .append(offset).append("    currentBall: ").append(currentBall).append(",\n")
            .append(offset).append("    ballQueue: [ "));
        for(Ball ball : ballQueue){
            temp.append(offset).append("\n        ").append(ball).append(",");
        }
        temp.setLength(temp.length()-1);
        return temp.append(offset).append("\n    holdingPosition: ").append(holdingPosition).append("\n")
            .append(offset).append("     isBallHeld: ").append(isBallHeld).append("\n")
            .append(offset).append("}").toString();
    }
    
    /**
     * Runnable for manually inbounding.
     */
    public static class ManualInboundRunnable implements Runnable{
        private Robot robot;
        
        /**
         * Constructor for a manual inbound runnable.
         * @param robot Robot to throw at 
         */
        public ManualInboundRunnable(final Robot robot){
            this.robot = robot;
        }
              
        /**
         * {@inheritDoc}
         */
        public void run() {
            throwAt(robot.getPosition(), robot.alliance);
        }
    }
    
    
    
    /**
     * Enum that represents the different human player posiitons.
     */
    public static enum HumanPlayerPosition{
        /**
         * Inbounder.
         */
        Close(Alliance.Red.farHumanPlayer, "Close"), 
        
        /**
         * Far HP, positive Z.
         */
        FarPosZ(Alliance.Red.closeHumanPlayer, "Far(+Z)"), 
        
        /**
         * Far HP, negative Z.
         */
        FarNegZ(Alliance.Red.closeHumanPlayer.mult(new Vector3f(1,1,-1)), "Far(-Z)");
        
        /**
         * Where the ball is held.
         */
        public final Vector3f holdingPosition;
        
        /**
         * Name of the position.
         */
        public final String name;
        
        /**
         * Constructor for a human player position.
         * @param holdingPosition   Where to hold the ball
         * @param name              Name of the position
         */
        private HumanPlayerPosition(final Vector3f holdingPosition, final String name){
            this.holdingPosition = holdingPosition;
            this.name = name;
        }
    }
    
    /**
     * Class for inbounders. Adds the ability to switch sides.
     */
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
        
        /**
        * Runnable for switching sides.
        */
       public static class SwitchSidesRunnable implements Runnable{
           private final Alliance alliance;

           /**
            * Constructor for a switch sides runnable.
            * @param alliance Alliance to move
            */
           public SwitchSidesRunnable(final Alliance alliance){
               this.alliance = alliance;
           }

           /**
            * {@inheritDoc}
            */
           public void run(){
               if(((CloseHumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.Close)).currentBall != null){
                   ((CloseHumanPlayer) humanPlayers.get(alliance).get(HumanPlayerPosition.Close)).moveBallToOtherSide();
               }
           }
       }
    }
}

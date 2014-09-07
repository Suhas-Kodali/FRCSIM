package org.frogforce503.FRCSIM;

import org.frogforce503.FRCSIM.AI.Position;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AI.AIFollowerProgram;
import org.frogforce503.FRCSIM.AbstractSubsystem.SubsystemType;
import org.frogforce503.FRCSIM.Ball.BallOwner;
import static org.frogforce503.FRCSIM.Main.in;

/**
 * Class that represents a robot.
 * @author Bryce Paputa
 */
public class Robot implements Position, BallOwner, DTSDebuggable{
    /**
     * HashMap of all of the subsystems.
     */
    protected final EnumMap<SubsystemType, AbstractSubsystem> subsystems;
    
    /**
     * Unique ID number.
     */
    public final int number = count++;
    
    private static int count = 1;
    
    /**
     * Returns the highest possible uid number. 
     * @return Highest uid
     */
    public static int getMaxRobotNum(){ return count; }
    
    /**
     * Array of all robots, divided into their alliances.
     */
    public static final EnumMap<Alliance, ArrayList<Robot>> robots = new EnumMap(Alliance.class);
    static {
        robots.put(Alliance.Red, new ArrayList(3));
        robots.put(Alliance.Blue, new ArrayList(3));
    }
    
    /**
     * Alliance of this robot.
     */
    public final Alliance alliance;
    
    /**
     * Stores whether or not this robot is tall.
     */
    public final boolean isTall;
    private boolean wantsBall;
    private final AIFollowerProgram ai;
    
    /**
     * Constructor for a Robot.
     * @param subsystems    Array of subsystems to build the robot out of
     * @param rootNode      Root node to add objects to
     * @param space         PhysicsSpace for the Robot to exist in
     * @param alliance      Alliance of the robot
     * @param pos           Position to spawn the robot at
     */
    public Robot(final ArrayList<AbstractSubsystem> subsystems, final Node rootNode, final PhysicsSpace space, final Alliance alliance, final Vector3f pos){
        this.subsystems = new EnumMap<SubsystemType, AbstractSubsystem>(SubsystemType.class);
        for(AbstractSubsystem subsystem : subsystems){
            if(this.subsystems.containsKey(subsystem.getType())){
                throw new IllegalArgumentException("Robot cannot have duplicate subsystems!\nDuplicate: " + subsystem);
            }
            this.subsystems.put(subsystem.getType(), subsystem);
        }
        
        if(this.subsystems.get(SubsystemType.Drivetrain) == null) {
            throw new IllegalArgumentException("Robot must have a drivetrain!");
        }
        
        this.alliance = alliance;
        
        for(AbstractSubsystem subsystem : subsystems){
            subsystem.registerOtherSubsystems(this.subsystems, this);
        }
        this.subsystems.get(SubsystemType.Drivetrain).registerPhysics(rootNode, space);
        ((AbstractDrivetrain) this.subsystems.get(SubsystemType.Drivetrain)).getVehicleControl().setPhysicsLocation(pos);
        
        ai = (AIFollowerProgram) this.subsystems.get(SubsystemType.Controller);
        
        isTall = this.subsystems.containsKey(SubsystemType.Box);
        
        robots.get(alliance).add(this);
    }

    /**
     * Runs the standard loop based functions of the subsystem.
     */
    public void update() {
        for(final AbstractSubsystem subsystem : this.subsystems.values()){
            subsystem.update();
        }        
    }
    
    /**
     * Updates all of the robots.
     */
    public static void updateAll(){
        for(final ArrayList<Robot> alliance : robots.values()){
            for(final Robot robot : alliance){
                robot.update();
            }
        }
    }
    
    /**
     * Gets the position of the robot.
     * @return Position of the robot
     */
    @Override
    public Vector3f getPosition(){
        return ((AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain)).getPosition();
    }
    
    /**
     * Gets the velocity of the robot.
     * @return Velocity of the robot
     */
    public Vector3f getVelocity(){
        return ((AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain)).getVelocity();
    }
    
    /**
     * Gets the closest robot of a specified alliance to a point.
     * @param point     Point to compare distances from
     * @param alliance  Alliance to look for
     * @return          Closest robot of given alliance to the point
     */
    public static Robot getClosestRobot(final Vector3f point, final Alliance alliance){
        float minDistance = Float.MAX_VALUE;
        Robot robot = null;
        for(final Robot curRobot : robots.get(alliance)){
            float curDistance = curRobot.getPosition().subtract(point).length();
            if(curDistance < minDistance){
                minDistance = curDistance;
                robot = curRobot;
            }
        }
        return robot;
    }

    /**
     * Gets whether or not this robot has a ball.
     * @return Whether or not this robot has a ball
     */
    public boolean hasBall() {
        try {
            return ((AbstractIntake) subsystems.get(SubsystemType.Intake)).hasBall();
        } catch (final NullPointerException e){
            return false;
        }
    }
    
    /**
     * Gets this robot's ball. Will return null whenever hasBall()==false.
     * @return The ball held by the robot
     */
    public Ball getCurrentBall(){
        try{
            return ((AbstractIntake) subsystems.get(SubsystemType.Intake)).getHeldBall();
        } catch (final NullPointerException e){
            return null;
        }
    }
    
    /**
     * Gets whether or not this robot is touching a wall.
     * @return Whether or not this robot is touching a wall.
     */
    public boolean isTouchingWall(){
        final Vector3f curPos = getPosition();
        return FastMath.abs(curPos.x)>=in(27*12-25) || FastMath.abs(curPos.z)>=in(12*12+4-25);
    }
    
    /**
     * Gets a flag that tells the human player whether or not to auto throw at this robot.
     * @return Should the human player auto throw at this robot
     */
    public boolean wantsBall(){
        return wantsBall;
    }
    
    /**
     * Sets a flag that tells the human player whether or not to auto throw at this robot.
     * @param value Should the human player auto throw at this robot
     */
    public void setWantsBall(final boolean value){
        wantsBall = value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode(){
        return number;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Robot) {
            final Robot other = (Robot) obj;
            return other.number == this.number;
        }
        return false;
    }
    
    /**
     * Gets a reference to this robot's AI
     * @return This robot's AI
     */
    public AIFollowerProgram getAIFollower(){
        return ai;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseBall() {
        ((AbstractIntake) subsystems.get(SubsystemType.Intake)).releaseBall();
    }
        /**
     * {@inheritDoc}
     */
    
    @Override
    public String toString(){
        return "Robot("+number+")";
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public String detailedToString(String offset){
        StringBuilder temp = new StringBuilder();
        temp.append(offset).append(this).append("{\n");
        temp.append(offset).append("    alliance: ").append(alliance).append(",\n");
        temp.append(offset).append("    subsystems: [ ");
        for(AbstractSubsystem subsystem : subsystems.values()){
            temp.append("\n").append(subsystem.detailedToString(offset + "        ")).append(",");
        }
        temp.setLength(temp.length()-1);
        temp.append("\n").append(offset).append("    ]\n");
        return temp.toString();
    }
}

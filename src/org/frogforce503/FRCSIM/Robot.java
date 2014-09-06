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

/**
 *
 * @author Bryce
 */
public class Robot implements Position, BallOwner, DTSDebuggable{
    final protected EnumMap<SubsystemType, AbstractSubsystem> subsystems;
    private static int count = 1;
    public final int number = count++;
    public static int getMaxRobotNum(){ return count; }
    public static final EnumMap<Alliance, ArrayList<Robot>> robots = new EnumMap(Alliance.class);
    static {
        robots.put(Alliance.RED, new ArrayList(3));
        robots.put(Alliance.BLUE, new ArrayList(3));
    }
    public final Alliance alliance;
    public final boolean isTall;
    public static void updateAll(){
        for(final ArrayList<Robot> alliance : robots.values()){
            for(final Robot robot : alliance){
                robot.update();
            }
        }
    }
    private boolean wantsBall;
    private final AIFollowerProgram ai;
    
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
        this.subsystems.get(SubsystemType.Drivetrain).registerPhysics(rootNode, space, alliance);
        
        ai = (AIFollowerProgram) this.subsystems.get(SubsystemType.Controller);
        
        setPhysicsLocation(pos);
        isTall = this.subsystems.containsKey(SubsystemType.Box);
        
        robots.get(alliance).add(this);
    }

    public void update() {
        for(final AbstractSubsystem subsystem : this.subsystems.values()){
            subsystem.update();
        }        
    }

    public final void setPhysicsLocation(final Vector3f pos) {
        ((AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain)).getVehicleControl().setPhysicsLocation(pos);
    }    
    
    public AbstractDrivetrain getDrivetrain(){
        return (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
    }
    
    public Vector3f getPosition(){
        return ((AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain)).getPosition();
    }
    
    public Vector3f getVelocity(){
        return ((AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain)).getVelocity();
    }
    
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

    public boolean hasBall() {
        try {
            return ((AbstractIntake) subsystems.get(SubsystemType.Intake)).hasBall();
        } catch (final NullPointerException e){
            return false;
        }
    }
    
    public Ball getCurrentBall(){
        try{
            return ((AbstractIntake) subsystems.get(SubsystemType.Intake)).getHeldBall();
        } catch (final NullPointerException e){
            return null;
        }
    }
    
    public boolean isTouchingWall(){
        final Vector3f curPos = getPosition();
        return FastMath.abs(curPos.x)>=Main.in(27*12-25) || FastMath.abs(curPos.z)>=Main.in(12*12+4-25);
    }
    
    public boolean wantsBall(){
        return wantsBall;
    }
    
    public void setWantsBall(final boolean value){
        wantsBall = value;
    }
    
    @Override
    public int hashCode(){
        return number;
    }

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
    
    public AIFollowerProgram getAIFollower(){
        return ai;
    }

    public void releaseBall() {
        ((AbstractIntake) subsystems.get(SubsystemType.Intake)).releaseBall();
    }
    
    public String toString(){
        return "Robot("+number+")";
    }
    
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

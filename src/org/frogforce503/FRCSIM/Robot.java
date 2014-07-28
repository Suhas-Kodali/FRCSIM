package org.frogforce503.FRCSIM;

import org.frogforce503.FRCSIM.AI.Position;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AI.AIFollowerProgram;
import org.frogforce503.FRCSIM.AbstractSubsystem.SubsystemType;

/**
 *
 * @author Bryce
 */
public class Robot extends Position{
    protected EnumMap<SubsystemType, AbstractSubsystem> subsystems;
    private static int count = 0;
    public final int number = count++;
//    protected static final ArrayList<Robot> robots = new ArrayList<Robot>(6);
    public static final EnumMap<Alliance, ArrayList<Robot>> robots = new EnumMap(Alliance.class);
    static {
        robots.put(Alliance.RED, new ArrayList(3));
        robots.put(Alliance.BLUE, new ArrayList(3));
    }
    public Alliance alliance;
    public final boolean isTall;
    public static void updateAll(){
        for(ArrayList<Robot> alliance : robots.values()){
            for(Robot robot : alliance){
                robot.update();
            }
        }
    }
    private boolean wantsBall;
    private AIFollowerProgram ai = null;
    
    public Robot(ArrayList<AbstractSubsystem> subsystems, Node rootNode, PhysicsSpace space, Alliance alliance, Vector3f pos){
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
        if(this.subsystems.get(SubsystemType.Controller) instanceof AIFollowerProgram){
            ai = (AIFollowerProgram) this.subsystems.get(SubsystemType.Controller);
        }
        
        setPhysicsLocation(pos);
        isTall = this.subsystems.containsKey(SubsystemType.Box);
        
        robots.get(alliance).add(this);
    }

    public void update() {
        for(AbstractSubsystem subsystem : this.subsystems.values()){
            subsystem.update();
        }        
    }

    public final void setPhysicsLocation(Vector3f pos) {
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
    
    public static Robot getClosestRobot(Vector3f point, Alliance alliance){
        float minDistance = Float.MAX_VALUE;
        Robot robot = null;
        for(Robot curRobot : robots.get(alliance)){
            float curDistance = curRobot.getPosition().subtract(point).length();
            if(curDistance < minDistance){
                minDistance = curDistance;
                robot = curRobot;
            }
        }
        return robot;
    }
    
    public static ArrayList<RobotPosition> getRobotPositions(){
        ArrayList<RobotPosition> positions = new ArrayList<RobotPosition>();
        for(ArrayList<Robot> alliance : robots.values()){
            for(Robot robot : alliance){
                positions.add(new RobotPosition(robot));
            }
        }
        
        return positions;
    }

    public boolean hasBall() {
        try {
            return ((AbstractIntake) subsystems.get(SubsystemType.Intake)).hasBall();
        } catch (NullPointerException e){
            return false;
        }
    }
    
    public Ball getCurrentBall(){
        try{
            return ((AbstractIntake) subsystems.get(SubsystemType.Intake)).getHeldBall();
        } catch (NullPointerException e){
            return null;
        }
    }
    
    public boolean isTouchingWall(){
        Vector3f curPos = getPosition();
        return FastMath.abs(curPos.x)>=Main.in(27*12-25) || FastMath.abs(curPos.z)>=Main.in(12*12+4-25);
    }
    
    public boolean wantsBall(){
        return wantsBall;
    }
    
    public void setWantsBall(boolean value){
        wantsBall = value;
    }
    
    @Override
    public int hashCode(){
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Robot other = (Robot) obj;
        return other.number == this.number;
    }
    
    public AIFollowerProgram getAIFollower(){
        return ai;
    }
    
    public static class RobotPosition{
        private final Vector3f pos;
        private final Alliance alliance;
        private final Vector3f forward;
        private final boolean isTall;
        public RobotPosition(Vector3f pos, Vector3f forward, Alliance alliance, boolean isTall){
            this.pos = pos;
            this.forward = forward;
            this.alliance = alliance;
            this.isTall = isTall;
        }
        
        public RobotPosition(Robot robot){
            pos = ((AbstractDrivetrain) robot.subsystems.get(SubsystemType.Drivetrain)).getVehicleControl().getPhysicsLocation();
            forward = ((AbstractDrivetrain) robot.subsystems.get(SubsystemType.Drivetrain)).getVehicleControl().getForwardVector(null);
            this.alliance = robot.alliance;
            this.isTall = robot.isTall;
        }
        
        public Vector3f getPosition(){
            return pos;
        }
        
        public Vector3f getForward(){
            return forward;
        }
        
        public Alliance getAlliance(){
            return alliance;
        }
        
        public boolean isTall(){
            return isTall;
        }
    }
}

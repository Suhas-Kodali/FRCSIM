package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractSubsystem.SubsystemType;

/**
 *
 * @author Bryce
 */
public class Robot{
    protected EnumMap<SubsystemType, AbstractSubsystem> subsystems;
    private Node robotNode;
    protected static final ArrayList<Robot> robots = new ArrayList<Robot>(6);
    protected Alliance alliance;
    
    public static void updateAll(){
        for(Robot robot : robots){
            robot.update();
        }
    }
    
    public Robot(AbstractSubsystem[] subsystems, Node rootNode, PhysicsSpace space, Alliance alliance, Vector3f pos){
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
        
        robotNode = ((AbstractDrivetrain) this.subsystems.get(SubsystemType.Drivetrain)).getVehicleNode();
        for(AbstractSubsystem subsystem : subsystems){
            subsystem.registerOtherSubsystems(this.subsystems, this);
            if(subsystem instanceof AbstractDrivetrain){
                subsystem.registerPhysics(rootNode, space, alliance);
            } else {
                subsystem.registerPhysics(robotNode, space, alliance);
            }
        }
        rootNode.attachChild(robotNode);
        robotNode.addControl(new RigidBodyControl());
        setPhysicsLocation(pos);
        this.alliance = alliance;
        robots.add(this);
    }

    public void update() {
        for(AbstractSubsystem subsystem : this.subsystems.values()){
            subsystem.update();
        }        
    }

    public final void setPhysicsLocation(Vector3f pos) {
        robotNode.getControl(RigidBodyControl.class).setPhysicsLocation(pos);
    }    
    
    public static ArrayList<RobotPosition> getRobotPositions(){
        ArrayList<RobotPosition> positions = new ArrayList<RobotPosition>();
        for(Robot robot : robots){
            positions.add(new RobotPosition(robot));
        }
        
        return positions;
    }
    
    public static class RobotPosition{
        private final Vector3f pos;
        private final Alliance alliance;
        private final Vector3f forward;
        public RobotPosition(Vector3f pos, Vector3f forward, Alliance alliance){
            this.pos = pos;
            this.forward = forward;
            this.alliance = alliance;
        }
        
        public RobotPosition(Robot robot){
            pos = ((AbstractDrivetrain) robot.subsystems.get(SubsystemType.Drivetrain)).getVehicleControl().getPhysicsLocation();
            forward = ((AbstractDrivetrain) robot.subsystems.get(SubsystemType.Drivetrain)).getVehicleControl().getForwardVector(null);
            this.alliance = robot.alliance;
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
    }
}

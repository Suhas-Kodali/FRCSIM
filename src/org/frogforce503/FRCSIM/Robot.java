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
    private EnumMap<SubsystemType, AbstractSubsystem> subsystems;
    private Node robotNode;
    private static final ArrayList<Robot> robots = new ArrayList<Robot>(6);
    
    public static void updateAll(){
        for(Robot robot : robots){
            robot.update();
        }
    }
    
    public Robot(AbstractSubsystem[] subsystems, Node rootNode, PhysicsSpace space, Alliance alliance, Vector3f pos){
        this.subsystems = new EnumMap<SubsystemType, AbstractSubsystem>(SubsystemType.class);
        for(AbstractSubsystem subsystem : subsystems){
            this.subsystems.put(subsystem.getType(), subsystem);
        }
        
        try{
            this.subsystems.get(SubsystemType.Drivetrain);
        } catch (Exception e) {
            throw new Error();
        }
        
        robotNode = ((AbstractDrivetrain) this.subsystems.get(SubsystemType.Drivetrain)).getVehicleNode();
        for(AbstractSubsystem subsystem : subsystems){
            subsystem.registerOtherSubsystems(this.subsystems);
            if(subsystem instanceof AbstractDrivetrain){
                subsystem.registerPhysics(rootNode, space, alliance);
            } else {
                subsystem.registerPhysics(robotNode, space, alliance);
            }
        }
        rootNode.attachChild(robotNode);
        robotNode.addControl(new RigidBodyControl());
        setPhysicsLocation(pos);
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
}

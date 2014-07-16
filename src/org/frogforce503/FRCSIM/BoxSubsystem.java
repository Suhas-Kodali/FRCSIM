package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.EnumMap;

/**
 *
 * @author Bryce
 */
public class BoxSubsystem extends AbstractSubsystem{
    float width, length, height;
    public BoxSubsystem(float width, float length, float height){
        this.width = width;
        this.length = length;
        this.height = height;
    }
    @Override
    public void update() {}

    @Override
    public SubsystemType getType() {
        return SubsystemType.Box;
    }

    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {}

    @Override
    public void registerPhysics(Node rootNode, PhysicsSpace space, Alliance alliance) {
        Box box = new Box(new Vector3f(length/2, Main.in(3), width/2), new Vector3f(-length/2, height+Main.in(3), -width/2));
        Geometry boxGeometry = new Geometry("Bumper Front", box);
        boxGeometry.setMaterial(Main.cage);
        boxGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(boxGeometry);
    }
    
}

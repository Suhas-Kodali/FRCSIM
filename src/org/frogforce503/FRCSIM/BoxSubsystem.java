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
    final float width, length, height;
    public BoxSubsystem(final float width, final float length, final float height){
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
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {}

    @Override
    public void registerPhysics(final Node rootNode, final PhysicsSpace space, final Alliance alliance) {
        final Box box = new Box(new Vector3f(length/2, Main.in(3), width/2), new Vector3f(-length/2, height+Main.in(3), -width/2));
        final Geometry boxGeometry = new Geometry("Box", box);
        boxGeometry.setMaterial(Main.allianceWalls);
        boxGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(boxGeometry);
    }
    
}

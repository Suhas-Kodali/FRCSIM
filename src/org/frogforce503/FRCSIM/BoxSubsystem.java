package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.EnumMap;
import static org.frogforce503.FRCSIM.Main.in;

/**
 * Box subsystem for blocking.
 * @author Bryce Paputa
 */
public class BoxSubsystem extends AbstractSubsystem{
    private final float width, length, height;
    
    /**
     * Box subsystem constructor.
     * @param width     Width of the box
     * @param length    Length of the box
     * @param height    Height of the box
     */
    public BoxSubsystem(final float width, final float length, final float height){
        this.width = width;
        this.length = length;
        this.height = height;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public SubsystemType getType() {
        return SubsystemType.Box;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPhysics(final Node rootNode, final PhysicsSpace space) {
        final Box box = new Box(new Vector3f(length/2, in(3), width/2), new Vector3f(-length/2, height+in(3), -width/2));
        final Geometry boxGeometry = new Geometry("Box", box);
        boxGeometry.setMaterial(Main.allianceWalls);
        boxGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        rootNode.attachChild(boxGeometry);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return "BoxSubsystem(" + width + "x" + length + "x" + height + ")";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String detailedToString(String offset){
        return offset + toString();
    }
}

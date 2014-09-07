package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;

/**
 * Basic shooter class.
 * @author Bryce Paputa
 */
public class BasicShooter extends AbstractShooter{
    /**
     * How hard the ball is shot in the y direction.
     */
    public static final float shootElevation = .675f;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void preShot(final float force){
        super.preShot(force);
        intake.getShootingBall().setVelocity((drivetrain.getForwards().add(0, shootElevation, 0)).mult(force));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void postShot(){
        super.postShot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPhysics(final Node rootNode, final PhysicsSpace space) { }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return "BasicShooter";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String detailedToString(String offset) {
        return offset + toString();
    }
}

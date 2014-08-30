package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;

/**
 *
 * @author Bryce
 */
public class BasicShooter extends AbstractShooter{
    public static final float shootElevation = .6f;
    
    @Override
    public void update() {
    }
    
    @Override
    public void preShot(final float force){
        super.preShot(force);
        intake.getShootingBall().getRigidBodyControl().setLinearVelocity((vehicle.getForwardVector(null).add(0, shootElevation, 0)).mult(force));
    }
    
    @Override
    public void postShot(){
        super.postShot();
    }

    @Override
    public void registerPhysics(final Node rootNode, final PhysicsSpace space, final Alliance alliance) { }
}

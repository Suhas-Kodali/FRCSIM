package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Bryce
 */
public class BasicShooter extends AbstractShooter{
    public static final int shootForce = 12;
    public static final float shootElevation = .6f;
    
    @Override
    public void update() {
    }
    
    @Override
    public void preShot(){
        super.preShot();
        intake.getShootingBall().getRigidBodyControl().setLinearVelocity((vehicle.getForwardVector(null).add(0, shootElevation, 0)).mult(shootForce));
    }
    
    @Override
    public void postShot(){
        super.postShot();
    }

    @Override
    public void registerPhysics(Node rootNode, PhysicsSpace space, Alliance alliance) {
    }
}

package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author Bryce
 */
public class BasicShooter extends AbstractShooter{
    public static final int shootForce = 12;
    public static final float shootElevation = .6f;

    public BasicShooter(AbstractIntake intake, VehicleControl vehicle){
        super(intake, vehicle);
    }
    
    @Override
    public void update() {
    }
    
    @Override
    public void preShot(){
        super.preShot();
        intake.getShootingBall().getRigidBodyControl().setLinearVelocity((vehicle.getForwardVector(null)).add(new Vector3f(0, shootElevation, 0)).mult(shootForce).add(vehicle.getLinearVelocity()));
    }
    
    @Override
    public void postShot(){
        super.postShot();
    }
}

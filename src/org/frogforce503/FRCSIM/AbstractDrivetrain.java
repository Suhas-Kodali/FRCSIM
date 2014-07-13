package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Bryce
 */
public abstract class AbstractDrivetrain extends AbstractSubsystem{

    @Override
    public SubsystemType getType() {
        return SubsystemType.Drivetrain;
    }
    
    public abstract VehicleControl getVehicleControl();
    public abstract Node getVehicleNode();
    public abstract void driveTowardsPoint(Vector3f point);
    
}

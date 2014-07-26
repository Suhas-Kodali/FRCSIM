package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.FastMath;
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
    
    public static enum DriveDirection{
        Towards, DontCare, Away;
    }
    
    public abstract void driveToPoint(Vector3f point, DriveDirection direction);
    public abstract void turnTowardsPoint(Vector3f point);
    
    public boolean driveToPointAndDirection(Vector3f point, Vector3f direction, Vector3f range, float angularRange){
        Vector3f distance = point.subtract(getPosition()), curPos = getPosition();
        distance.maxLocal(distance.negate());
        boolean isInRange = true;
        isInRange = isInRange && distance.x < range.x;
        isInRange = isInRange && distance.z < range.z;
        
        
        if(isInRange){
            if(FastMath.abs(getVehicleControl().getForwardVector(null).angleBetween(direction.subtract(curPos).normalize())) < angularRange * FastMath.DEG_TO_RAD){
                turnTowardsPoint(direction);
                return true;
            } 
            turnTowardsPoint(direction);
        } else {
            driveToPoint(point, point.subtract(curPos).dot(direction.subtract(curPos)) > 0? DriveDirection.Towards : DriveDirection.Away);
        }
        return false;
    }
    
    public Vector3f getPosition(){
        return getVehicleControl().getPhysicsLocation();
    }
    public Vector3f getVelocity(){
        return getVehicleControl().getLinearVelocity();
    }
}

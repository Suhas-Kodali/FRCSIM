package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
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
    
    public abstract void driveToPoint(final Vector3f point, final DriveDirection direction);
    public abstract void turnTowardsPoint(final Vector3f point);
    
    public boolean driveToPointAndDirection(final Vector3f point, final Vector3f direction, final Vector3f range, final float angularRange){
        final Vector3f distance = point.subtract(getPosition()), curPos = getPosition();
        distance.maxLocal(distance.negate());
        
        if(distance.x < range.x && distance.z < range.z){
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
    
    private boolean isOnDefense = false;
    public void setOnDefense(final boolean defense){
        isOnDefense = defense;
    }
    
    
    public Vector3f avoidObstructions(final Vector3f curPos, final Vector3f target, final Vector3f obstruction){
        if(isOnDefense){
            return target;
        }
        Vector3f vectorToTarget = target.subtract(curPos);
        Plane planeAgainstTarget = new Plane();
        planeAgainstTarget.setOriginNormal(curPos, vectorToTarget);
        
        if(planeAgainstTarget.pseudoDistance(obstruction) > 0 && planeAgainstTarget.pseudoDistance(obstruction) < 3){
            Plane planeToTarget = new Plane();
            planeToTarget.setOriginNormal(curPos, vectorToTarget.cross(Vector3f.UNIT_Y));
            if(Math.abs(planeToTarget.pseudoDistance(obstruction))<.5f){
                if(Math.abs(curPos.z)>Main.in(12*15)){
                    target.setZ(0);
                } else {
                    target.setZ(Main.in(20*12*FastMath.sign(curPos.z)));
                }
                return (curPos.add(curPos.subtract(target).mult(2f).cross(target.z == 0?Vector3f.UNIT_Y:Vector3f.UNIT_Y.negate()))).interpolate(target, .5f);
            }
        }
        return target;
    }
    
    public void applyDownforce(){
        getVehicleControl().applyCentralForce(Vector3f.UNIT_Y.mult(-200));
    }
    
    public Vector3f getPosition(){
        return getVehicleControl().getPhysicsLocation();
    }
    public Vector3f getVelocity(){
        return getVehicleControl().getLinearVelocity();
    }
}

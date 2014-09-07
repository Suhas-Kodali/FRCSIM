package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import org.frogforce503.FRCSIM.AI.Position;
import static org.frogforce503.FRCSIM.Main.in;

/**
 * Abstract class representing drivetrains subsystems.
 * @author Bryce Paputa
 */
public abstract class AbstractDrivetrain extends AbstractSubsystem implements Position{
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SubsystemType getType() {
        return SubsystemType.Drivetrain;
    }
    
    /**
     * Getter for the drivetrain's vehicleControl. 
     * 
     * @return  VehicleControl VehicleControl of the drivetrain
     * @see     com.jme3.bullet.control.VehicleControl
     */
    protected abstract VehicleControl getVehicleControl();
    
    /**
     * Enum that tells the drivetrain control methods which orientation is prefered.
     */
    public static enum DriveDirection{
        /**
         * Makes the drivetrain drive with the front facing the target.
         */
        Towards, 
        
        /**
         * Makes the drvietrain drive in the most efficient way.
         */
        DontCare, 
        
        /**
         * Makes the drivetrain drive the the front facing away from the target.
         */
        Away;
    }
    
    /**
     * Drives towards a point.
     * @param point     Target point
     * @param direction Direction to face
     */
    public abstract void driveToPoint(final Vector3f point, final DriveDirection direction);
    
    /**
     * Turns towards a point.
     * @param point Target point
     */
    public abstract void turnTowardsPoint(final Vector3f point);
    
    /**
     * Drives to a point and turns towards a different point once it is in a specifc range.
     * @param point         Point to drive to
     * @param direction     Point to turn torwards
     * @param range         Range for the drivetrain's location
     * @param angularRange  Range for the drivetrain's facing
     * @return              Whether or not the drivetrain in both specified ranges
     */
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
    
    /**
     * Changes the driving style from a more efficent one to one meant to slow down offense.
     */
    private boolean isOnDefense = false;
    
    /**
     * Changes the driving style from a more efficent one to one meant to slow down offense.
     * @param defense New value
     */
    public void setOnDefense(final boolean defense){
        isOnDefense = defense;
    }
    
    /**
     * Takes a point to point trajectory and modifies it to avoid an obstruction by doing a spin move. TODO: NEEDS WORK
     * @param curPos        Current position
     * @param target        Original target
     * @param obstruction   Location of obstruction
     * @return              New target
     */
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
                if(Math.abs(curPos.z)>in(12*15)){
                    target.setZ(0);
                } else {
                    target.setZ(in(20*12*FastMath.sign(curPos.z)));
                }
                return (curPos.add(curPos.subtract(target).mult(2f).cross(target.z == 0?Vector3f.UNIT_Y:Vector3f.UNIT_Y.negate()))).interpolate(target, .5f);
            }
        }
        return target;
    }
    
    /**
     * Applies a standard ammount of downforce to prevent robots from climbing on top of each.
     */
    protected void applyDownforce(){
        getVehicleControl().applyCentralForce(Vector3f.UNIT_Y.mult(-200));
    }
    
    /**
     * Gets the vehicle's location.
     * @return Location of vehicle
     */
    @Override
    public Vector3f getPosition(){
        return getVehicleControl().getPhysicsLocation();
    }
    
    /**
     * Gets the vehicle's forwards vector.
     * @return Forwards vector of vehicle
     */
    public Vector3f getForwards(){
        return getVehicleControl().getForwardVector(null);
    }
    
    /**
     * Gets the vehicle's velocity.
     * @return Velocity of vehicle
     */
    public Vector3f getVelocity(){
        return getVehicleControl().getLinearVelocity();
    }
}

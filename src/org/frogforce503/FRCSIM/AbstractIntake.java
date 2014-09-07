package org.frogforce503.FRCSIM;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.Ball.BallOwner;

/**
 * Abstract class representing ball intake subsystems.
 * @author Bryce Paputa
 */
public abstract class AbstractIntake extends AbstractSubsystem implements BallOwner{
    /**
     * Reference to the parent robot.
     */
    protected Robot robot;
    
    /**
     * Gets the currently held ball.
     * @return Current held ball
     */
    public abstract Ball getHeldBall();
    
    /**
     * Gets the ball that is prepared for shooting
     * @return Ball to shoot
     */
    public abstract Ball getShootingBall();
    
    /**
     * Returns whether or not the intake is holding a ball
     * @return Whether or not the intake is holding a ball
     */
    public boolean hasBall(){
        return getHeldBall() != null;
    }
    
    /**
     * Returns whether or not the intake is ready to shoot
     * @return Whether or not hte intake is ready to shoot
     */
    public boolean readyToShoot(){
        return getShootingBall() != null;
    }
    
    /**
     * Prepares the intake for shooting.
     */
    public abstract void preShot();
    
    /**
     * Called after the shoorter is done shooting.
     */
    public abstract void postShot();
    
    /**
     * Retracts intake.
     */
    public abstract void retract();
    
    /**
     * Extends intake.
     */
    public abstract void extend();
    
    /**
     * Returns whether or not the intake is extended.
     * @return Whether or not the intake is extended 
     */
    public abstract boolean isExtended();
    
    /**
     * Toggles the intake up and down.
     */
    protected Runnable toggle = new Runnable(){
        public void run() {
            if(isExtended()){
                retract();
            } else {
                extend();
            }
        }        
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public SubsystemType getType() {
        return SubsystemType.Intake;
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        this.robot = robot;
    }
}

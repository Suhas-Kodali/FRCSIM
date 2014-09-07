package org.frogforce503.FRCSIM;

import java.util.EnumMap;
import java.util.Timer;
import java.util.TimerTask;
import org.frogforce503.FRCSIM.AbstractSubsystem.SubsystemType;

/**
 * Abstract class that represents shooter subsystems.
 * @author Bryce Paputa
 */
public abstract class AbstractShooter extends AbstractSubsystem {
    /**
     * Intake of the parent robot.
     */
    protected AbstractIntake intake;
    
    /**
     * Drivetrain of the parent robot.
     */
    protected AbstractDrivetrain drivetrain;
    
    /**
     * Parent robot.
     */
    protected Robot robot;
    
    /**
     * How long the shot lasts for.
     */
    public static final int shootLength = 1000;
    
    private boolean isShooting = false;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot){
        if(subsystems.containsKey(SubsystemType.Intake)){
            this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        } else {
            throw new IllegalArgumentException("Robots with shooters must have intakes!");
        }
        this.drivetrain = (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
        this.robot = robot;
    }
    
    /**
     * Starts shooting the ball and schedules the task that calls postShot. Any overriding methods must call this.
     * @param force How hard to shoot the ball
     */
    protected void preShot(final float force){
        isShooting = true;
        (new Timer()).schedule(new TimerTask(){public void run(){intake.postShot(); postShot(); assert isShooting == false;}}, shootLength);        
    }
    
    /**
     * Cleans up after the shot. Any overriding methods must call this.
     */
    protected void postShot(){
        isShooting = false;
    }    
    
    /**
     * Shoots the ball.
     */
    public final Runnable shoot = new Runnable(){
        public void run(){
            shoot(12);
        }
    };
    
    /**
     * Spits the ball out.
     */
    public final Runnable spit = new Runnable(){
        public void run(){
            shoot(6);
        }
    };
    
    /**
     * Shoots the ball.
     * @param force How hard to shoot
     */
    public void shoot(final float force){
        if(intake.hasBall() && ! isShooting){
            intake.preShot();
            if(intake.readyToShoot()){
                preShot(force);
                assert isShooting == true;
            }
        }
    }
    
    /**
     * {@inheritDoc} 
     */
    public SubsystemType getType(){
        return SubsystemType.Shooter;
    }
}

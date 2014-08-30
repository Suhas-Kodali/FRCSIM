package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.VehicleControl;
import java.util.EnumMap;
import java.util.Timer;
import java.util.TimerTask;
import org.frogforce503.FRCSIM.AbstractSubsystem.SubsystemType;

/**
 *
 * @author Bryce
 */
public abstract class AbstractShooter extends AbstractSubsystem {
    protected AbstractIntake intake;
    protected VehicleControl vehicle;
    protected AbstractDrivetrain drivetrain;
    protected Robot robot;
    
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot){
        if(subsystems.containsKey(SubsystemType.Intake)){
            this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        } else {
            throw new IllegalArgumentException("Robots with shooters must have intakes!");
        }
        this.vehicle = ((AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain)).getVehicleControl();
        this.robot = robot;
    }
    
    public abstract void update();
    
    protected void preShot(final float force){
        isShooting = true;
        (new Timer()).schedule(new TimerTask(){public void run(){intake.postShot(); postShot(); }}, shootLength);        
    }
    
    protected void postShot(){
        isShooting = false;
    }
    
    public static final int shootLength = 1000;
    private boolean isShooting = false;
    public final Runnable shoot = new Runnable(){
        public void run(){
            shoot(12);
        }
    };
    
    public final Runnable spit = new Runnable(){
        public void run(){
            shoot(6);
        }
    };
    
    public void shoot(final float force){
        if(intake.hasBall() && ! isShooting){
            intake.preShot();
            if(intake.readyToShoot()){
                preShot(force);
            }
        }
    }
    
    public SubsystemType getType(){
        return SubsystemType.Shooter;
    }
}

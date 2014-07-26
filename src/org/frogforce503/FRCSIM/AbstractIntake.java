package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.VehicleControl;
import java.util.EnumMap;

/**
 *
 * @author Bryce
 */
public abstract class AbstractIntake extends AbstractSubsystem{
    protected VehicleControl vehicle;
    protected Robot robot;
    
    public abstract Ball getHeldBall();
    public abstract Ball getShootingBall();
    
    public boolean hasBall(){
        return getHeldBall() != null;
    }
    
    public boolean readyToShoot(){
        return getShootingBall() != null;
    }
    
    private boolean isShooting = false;
    public void preShot(){
        isShooting = true;
    }
    
    public void postShot(){
        isShooting = false;
    }
    
    public boolean isShooting(){
        return isShooting;
    }
    
    public abstract void update();
    public abstract void retract();
    public abstract void extend();
    public abstract boolean isExtended();
    
    protected Runnable toggle = new Runnable(){
        public void run() {
            if(isExtended()){
                retract();
            } else {
                extend();
            }
        }        
    };

    @Override
    public SubsystemType getType() {
        return SubsystemType.Intake;
    }
    
    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        this.vehicle = ((AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain)).getVehicleControl();
        this.robot = robot;
    }
}

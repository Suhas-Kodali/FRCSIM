package org.frogforce503.FRCSIM;

import com.jme3.bullet.control.VehicleControl;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Bryce
 */
public abstract class AbstractShooter {
    protected final AbstractIntake intake;
    protected final VehicleControl vehicle;
    
    public AbstractShooter(AbstractIntake intake, VehicleControl vehicle){
        this.intake = intake;
        this.vehicle = vehicle;
    }
    
    public abstract void update();
    
    protected void preShot(){
        isShooting = true;
        (new Timer()).schedule(new TimerTask(){public void run(){intake.postShot(); postShot(); }}, shootLength);        
    }
    
    protected void postShot(){
        isShooting = false;
    }
    
    public static int shootLength = 1000;
    private boolean isShooting = false;
    public Runnable shoot = new Runnable(){
        public void run(){
            shoot();
        }
    };
    
    public void shoot(){
        if(intake.hasBall() && ! isShooting){
            intake.preShot();
            if(intake.readyToShoot()){
                preShot();
            }
        }
    }
}

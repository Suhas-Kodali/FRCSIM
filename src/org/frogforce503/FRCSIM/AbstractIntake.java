package org.frogforce503.FRCSIM;

/**
 *
 * @author Bryce
 */
public abstract class AbstractIntake {
    public abstract Ball getHeldBall();
    
    public boolean hasBall(){
        return getHeldBall() != null;
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
    protected abstract void retract();
    protected abstract void extend();
    protected abstract boolean isExtended();
    
    protected Runnable toggle = new Runnable(){
        public void run() {
            if(isExtended()){
                retract();
            } else {
                extend();
            }
        }        
    };
}

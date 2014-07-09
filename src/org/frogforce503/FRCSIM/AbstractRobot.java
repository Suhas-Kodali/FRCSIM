package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Bryce
 */
public abstract class AbstractRobot {
    public abstract void update();
    public abstract void setPhysicsLocation(Vector3f pos);
    private static final ArrayList<AbstractRobot> robots = new ArrayList<AbstractRobot>(6);
    
    public AbstractRobot(){
        robots.add(this);
    }
    
    public static void updateAll(){
        for(AbstractRobot robot : robots){
            robot.update();
        }
    }
}

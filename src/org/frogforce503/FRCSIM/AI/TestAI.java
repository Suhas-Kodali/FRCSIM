package org.frogforce503.FRCSIM.AI;

import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Zone;

/**
 *
 * @author Bryce
 */
public class TestAI extends AIControl{
    Zone[] zones = Zone.values();
    int i = 0;
    public AbstractProgram chooseNextProgram() {
        i++;
        if(i>2){
            i = 0;
        }
        return new GoToZoneProgram(zones[i], 1);
    }
}

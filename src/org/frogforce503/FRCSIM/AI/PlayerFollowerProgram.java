package org.frogforce503.FRCSIM.AI;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractControl;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Main;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class PlayerFollowerProgram extends AIFollowerProgram{
    private final AbstractControl playerControl;
    private static int baseID = AbstractProgram.getProgramNum();
    private int uid = -baseID;
    
    
    public PlayerFollowerProgram(final AbstractControl playerControl){
        this.playerControl = playerControl;
    }
    
    @Override
    public void update(){
        playerControl.update();
        if(coach!=null){
            coach.update();
        }
        if(this.program != null && this.program instanceof EjectProgram){
            program.update();
        }
    }
    
    @Override
    public void setProgram(final AbstractProgram program){
        program.registerOtherSubsystems(subsystems, robot);
        if(this.program == null || this.program.getUID() != program.getUID()){
            this.program = program;
            Main.scene.updateDirection();
        }
    }
    
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) { 
        super.registerOtherSubsystems(subsystems, robot);
        playerControl.registerOtherSubsystems(subsystems, robot);
        uid = baseID + robot.number * AbstractProgram.getMaxProgramNum();
    }
    
    public int getUID(){
        return uid;
    }
}

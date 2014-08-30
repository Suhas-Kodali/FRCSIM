package org.frogforce503.FRCSIM.AI;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractControl;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class PlayerFollowerProgram extends AIFollowerProgram{
    private AbstractControl playerControl;
    private String name = "Player Follower Program";
    public PlayerFollowerProgram(AbstractControl playerControl){
        this.playerControl = playerControl;
    }
    
    @Override
    public void update(){
        playerControl.update();
        if(coach!=null){
            coach.update();
        }
    }
    
    @Override
    public void setProgram(AbstractProgram program){
        if(this.program == null || (this.program.getName() == null ? true : !this.program.getName().equals(program.getName()))){
            this.program = program;
            System.out.println(program.getName());
        }
    }
    
    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) { 
        super.registerOtherSubsystems(subsystems, robot);
        playerControl.registerOtherSubsystems(subsystems, robot);
    }
    
    @Override
    public String getName() {
        return name;
    }
}

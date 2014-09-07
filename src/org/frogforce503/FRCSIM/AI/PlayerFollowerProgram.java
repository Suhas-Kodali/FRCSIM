package org.frogforce503.FRCSIM.AI;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractControl;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Main;
import org.frogforce503.FRCSIM.Robot;

/**
 * Extension of AIFollowerProgram that uses player input instead of running AI.
 * @author Bryce Paputa
 */
public class PlayerFollowerProgram extends AIFollowerProgram{
    private final AbstractControl playerControl;
    private static int baseID = AbstractProgram.getProgramBaseID();
    private int uid = -baseID;
    
    /**
     * Constructor for a new Player Follower.
     * @param playerControl Constrol system that controls the player's robot.
     */
    public PlayerFollowerProgram(final AbstractControl playerControl){
        this.playerControl = playerControl;
    }
    
    /**
     * {@inheritDoc}
     */
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
    
    /**
     * {@inheritDoc}
     * Doesn't actually execute any programs other than eject programs.
     */
    @Override
    public void setProgram(final AbstractProgram program){
        program.registerOtherSubsystems(subsystems, robot);
        if(this.program == null || this.program.getUID() != program.getUID()){
            this.program = program;
            Main.scene.updateDirection();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) { 
        super.registerOtherSubsystems(subsystems, robot);
        playerControl.registerOtherSubsystems(subsystems, robot);
        uid = baseID + robot.number * AbstractProgram.getMaxProgramBaseID();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getUID(){
        return uid;
    }
}

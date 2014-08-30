package org.frogforce503.FRCSIM.AI;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class AIFollowerProgram extends AbstractProgram{
    protected AISuperCoach coach;
    protected Robot robot;
    protected AbstractProgram program = null;
    private String name = "AIFollower ("+this.toString()+")";
    private EnumMap<SubsystemType, AbstractSubsystem> subsystems;
    
    public void registerCoach(AISuperCoach coach){
        this.coach = coach;
    }

    @Override
    public void update() {
        if(coach != null){
            coach.update();
        }
        if(program != null){
            program.update();
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    public void setProgram(AbstractProgram program){
        if(this.program == null || (this.program.getName() == null ? true : !this.program.getName().equals(program.getName()))){
            this.program = program;
            program.registerOtherSubsystems(subsystems, robot);
        }
    }
    
    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) { 
        this.robot = robot;
        this.subsystems = subsystems;
    }

    @Override
    public String getName() {
        return name;
    }
}

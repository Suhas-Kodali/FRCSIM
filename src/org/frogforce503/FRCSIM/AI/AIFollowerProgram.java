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
    private static int baseID = AbstractProgram.getProgramNum();
    private int uid = -baseID; 
    protected EnumMap<SubsystemType, AbstractSubsystem> subsystems;
    
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

    public void setProgram(final AbstractProgram program){
        program.registerOtherSubsystems(subsystems, robot);
        if(this.program == null || this.program.getUID() != program.getUID()){
            this.program = program;
        }
    }
    
    public String getHRProgramName(){
        return program.getHRName();
    }
    
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) { 
        this.robot = robot;
        uid = baseID + robot.number * AbstractProgram.getMaxProgramNum();
        this.subsystems = subsystems;
    }
    
    @Override
    public int getUID(){
        return uid;
    }

    @Override
    public String getHRName() {
        return "AIFollowerProgram for robot #"+robot.number;
    }
}

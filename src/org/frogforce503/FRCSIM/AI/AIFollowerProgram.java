package org.frogforce503.FRCSIM.AI;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 * Class that runs the AI coach and the AI programs that it assigns.
 * @author Bryce Paputa
 */
public class AIFollowerProgram extends AbstractProgram {
    /**
     * AI coach to manage.
     */
    protected AISuperCoach coach;
    
    /**
     * Robot to run programs on.
     */
    protected Robot robot;
    
    /**
     * Currently assigned program.
     */
    protected AbstractProgram program = null;
    
    /**
     * List of all subsystems of the Robot being used.
     */
    protected EnumMap<SubsystemType, AbstractSubsystem> subsystems;
    
    private static int baseID = AbstractProgram.getProgramBaseID();
    private int uid = -baseID; 
    
    /**
     * Registers a coach with this so that it gets updated. 
     * Each coach should only be registered with one follower.
     * @param coach Coach to manage
     */
    public void registerCoach(AISuperCoach coach){
        this.coach = coach;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        if(coach != null){
            coach.update();
        }
        if(program != null){
            program.update();
        }
    }

    /**
     * Assign a new program to this robot. If both the current program and the 
     * new program have the same UIDs, this will do nothing.
     * @param program 
     */
    public void setProgram(final AbstractProgram program){
        program.registerOtherSubsystems(subsystems, robot);
        if(this.program == null || this.program.getUID() != program.getUID()){
            this.program = program;
        }
    }
    
    /**
     * Gets a human readable name of the current program.
     * @return Human readable name of current program
     */
    public String getHRProgramName(){
        return program.getHRName();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) { 
        this.robot = robot;
        uid = baseID + robot.number * AbstractProgram.getMaxProgramBaseID();
        this.subsystems = subsystems;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getUID(){
        return uid;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getHRName() {
        return "AIFollowerProgram for robot #"+robot.number;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String detailedToString(String offset){
        return offset + "Program("+getUID()+", \"" + getHRName() + "\", \"" + (program==null? "" : program.detailedToString("")) + "\")";
    }
}

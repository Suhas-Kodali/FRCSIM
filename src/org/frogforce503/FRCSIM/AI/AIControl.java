package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Queue;
import org.frogforce503.FRCSIM.AbstractControl;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public abstract class AIControl extends AbstractProgram{
    private Deque<AbstractProgram> programList = new ArrayDeque(3);
    private EnumMap<SubsystemType, AbstractSubsystem> subsystems; 
    protected Robot robot;
    @Override
    public void update() {
        if(programList.isEmpty()){
            callProgram(chooseNextProgram());
        }
        AbstractProgram curProgram = programList.getFirst();
        curProgram.update();
        if(curProgram.isFinished()){
            programList.pop();
        }
    }

    public abstract AbstractProgram chooseNextProgram();
    
    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot) {
        this.subsystems = subsystems;
        this.robot = robot;
    }
    
    public void callProgram(AbstractProgram program){
        programList.addFirst(program);
        program.giveOwner(this);
        program.registerOtherSubsystems(subsystems, robot);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}

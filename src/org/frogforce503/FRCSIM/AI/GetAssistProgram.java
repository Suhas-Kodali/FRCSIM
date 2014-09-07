package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractIntake;
import org.frogforce503.FRCSIM.AbstractShooter;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Robot;

/**
 * Program that gets an assist.
 * @author Bryce Paputa
 */
public class GetAssistProgram extends AbstractProgram{
    private AbstractDrivetrain drivetrain;
    private Robot robot;
    private AbstractIntake intake;
    private AbstractShooter shooter;
    private final Robot target;
    private static int baseID = AbstractProgram.getProgramBaseID();
    private int uid = -baseID;
    
    /**
     * Constructor that makes a program to get the nearest available assist.
     */
    public GetAssistProgram(){
        target = null;
    }
    
    /**
     * Constructor that makes a program to get a specific assist.
     * @param target Robot to get an assist with
     */
    public GetAssistProgram(final Robot target){
        this.target = target;
    }
    
    /**
     * {@inheritDoc}
     * Finds the nearest target if one was not specified, drives to it, and spits out the ball to it.
     */
    @Override
    public void update() {
        if(robot.hasBall()){
            Robot localTarget = target;
            if(localTarget == null){
                float minDist = Float.MAX_VALUE;
                for(Robot other : Robot.robots.get(robot.alliance)){
                    final float distance = other.getPosition().distance(robot.getPosition());
                    if(!intake.getHeldBall().hasBeenOwnedBy(other) && distance < minDist && !other.hasBall()){
                        minDist = distance;
                        localTarget = other;
                    }
                }        
            }
            if(localTarget != null){
                if(drivetrain.driveToPointAndDirection(localTarget.getPosition(), localTarget.getPosition(), Vector3f.UNIT_XYZ.mult(2), 30) && !localTarget.hasBall()){
                    shooter.spit.run();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
        this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        this.shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
        this.robot = robot;
        uid = baseID  + robot.number * AbstractProgram.getMaxProgramBaseID() + (target == null? 0 : target.number) * AbstractProgram.getMaxProgramBaseID() * Robot.getMaxRobotNum();
        drivetrain.setOnDefense(false);
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
    public String getHRName(){
        return "Get Assist!";
    }    
}

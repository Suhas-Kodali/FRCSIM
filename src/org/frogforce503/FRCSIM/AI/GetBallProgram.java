package org.frogforce503.FRCSIM.AI;

import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.AbstractDrivetrain;
import org.frogforce503.FRCSIM.AbstractDrivetrain.DriveDirection;
import org.frogforce503.FRCSIM.AbstractIntake;
import org.frogforce503.FRCSIM.AbstractSubsystem;
import org.frogforce503.FRCSIM.Ball;
import org.frogforce503.FRCSIM.Robot;

/**
 *
 * @author Bryce
 */
public class GetBallProgram extends AbstractProgram{
    private AbstractDrivetrain drivetrain;
    private Robot robot;
    private AbstractIntake intake;
    private Position defense;
    private final Ball target;
    private static int baseID = AbstractProgram.getProgramNum();
    private int uid = -baseID;
    
    public GetBallProgram(){
        target = null;
    }
    
    public GetBallProgram(Ball target){
        this.target = target;
    }
    
    @Override
    public void update() {
        if(robot.hasBall()){
            intake.retract();
            return;
        }
        intake.extend();
        robot.setWantsBall(true);
        Ball localTarget = target;
        boolean owned = false;
        if(localTarget==null){
            float minDist = Float.MAX_VALUE;
            for(final Ball ball : Ball.balls){
                float distance = ball.getPosition().distanceSquared(robot.getPosition());
                if(ball.alliance == robot.alliance && !(ball.getOwner() instanceof Robot) && distance < minDist){
                    localTarget = ball;
                    minDist = distance;
                    owned = false;
                }
            }
            if(localTarget == null || localTarget.getPosition() == null){
                for(final Ball ball : Ball.balls){
                    float distance = ball.getPosition().distanceSquared(robot.getPosition());
                    if(ball.alliance == robot.alliance && !ball.hasBeenOwnedBy(robot) && distance < minDist){
                        localTarget = ball;
                        owned = true;
                        minDist = distance;
                    }
                }      
            }
        } else {
            owned = localTarget.isOwned() && localTarget.getOwner() instanceof Robot;
        }
        if(localTarget != null && localTarget.getPosition() != null){
            if(!owned){
                drivetrain.driveToPoint(localTarget.getPosition(), DriveDirection.Towards);
            } else {
                if(((Robot) localTarget.getOwner()).isTouchingWall()){
                    drivetrain.driveToPoint(Vector3f.ZERO, DriveDirection.DontCare);
                } else {
                    drivetrain.driveToPoint(localTarget.getPosition().interpolate(robot.getPosition(), robot.getPosition().subtract(localTarget.getPosition()).length() > 2? 0.6f : 1.5f), DriveDirection.Towards);
                }                
            }
        } else {
            drivetrain.driveToPoint(defense.getPosition(), DriveDirection.DontCare);         
        }
    }

    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        this.drivetrain = (AbstractDrivetrain) subsystems.get(SubsystemType.Drivetrain);
        this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
        this.robot = robot; 
        defense = new InterferencePosition(robot);
        uid = baseID + robot.number * AbstractProgram.getMaxProgramNum() + (target==null? 0 : target.number) * AbstractProgram.getMaxProgramNum() * Robot.getMaxRobotNum();
        drivetrain.setOnDefense(false);
    }
    
    @Override
    public int getUID(){
        return uid;
    }
    
    @Override
    public String getHRName() {
        return "Get The Ball!";
    }
    
}

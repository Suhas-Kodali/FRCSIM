package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;
import java.util.EnumMap;

/**
 *
 * @author Bryce
 */
public class SwervePlayer extends AbstractControl{
    private SwerveDrivetrain drivetrain;
    private AbstractIntake intake;
    private AbstractShooter shooter;
    private Robot robot;
    
    
    private Alliance alliance;
    
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        if(subsystems.get(SubsystemType.Drivetrain) instanceof SwerveDrivetrain){
            this.drivetrain = (SwerveDrivetrain) subsystems.get(SubsystemType.Drivetrain);
            this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
            this.shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
            this.robot = robot;
            this.alliance = robot.alliance;
            robot.setWantsBall(true);
        } else {
            throw new IllegalArgumentException("SwervePlayer only controls SwerveDrivetrains");
        }
        setKeyMapping(tempMapping);
    }

    public String toString(){
        return "SwervePlayer";
    }
    
    public String detailedToString(String offset) {
        return offset + "SwervePlayer{\n    type: "+type+"\n    keyMapping: "+keyMapping+"\n}";
    }
    
    public static enum SwerveType{
        FieldCentricSpectatorCam(), FieldCentricBlueDriverCam(), FieldCentricRedDriverCam(), RobotCentric();
    }
    
    private SwerveType type;
    
    public SwervePlayer(final SwerveKeyMapping keyMapping, final SwerveType type){
        tempMapping = keyMapping;
        this.type = type;
    }
    
    @Override
    public void update() {
        if(Main.InputManager.isPressed("g")){
            drivetrain.driveToPointAndDirection(Vector3f.ZERO, Vector3f.UNIT_X.mult(40), Vector3f.UNIT_XYZ, 0);
        } else {
            float FWR = Main.InputManager.isPressedi(keyMapping.up)-Main.InputManager.isPressedi(keyMapping.down),
                    STR = Main.InputManager.isPressedi(keyMapping.right)-Main.InputManager.isPressedi(keyMapping.left),
                    omega = Main.InputManager.isPressedi(keyMapping.rotateCW)-Main.InputManager.isPressedi(keyMapping.rotateCCW);
            switch(type){
                case FieldCentricSpectatorCam:
                    drivetrain.updateFCSC(FWR, STR, omega);
                    break;
                case FieldCentricBlueDriverCam:
                    drivetrain.updateFCBDC(FWR, STR, omega);
                    break;
                case FieldCentricRedDriverCam:
                    drivetrain.updateFCRDC(FWR, STR, omega);
                    break;
                case RobotCentric:
                    drivetrain.updateRC(FWR, STR, omega);
            }
        }
    }
    
    SwerveKeyMapping keyMapping = SwerveKeyMapping.NULL, tempMapping;
    
    public static class SwerveKeyMapping{
        public final String up, down, left, right, rotateCCW, rotateCW, toggleIntake, shoot, spit, inbound, switchSides;
        public SwerveKeyMapping(final String up, final String down, final String left, final String right, 
                final String rotateCCW, final String rotateCW, final String load, final String shoot, 
                final String spit, final String inbound, final String switchSides){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.rotateCCW = rotateCCW;
            this.rotateCW = rotateCW;
            this.toggleIntake = load;
            this.shoot = shoot;
            this.spit = spit;
            this.inbound = inbound;
            this.switchSides = switchSides;
        }
        
        public final static SwerveKeyMapping wasd = new SwerveKeyMapping("w", "s", "a", "d", "left", "right", "r", "space", "shift", "i", "o");
        public final static SwerveKeyMapping NULL = new SwerveKeyMapping("", "", "", "", "", "", "", "", "", "", "");
    }
    
    public void setKeyMapping(final SwerveKeyMapping src){
        if(keyMapping != SwerveKeyMapping.NULL){
            if(intake != null){
                Main.InputManager.removeListener(keyMapping.toggleIntake);
            }
            if(shooter != null){
                Main.InputManager.removeListener(keyMapping.shoot);
                Main.InputManager.removeListener(keyMapping.spit);
            }
            Main.InputManager.removeListener(keyMapping.inbound);
            Main.InputManager.removeListener(keyMapping.switchSides);
        }
        keyMapping = src;
        if(keyMapping != SwerveKeyMapping.NULL){
            Main.InputManager.addListener(keyMapping.toggleIntake, intake.toggle);
            Main.InputManager.addListener(keyMapping.inbound, new HumanPlayer.ManualInboundRunnable(robot));
            Main.InputManager.addListener(keyMapping.switchSides, new HumanPlayer.SwitchSidesRunnable(alliance));
            if(shooter != null){
                Main.InputManager.addListener(keyMapping.shoot, shooter.shoot);
                Main.InputManager.addListener(keyMapping.spit, shooter.spit);
            }
        }
    }
}

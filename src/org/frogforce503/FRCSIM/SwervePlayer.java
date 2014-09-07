package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;
import java.util.EnumMap;
import org.frogforce503.FRCSIM.HumanPlayer.CloseHumanPlayer;

/**
 * Class that takes player inputs and controls a swerve drivetrain.
 * @author Bryce Paputa
 */
public class SwervePlayer extends AbstractControl{
    private SwerveDrivetrain drivetrain;
    private AbstractIntake intake;
    private AbstractShooter shooter;
    private Robot robot;
    private Alliance alliance;
    private SwerveControlMethod type;
    private SwerveKeyMapping keyMapping = SwerveKeyMapping.NULL, tempMapping;
    
    /**
     * {@inheritDoc}
     */
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return "SwervePlayer";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String detailedToString(String offset) {
        return offset + "SwervePlayer{\n    type: "+type+"\n    keyMapping: "+keyMapping+"\n}";
    }
    
    /**
     * Enum that represents different swerve control methods
     */
    public static enum SwerveControlMethod{
        /**
         * Field centric control from a spectator's viewpoint.
         */
        FieldCentricSpectatorCam(),
        
        /**
         * Field centric control from a blue driver's viewpoint.
         */
        FieldCentricBlueDriverCam(), 
        
        /**
         * Field centric control from a red driver's viewpoint.
         */
        FieldCentricRedDriverCam(), 
        
        /**
         * Robot centric control.
         */
        RobotCentric();
    }
    
    /**
     * Constructor for a swerve player.
     * @param keyMapping    Which key mapping to use
     * @param type          Which control method to use
     */
    public SwervePlayer(final SwerveKeyMapping keyMapping, final SwerveControlMethod type){
        tempMapping = keyMapping;
        this.type = type;
    }
    
    /**
     * {@inheritDoc}
     */
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
    
    /**
     * Class that stores key mappings.
     */
    public static class SwerveKeyMapping{
        /**
         * Moves forwards. 
         */
        public final String up;
        
        /**
         * Moves backwards.
         */
        public final String down;
        
        /**
         * Strafes left.
         */
        public final String left;
        
        /**
         * Strafes right.
         */
        public final String right;
        
        /**
         * Rotates counterclockwise.
         */
        public final String rotateCCW;
        
        /**
         * Rotates clockwise.
         */
        public final String rotateCW;
        
        /**
         * Toggles the intake up and down.
         */
        public final String toggleIntake;
        
        /**
         * Shoots the ball.
         */
        public final String shoot;
        
        /**
         * Spits the ball out.
         */
        public final String spit;
        
        /**
         * Manually inbounds.
         */
        public final String inbound;
        
        /**
         * Tells the inbounder to switch sides.
         */
        public final String switchSides;
        
        /**
         * Constructor for a new key mapping.
         * @param up            Moves forwards
         * @param down          Moves backwards
         * @param left          Strafes left
         * @param right         Strafes right
         * @param rotateCCW     Rotates counterclockwise
         * @param rotateCW      Rotates clockwise
         * @param toggleIntake  Toggles the intake up and down
         * @param shoot         Shoots the ball
         * @param spit          Spits the ball out
         * @param inbound       Manually inbounds
         * @param switchSides   Tells the inbounder to switch sides
         */
        private SwerveKeyMapping(final String up, final String down, final String left, final String right, 
                final String rotateCCW, final String rotateCW, final String toggleIntake, final String shoot, 
                final String spit, final String inbound, final String switchSides){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.rotateCCW = rotateCCW;
            this.rotateCW = rotateCW;
            this.toggleIntake = toggleIntake;
            this.shoot = shoot;
            this.spit = spit;
            this.inbound = inbound;
            this.switchSides = switchSides;
        }
        
        /**
         * Controls the drivetrain with WASD strafing and driving and left and right arrows turning.
         */
        public final static SwerveKeyMapping wasd = new SwerveKeyMapping("w", "s", "a", "d", "left", "right", "r", "space", "shift", "i", "o");
        
        /**
         * Null placeholder.
         */
        public final static SwerveKeyMapping NULL = new SwerveKeyMapping("", "", "", "", "", "", "", "", "", "", "");
    }
    
    /**
     * Sets this player's key mapping.
     * @param src New key mapping
     */
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
            Main.InputManager.addListener(keyMapping.switchSides, new CloseHumanPlayer.SwitchSidesRunnable(alliance));
            if(shooter != null){
                Main.InputManager.addListener(keyMapping.shoot, shooter.shoot);
                Main.InputManager.addListener(keyMapping.spit, shooter.spit);
            }
        }
    }
}

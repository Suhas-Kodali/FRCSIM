package org.frogforce503.FRCSIM;

import java.util.EnumMap;
import org.frogforce503.FRCSIM.HumanPlayer.CloseHumanPlayer;

/**
 * Class that takes player inputs and controls a Tank drivetrain.
 * @author Bryce Paputa
 */
public class TankPlayer extends AbstractControl{
    private TankDrivetrain drivetrain;
    private AbstractIntake intake;
    private AbstractShooter shooter;
    private TankKeyMapping tempMapping;
    private Robot robot;
    private Alliance alliance;
    private final TankControlMethod type;
    private TankKeyMapping keyMapping = TankKeyMapping.NULL;
    
    /**
     * Constructor for a new Tank player
     * @param keyMapping    Key mapping to use
     * @param type          Control method to use
     */
    public TankPlayer(final TankKeyMapping keyMapping, final TankControlMethod type){
        tempMapping = keyMapping;
        this.type = type;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot){
        if(subsystems.get(SubsystemType.Drivetrain) instanceof TankDrivetrain){
            this.drivetrain = (TankDrivetrain) subsystems.get(SubsystemType.Drivetrain);
            this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
            this.shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
            this.alliance = robot.alliance;
        } else {
            throw new IllegalArgumentException("TankPlayer only controls TankDrivetrains");
        }
        this.robot = robot;
        robot.setWantsBall(true);
        setKeyMapping(tempMapping);
    }
    
    /**
     * Enum that represents different tank control methods
     */
    public enum TankControlMethod{
        Arcade(), Tank();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        switch(type){
            case Arcade:
                float power, turn;
                if(keyMapping.joystick == -1){
                    turn = Main.InputManager.isPressedi(keyMapping.left) - Main.InputManager.isPressedi(keyMapping.right);
                    power = Main.InputManager.isPressedi(keyMapping.up) - Main.InputManager.isPressedi(keyMapping.down);
                }else{
                    power = -Main.InputManager.getAxisValue(keyMapping.joystick, 0, .5f);
                    turn = -Main.InputManager.getAxisValue(keyMapping.joystick, 1, .5f);
                }
                drivetrain.updateArcade(power, turn);
                break;
            case Tank:
                float left, right;
                if(keyMapping.joystick == -1){
                    left = Main.InputManager.isPressedi(keyMapping.leftForward) - Main.InputManager.isPressedi(keyMapping.left);
                    right = Main.InputManager.isPressedi(keyMapping.rightForward) - Main.InputManager.isPressedi(keyMapping.right);
                }else{
                    left = -Main.InputManager.getAxisValue(keyMapping.joystick, 0, 0.5f);
                    right = -Main.InputManager.getAxisValue(keyMapping.extraJoystick, 0, 0.5f);
                }
                drivetrain.updateTank(left, right);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return "TankPlayer";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String detailedToString(String offset) {
        return offset + "TankPlayer{\n    type: "+type+"\n    keyMapping: "+keyMapping+"\n}";
    }
    
    /**
     * Class that stores key mappings.
     */
    public static class TankKeyMapping{
        /**
         * Drives forwards.
         */
        public final String up; 
        
        /**
         * Drives backwards.
         */
        public final String down;
        
        /**
         * Turns left in arcade mode, drives the left side backwards in tank mode.
         */
        public final String left;
        
        /**
         * Turns right in arcade mode, drives the right side backwards in tank mode.
         */
        public final String right;
        
        /**
         * Drives the left side forwards.
         */
        public final String leftForward;
        
        /**
         * Drives the right side forwards.
         */
        public final String rightForward;
        
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
         * Manually inbounds the ball.
         */
        public final String inbound;
        
        /**
         * Tells the inbounder to switch sides.
         */
        public final String switchSides;
        
        /**
         * Joystick id.
         */
        public final int joystick, extraJoystick;
        
        /**
         * Constructor for a new key mapping.
         * @param up            Drives forwards
         * @param down          Drives backwards
         * @param left          Turns left in arcade mode, drives the left side backwards in tank mode
         * @param right         Turns right in arcade mode, drives the right side backwards in tank mode
         * @param leftForward   Drives the left side forwards
         * @param rightForward  Drives the right side forwards
         * @param toggleIntake  Toggles the intake up and down
         * @param shoot         Shoots the ball
         * @param spit          Spits the ball out
         * @param inbound       Manually inbounds the ball
         * @param switchSides   Tells the inbounder to switch sides
         * @param joystick      Main joystick id
         * @param extraJoystick Extra joystick id
         */
        private TankKeyMapping(final String up, final String down, final String left, final String right, 
                final String leftForward, final String rightForward, final String toggleIntake, 
                final String shoot, final String spit, final String inbound, final String switchSides, 
                final int joystick, final int extraJoystick){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.leftForward = leftForward;
            this.rightForward = rightForward;
            this.toggleIntake = toggleIntake;
            this.shoot = shoot;
            this.spit = spit;
            this.inbound = inbound;
            this.switchSides = switchSides;
            this.joystick = joystick;
            this.extraJoystick = extraJoystick;
        }
       
        /**
         * Uses WASD or QAED to control the drivetrain.
         */
        public final static TankKeyMapping wasd = new TankKeyMapping("w", "s", "a", "d", "q", "e", "r", "space", "shift", "i", "o", -1, -1);
        
        /**
         * Uses a joystick to control the robot.
         */
        public final static TankKeyMapping joy = new TankKeyMapping("", "", "", "", "", "", "Button 1", "Button 0", "Button 2", "Button 5", "", 0, 1);
        
        /**
         * Null placeholder.
         */
        public final static TankKeyMapping NULL = new TankKeyMapping("", "", "", "","", "", "", "", "", "", "", -1, -1);
    }
    
    /**
     * Sets this player's key mapping.
     * @param src New key mapping
     */
    public void setKeyMapping(final TankKeyMapping src){
        if(keyMapping != TankKeyMapping.NULL){
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
        if(keyMapping != TankKeyMapping.NULL){
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



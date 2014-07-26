package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;
import java.util.EnumMap;

/**
 *
 * @author Bryce Paputa
 */
public class TankPlayer extends AbstractControl{
    TankDrivetrain drivetrain;
    private AbstractIntake intake;
    private AbstractShooter shooter;
    private TankKeyMapping tempMapping;
    private Robot robot;
    private Alliance alliance;
    
    public TankPlayer(TankKeyMapping keyMapping, TankType type){
        tempMapping = keyMapping;
        this.type = type;
    }
    
    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems, Robot robot){
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
    
    public enum TankType{
        arcade(), tank();
    }
    
    private TankType type;
    
    private TankKeyMapping keyMapping = TankKeyMapping.NULL;
    
    @Override
    public void update() {
        switch(type){
            case arcade:
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
            case tank:
                float left, right;
                if(keyMapping.joystick == -1){
                    left = Main.InputManager.isPressedButtonsi(keyMapping.leftForward, keyMapping.left);
                    right = Main.InputManager.isPressedButtonsi(keyMapping.rightForward, keyMapping.right);
                }else{
                    left = -Main.InputManager.getAxisValue(keyMapping.joystick, 0, 0.5f);
                    right = -Main.InputManager.getAxisValue(keyMapping.extraJoystick, 0, 0.5f);
                }
                drivetrain.updateTank(left, right);
        }
    }
    
    public static class TankKeyMapping{
        public final String up, down, left, right, leftForward, rightForward, toggleIntake, shoot, spit, inbound, switchSides;
        public final int joystick, extraJoystick;
        public TankKeyMapping(String up, String down, String left, String right, String leftForward, String rightForward, String toggleIntake, String shoot, String spit, String inbound, String switchSides, int joystick, int extraJoystick){
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
       
        public final static TankKeyMapping std = new TankKeyMapping("up", "down", "left", "right", "pgup", "pgdwn", "shift", "enter", "shift", "p", "o", -1, -1);
        public final static TankKeyMapping wasd = new TankKeyMapping("w", "s", "a", "d", "q", "e", "r", "space", "c", "i", "o", -1, -1);
        public final static TankKeyMapping joy = new TankKeyMapping("", "", "", "", "", "", "Button 1", "Button 0", "Button 2", "Button 5", "", 0, 1);
        public final static TankKeyMapping NULL = new TankKeyMapping("", "", "", "","", "", "", "", "", "", "", -1, -1);
    }
    
    public void setKeyMapping(TankKeyMapping src){
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
            Main.InputManager.addListener(keyMapping.switchSides, new HumanPlayer.SwitchSidesRunnable(alliance));
            if(shooter != null){
                Main.InputManager.addListener(keyMapping.shoot, shooter.shoot);
                Main.InputManager.addListener(keyMapping.spit, shooter.spit);
            }
        }
    }
}



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
        } else {
            throw new IllegalArgumentException("TankPlayer only controls TankDrivetrains");
        }
        setKeyMapping(tempMapping);
        this.robot = robot;
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
                if(Main.InputManager.isPressed("g")){
                    drivetrain.driveTowardsPoint(Vector3f.ZERO);
                    return;
                }
                drivetrain.updateArcade(power, turn);
                break;
            case tank:
                float left, right;
                if(keyMapping.joystick == -1){
                    left = Main.InputManager.isPressedButtonsi(keyMapping.leftForward, keyMapping.left);
                    right = Main.InputManager.isPressedButtonsi(keyMapping.rightForward, keyMapping.right);
                }else{
                    left = Main.InputManager.getAxisValue(keyMapping.joystick, 0, 0.9f);
                    right = Main.InputManager.getAxisValue(keyMapping.extraJoystick, 0, 0.9f);
                }
                drivetrain.updateTank(left, right);
        }
        if(Main.InputManager.isPressed("g")){
            drivetrain.driveTowardsPoint(Vector3f.ZERO);
            return;
        }
    }
    
    public static class TankKeyMapping{
        public final String up, down, left, right, leftForward, rightForward, load, shoot, inbound;
        public final int joystick, extraJoystick;
        public TankKeyMapping(String up, String down, String left, String right, String leftForward, String rightForward, String load, String shoot, String inbound, int joystick, int extraJoystick){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.leftForward = leftForward;
            this.rightForward = rightForward;
            this.load = load;
            this.shoot = shoot;
            this.inbound = inbound;
            this.joystick = joystick;
            this.extraJoystick = extraJoystick;
        }
       
        public final static TankKeyMapping std = new TankKeyMapping("up", "down", "left", "right", "pgup", "pgdwn", "l", "enter", "p", -1, -1);
        public final static TankKeyMapping wasd = new TankKeyMapping("w", "s", "a", "d", "e", "q", "r", "space", "i", -1, -1);
        public final static TankKeyMapping joy = new TankKeyMapping("", "", "", "", "", "", "Button 1", "Button 0", "Button 5", 0, 1);
        public final static TankKeyMapping NULL = new TankKeyMapping("", "", "", "","", "", "", "", "", -1, -1);
    }
    
    public void setKeyMapping(TankKeyMapping src){
        if(keyMapping != TankKeyMapping.NULL){
            if(intake != null){
                Main.InputManager.removeListener(keyMapping.load);
            }
            if(shooter != null){
                Main.InputManager.removeListener(keyMapping.shoot);
            }
            Main.InputManager.removeListener(keyMapping.inbound);
        }
        keyMapping = src;
        if(keyMapping != TankKeyMapping.NULL){
            if(intake != null){
                Main.InputManager.addListener(keyMapping.load, intake.toggle);
            }
            if(shooter != null){
                Main.InputManager.addListener(keyMapping.shoot, shooter.shoot);
            }
            
        }
    }
}


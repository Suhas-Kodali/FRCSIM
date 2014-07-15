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
    
    public TankPlayer(TankKeyMapping keyMapping){
        tempMapping = keyMapping;
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
    
    private TankKeyMapping keyMapping = TankKeyMapping.NULL;
    
    @Override
    public void update() {
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
        drivetrain.update(power, turn);
    }
    
    public static class TankKeyMapping{
        public final String up, down, left, right, load, shoot, spit, inbound;
        public final int joystick;
        public TankKeyMapping(String up, String down, String left, String right, String load, String shoot, String spit, String inbound, int joystick){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.load = load;
            this.shoot = shoot;
            this.spit = spit;
            this.inbound = inbound;
            this.joystick = joystick;
        }
       
        public final static TankKeyMapping std = new TankKeyMapping("up", "down", "left", "right", "pgdwn", "enter", "shift", "p", -1);
        public final static TankKeyMapping wasd = new TankKeyMapping("w", "s", "a", "d", "r", "space", "c", "i", -1);
        public final static TankKeyMapping joy = new TankKeyMapping("", "", "", "", "Button 1", "Button 0", "Button 2", "Button 5", 0);
        public final static TankKeyMapping NULL = new TankKeyMapping("", "", "", "", "", "", "", "", -1);
    }
    
    public void setKeyMapping(TankKeyMapping src){
        if(keyMapping != TankKeyMapping.NULL){
            if(intake != null){
                Main.InputManager.removeListener(keyMapping.load);
            }
            if(shooter != null){
                Main.InputManager.removeListener(keyMapping.shoot);
            }
            if(shooter != null){
                Main.InputManager.removeListener(keyMapping.spit);
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
            if(shooter != null){
                Main.InputManager.addListener(keyMapping.spit, shooter.spit);
            }
        }
    }
}


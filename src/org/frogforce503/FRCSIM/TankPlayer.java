package org.frogforce503.FRCSIM;

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
    
    public TankPlayer(TankKeyMapping keyMapping){
        tempMapping = keyMapping;
    }
    
    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems){
        if(subsystems.containsKey(SubsystemType.Drivetrain) && subsystems.containsKey(SubsystemType.Intake) && subsystems.containsKey(SubsystemType.Shooter)){
            if(subsystems.get(SubsystemType.Drivetrain) instanceof TankDrivetrain){
                this.drivetrain = (TankDrivetrain) subsystems.get(SubsystemType.Drivetrain);
                this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
                this.shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
            } else {
                throw new Error();
            }
        } else {
            throw new Error();
        }
        setKeyMapping(tempMapping);
    }
    
    private TankKeyMapping keyMapping = TankKeyMapping.NULL;
    
    @Override
    public void update() {
        float cleft = Main.InputManager.isPressedi(keyMapping.left),
                cright = Main.InputManager.isPressedi(keyMapping.right),
                cup = Main.InputManager.isPressedi(keyMapping.up),
                cdown = Main.InputManager.isPressedi(keyMapping.down);
        drivetrain.update(cup, cdown, cleft, cright);
    }
    
    public static class TankKeyMapping{
        public final String up, down, left, right, load, shoot, inbound;
        public TankKeyMapping(String up, String down, String left, String right, String load, String shoot, String inbound){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.load = load;
            this.shoot = shoot;
            this.inbound = inbound;
        }
        public final static TankKeyMapping std = new TankKeyMapping("up", "down", "left", "right", "pgdwn", "enter", "p");
        public final static TankKeyMapping wasd = new TankKeyMapping("w", "s", "a", "d", "r", "space", "i");
        public final static TankKeyMapping NULL = new TankKeyMapping("", "", "", "", "", "", "");
    }
    
    public void setKeyMapping(TankKeyMapping src){
        if(keyMapping != TankKeyMapping.NULL){
            Main.InputManager.removeListener(keyMapping.load);
            Main.InputManager.removeListener(keyMapping.shoot);
            Main.InputManager.removeListener(keyMapping.inbound);
        }
        keyMapping = src;
        if(keyMapping != TankKeyMapping.NULL){
            Main.InputManager.addListener(keyMapping.load, intake.toggle);
            Main.InputManager.addListener(keyMapping.shoot, shooter.shoot);
            Main.InputManager.addListener(keyMapping.inbound, Main.field.redHumanPlayer.action);
            
        }
    }
}

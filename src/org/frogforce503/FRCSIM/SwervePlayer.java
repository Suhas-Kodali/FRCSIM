package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.EnumMap;

/**
 *
 * @author Bryce
 */
public class SwervePlayer extends AbstractControl{
    private SwerveDrivetrain drivetrain;
    private AbstractIntake intake;
    private AbstractShooter shooter;

    @Override
    public void registerOtherSubsystems(EnumMap<SubsystemType, AbstractSubsystem> subsystems) {
        if(subsystems.get(SubsystemType.Drivetrain) instanceof SwerveDrivetrain){
            this.drivetrain = (SwerveDrivetrain) subsystems.get(SubsystemType.Drivetrain);
            this.intake = (AbstractIntake) subsystems.get(SubsystemType.Intake);
            this.shooter = (AbstractShooter) subsystems.get(SubsystemType.Shooter);
        } else {
            throw new IllegalArgumentException("SwervePlayer only controls SwerveDrivetrains");
        }
        setKeyMapping(tempMapping);
    }
    
    public static enum SwerveType{
        FieldCentric(), RobotCentric();
    }
    
    private SwerveType type;
    
    public SwervePlayer(SwerveKeyMapping keyMapping, SwerveType type){
        tempMapping = keyMapping;
        this.type = type;
    }
    
    @Override
    public void update() {
        float FWR = Main.InputManager.isPressedi(keyMapping.up)-Main.InputManager.isPressedi(keyMapping.down),
                STR = Main.InputManager.isPressedi(keyMapping.right)-Main.InputManager.isPressedi(keyMapping.left),
                omega = Main.InputManager.isPressedi(keyMapping.rotateCW)-Main.InputManager.isPressedi(keyMapping.rotateCCW);
        switch(type){
            case FieldCentric:
                drivetrain.updateFC(FWR, STR, omega);
                break;
            case RobotCentric:
                drivetrain.updateRC(FWR, STR, omega);
        }
    }
    
    SwerveKeyMapping keyMapping = SwerveKeyMapping.NULL, tempMapping;
    
    public static class SwerveKeyMapping{
        public final String up, down, left, right, rotateCCW, rotateCW, load, shoot, inbound;
        public SwerveKeyMapping(String up, String down, String left, String right, String rotateCCW, String rotateCW, String load, String shoot, String inbound){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.rotateCCW = rotateCCW;
            this.rotateCW = rotateCW;
            this.load = load;
            this.shoot = shoot;
            this.inbound = inbound;
        }
        public final static SwerveKeyMapping std = new SwerveKeyMapping("up", "down", "left", "right", "a", "d", "r", "space", "p");
        public final static SwerveKeyMapping wasd = new SwerveKeyMapping("w", "s", "a", "d", "left", "right", "r", "space", "i");
        public final static SwerveKeyMapping NULL = new SwerveKeyMapping("", "", "", "", "", "", "", "", "");
    }
    
    public void setKeyMapping(SwerveKeyMapping src){
        if(keyMapping != SwerveKeyMapping.NULL){
            if(intake != null){
                Main.InputManager.removeListener(keyMapping.load);
            }
            if(shooter != null){
                Main.InputManager.removeListener(keyMapping.shoot);
            }
            Main.InputManager.removeListener(keyMapping.inbound);
        }
        keyMapping = src;
        if(keyMapping != SwerveKeyMapping.NULL){
            if(intake != null){
                Main.InputManager.addListener(keyMapping.load, intake.toggle);
            }
            if(shooter != null){
                Main.InputManager.addListener(keyMapping.shoot, shooter.shoot);
            }
        }
    }
    
}

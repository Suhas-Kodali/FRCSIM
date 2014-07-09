package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Bryce
 */
public class SwervePlayer extends SwerveRobot{
    
    public static enum SwerveType{
        FieldCentric(), RobotCentric();
    }
    
    private SwerveType type;
    
    public SwervePlayer(Node rootNode, PhysicsSpace physicsSpace, Alliance alliance, SwervePlayer.SwerveKeyMapping keyMapping, Vector3f pos, SwerveType type){
        super(rootNode, physicsSpace, alliance);
        setKeyMapping(keyMapping);
        //setPhysicsLocation(pos);
        this.type = type;
    }
    
    @Override
    public void update() {
        float FWR = Main.InputManager.isPressedi(keyMapping.up)-Main.InputManager.isPressedi(keyMapping.down),
                STR = Main.InputManager.isPressedi(keyMapping.right)-Main.InputManager.isPressedi(keyMapping.left),
                omega = Main.InputManager.isPressedi(keyMapping.rotateCW)-Main.InputManager.isPressedi(keyMapping.rotateCCW);
        switch(type){
            case FieldCentric:
                super.updateFC(FWR, STR, omega);
                break;
            case RobotCentric:
                super.updateRC(FWR, STR, omega);
        }
    }

    @Override
    public void setPhysicsLocation(Vector3f pos) {
    }
    
    SwerveKeyMapping keyMapping = SwerveKeyMapping.NULL;
    
    public static class SwerveKeyMapping{
        public final String up, down, left, right, rotateCCW, rotateCW, load, shoot;
        public SwerveKeyMapping(String up, String down, String left, String right, String rotateCCW, String rotateCW, String load, String shoot){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.rotateCCW = rotateCCW;
            this.rotateCW = rotateCW;
            this.load = load;
            this.shoot = shoot;
        }
        public final static SwerveKeyMapping std = new SwerveKeyMapping("up", "down", "left", "right", "a", "d", "r", "space");
        public final static SwerveKeyMapping wasd = new SwerveKeyMapping("w", "s", "a", "d", "left", "right", "r", "space");
        public final static SwerveKeyMapping NULL = new SwerveKeyMapping("", "", "", "", "", "", "", "");
    }
    
    public void setKeyMapping(SwerveKeyMapping src){
        if(keyMapping != SwerveKeyMapping.NULL){
            Main.InputManager.removeListener(keyMapping.load);
            Main.InputManager.removeListener(keyMapping.shoot);
        }
        keyMapping = src;
        if(keyMapping != SwerveKeyMapping.NULL){
            Main.InputManager.addListener(keyMapping.load, intake.toggle);
            Main.InputManager.addListener(keyMapping.shoot, shooter.shoot);
        }
    }
    
}

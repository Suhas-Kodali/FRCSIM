package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Bryce
 */
public class SwervePlayer extends SwerveRobot{

    public SwervePlayer(Node rootNode, PhysicsSpace physicsSpace, Alliance alliance, SwervePlayer.SwerveKeyMapping keyMapping, Vector3f pos){
        super(rootNode, physicsSpace, alliance);
        setKeyMapping(keyMapping);
        //setPhysicsLocation(pos);
    }
    
    @Override
    public void update() {
        float FWR = Main.InputManager.isPressedi(keyMapping.up)-Main.InputManager.isPressedi(keyMapping.down),
                STR = Main.InputManager.isPressedi(keyMapping.left)-Main.InputManager.isPressedi(keyMapping.right),
                omega = Main.InputManager.isPressedi(keyMapping.rotateCW)-Main.InputManager.isPressedi(keyMapping.rotateCCW);
        super.update(FWR, STR, omega);
    }

    @Override
    public void setPhysicsLocation(Vector3f pos) {
    }
    
    SwerveKeyMapping keyMapping;
    
    public static class SwerveKeyMapping{
        public final String up, down, left, right, rotateCCW, rotateCW;
        public SwerveKeyMapping(String up, String down, String left, String right, String rotateCCW, String rotateCW){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.rotateCCW = rotateCCW;
            this.rotateCW = rotateCW;
        }
        public final static SwerveKeyMapping std = new SwerveKeyMapping("up", "down", "left", "right", "a", "d");
        public final static SwerveKeyMapping wasd = new SwerveKeyMapping("w", "s", "a", "d", "left", "right");
        public final static SwerveKeyMapping NULL = new SwerveKeyMapping("", "", "", "", "", "");
    }
    
    public void setKeyMapping(SwerveKeyMapping src){
        //if(keyMapping != SwerveKeyMapping.NULL){
        //    Main.InputManager.removeListener(keyMapping.load);
        //    Main.InputManager.removeListener(keyMapping.shoot);
        //}
        keyMapping = src;
        //if(keyMapping != SwerveKeyMapping.NULL){
        //    Main.InputManager.addListener(keyMapping.load, toggleIntake);
        //    Main.InputManager.addListener(keyMapping.shoot, shoot);
        //}
    }
    
}

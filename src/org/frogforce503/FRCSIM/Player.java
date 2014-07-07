package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Bryce Paputa
 */
public class Player extends Robot{
    
    public Player(Node rootNode, PhysicsSpace physicsSpace, Alliance alliance, KeyMapping keyMapping, Vector3f pos){
        super(rootNode, physicsSpace, alliance);
        setKeyMapping(keyMapping);
        setPhysicsLocation(pos);
    }
    
    private KeyMapping keyMapping = KeyMapping.NULL;
    
    @Override
    public void update() {
        float cleft = Main.InputManager.isPressedi(keyMapping.left),
                cright = Main.InputManager.isPressedi(keyMapping.right),
                cup = Main.InputManager.isPressedi(keyMapping.up),
                cdown = Main.InputManager.isPressedi(keyMapping.down);
        super.update(cup, cdown, cleft, cright);
    }
    
    public static class KeyMapping{
        public final String up, down, left, right, load, shoot;
        public KeyMapping(String up, String down, String left, String right, String load, String shoot){
            this.up = up;
            this.down = down;
            this.left = left;
            this.right = right;
            this.load = load;
            this.shoot = shoot;
        }
        public final static KeyMapping std = new KeyMapping("up", "down", "left", "right", "pgdwn", "enter");
        public final static KeyMapping wasd = new KeyMapping("w", "s", "a", "d", "r", "space");
        public final static KeyMapping NULL = new KeyMapping("", "", "", "", "", "");
    }
    
    public void setKeyMapping(KeyMapping src){
        if(keyMapping != KeyMapping.NULL){
            Main.InputManager.removeListener(keyMapping.load);
            Main.InputManager.removeListener(keyMapping.shoot);
        }
        keyMapping = src;
        if(keyMapping != KeyMapping.NULL){
            Main.InputManager.addListener(keyMapping.load, toggleIntake);
            Main.InputManager.addListener(keyMapping.shoot, shoot);
        }
    }
}

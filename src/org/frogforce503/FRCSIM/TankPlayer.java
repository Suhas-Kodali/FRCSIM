package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Bryce Paputa
 */
public class TankPlayer extends TankRobot{
    
    public TankPlayer(Node rootNode, PhysicsSpace physicsSpace, Alliance alliance, TankKeyMapping keyMapping, Vector3f pos){
        super(rootNode, physicsSpace, alliance);
        setKeyMapping(keyMapping);
        setPhysicsLocation(pos);
    }
    
    private TankKeyMapping keyMapping = TankKeyMapping.NULL;
    
    @Override
    public void update() {
        float cleft = Main.InputManager.isPressedi(keyMapping.left),
                cright = Main.InputManager.isPressedi(keyMapping.right),
                cup = Main.InputManager.isPressedi(keyMapping.up),
                cdown = Main.InputManager.isPressedi(keyMapping.down);
        super.update(cup, cdown, cleft, cright);
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
        }
        keyMapping = src;
        if(keyMapping != TankKeyMapping.NULL){
            Main.InputManager.addListener(keyMapping.load, intake.toggle);
            Main.InputManager.addListener(keyMapping.shoot, shooter.shoot);
        }
    }
}

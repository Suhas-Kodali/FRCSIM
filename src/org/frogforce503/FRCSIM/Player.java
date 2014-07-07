package org.frogforce503.FRCSIM;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.cinematic.MotionPath;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import static org.frogforce503.FRCSIM.Main.in;

/**
 *
 * @author Bryce Paputa
 */
public class Player extends Robot{
    
    public Player(Node rootNode, PhysicsSpace physicsSpace, Alliance alliance, AssetManager assetManager){
        super(rootNode, physicsSpace, alliance, assetManager);
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

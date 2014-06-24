package org.frogforce503.FRCSIM;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import org.frogforce503.FRCSIM.Player.Alliance;

/**
 *
 * @author Bryce Paputa
 */
public class Main extends SimpleApplication implements ActionListener {

    public static Material red, black, blue, green, darkGray;
    private BulletAppState bulletAppState;
    private Player player1, player2;
    private Ball ball;
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        red = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        red.setBoolean("UseMaterialColors", true); 
        red.setColor("Ambient", ColorRGBA.Red); 
        red.setColor("Diffuse", ColorRGBA.Red); 
        black = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        black.setBoolean("UseMaterialColors", true); 
        black.setColor("Ambient", ColorRGBA.Black); 
        black.setColor("Diffuse", ColorRGBA.Black); 
        blue = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        blue.setBoolean("UseMaterialColors", true); 
        blue.setColor("Ambient", ColorRGBA.Blue); 
        blue.setColor("Diffuse", ColorRGBA.Blue); 
        green = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        green.setBoolean("UseMaterialColors", true); 
        green.setColor("Ambient", ColorRGBA.Green); 
        green.setColor("Diffuse", ColorRGBA.Green); 
        darkGray = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        darkGray.setBoolean("UseMaterialColors", true); 
        darkGray.setColor("Ambient", ColorRGBA.DarkGray); 
        darkGray.setColor("Diffuse", ColorRGBA.DarkGray); 
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        new Field(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        setupKeys();
        player1 = new Player(rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED);
        player1.setKeyMapping(Player.KeyMapping.wasd);
        player2 = new Player(rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE);
        player2.setKeyMapping(Player.KeyMapping.std);
        ball = new Ball(black, rootNode, bulletAppState.getPhysicsSpace());
        cam.lookAt(player1.getPhysicsLocation(), Vector3f.UNIT_Y);
    }

    private PhysicsSpace getPhysicsSpace(){
        return bulletAppState.getPhysicsSpace();
    }

    private void setupKeys() {
        flyCam.setEnabled(false);
        
        inputManager.addMapping("a", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("b", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("c", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addMapping("d", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("e", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("f", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("g", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("h", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("i", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("j", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("k", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("l", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("m", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("n", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addMapping("o", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping("p", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("q", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("r", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("s", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("t", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("u", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("v", new KeyTrigger(KeyInput.KEY_V));
        inputManager.addMapping("w", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("x", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("y", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("z", new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("enter", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "a");
        inputManager.addListener(this, "b");
        inputManager.addListener(this, "c");
        inputManager.addListener(this, "d");
        inputManager.addListener(this, "e");
        inputManager.addListener(this, "f");
        inputManager.addListener(this, "g");
        inputManager.addListener(this, "h");
        inputManager.addListener(this, "i");
        inputManager.addListener(this, "j");
        inputManager.addListener(this, "k");
        inputManager.addListener(this, "l");
        inputManager.addListener(this, "m");
        inputManager.addListener(this, "n");
        inputManager.addListener(this, "o");
        inputManager.addListener(this, "p");
        inputManager.addListener(this, "q");
        inputManager.addListener(this, "r");
        inputManager.addListener(this, "s");
        inputManager.addListener(this, "t");
        inputManager.addListener(this, "u");
        inputManager.addListener(this, "v");
        inputManager.addListener(this, "w");
        inputManager.addListener(this, "x");
        inputManager.addListener(this, "y");
        inputManager.addListener(this, "z");
        inputManager.addListener(this, "left");
        inputManager.addListener(this, "right");
        inputManager.addListener(this, "up");
        inputManager.addListener(this, "down");
        inputManager.addListener(this, "space");
        inputManager.addListener(this, "enter");
    }
    
    public static float in(float in){
        return in/39.3701f;
    }    

    @Override
    public void simpleUpdate(float tpf) {
        //cam.lookAt(player.getPhysicsLocation(), Vector3f.UNIT_Y);
        player1.update();
        player2.update();
    }
    
    public static class InputManager{
        private static ArrayList<String> pressed = new ArrayList<String>();
        
        public static void press(String key){
            pressed.add(key);
        }
        
        public static void release(String key){
            pressed.remove(key);
        }
        
        public static boolean isPressed(String key){
            return pressed.contains(key);
        }
    }
    
    public void onAction(String binding, boolean value, float tpf) {
        if(value == true){
            InputManager.press(binding);
        } else {
            InputManager.release(binding);
        }
        if (binding.equals("space")) {
            if (value) {
                player1.jump();
                player2.jump();
            }
        } else if (binding.equals("enter")) {
            if (value) {
                player1.reset();
                player2.reset();
            }
        }
        System.out.println(binding);
    }
}

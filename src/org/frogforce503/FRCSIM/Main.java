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
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import org.frogforce503.FRCSIM.Alliance;

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
        AppSettings appSettings = new AppSettings(true);
        appSettings.setSettingsDialogImage("Textures/first-vertical.png");
        appSettings.setUseJoysticks(true);
        appSettings.setResolution(1280, 768);
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        app.setSettings(appSettings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        red = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        red.getAdditionalRenderState().setWireframe(false);
        red.setBoolean("UseMaterialColors", true); 
        red.setColor("Ambient", ColorRGBA.Red); 
        red.setColor("Diffuse", ColorRGBA.Red); 
        black = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        black.getAdditionalRenderState().setWireframe(false);
        black.setBoolean("UseMaterialColors", true); 
        black.setColor("Ambient", ColorRGBA.Black); 
        black.setColor("Diffuse", ColorRGBA.Black); 
        blue = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        blue.getAdditionalRenderState().setWireframe(false);
        blue.setBoolean("UseMaterialColors", true); 
        blue.setColor("Ambient", ColorRGBA.Blue); 
        blue.setColor("Diffuse", ColorRGBA.Blue); 
        green = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        green.getAdditionalRenderState().setWireframe(false);
        green.setBoolean("UseMaterialColors", true); 
        green.setColor("Ambient", ColorRGBA.Green); 
        green.setColor("Diffuse", ColorRGBA.Green); 
        darkGray = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        darkGray.getAdditionalRenderState().setWireframe(false);
        darkGray.setBoolean("UseMaterialColors", true); 
        darkGray.setColor("Ambient", ColorRGBA.DarkGray); 
        darkGray.setColor("Diffuse", ColorRGBA.DarkGray); 
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        new Field(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        setupKeys();
        player1 = new Player(rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED);
        player1.setKeyMapping(Player.KeyMapping.wasd);
        player2 = new Player(rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE);
        player2.setKeyMapping(Player.KeyMapping.std);
        ball = new Ball(rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE);
        cam.setLocation(new Vector3f(0,4,12));
        cam.lookAt(new Vector3f(0,-5,0), Vector3f.UNIT_Y);
        
    }

    private PhysicsSpace getPhysicsSpace(){
        return bulletAppState.getPhysicsSpace();
    }

    private void setupKeys() {
        flyCam.setEnabled(false);
        int[] keys = new int[]{KeyInput.KEY_A,KeyInput.KEY_B,KeyInput.KEY_C,
                KeyInput.KEY_D,KeyInput.KEY_E,KeyInput.KEY_F,KeyInput.KEY_G,
                KeyInput.KEY_H,KeyInput.KEY_I,KeyInput.KEY_J,KeyInput.KEY_K,
                KeyInput.KEY_L,KeyInput.KEY_M,KeyInput.KEY_N,KeyInput.KEY_O,
                KeyInput.KEY_P,KeyInput.KEY_Q,KeyInput.KEY_R,KeyInput.KEY_S,
                KeyInput.KEY_T,KeyInput.KEY_U,KeyInput.KEY_V,KeyInput.KEY_W,
                KeyInput.KEY_X,KeyInput.KEY_Y,KeyInput.KEY_Z};
        for(char i = 0; i+'a' <= 'z'; i++){
            inputManager.addMapping(new String(new char[]{(char)(i+'a')}), new KeyTrigger(keys[i]));
            inputManager.addListener(this, new String(new char[]{(char)(i+'a')}));
        }
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("enter", new KeyTrigger(KeyInput.KEY_RETURN));
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
    }
}

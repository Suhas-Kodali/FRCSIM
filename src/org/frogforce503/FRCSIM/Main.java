package org.frogforce503.FRCSIM;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.Joystick;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.HashMap;
import org.frogforce503.FRCSIM.AI.TestAI;
import org.frogforce503.FRCSIM.SwervePlayer.SwerveType;

/**
 *
 * @author Bryce Paputa
 */
public class Main extends SimpleApplication implements ActionListener {

    public static Material red, black, blue, green, darkGray, cage;
    public static Scene scene;
    public static Field field;
    public static Main app;
    public static BulletAppState bulletAppState;
    public static boolean isStarted = false;
    public static void main(String[] args) {
        app = new Main();
        AppSettings appSettings = new AppSettings(true);
        appSettings.setSettingsDialogImage("Textures/first-vertical.png");
        appSettings.setUseJoysticks(true);
        appSettings.setResolution(1280, 600);
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        app.setSettings(appSettings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        red = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        red.getAdditionalRenderState().setWireframe(false);
        red.setColor("Color", ColorRGBA.Red); 
        black = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        black.getAdditionalRenderState().setWireframe(false);
        black.setColor("Color", ColorRGBA.Black); 
        cage = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        cage.getAdditionalRenderState().setWireframe(false);
        cage.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        TextureKey key = new TextureKey("Textures/cage.png");
        Texture tex = assetManager.loadTexture(key);
        cage.setTexture("ColorMap", tex);
        blue = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        blue.getAdditionalRenderState().setWireframe(false);
        blue.setColor("Color", ColorRGBA.Blue);
        green = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        green.getAdditionalRenderState().setWireframe(false);
        green.setColor("Color", ColorRGBA.Green); 
        darkGray = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        darkGray.getAdditionalRenderState().setWireframe(false);
        darkGray.setColor("Color", ColorRGBA.DarkGray); 
        scene = new Scene(assetManager, inputManager, audioRenderer, guiViewPort, flyCam);
        scene.initScreens();
        scene.startScreen();
        
        
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        field = new Field(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        setupKeys();
        
        //new TankPlayer(rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED, TankPlayer.TankKeyMapping.std, Vector3f.ZERO);
        //new TankPlayer(rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE, TankPlayer.TankKeyMapping.wasd, new Vector3f(1,0,1));
        //new SwervePlayer(rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED, SwervePlayer.SwerveKeyMapping.std, new Vector3f(1,0,1), SwervePlayer.SwerveType.FieldCentric);
        AbstractSubsystem drivetrain = new TankDrivetrain(), 
                shooter = new BasicShooter(), 
                intake = new BasicIntake(),
                control = new TankPlayer(TankPlayer.TankKeyMapping.wasd);
        AbstractSubsystem[] subsystems = new AbstractSubsystem[]{drivetrain, intake, control, shooter};
        Robot player = new Robot(subsystems, rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED, new Vector3f(0,0,0));
        AbstractSubsystem aidrivetrain = new TankDrivetrain(), 
                aicontrol = new TestAI(player);
        AbstractSubsystem[] aisubsystems = new AbstractSubsystem[]{aidrivetrain, aicontrol};
        new Robot(aisubsystems, rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE, new Vector3f(3,0,3));
        
        new Ball(rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED);
        
        cam.setLocation(new Vector3f(0, 12, 12));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        
        isStarted = true;
    }

    private void setupKeys() {
        
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
        inputManager.addMapping("pgdwn", new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addListener(this, "left");
        inputManager.addListener(this, "right");
        inputManager.addListener(this, "up");
        inputManager.addListener(this, "down");
        inputManager.addListener(this, "space");
        inputManager.addListener(this, "enter");
        inputManager.addListener(this, "pgdwn");
        Joystick[] joysticks = inputManager.getJoysticks();
        for(Joystick joystick : joysticks){
            InputManager.addJoystick(joystick.getJoyId(), 0, 1);
        }
        
        inputManager.addRawInputListener( new JoystickEventManager() );
    }
    
    public static float in(float in){
        return in/39.3701f;
    }    

    @Override
    public void simpleUpdate(float tpf) {
        if(isStarted){
            Robot.updateAll();
            Ball.updateAll();
            HumanPlayer.updateAll();
            field.update();
            scene.update();
        }
    }
    
    public static final class JoystickEventManager implements RawInputListener{

        public void beginInput() {}

        public void endInput() {}

        public void onJoyAxisEvent(JoyAxisEvent evt) {
            InputManager.joystickAxisEvent(evt.getJoyIndex(), evt.getAxisIndex(), evt.getValue());
        }

        public void onJoyButtonEvent(JoyButtonEvent evt) {
            if(evt.isPressed()){
            InputManager.press(evt.getButton().getName());
            }else{
                InputManager.release(evt.getButton().getName());
            }
        }

        public void onMouseMotionEvent(MouseMotionEvent evt) {}

        public void onMouseButtonEvent(MouseButtonEvent evt) {}

        public void onKeyEvent(KeyInputEvent evt) {}

        public void onTouchEvent(TouchEvent evt) {}
        
    }
    
    public static final class InputManager{
        private InputManager() {}
        private static ArrayList<HashMap<Integer, Float>> axisMaps = new ArrayList<HashMap<Integer, Float>>();
        private static ArrayList<String> pressed = new ArrayList<String>();
        private static HashMap<String, Runnable> listeners = new HashMap<String, Runnable>();
        
        public static void press(String key){
            pressed.add(key);
            if(listeners.containsKey(key)){
                listeners.get(key).run();
            }
        }
        
        public static void release(String key){
            pressed.remove(key);
        }
        
        public static boolean isPressed(String key){
            return pressed.contains(key);
        }
        
        public static void addListener(String key, Runnable function){
            listeners.put(key, function);
        }
        
        public static void removeListener(String key){
            listeners.remove(key);
        }
        
        public static void addJoystick(int id, int upDownAxis, int leftRightAxis){
            HashMap<Integer, Float> axes = new HashMap();
            axes.put(upDownAxis, 0f);
            axes.put(leftRightAxis, 0f);
            axisMaps.add(id, axes);
        }
        
        public static void joystickAxisEvent(int id, int axis, float value){
            if(axisMaps.get(id).get(axis) != null){
                axisMaps.get(id).put(axis, value);
            }
        }
        
        public static float getAxisValue(int id, int axis){
            if(id == -1){
                return 0;
            }
            return axisMaps.get(id).get(axis);
        }
        
        public static int isPressedi(String key){
            return (pressed.contains(key)?1:0);
        }
    }
    
    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if(value == true){
            InputManager.press(binding);
        } else {
            InputManager.release(binding);
        }
    }
    
    public static Node getRoot(){
        return app.rootNode;
    }
}

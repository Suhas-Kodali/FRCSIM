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
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.HashMap;
import org.frogforce503.FRCSIM.AI.OffensiveAITest;
import org.frogforce503.FRCSIM.SwervePlayer.SwerveKeyMapping;
import org.frogforce503.FRCSIM.SwervePlayer.SwerveType;
import org.frogforce503.FRCSIM.TankPlayer.TankKeyMapping;
import org.frogforce503.FRCSIM.TankPlayer.TankType;

/**
 *
 * @author Bryce Paputa
 */
public class Main extends SimpleApplication implements ActionListener {

    public static Material red, black, blue, green, darkGray, allianceWalls, sides, chassis;
    public static Field field;
    public static Main app;
    public static BulletAppState bulletAppState;
    public static boolean isStarted = false;
    public static Scene scene;
    public static TankKeyMapping keyMapping;
    public static Joystick[] joysticks;
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
    private Robot player;

    @Override
    public void simpleInitApp() {
        
        initMaterials();
        setupKeys();
        
        
        scene = new Scene(assetManager, inputManager, audioRenderer, guiViewPort, flyCam);
        
        scene.initScreens();
        scene.startScreen();
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
    }

    
    public static float in(float in){
        return in/39.3701f;
    }
    
    public void initMaterials(){
        red = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        red.getAdditionalRenderState().setWireframe(false);
        red.setColor("Color", ColorRGBA.Red); 
        black = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        black.getAdditionalRenderState().setWireframe(false);
        black.setColor("Color", ColorRGBA.Black); 
        
        allianceWalls = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        allianceWalls.getAdditionalRenderState().setWireframe(false);
        allianceWalls.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        TextureKey key = new TextureKey("Textures/goalTest.png");
        Texture tex = assetManager.loadTexture(key);
        allianceWalls.setTexture("ColorMap", tex);
        allianceWalls.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        
        chassis = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        chassis.getAdditionalRenderState().setWireframe(false);
        chassis.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        TextureKey keyChassis = new TextureKey("Textures/cage.png");
        Texture texChassis = assetManager.loadTexture(keyChassis);
        chassis.setTexture("ColorMap", texChassis);
        chassis.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        
        sides = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        sides.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        TextureKey key1 = new TextureKey("Textures/fieldSides.png");
        Texture tex1 = assetManager.loadTexture(key1);
        sides.setTexture("ColorMap", tex1);
        sides.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        
        blue = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        blue.getAdditionalRenderState().setWireframe(false);
        blue.setColor("Color", ColorRGBA.Blue);
        green = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        green.getAdditionalRenderState().setWireframe(false);
        green.setColor("Color", ColorRGBA.Green); 
        darkGray = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        darkGray.getAdditionalRenderState().setWireframe(false);
        darkGray.setColor("Color", ColorRGBA.DarkGray); 
       
    }
    
    public void startGame(){
        field = new Field(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        AbstractSubsystem shooter = new BasicShooter(), 
                intake = new BasicIntake(),
                control = new TankPlayer(keyMapping, TankType.tank);
        ArrayList<AbstractSubsystem> subsystems1 = new ArrayList<AbstractSubsystem>(4);
        subsystems1.add(shooter);
        subsystems1.add(intake);
        subsystems1.add(control);
        AbstractDrivetrain drivetrain = new TankDrivetrain(subsystems1, bulletAppState.getPhysicsSpace());
        subsystems1.add(drivetrain);
        player = new Robot(subsystems1, rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED, new Vector3f(0,0,0));
        new Ball(rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED);
        //new Ball(rootN
        //new Ball(rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE);
//        AbstractSubsystem drivetrain = new TankDrivetrain(), 
//                shooter = new BasicShooter(), 
//                intake = new BasicIntake(),
//                control = new TankPlayer(TankPlayer.TankKeyMapping.wasd);
//        AbstractSubsystem[] subsystems = new AbstractSubsystem[]{drivetrain, intake, control, shooter};
//        Robot player = new Robot(subsystems, rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED, new Vector3f(0,0,0));
//        AbstractSubsystem aidrivetrain = new TankDrivetrain(), 
//                aicontrol = new TestAI(player);
//        AbstractSubsystem[] aisubsystems = new AbstractSubsystem[]{aidrivetrain, aicontrol};
//        new Robot(aisubsystems, rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE, new Vector3f(3,0,3));

    
       
        
        ArrayList<ArrayList<AbstractSubsystem>> subsystems = new ArrayList<ArrayList<AbstractSubsystem>>(6);
        for(int i = 0; i < 4; i++){
            subsystems.add(i, new ArrayList<AbstractSubsystem>(4));
            subsystems.get(i).add(new BasicIntake());
            subsystems.get(i).add(new BasicShooter());
        }
        subsystems.get(0).add(new TankPlayer(keyMapping, TankType.arcade));
        //subsystems.get(0).add(new OffensiveAITest());        
        subsystems.get(0).add(new TankDrivetrain(subsystems.get(0), bulletAppState.getPhysicsSpace()));
        player = new Robot(subsystems.get(0), rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED, new Vector3f(3,0,3));
//        subsystems.get(1).add(new OffensiveAITest());
//        subsystems.get(1).add(new SwerveDrivetrain(subsystems.get(1), bulletAppState.getPhysicsSpace()));
//        new Robot(subsystems.get(1), rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED, new Vector3f(0,0,3));
//        subsystems.get(2).add(new OffensiveAITest());
//        subsystems.get(2).add(new TankDrivetrain(subsystems.get(2), bulletAppState.getPhysicsSpace()));
//        new Robot(subsystems.get(2), rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED, new Vector3f(-3,0,3));
        //subsystems.get(3).add(new OffensiveAITest());
       // subsystems.get(3).add(new SwervePlayer(SwerveKeyMapping.wasd, SwerveType.FieldCentric));
        //subsystems.get(3).add(new BoxSubsystem(Main.in(28), Main.in(28), Main.in(60)));
        //subsystems.get(3).add(new SwerveDrivetrain(subsystems.get(3), bulletAppState.getPhysicsSpace()));
        //new Robot(subsystems.get(3), rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE, new Vector3f(3,0,-3));
//        subsystems.get(4).add(new OffensiveAITest());
//        subsystems.get(4).add(new SwerveDrivetrain(subsystems.get(4), bulletAppState.getPhysicsSpace()));
//        new Robot(subsystems.get(4), rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE, new Vector3f(0,0,-3));
//        subsystems.get(5).add(new OffensiveAITest());
//        subsystems.get(5).add(new TankDrivetrain(subsystems.get(5), bulletAppState.getPhysicsSpace()));
//        new Robot(subsystems.get(5), rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE, new Vector3f(-3,0,-3));
        
        cam.setLocation(new Vector3f(0, 5, 12));
        cam.lookAt(player.getPosition(), Vector3f.UNIT_Y);
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(20);
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
        inputManager.addMapping("pgup", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("shift", new KeyTrigger(KeyInput.KEY_RSHIFT));
        inputManager.addListener(this, "left");
        inputManager.addListener(this, "right");
        inputManager.addListener(this, "up");
        inputManager.addListener(this, "down");
        inputManager.addListener(this, "space");
        inputManager.addListener(this, "enter");
        inputManager.addListener(this, "pgdwn");
        inputManager.addListener(this, "pgup");
        inputManager.addListener(this, "shift");
        joysticks = inputManager.getJoysticks();
        for(Joystick joystick : joysticks){
            if(!InputManager.joysticks.contains(joystick)){
                InputManager.addJoystick(joystick, 0, 1);
            }
        }
        
        inputManager.addRawInputListener( new JoystickEventManager() );
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        if(isStarted){
            Robot.updateAll();
            Ball.updateAll();
            HumanPlayer.updateAll();
            field.update();
            scene.update();
            cam.lookAt(player.getPosition(), Vector3f.UNIT_Y);
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
        private static ArrayList<Joystick> joysticks = new ArrayList<Joystick>();
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
            System.out.println(key);
            System.out.println(function);
            listeners.put(key, function);
        }
        
        public static void removeListener(String key){
            listeners.remove(key);
        }
        
        public static void addJoystick(Joystick joystick, int upDownAxis, int leftRightAxis){
            HashMap<Integer, Float> axes = new HashMap();
            axes.put(upDownAxis, 0f);
            axes.put(leftRightAxis, 0f);
            joysticks.add(joystick);
            axisMaps.add(joystick.getJoyId(), axes);
        }
        
        public static void joystickAxisEvent(int id, int axis, float value){
            if(axisMaps.get(id).get(axis) != null){
                axisMaps.get(id).put(axis, value);
            }
        }
        
        private static float scale(float input, float sensitivity){
            return sensitivity * (input*input*input) + (1-sensitivity) * input;
        }
        
        public static float getAxisValue(int id, int axis, float sensitivity){
            if(id < axisMaps.size()){
                if(axisMaps.get(id).get(axis) > 0.1 || axisMaps.get(id).get(axis) < -0.1){
                System.out.println(scale(axisMaps.get(id).get(axis), sensitivity));
                return scale(axisMaps.get(id).get(axis), sensitivity);
                }else{
                    return 0;
                }
            }else{
                return 0;
            }
        }
        
        public static int isPressedi(String key){
            return (pressed.contains(key)?1:0);
        }
        
        public static int isPressedButtonsi(String keyPos, String keyNeg){
            if(pressed.contains(keyPos)){
                return 1;
            }else if(pressed.contains(keyNeg)){
                return -1;
            }else{
                return 0;
            }
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

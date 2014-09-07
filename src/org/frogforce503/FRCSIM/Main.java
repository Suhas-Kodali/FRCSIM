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
import com.jme3.system.AppSettings;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.frogforce503.FRCSIM.AI.AIFollowerProgram;
import org.frogforce503.FRCSIM.AI.AISuperCoach;
import org.frogforce503.FRCSIM.AI.PlayerFollowerProgram;
import org.frogforce503.FRCSIM.SwervePlayer.SwerveKeyMapping;
import org.frogforce503.FRCSIM.SwervePlayer.SwerveType;
import org.frogforce503.FRCSIM.TankPlayer.TankKeyMapping;
import org.frogforce503.FRCSIM.TankPlayer.TankType;


/**`
 *
 * @author Bryce Paputa
 */
public class Main extends SimpleApplication implements ActionListener, DTSDebuggable {

    public static Material red, black, blue, green, darkGray, allianceWalls, sides, chassis, orange, cyan, gray, blackNoAlpha;
    public static Field field;
    public static Main app;
    public static BulletAppState bulletAppState;
    public static boolean isStarted = false;
    public static Scene scene;
    public static TankKeyMapping keyMapping;
    public static Joystick[] joysticks;
    public static Robot player;
    public static final ArrayList<AISuperCoach> coaches = new ArrayList<AISuperCoach>(2);
    
    public static void main(String[] args) {
        app = new Main();
        AppSettings appSettings = new AppSettings(true);
        appSettings.setSettingsDialogImage("Textures/frog_force.png");
        appSettings.setUseJoysticks(true);
        appSettings.setResolution(1280, 600);
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        app.setSettings(appSettings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        initMaterials();
        setupKeys();
        
        
        scene = new Scene(assetManager, inputManager, audioRenderer, guiViewPort, flyCam);
        scene.startScreen();
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
//        bulletAppState.setDebugEnabled(true);
    }

    public static float in(final float in){
        return in/39.3701f;
    }
    
    public void initMaterials(){
        red = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        red.getAdditionalRenderState().setWireframe(false);
        red.setColor("Color", ColorRGBA.Red); 
        blue = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        blue.getAdditionalRenderState().setWireframe(false);
        blue.setColor("Color", ColorRGBA.Blue);
        cyan = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        cyan.getAdditionalRenderState().setWireframe(false);
        cyan.setColor("Color", ColorRGBA.Cyan);
        orange = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        orange.getAdditionalRenderState().setWireframe(false);
        orange.setColor("Color", ColorRGBA.Orange);
        green = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        green.getAdditionalRenderState().setWireframe(false);
        green.setColor("Color", ColorRGBA.Green); 
        darkGray = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        darkGray.getAdditionalRenderState().setWireframe(false);
        darkGray.setColor("Color", ColorRGBA.DarkGray); 
        darkGray.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        black = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        black.getAdditionalRenderState().setWireframe(false);
        black.setColor("Color", ColorRGBA.Black); 
        gray = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        gray.getAdditionalRenderState().setWireframe(false);
        gray.setColor("Color", ColorRGBA.Gray); 
        blackNoAlpha = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        blackNoAlpha.getAdditionalRenderState().setWireframe(false);
        blackNoAlpha.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        blackNoAlpha.setColor("Color", ColorRGBA.BlackNoAlpha);
        blackNoAlpha.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        
        allianceWalls = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        allianceWalls.getAdditionalRenderState().setWireframe(false);
        allianceWalls.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        allianceWalls.setTexture("ColorMap", assetManager.loadTexture(new TextureKey("Textures/goalTest.png")));
        allianceWalls.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        
        chassis = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        chassis.getAdditionalRenderState().setWireframe(false);
        chassis.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        chassis.setTexture("ColorMap", assetManager.loadTexture(new TextureKey("Textures/cage.png")));
        chassis.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        
        sides = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        sides.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        sides.setTexture("ColorMap", assetManager.loadTexture(new TextureKey("Textures/fieldSides.png")));
        sides.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
       
        playerAlliance = Alliance.RED;
        debuggables.add(Ball.balls);
        debuggables.add(Robot.robots.get(Alliance.RED));
        debuggables.add(Robot.robots.get(Alliance.BLUE));
        debuggables.add(coaches);
        try {
            FRCSIMLogger.setup();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private final static Logger logger = Logger.getLogger(Main.class.getName()); 
    public static int nblue, nred, numOnAlliance = 2, numOnOtherAlliance = 3; 
    public static Alliance playerAlliance = null;//Alliance.RED; 
    public static boolean isTank = true, isInStation = true;
    public static SwerveType swerveType = SwerveType.FieldCentricRedDriverCam;
    public static TankType tankType = TankType.arcade;
    public static int maxBalls = 1;
    
    public void startGame(){       
        field = new Field(rootNode, bulletAppState.getPhysicsSpace());
        
        if(playerAlliance == Alliance.BLUE){
            nblue = numOnAlliance;
            nred = numOnOtherAlliance;
        } else {
            nblue = numOnOtherAlliance;
            nred = numOnAlliance;
        }
        
        final ArrayList<ArrayList<AbstractSubsystem>> subsystems = new ArrayList<ArrayList<AbstractSubsystem>>(6);
        final AISuperCoach redCoach = new AISuperCoach(Alliance.RED);
        
        for(int i = 0; i < nred; i++){
            subsystems.add(i, new ArrayList<AbstractSubsystem>(4));
            subsystems.get(i).add(new BasicIntake());
            subsystems.get(i).add(new BasicShooter());
            subsystems.get(i).add(new AIFollowerProgram());
            if(i == 0){
                ((AIFollowerProgram) subsystems.get(i).get(2)).registerCoach(redCoach);
            }
            subsystems.get(i).add(new TankDrivetrain(subsystems.get(i), bulletAppState.getPhysicsSpace(), false));
            new Robot(subsystems.get(i), rootNode, bulletAppState.getPhysicsSpace(), Alliance.RED, new Vector3f(3, 0, (i-1)*3));
        }
        
        final AISuperCoach blueCoach = new AISuperCoach(Alliance.BLUE);
        for(int i = nred; i < nred + nblue; i++){
            subsystems.add(i, new ArrayList<AbstractSubsystem>(4));
            subsystems.get(i).add(new BasicIntake());
            subsystems.get(i).add(new BasicShooter());
            subsystems.get(i).add(new AIFollowerProgram());
            if(i == nred){
                ((AIFollowerProgram) subsystems.get(i).get(2)).registerCoach(blueCoach);
            }
            subsystems.get(i).add(new TankDrivetrain(subsystems.get(i), bulletAppState.getPhysicsSpace(), false));
            new Robot(subsystems.get(i), rootNode, bulletAppState.getPhysicsSpace(), Alliance.BLUE, new Vector3f(-3, 0, (i-1-nred)*3));
        }
        
        coaches.add(redCoach);
        coaches.add(blueCoach);
        
        if(playerAlliance != null){
            ArrayList<AbstractSubsystem> playersubsystems = new ArrayList<AbstractSubsystem>();
            playersubsystems.add(new BasicIntake());
            playersubsystems.add(new BasicShooter());
            if(isTank){
                playersubsystems.add(new PlayerFollowerProgram(new TankPlayer(TankKeyMapping.wasd, tankType)));
                playersubsystems.add(new TankDrivetrain(playersubsystems, bulletAppState.getPhysicsSpace(), true));
            } else {
                playersubsystems.add(new PlayerFollowerProgram(new SwervePlayer(SwerveKeyMapping.wasd, swerveType)));           
                playersubsystems.add(new SwerveDrivetrain(playersubsystems, bulletAppState.getPhysicsSpace(), true));
            }
            player = new Robot(playersubsystems, rootNode, bulletAppState.getPhysicsSpace(), playerAlliance, new Vector3f(-3 * playerAlliance.side, 0, 3));
        }
        
        if(isInStation){
            cam.setLocation(new Vector3f((Field.length/2 + Main.in(70))*(playerAlliance == Alliance.RED? -1 : 1), Main.in(68), -Field.width/3*(System.nanoTime()%3-1)));
            if(player != null){
                cam.lookAt(player.getPosition(), Vector3f.UNIT_Y);
            } else {
                cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            }
        } else {
            cam.setLocation(new Vector3f(0, 12, 12));
            cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        }
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
            inputManager.addMapping(String.valueOf((char)( i+'a')), new KeyTrigger(keys[i]));
            inputManager.addListener(this, String.valueOf((char)( i+'a')));
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
            if(player != null && isInStation){
                cam.lookAt(player.getPosition(), Vector3f.UNIT_Y);
            }
        }
    }
    
    public static final ArrayList<ArrayList<? extends DTSDebuggable>> debuggables = new ArrayList<ArrayList<? extends DTSDebuggable>>(4);
    public static PrintWriter debugWriter;
    
    public String detailedToString(String offset) {
        StringBuilder temp = new StringBuilder();
        for(ArrayList<? extends DTSDebuggable> list : debuggables){
            for(DTSDebuggable debuggable : list){
                temp.append(debuggable.detailedToString(offset)).append("\n");
            }
        }
        return temp.toString();
    }
    
    public static void printDebugMessage(){
        logger.info(Main.app.detailedToString(""));
    }
    
    @Override
    public void destroy(){
        printDebugMessage();
        super.destroy();
    }
    
    public static final class JoystickEventManager implements RawInputListener{

        public void beginInput() {}

        public void endInput() {}

        public void onJoyAxisEvent(final JoyAxisEvent evt) {
            InputManager.joystickAxisEvent(evt.getJoyIndex(), evt.getAxisIndex(), evt.getValue());
        }

        public void onJoyButtonEvent(final JoyButtonEvent evt) {
            if(evt.isPressed()){
                InputManager.press(evt.getButton().getName());
            } else {
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
        private static final ArrayList<HashMap<Integer, Float>> axisMaps = new ArrayList<HashMap<Integer, Float>>();
        private static final ArrayList<Joystick> joysticks = new ArrayList<Joystick>();
        private static final ArrayList<String> pressed = new ArrayList<String>();
        private static final HashMap<String, Runnable> listeners = new HashMap<String, Runnable>();
        
        public static void press(final String key){
            pressed.add(key);
            if(listeners.containsKey(key)){
                listeners.get(key).run();
            }
        }
        
        public static void release(final String key){
            pressed.remove(key);
        }
        
        public static boolean isPressed(final String key){
            return pressed.contains(key);
        }
        
        public static void addListener(final String key, final Runnable function){
            listeners.put(key, function);
        }
        
        public static void removeListener(final String key){
            listeners.remove(key);
        }
        
        public static void addJoystick(final Joystick joystick, final int upDownAxis, final int leftRightAxis){
            HashMap<Integer, Float> axes = new HashMap();
            axes.put(upDownAxis, 0f);
            axes.put(leftRightAxis, 0f);
            joysticks.add(joystick);
            axisMaps.add(joystick.getJoyId(), axes);
        }
        
        public static void joystickAxisEvent(final int id, final int axis, final float value){
            if(axisMaps.get(id).get(axis) != null){
                axisMaps.get(id).put(axis, value);
            }
        }
        
        private static float scale(final float input, final float sensitivity){
            return sensitivity * (input*input*input) + (1-sensitivity) * input;
        }
        
        public static float getAxisValue(final int id, final int axis, final float sensitivity){
            if(id < axisMaps.size()){
                final float raw = axisMaps.get(id).get(axis);
                if(raw > 0.05 || raw < -0.05){
                    return scale(raw, sensitivity);
                }else{
                    return 0;
                }
            }else{
                return 0;
            }
        }
        
        public static int isPressedi(final String key){
            return (pressed.contains(key)?1:0);
        }
        
        public static int isPressedButtonsi(final String keyPos, final String keyNeg){
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
    public void onAction(final String binding, final boolean value, final float tpf) {
        if(value == true){
            InputManager.press(binding);
        } else {
            InputManager.release(binding);
        }
    }
}

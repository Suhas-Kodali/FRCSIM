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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.frogforce503.FRCSIM.AI.AIFollowerProgram;
import org.frogforce503.FRCSIM.AI.AISuperCoach;
import org.frogforce503.FRCSIM.AI.PlayerFollowerProgram;
import org.frogforce503.FRCSIM.AI.TestProgram;
import org.frogforce503.FRCSIM.SwervePlayer.SwerveKeyMapping;
import org.frogforce503.FRCSIM.SwervePlayer.SwerveControlMethod;
import org.frogforce503.FRCSIM.TankPlayer.TankKeyMapping;
import org.frogforce503.FRCSIM.TankPlayer.TankControlMethod;


/**
 * Main class.
 * @author Bryce Paputa
 */
public class Main extends SimpleApplication implements ActionListener, DTSDebuggable {
    /**
     * Material used by the game.
     */
    public static Material red, black, blue, green, darkGray, allianceWalls, sides, chassis, orange, cyan, gray, blackNoAlpha;
    
    /**
     * The field.
     */
    public static Field field;
    
    /**
     * Instance of Main.
     */
    public static Main app;
    
    /**
     * App state that generates the PhysicsSpace.
     */
    public static BulletAppState bulletAppState;
    
    /**
     * Stores whether or not the game has started yet.
     */
    public static boolean isStarted = false;
    
    /**
     * Array of joysticks.
     */
    public static Joystick[] joysticks;
    
    /**
     * The player's robot, null if there is no player.
     */
    public static Robot player;
    
    /**
     * Array of AI coaches.
     */
    public static final ArrayList<AISuperCoach> coaches = new ArrayList<AISuperCoach>(2);
    
    /**
     * Interface to the Nifty screen.
     */
    public static Scene scene;
    
    /**
     * Number of ai robots on the blue alliance
     */
    public static int nblue;
    
    /**
     * Number of ai robots on the red allaince.
     */
    public static int nred;
    
    /**
     * Number of ai robots on the player's alliance.
     */
    public static int numOnAlliance = 2;
    
    /**
     * Number of ai robots on the opposing alliance.
     */
    public static int numOnOtherAlliance = 3; 
    
    /**
     * The alliance the the player will be on, set to Alliance.Red in initMaterials().
     */
    public static Alliance playerAlliance = null;//Alliance.Red; 
    
    /**
     * Will the player be a Tank drive?
     */
    public static boolean isTank = true;
    
    /**
     * Will the player's perspective be in the driver station?
     */
    public static boolean isInStation = true;
    
    /**
     * Stores the type of swerve drive to use.
     */
    public static SwerveControlMethod swerveType = SwerveControlMethod.FieldCentricRedDriverCam;
    
    /**
     * Stores the type of Tank drive to use.
     */
    public static TankControlMethod tankType = TankControlMethod.ArcadeKey;
    
    /**
     * Stores the maximum number of balls.
     */
    public static int maxBalls = 1;
    
    private static final ArrayList<ArrayList<? extends DTSDebuggable>> debuggables = new ArrayList<ArrayList<? extends DTSDebuggable>>(4);
    private final static Logger logger = Logger.getLogger(Main.class.getName()); 
    
    private Main(){}
    
    /**
     * main method, starts the game.
     * @param args Unused
     */
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

    /**
     * Sets up the game.
     */
    @Override
    public void simpleInitApp() {
        initMaterials();
        setupKeys();
        (scene = new Scene(assetManager, inputManager, audioRenderer, guiViewPort, flyCam)).startScreen();
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
//        bulletAppState.setDebugEnabled(true);
    }

    /**
     * Converts inches to meters.
     * @param in    Value in inches
     * @return      Value in meters
     */
    public static float in(final float in){
        return in/39.3701f;
    }
    
    /**
     * Sets up materials, must be called before any references to alliances.
     */
    private void initMaterials(){
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
       
        playerAlliance = Alliance.Red;
        debuggables.add(Ball.balls);
        debuggables.add(Robot.robots.get(Alliance.Red));
        debuggables.add(Robot.robots.get(Alliance.Blue));
        debuggables.add(coaches);
        try {
            Logger llogger = Logger.getLogger("");
            llogger.setLevel(Level.INFO);
            FileHandler fileTxt = new FileHandler("FRCSIMLog.fsd");
            fileTxt.setFormatter(new SimpleFormatter());
            fileTxt.setLevel(Level.INFO);
            llogger.addHandler(fileTxt);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Starts the game.
     */
    public void startGame(){       
        field = new Field(rootNode, bulletAppState.getPhysicsSpace());
        
        if(playerAlliance == Alliance.Blue){
            nblue = numOnAlliance;
            nred = numOnOtherAlliance;
        } else {
            nblue = numOnOtherAlliance;
            nred = numOnAlliance;
        }
        
        final ArrayList<ArrayList<AbstractSubsystem>> subsystems = new ArrayList<ArrayList<AbstractSubsystem>>(6);
        final AISuperCoach redCoach = new AISuperCoach(Alliance.Red);
        
        for(int i = 0; i < nred; i++){
            subsystems.add(i, new ArrayList<AbstractSubsystem>(4));
            subsystems.get(i).add(new BasicIntake());
            subsystems.get(i).add(new BasicShooter());
            subsystems.get(i).add(new AIFollowerProgram());
            if(i == 0){
                ((AIFollowerProgram) subsystems.get(i).get(2)).registerCoach(redCoach);
            }
            subsystems.get(i).add(new TankDrivetrain(subsystems.get(i), bulletAppState.getPhysicsSpace(), false));
            new Robot(subsystems.get(i), rootNode, bulletAppState.getPhysicsSpace(), Alliance.Red, new Vector3f(3, 0, (i-1)*3));
        }
        
        final AISuperCoach blueCoach = new AISuperCoach(Alliance.Blue);
        for(int i = nred; i < nred + nblue; i++){
            subsystems.add(i, new ArrayList<AbstractSubsystem>(4));
            subsystems.get(i).add(new BasicIntake());
            subsystems.get(i).add(new BasicShooter());
            subsystems.get(i).add(new AIFollowerProgram());
            if(i == nred){
                ((AIFollowerProgram) subsystems.get(i).get(2)).registerCoach(blueCoach);
            }
            subsystems.get(i).add(new TankDrivetrain(subsystems.get(i), bulletAppState.getPhysicsSpace(), false));
            new Robot(subsystems.get(i), rootNode, bulletAppState.getPhysicsSpace(), Alliance.Blue, new Vector3f(-3, 0, (i-1-nred)*3));
        }
        
        coaches.add(redCoach);
        coaches.add(blueCoach);
        
//        ArrayList<AbstractSubsystem> testsystems = new ArrayList<AbstractSubsystem>(4);
//        testsystems.add(new BasicIntake());
//        testsystems.add(new BasicShooter());
//        testsystems.add(new TestProgram());
//        testsystems.add(new TankDrivetrain(testsystems, bulletAppState.getPhysicsSpace(), false));
//        
//        new Robot(testsystems, rootNode, bulletAppState.getPhysicsSpace(), playerAlliance.invert(), Vector3f.ZERO);
        
        if(playerAlliance != null){
            ArrayList<AbstractSubsystem> playersubsystems = new ArrayList<AbstractSubsystem>(4);
            playersubsystems.add(new BasicIntake());
            playersubsystems.add(new BasicShooter());
            if(isTank){
                if(tankType == TankControlMethod.ArcadeKey || tankType == TankControlMethod.Tank){
                    playersubsystems.add(new PlayerFollowerProgram(new TankPlayer(TankKeyMapping.wasd, tankType)));
                    playersubsystems.add(new TankDrivetrain(playersubsystems, bulletAppState.getPhysicsSpace(), true));
                } else {
                    playersubsystems.add(new PlayerFollowerProgram(new TankPlayer(TankKeyMapping.joy, tankType)));
                    playersubsystems.add(new TankDrivetrain(playersubsystems, bulletAppState.getPhysicsSpace(), true));                    
                }
            } else {
                playersubsystems.add(new PlayerFollowerProgram(new SwervePlayer(SwerveKeyMapping.wasd, swerveType)));           
                playersubsystems.add(new SwerveDrivetrain(playersubsystems, bulletAppState.getPhysicsSpace(), true));
            }
            player = new Robot(playersubsystems, rootNode, bulletAppState.getPhysicsSpace(), playerAlliance, new Vector3f(-3 * playerAlliance.side, 0, 3));
        }
        
        if(isInStation){
            cam.setLocation(new Vector3f((Field.length/2 + in(70))*(playerAlliance == Alliance.Red? -1 : 1), in(68), -Field.width/3*(System.nanoTime()%3-1)));
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
                InputManager.addJoystick(joystick, 0, 1, 2, 3);
            }
        }
        
        inputManager.addRawInputListener( new JoystickEventManager() );
    }
    
    /**
     * Calls all of the update methods of other classes.
     * @param tpf Unused
     */
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
    
    /**
     * {@inheritDoc} 
     */
    public String detailedToString(String offset) {
        StringBuilder temp = new StringBuilder();
        for(ArrayList<? extends DTSDebuggable> list : debuggables){
            for(DTSDebuggable debuggable : list){
                temp.append(debuggable.detailedToString(offset)).append("\n");
            }
        }
        return temp.toString();
    }
    
    /**
     * Logs a debugging message.
     */
    public static void printDebugMessage(){
        logger.info(Main.app.detailedToString(""));
    }
    
    /**
     * Called at the end of the program, prints out a last debugging message.
     */
    @Override
    public void destroy(){
        printDebugMessage();
        super.destroy();
    }
    
    /**
     * Class that listens to joystick input.
     */
    private static final class JoystickEventManager implements RawInputListener{
        
        /**
         * {@inheritDoc}
         */
        public void beginInput() {}

        /**
         * {@inheritDoc}
         */
        public void endInput() {}

        /**
         * {@inheritDoc}
         */
        public void onJoyAxisEvent(final JoyAxisEvent evt) {
            InputManager.joystickAxisEvent(evt.getJoyIndex(), evt.getAxisIndex(), evt.getValue());
        }

        /**
         * {@inheritDoc}
         */
        public void onJoyButtonEvent(final JoyButtonEvent evt) {
            if(evt.isPressed()){
                InputManager.press(evt.getButton().getName());
            } else {
                InputManager.release(evt.getButton().getName());
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onMouseMotionEvent(MouseMotionEvent evt) {}

        /**
         * {@inheritDoc}
         */
        public void onMouseButtonEvent(MouseButtonEvent evt) {}

        /**
         * {@inheritDoc}
         */
        public void onKeyEvent(KeyInputEvent evt) {}

        /**
         * {@inheritDoc}
         */
        public void onTouchEvent(TouchEvent evt) {}
    }
    
    /**
     * Class that manages input.
     */
    public static final class InputManager{
        private InputManager() {}
        private static final ArrayList<HashMap<Integer, Float>> axisMaps = new ArrayList<HashMap<Integer, Float>>();
        private static final ArrayList<Joystick> joysticks = new ArrayList<Joystick>();
        private static final ArrayList<String> pressed = new ArrayList<String>();
        private static final HashMap<String, Runnable> listeners = new HashMap<String, Runnable>();
        
        /**
         * Called when a key is pressed, marks that the key is pressed and calls any listeners on the key.
         * @param key Key that was pressed
         */
        public static void press(final String key){
            pressed.add(key);
            if(listeners.containsKey(key)){
                listeners.get(key).run();
            }
        }
        
        /**
         * Called when a key is released, marks that the key is no longer pressed.
         * @param key Key that was released
         */
        public static void release(final String key){
            pressed.remove(key);
        }
        
        /**
         * Checks if a key is pressed.
         * @param key   Key to check
         * @return      Whether or not the key is pressed
         */
        public static boolean isPressed(final String key){
            return pressed.contains(key);
        }
        
        /**
         * Adds a listener to a key.
         * @param key       Key to listen to
         * @param function  Function to run on press
         */
        public static void addListener(final String key, final Runnable function){
            listeners.put(key, function);
        }
        
        /**
         * Remove the listener from a key.
         * @param key Key to remove from
         */
        public static void removeListener(final String key){
            listeners.remove(key);
        }
        
        /**
         * Adds a joystick.
         * @param joystick      Joystick to add
         * @param y1    Y axis of controller
         * @param x1 X axis of controller
         */
        public static void addJoystick(final Joystick joystick, final int y1, final int x1, final int y2, final int x2){
            HashMap<Integer, Float> axes = new HashMap();
            axes.put(y1, 0f);
            axes.put(x1, 0f);
            axes.put(y2, 0f);
            axes.put(x2, 0f);
            joysticks.add(joystick);
            axisMaps.add(joystick.getJoyId(), axes);
        }
        
        /**
         * Called when a joystick value changes.
         * @param id    ID of the joystick
         * @param axis  Axis that changed
         * @param value New value
         */
        public static void joystickAxisEvent(final int id, final int axis, final float value){
            if(axisMaps.get(id).get(axis) != null){
                axisMaps.get(id).put(axis, value);
            }
        }
        
        private static float scale(final float input, final float sensitivity){
            return sensitivity * (input*input*input) + (1-sensitivity) * input;
        }
        
        /**
         * Get the value of a joystick axis.
         * @param id            ID of joystick
         * @param axis          Axis to get
         * @param sensitivity   Sensitivity for scale function (a*x^3+(1-a)*x)
         * @return              Scaled value
         */
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
        
        /**
         * Checks if a key is pressed.
         * @param key   Key to check
         * @return      1 if pressed, 0 otherwise
         */
        public static int isPressedi(final String key){
            return (pressed.contains(key)?1:0);
        }
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void onAction(final String binding, final boolean value, final float tpf) {
        if(value == true){
            InputManager.press(binding);
        } else {
            InputManager.release(binding);
        }
    }
}

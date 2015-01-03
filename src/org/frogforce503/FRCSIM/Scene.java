package org.frogforce503.FRCSIM;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.RadioButtonGroupStateChangedEvent;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.awt.Color;
import org.frogforce503.FRCSIM.SwervePlayer.SwerveControlMethod;
import org.frogforce503.FRCSIM.SwervePlayer.SwerveKeyMapping;
import org.frogforce503.FRCSIM.TankPlayer.TankControlMethod;

/**
 * Class that interfaces with the Nifty GUI system.
 * @author skodali
 */
public class Scene implements ScreenController {
    private static Nifty nifty;
    private static int time = 2;
    /**
     * Constructor or a new scene object.
     * @param assetManager  JME3 AssetManager
     * @param inputManager  JME3 InputManager
     * @param audioRenderer JME3 AudioRenderer
     * @param guiViewPort   JME3 ViewPort
     * @param flyCam        JME3 FlyByCamera
     */
    public Scene(final AssetManager assetManager, final InputManager inputManager, final AudioRenderer audioRenderer, final ViewPort guiViewPort, final FlyByCamera flyCam){
        final NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/GUI.xml", "start", this);
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);
    }
    
    /**
     * Goes to the start screen.
     */
    public void startScreen(){
        nifty.gotoScreen("start");
        nifty.setIgnoreKeyboardEvents(false);
    }
    
    /**
     * Goes to the first setup screen.
     */
    public void setup1Screen(){
        nifty.gotoScreen("setup1");
        nifty.setIgnoreKeyboardEvents(false);
    }
    
    /**
     * Goes to the HUD screen.
     */
    public void hudScreen(){
        nifty.gotoScreen("hud");
        nifty.setIgnoreKeyboardEvents(true);
        updateScore();
        updateDirection();
    }
    
    public void endScreen(){
        nifty.gotoScreen("endScreen");
        nifty.getCurrentScreen().findElementByName("score").getRenderer(TextRenderer.class).setText(getFinalScore());
    }
    
    /**
     * Updates the score.
     */
    public void updateScore(){
        nifty.getCurrentScreen().findElementByName("score").getRenderer(TextRenderer.class).setText(getScore());
    }
    
    public void updateTime(){
        if(time != 0){
            nifty.getCurrentScreen().findElementByName("time").getRenderer(TextRenderer.class).setText(String.format("%1$.1f", Main.app.getTime()));
            if(Main.app.getTime() < 30){
                nifty.getCurrentScreen().findElementByName("time").getRenderer(TextRenderer.class).setColor(de.lessvoid.nifty.tools.Color.randomColor());
            } 
        } else {
            nifty.getCurrentScreen().findElementByName("time").getRenderer(TextRenderer.class).setText("No Time Limit");
        }
    }
    
    /**
     * Updates the AI instructions.
     */
    public void updateDirection(){
        if(nifty.getCurrentScreen().findElementByName("direction") != null){
            nifty.getCurrentScreen().findElementByName("direction").getRenderer(TextRenderer.class).setText(getDirection());
        }
    }
   
    /**
     * {@inheritDoc}
     */
    @Override
    public void bind(final Nifty nifty, final Screen screen) {}
   
    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartScreen() {}
   
    /**
     * {@inheritDoc}
     */
    @Override
    public void onEndScreen() {}

    /**
     * Starts the game and goes to the correct screen.
     */
    public void startGame(){
        Main.app.startGame();
        hudScreen();
    }

    /**
     * Exits the game.
     */
    public void quitGame() {
        Main.app.stop();  
    }
    
    public void timeUp(){
        time++;
        nifty.getCurrentScreen().findElementByName("timeLabel").getRenderer(TextRenderer.class).setText(Integer.toString(time));
        Main.time = time;
    }
    
    public void timeDown(){
        if(time > 1){
            time--;
            nifty.getCurrentScreen().findElementByName("timeLabel").getRenderer(TextRenderer.class).setText(Integer.toString(time));
            Main.time = time;
        } else if(time == 1){
            Main.time = Integer.MAX_VALUE/61;
            nifty.getCurrentScreen().findElementByName("timeLabel").getRenderer(TextRenderer.class).setText("No Limit");
            time = 0;
        }
    }

    /**
     * Gets a string representing the score.
     * @return Human readable score
     */
    public String getScore(){
        return "R"+ Alliance.Red.getScore() + " B"+Alliance.Blue.getScore();
    }
    
    public String getFinalScore(){
        return "RED: "+ Alliance.Red.getScore() + "\n BLUE: "+Alliance.Blue.getScore();
    }

    /**
     * Gets AI instructions for the player.
     * @return AI's instructions
     */
    public String getDirection(){
        return Main.player.getAIFollower().getHRProgramName();
    }
    
    /**
     * Updates Main's state variables when the alliance changes.
     * @param id    ID of the selected radio button
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="alliance")
    public void allianceRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        final String alliance = e.getSelectedId();
        if("red".equals(alliance)){
            Main.playerAlliance = Alliance.Red;
            if(Main.swerveType == SwerveControlMethod.FieldCentricBlueDriverCam){
                Main.swerveType = SwerveControlMethod.FieldCentricRedDriverCam;
            }
        } else if ("blue".equals(alliance)){
            Main.playerAlliance = Alliance.Blue;
            if(Main.swerveType == SwerveControlMethod.FieldCentricRedDriverCam){
                Main.swerveType = SwerveControlMethod.FieldCentricBlueDriverCam;
            }
        } else {
            Main.playerAlliance = null;
        }
    }
    
    @NiftyEventSubscriber(id="omni")
    public void omniRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        final String omni = e.getSelectedId();
        if("noOmni".equals(omni)){
            Main.isOmni = false;
        } else if ("omni".equals(omni)){
            Main.isOmni = true;
        }
    }
    
    /**
     * Updates Main's state variables when the view changes.
     * @param id    ID of the selected radio button
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="view")
    public void viewRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        if("driver".equals(e.getSelectedId())){
            Main.isInStation = true;
            if(Main.swerveType == SwerveControlMethod.FieldCentricSpectatorCam){
                if(Main.playerAlliance == Alliance.Blue){
                    Main.swerveType = SwerveControlMethod.FieldCentricBlueDriverCam;
                } else {
                    Main.swerveType = SwerveControlMethod.FieldCentricRedDriverCam;
                }
            }
        } else {
            Main.isInStation = false;
            if(Main.swerveType == SwerveControlMethod.FieldCentricBlueDriverCam || Main.swerveType == SwerveControlMethod.FieldCentricRedDriverCam){
                Main.swerveType = SwerveControlMethod.FieldCentricSpectatorCam;
            }
        }
    }
    
    /**
     * Updates Main's state variables when the drivetrain type changes.
     * @param id    ID of the selected radio button
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="dt")
    public void dtRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        final Element tankPanel = nifty.getCurrentScreen().findElementByName("tankPanel"), 
                swervePanel1 = nifty.getCurrentScreen().findElementByName("swervePanel1"), 
                swervePanel2 = nifty.getCurrentScreen().findElementByName("swervePanel2");
        if("tank".equals(e.getSelectedId())){
            Main.isTank = true;
            tankPanel.show();
            swervePanel1.hide();
            swervePanel2.hide();
        } else {
            Main.isTank = false;
            tankPanel.hide();
            swervePanel1.show();
            swervePanel2.show();
        }
    }
    
    /**
     * Updates Main's state variables when the drivetrain type changes.
     * @param id    ID of the selected radio button
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="tankType")
    public void tankTypeRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        if("tankDriveKey".equals(e.getSelectedId())){
            Main.tankType = TankControlMethod.TankKey;
        } else if("tankDriveJoy".equals(e.getSelectedId())){
            Main.tankType = TankControlMethod.TankJoy;
        } else if("arcadeDriveKey".equals(e.getSelectedId())){
            Main.tankType = TankControlMethod.ArcadeKey;
        } else if("arcadeDrive1Joy".equals(e.getSelectedId())){
            Main.tankType = TankControlMethod.Arcade1Joy;
        } else if("arcadeDrive2Joy".equals(e.getSelectedId())){
            Main.tankType = TankControlMethod.Arcade2Joy;
        }
    }
    
    /**
     * Updates Main's state variables when the drivetrain type changes.
     * @param id    ID of the selected radio button
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="swerveType")
    public void swerveTypeRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        if("FCDrive".equals(e.getSelectedId())){
            if(Main.isInStation){
                if(Main.playerAlliance==Alliance.Blue){
                    Main.swerveType = SwerveControlMethod.FieldCentricBlueDriverCam;
                } else {
                    Main.swerveType = SwerveControlMethod.FieldCentricRedDriverCam;
                }
            } else {
                Main.swerveType = SwerveControlMethod.FieldCentricSpectatorCam;
            }
        } else {
            Main.swerveType = SwerveControlMethod.RobotCentric;
        }
    }
    
    /**
     * Updates Main's state variables when the drivetrain key mapping changes.
     * @param id    ID of the selected radio button
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="swerveKeyMapping")
    public void swerveKeyMappingRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        if("Keyboard".equals(e.getSelectedId())){
            Main.swerveKeyMapping = SwerveKeyMapping.wasd;
        } else {
            Main.swerveKeyMapping = SwerveKeyMapping.joy;
        }
    }
    
    /**
     * Updates Main's state variables when the alliance configuration changes.
     * @param id    ID of the modified slider
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="yourAllianceSlider")
    public void yaSliderChangeEvent(final String id, final SliderChangedEvent e){
        nifty.getCurrentScreen().findElementByName("yourAllianceLabel").getRenderer(TextRenderer.class).setText(Math.round(e.getValue())+"");
        Main.numOnAlliance = Math.round(e.getValue());
    }
    
    /**
     * Updates Main's state variables when the alliance configuration changes.
     * @param id    ID of the modified slider
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="otherAllianceSlider")
    public void oaSliderChangeEvent(final String id, final SliderChangedEvent e){
        nifty.getCurrentScreen().findElementByName("otherAllianceLabel").getRenderer(TextRenderer.class).setText(Math.round(e.getValue())+""); 
        Main.numOnOtherAlliance = Math.round(e.getValue());
    }
    
    /**
     * Updates Main's state variables when the ball configuration changes.
     * @param id    ID of the modified slider
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="ballSlider")
    public void ballSliderChangeEvent(final String id, final SliderChangedEvent e){
        nifty.getCurrentScreen().findElementByName("ballLabel").getRenderer(TextRenderer.class).setText(Math.round(e.getValue())+""); 
        Main.maxBalls = Math.round(e.getValue());
    }
    
}

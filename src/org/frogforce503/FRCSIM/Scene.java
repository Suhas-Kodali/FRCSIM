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
import org.frogforce503.FRCSIM.SwervePlayer.SwerveControlMethod;
import org.frogforce503.FRCSIM.TankPlayer.TankControlMethod;

/**
 * Class that interfaces with the Nifty GUI system.
 * @author skodali
 */
public class Scene implements ScreenController {
    private static Nifty nifty;
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
    
    /**
     * Updates the score.
     */
    public void updateScore(){
        nifty.getCurrentScreen().findElementByName("score").getRenderer(TextRenderer.class).setText(getScore());
    }
    
    /**
     * Updates the AI instructions.
     */
    public void updateDirection(){
        nifty.getCurrentScreen().findElementByName("direction").getRenderer(TextRenderer.class).setText(getDirection());
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

    /**
     * Gets a string representing the score.
     * @return Human readable score
     */
    public String getScore(){
        return "R"+ Alliance.Red.getScore() + " B"+Alliance.Blue.getScore();
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
                swervePanel = nifty.getCurrentScreen().findElementByName("swervePanel");
        if("tank".equals(e.getSelectedId())){
            Main.isTank = true;
            tankPanel.show();
            swervePanel.hide();
        } else {
            Main.isTank = false;
            tankPanel.hide();
            swervePanel.show();
        }
    }
    
    /**
     * Updates Main's state variables when the drivetrain type changes.
     * @param id    ID of the selected radio button
     * @param e     Event object
     */
    @NiftyEventSubscriber(id="tankType")
    public void tankTypeRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        if("tankDrive".equals(e.getSelectedId())){
            Main.tankType = TankControlMethod.Tank;
        } else {
            Main.tankType = TankControlMethod.Arcade;
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

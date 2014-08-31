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
import org.frogforce503.FRCSIM.SwervePlayer.SwerveType;
import org.frogforce503.FRCSIM.TankPlayer.TankType;

/**
 *
 * @author skodali
 */
public class Scene implements ScreenController {
    private static Nifty nifty;
    public Scene(final AssetManager assetManager, final InputManager inputManager, final AudioRenderer audioRenderer, final ViewPort guiViewPort, final FlyByCamera flyCam){
        final NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/GUI.xml", "start", this);
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);
    }
    
    public void startScreen(){
        nifty.gotoScreen("start");
        nifty.setIgnoreKeyboardEvents(false);
    }
    
    public void setup1Screen(){
        nifty.gotoScreen("setup1");
        nifty.setIgnoreKeyboardEvents(false);
    }
    
    public void hudScreen(){
        nifty.gotoScreen("hud");
        nifty.setIgnoreKeyboardEvents(true);
        updateScore();
        updateDirection();
    }
    
    public void updateScore(){
        nifty.getCurrentScreen().findElementByName("score").getRenderer(TextRenderer.class).setText(getScore());
    }
    
    public void updateDirection(){
        nifty.getCurrentScreen().findElementByName("direction").getRenderer(TextRenderer.class).setText(getDirection());
    }
   
    @Override
    public void bind(final Nifty nifty, final Screen screen) {}

    @Override
    public void onStartScreen() {}

    @Override
    public void onEndScreen() {}

    public void startGame(){
        Main.app.startGame();
        hudScreen();
    }

    public void quitGame() {
        Main.app.stop();  
    }

    public String getScore(){
        return "R"+ Alliance.RED.getScore() + " B"+Alliance.BLUE.getScore();
    }

    public String getDirection(){
        return Main.player.getAIFollower().getHRProgramName();
    }
    
    @NiftyEventSubscriber(id="alliance")
    public void allianceRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        final String alliance = e.getSelectedId();
        if("red".equals(alliance)){
            Main.playerAlliance = Alliance.RED;
            if(Main.swerveType == SwerveType.FieldCentricBlueDriverCam){
                Main.swerveType = SwerveType.FieldCentricRedDriverCam;
            }
        } else if ("blue".equals(alliance)){
            Main.playerAlliance = Alliance.BLUE;
            if(Main.swerveType == SwerveType.FieldCentricRedDriverCam){
                Main.swerveType = SwerveType.FieldCentricBlueDriverCam;
            }
        } else {
            Main.playerAlliance = null;
        }
    }
    
    @NiftyEventSubscriber(id="view")
    public void viewRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        if("driver".equals(e.getSelectedId())){
            Main.isInStation = true;
            if(Main.swerveType == SwerveType.FieldCentricSpectatorCam){
                if(Main.playerAlliance == Alliance.BLUE){
                    Main.swerveType = SwerveType.FieldCentricBlueDriverCam;
                } else {
                    Main.swerveType = SwerveType.FieldCentricRedDriverCam;
                }
            }
        } else {
            Main.isInStation = false;
            if(Main.swerveType == SwerveType.FieldCentricBlueDriverCam || Main.swerveType == SwerveType.FieldCentricRedDriverCam){
                Main.swerveType = SwerveType.FieldCentricSpectatorCam;
            }
        }
    }
    
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
    
    @NiftyEventSubscriber(id="tankType")
    public void tankTypeRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        if("tankDrive".equals(e.getSelectedId())){
            Main.tankType = TankType.tank;
        } else {
            Main.tankType = TankType.arcade;
        }
    }
    
    @NiftyEventSubscriber(id="swerveType")
    public void swerveTypeRadioButtonChangeEvent(final String id, final RadioButtonGroupStateChangedEvent e){
        if("FCDrive".equals(e.getSelectedId())){
            if(Main.isInStation){
                if(Main.playerAlliance==Alliance.BLUE){
                    Main.swerveType = SwerveType.FieldCentricBlueDriverCam;
                } else {
                    Main.swerveType = SwerveType.FieldCentricRedDriverCam;
                }
            } else {
                Main.swerveType = SwerveType.FieldCentricSpectatorCam;
            }
        } else {
            Main.swerveType = SwerveType.RobotCentric;
        }
    }
    
    @NiftyEventSubscriber(id="yourAllianceSlider")
    public void yaSliderChangeEvent(final String id, final SliderChangedEvent e){
        nifty.getCurrentScreen().findElementByName("yourAllianceLabel").getRenderer(TextRenderer.class).setText(Math.round(e.getValue())+"");
        Main.numOnAlliance = Math.round(e.getValue());
    }
    
    @NiftyEventSubscriber(id="otherAllianceSlider")
    public void oaSliderChangeEvent(final String id, final SliderChangedEvent e){
        nifty.getCurrentScreen().findElementByName("otherAllianceLabel").getRenderer(TextRenderer.class).setText(Math.round(e.getValue())+""); 
        Main.numOnOtherAlliance = Math.round(e.getValue());
    }
    
    @NiftyEventSubscriber(id="ballSlider")
    public void ballSliderChangeEvent(final String id, final SliderChangedEvent e){
        nifty.getCurrentScreen().findElementByName("ballLabel").getRenderer(TextRenderer.class).setText(Math.round(e.getValue())+""); 
        Main.maxBalls = Math.round(e.getValue());
    }
}

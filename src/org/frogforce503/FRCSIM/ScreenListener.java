package org.frogforce503.FRCSIM;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import static org.frogforce503.FRCSIM.Scene.nifty;

/**
 *
 * @author skodali
 */
public class ScreenListener extends AbstractAppState implements ScreenController {

 
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
    }
 
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
 
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        
    }

    @Override
    public void onStartScreen() {
        
    }

    @Override
    public void onEndScreen() {
        
    }
    
     /** custom methods */ 
    public void changeScreen(String nextScreen) {
        Main.scene.screen = nextScreen;
        nifty.gotoScreen(nextScreen);
    }

    public void quitGame() {
        Main.app.stop();  
    }
    
    public String getScore(){
        return new Integer(Alliance.RED.getScore()).toString();
  }
}

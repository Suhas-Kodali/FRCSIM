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
    
    public void bind(Nifty nifty, Screen screen) {
        
    }

    public void onStartScreen() {
        
    }

    public void onEndScreen() {
        
    }
    
     /** custom methods */ 
    public void startGame(String nextScreen) {
        nifty.gotoScreen(nextScreen);  // switch to another screen
        // start the game and do some more stuff...
        nifty.setIgnoreKeyboardEvents(true);
    }

    public void quitGame() {
        Main.app.stop();  // switch to another screen
    }
    
    public String getScore(){
        System.out.println(System.getProperty("user.name"));
        return System.getProperty("user.name");
  }
}

package org.frogforce503.FRCSIM;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;

/**
 *
 * @author skodali
 */
public class Scene {
    public static Nifty nifty;
    public String screen = "start_screen";
    public Scene(AssetManager assetManager, InputManager inputManager, AudioRenderer audioRenderer, ViewPort guiViewPort, FlyByCamera flyCam){
         NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
    assetManager, inputManager, audioRenderer, guiViewPort);
    nifty = niftyDisplay.getNifty();
    guiViewPort.addProcessor(niftyDisplay);
    flyCam.setDragToRotate(true);
 
    nifty.loadStyleFile("nifty-default-styles.xml");
    nifty.loadControlFile("nifty-default-controls.xml");
    }
    
    public void initScreens(){
        // <screen>
     nifty.addScreen("start_screen", new ScreenBuilder("Hello Nifty Screen"){{
        controller(new ScreenListener()); // Screen properties       
 
        // <layer>
        layer(new LayerBuilder("Layer_ID") {{
            childLayoutVertical(); // layer properties, add more...
            
               height("100%");
               width("100%");
 
            // <panel>
            panel(new PanelBuilder("panel_bottom_left") {{
                    childLayoutCenter();
                    valignCenter();
                    alignLeft();
                    backgroundColor("#000");
                    height("50%");
                    width("100%");
 
                    // add control
                    control(new ButtonBuilder("StartButton", "Start") {{
                      alignCenter();
                      valignCenter();
                      height("50%");
                      width("50%");
                      interactOnClick("changeScreen(hud)");
                    }});
 
                }});
 
                panel(new PanelBuilder("panel_bottom_right") {{
                    childLayoutCenter();
                    valignCenter();
                    alignRight();
                    backgroundColor("#000");
                    height("50%");
                    width("100%");
 
                    // add control
                    control(new ButtonBuilder("QuitButton", "Quit") {{
                      alignCenter();
                      valignCenter();
                      height("50%");
                      width("50%");
                      interactOnClick("quitGame()");
                    }});
 
                }});
            // </panel>
          }});
        // </layer>
      }}.build(nifty));
     
        // </screen>
     
     nifty.addScreen("hud", new ScreenBuilder("Hello Nifty Screen"){{
        controller(new ScreenListener()); // Screen properties       
 
        // <layer>
        layer(new LayerBuilder("Layer_1") {{
            childLayoutVertical(); // layer properties, add more...
            
               height("100%");
               width("100%");
 
            // <panel>
           
 
                panel(new PanelBuilder("panel_bottom_right") {{
                    childLayoutCenter();
                    valignTop();
                    alignLeft();
                    backgroundColor("#FFF");
                    height("5%");
                    width("10%");
                    //marginTop("10%");
 
                    // add control
                    
                    control(new ButtonBuilder("OptionButton", "Options") {{
                      alignCenter();
                      valignCenter();
                      height("100%");
                      width("100%");
                    }});
                }});
            // </panel>
          }});
        
         layer(new LayerBuilder("Layer_2") {{
            childLayoutVertical(); // layer properties, add more...
            
               height("100%");
               width("100%");
 
            // <panel>
            panel(new PanelBuilder("score_panel") {{
                    childLayoutCenter();
                    valignTop();
                    alignRight();
                    backgroundColor("#FFF");
                    height("5%");
                    width("10%");
 
                    // add control
                    text(new TextBuilder() {{
                    text("Score: ${CALL.getScore()}");
                    font("Interface/Fonts/Default.fnt");
                    color("#000");
                    height("5%");
                    width("5%");
                    }});
 
                }});
 
               
            // </panel>
          }});
        // </layer>
      }}.build(nifty));
     
     
    }
    
    public void startScreen(){
        nifty.gotoScreen("start_screen"); // start the screen
    }
    
    public void hudScreen(){
        nifty.gotoScreen("hud"); // start the screen
    }
    
    public void updateVariables(){
        initScreens();
        nifty.gotoScreen(screen);   
    }
    
    public void update(){
        if(screen != "start_screen"){
            nifty.setIgnoreKeyboardEvents(true);
        }else{
            nifty.setIgnoreKeyboardEvents(false);
        }
    }
    
}

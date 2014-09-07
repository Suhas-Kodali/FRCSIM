package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import static org.frogforce503.FRCSIM.Main.in;

/**
 * Class for bumpers.
 * @author Bryce Paputa
 */
public class Bumpers {
    private final Geometry frontGeometry;
    private final Geometry leftGeometry;
    private final Geometry backGeometry;
    private final Geometry rightGeometry;
    
    /**
     * Bumper constructor.
     * @param chassisNode   Chassis node to add the bumpers to
     * @param width         Width of the chassis
     * @param length        Length of the chassis
     * @param height        Distance to bottom of the bumpers
     */
    public Bumpers(final Node chassisNode, final float width, final float length, final float height){
        final Node bumperNode = new Node("Bumpers");
        
        final Box frontBox = new Box(new Vector3f(length/2+in(3.5f), height, width/2+.001f), new Vector3f(-length/2-in(3.5f), height+in(5), width/2+in(3.5f)));
        frontGeometry = new Geometry("Bumper Front", frontBox);
        frontGeometry.setMaterial(Main.black);
        bumperNode.attachChild(frontGeometry);
        
        final Box leftBox = new Box(new Vector3f(length/2+.001f, height, width/2), new Vector3f(length/2+in(3.5f), height+in(5), -width/2));
        leftGeometry = new Geometry("Bumper Left", leftBox);
        leftGeometry.setMaterial(Main.black);
        bumperNode.attachChild(leftGeometry);
        
        final Box backBox = new Box(new Vector3f(-length/2-in(3.5f), height, -width/2-.001f), new Vector3f(length/2+in(3.5f), height+in(5), -width/2-in(3.5f)));
        backGeometry = new Geometry("Bumper Front", backBox);
        backGeometry.setMaterial(Main.black);
        bumperNode.attachChild(backGeometry);
        
        final Box rightBox = new Box(new Vector3f(-length/2-.001f, height, -width/2), new Vector3f(-length/2-in(3.5f), height+in(5), +width/2));
        rightGeometry = new Geometry("Bumper Left", rightBox);
        rightGeometry.setMaterial(Main.black);
        bumperNode.attachChild(rightGeometry);
        
        chassisNode.attachChild(bumperNode);
    }
    
    /**
     * Sets the materials.
     * @param alliance  Alliance of the robot
     * @param isPlayer  Whether or not these bumpers are for a human player
     */
    public void registerAlliance(final Alliance alliance, final boolean isPlayer){
        if(isPlayer){
            frontGeometry.setMaterial(alliance.playermaterial);
            leftGeometry.setMaterial(alliance.playermaterial);
            backGeometry.setMaterial(alliance.playermaterial);
            rightGeometry.setMaterial(alliance.playermaterial);
        }else{
            frontGeometry.setMaterial(alliance.material);
            leftGeometry.setMaterial(alliance.material);
            backGeometry.setMaterial(alliance.material);
            rightGeometry.setMaterial(alliance.material);
        }
    }
}

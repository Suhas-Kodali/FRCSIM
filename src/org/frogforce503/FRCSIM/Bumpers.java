package org.frogforce503.FRCSIM;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Bryce Paputa
 */
public class Bumpers {
    private final Geometry frontGeometry;
    private final Geometry leftGeometry;
    private final Geometry backGeometry;
    private final Geometry rightGeometry;
    
    public Bumpers(Node chassisNode, float width, float length, float height){
        Node bumperNode = new Node("Bumpers");
        
        Box frontBox = new Box(new Vector3f(length/2+Main.in(3.5f), height, width/2+.001f), new Vector3f(-length/2-Main.in(3.5f), height+Main.in(5), width/2+Main.in(3.5f)));
        frontGeometry = new Geometry("Bumper Front", frontBox);
        frontGeometry.setMaterial(Main.black);
        bumperNode.attachChild(frontGeometry);
        
        Box leftBox = new Box(new Vector3f(length/2+.001f, height, width/2), new Vector3f(length/2+Main.in(3.5f), height+Main.in(5), -width/2));
        leftGeometry = new Geometry("Bumper Left", leftBox);
        leftGeometry.setMaterial(Main.black);
        bumperNode.attachChild(leftGeometry);
        
        Box backBox = new Box(new Vector3f(-length/2-Main.in(3.5f), height, -width/2-.001f), new Vector3f(length/2+Main.in(3.5f), height+Main.in(5), -width/2-Main.in(3.5f)));
        backGeometry = new Geometry("Bumper Front", backBox);
        backGeometry.setMaterial(Main.black);
        bumperNode.attachChild(backGeometry);
        
        Box rightBox = new Box(new Vector3f(-length/2-.001f, height, -width/2), new Vector3f(-length/2-Main.in(3.5f), height+Main.in(5), +width/2));
        rightGeometry = new Geometry("Bumper Left", rightBox);
        rightGeometry.setMaterial(Main.black);
        bumperNode.attachChild(rightGeometry);
        
        chassisNode.attachChild(bumperNode);
    }
    
    public void registerAlliance(Alliance alliance){
        frontGeometry.setMaterial(alliance.material);
        leftGeometry.setMaterial(alliance.material);
        backGeometry.setMaterial(alliance.material);
        rightGeometry.setMaterial(alliance.material);
    }
}

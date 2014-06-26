package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Bryce Paputa
 */
public class Ball {
    public final Alliance alliance;
    private static double speed;
    private static RigidBodyControl sphereControl;
    public Ball(Node rootNode, PhysicsSpace space, Alliance alliance, int ballNumber){
        Sphere sphere = new Sphere(32, 32, Main.in(13));
        Geometry sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(alliance.material);
        sphereGeometry.setLocalTranslation(alliance.position[ballNumber]);
        sphereControl = new RigidBodyControl(.907f);
        sphereGeometry.addControl(sphereControl);
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);
        this.alliance = alliance;
    }
    
    public void update(){
            float slipperiness = 3.5f;
            //sphereControl.applyForce(, new Vector3f(sphereControl.getLinearVelocity().x, sphereControl.getLinearVelocity().y, sphereControl.getLinearVelocity().z));
            sphereControl.applyCentralForce(new Vector3f(-sphereControl.getLinearVelocity().x/slipperiness, -sphereControl.getLinearVelocity().y/slipperiness, -sphereControl.getLinearVelocity().z/slipperiness));
            System.out.println(sphereControl.getPhysicsLocation());
    }
    
}

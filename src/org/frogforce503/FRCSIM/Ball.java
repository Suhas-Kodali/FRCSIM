package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;

/**
 *
 * @author Bryce Paputa
 */
public class Ball {
    public final Alliance alliance;
    private RigidBodyControl sphereControl;
    public static final float drag = 1f/3f;
    private static final ArrayList<Ball> balls = new ArrayList<Ball>(6);
    public Ball(Node rootNode, PhysicsSpace space, Alliance alliance){
        Sphere sphere = new Sphere(32, 32, Main.in(13));
        Geometry sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(alliance.material);
        sphereControl = new RigidBodyControl(.907f);
        sphereGeometry.addControl(sphereControl);
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);
        this.alliance = alliance;
        balls.add(this);
    }
    
    public void update(){
        sphereControl.applyCentralForce(sphereControl.getLinearVelocity().normalize().mult(sphereControl.getLinearVelocity().distanceSquared(Vector3f.ZERO)).mult(-drag));
    }
    
    public static void updateBalls(){
        for(Ball ball : balls){
            ball.update();
        }
    }
}

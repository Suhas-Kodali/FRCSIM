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
    public Geometry sphereGeometry;
    private RigidBodyControl sphereControl;
    public static final float drag = 1f/3f;
    public static final ArrayList<Ball> balls = new ArrayList<Ball>(6);
    public final int number;
    private static int count = 0;
    private boolean scored = false;
    private PhysicsSpace space;
    
    public Ball(Node rootNode, PhysicsSpace space, Alliance alliance){
        this.space = space;
        Sphere sphere = new Sphere(32, 32, Main.in(13));
        sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(alliance.material);
        sphereControl = new RigidBodyControl(.907f);
        sphereGeometry.addControl(sphereControl);
        sphereControl.setUserObject(this);
        sphereControl.setPhysicsLocation(new Vector3f(1, 0, 0));
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);
        this.alliance = alliance;        
        balls.add(this);
        number = count++;
    }
    
    public void update(){
        sphereControl.applyCentralForce(sphereControl.getLinearVelocity().normalize().mult(sphereControl.getLinearVelocity().distanceSquared(Vector3f.ZERO)).mult(-drag));
    }
    
    public static void updateAll(){
        for(Ball ball : balls){
            ball.update();
        }
    }
    
    public RigidBodyControl getRigidBodyControl(){
        return this.sphereControl;
    }
    
    public Geometry getGeometry(){
        return this.sphereGeometry;
    }
    
    @Override
    public boolean equals(Object other){
        if(other instanceof Ball){
            return ((Ball) other).number == this.number;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.number;
        return hash;
    }
    
    public void score(){
        this.scored = true;
    }
    
    public boolean isScored(){
        return scored;
    }
    
    public void destroy(){
        getGeometry().removeFromParent();
        space.remove(getGeometry());
    }
    
    public Vector3f getPosition(){
        return sphereControl.getPhysicsLocation();
    }
    
    public Vector3f getVelocity(){
        return sphereControl.getLinearVelocity();
    }
    
    public void setVelocity(Vector3f v){
        sphereControl.setLinearVelocity(v);
    }
    
    public void setPosition(Vector3f pos){
        sphereControl.setPhysicsLocation(pos);
    }
}

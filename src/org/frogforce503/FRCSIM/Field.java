package org.frogforce503.FRCSIM;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Bryce Paputa
 */
public class Field {

    /**
     * creates a field
     * @param rootNode
     * @param assetManager
     * @param space
     */
    
    public final float width = Main.in(24*12+8);
    public final float length = Main.in(54*12);
    
    public Field(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        AmbientLight ambient = new AmbientLight();
        //rootNode.addLight(ambient);    
        PointLight lamp = new PointLight();
        lamp.setPosition(new Vector3f(0f, 40f, 0f));
        lamp.setRadius(0);
        rootNode.addLight(lamp); 
        
        Box floorBox = new Box(140, 0.25f, 140);
        Geometry floorGeometry = new Geometry("Floor Box", floorBox);
        floorGeometry.setMaterial(Main.darkGray);
        floorGeometry.setLocalTranslation(0, -0, 0);
        Plane floorPlane = new Plane();
        floorPlane.setOriginNormal(new Vector3f(0, 0.25f, 0), Vector3f.UNIT_Y);
        RigidBodyControl floorControl = new RigidBodyControl(new PlaneCollisionShape(floorPlane), 0);
        floorGeometry.addControl(floorControl);
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);
        
        
        Box northWall = new Box(length/2, Main.in(20)/2, Main.in(20f)/2);
        Geometry northWall_geo = new Geometry("east_wall", northWall);
        northWall_geo.setMaterial(Main.green);
        rootNode.attachChild(northWall_geo);
        northWall_geo.setLocalTranslation(0, Main.in(20)/2, -width/2 - Main.in(20)/2);
        RigidBodyControl north_phy = new RigidBodyControl(0f);
        northWall_geo.addControl(north_phy);
        space.add(northWall_geo);
        
        Box southWall = new Box(length/2, Main.in(20)/2, Main.in(20f)/2);
        Geometry southWall_geo = new Geometry("east_wall", southWall);
        southWall_geo.setMaterial(Main.green);
        rootNode.attachChild(southWall_geo);
        southWall_geo.setLocalTranslation(0, Main.in(20)/2, width/2 + Main.in(20)/2);
        RigidBodyControl south_phy = new RigidBodyControl(0f);
        southWall_geo.addControl(south_phy);
        space.add(southWall_geo);
        
        Box goal1 = new Box(Main.in(12)/2, Main.in(6*12+11)/2, width/2 +Main.in(20));
        Geometry goal1Geometry = new Geometry("Goal", goal1);
        goal1Geometry.setMaterial(Main.green);
        goal1Geometry.setLocalTranslation(length/2, Main.in(6*12+11)/2, 0);
        goal1Geometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal1Geometry);
        space.add(goal1Geometry);
        
        Box goal2 = new Box(Main.in(12)/2, Main.in(6*12+11)/2, width/2 + Main.in(20));
        Geometry goal2Geometry = new Geometry("Goal", goal2);
        goal2Geometry.setMaterial(Main.green);
        goal2Geometry.setLocalTranslation(-length/2, Main.in(6*12+11)/2, 0);
        goal2Geometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal2Geometry);
        space.add(goal2Geometry);
        
    }
}

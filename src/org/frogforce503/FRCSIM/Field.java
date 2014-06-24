package org.frogforce503.FRCSIM;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
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
    public Field(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.LightGray);
        rootNode.addLight(light);
        
        Box floorBox = new Box(140, 0.25f, 140);
        Geometry floorGeometry = new Geometry("Floor Box", floorBox);
        floorGeometry.setMaterial(Main.darkGray);
        floorGeometry.setLocalTranslation(0, -5.251f, 0);
        Plane floorPlane = new Plane();
        floorPlane.setOriginNormal(new Vector3f(0, 0.25f, 0), Vector3f.UNIT_Y);
        floorGeometry.addControl(new RigidBodyControl(new PlaneCollisionShape(floorPlane), 0));
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);
        
        float width = Main.in(24*12+8);
        float length = Main.in(54*12);
        
        Box wall1 = new Box(new Vector3f(-length/2f, Main.in(20)-5, -Main.in(20)-width/2f), new Vector3f(length/2f, -5, -width/2f));
        Geometry wall1Geometry = new Geometry("Wall", wall1);
        wall1Geometry.setMaterial(Main.green);
        Plane wallPlane1 = new Plane();
        wallPlane1.setOriginNormal(new Vector3f(0, 0, -width/2f), new Vector3f(0,0,1));
        wall1Geometry.addControl(new RigidBodyControl(new PlaneCollisionShape(wallPlane1), 0));
        rootNode.attachChild(wall1Geometry);
        space.add(wall1Geometry);
        
        Box wall2 = new Box(new Vector3f(-length/2f, Main.in(20)-5, +Main.in(20)+width/2f), new Vector3f(length/2f, -5, width/2f));
        Geometry wall2Geometry = new Geometry("Wall", wall2);
        wall2Geometry.setMaterial(Main.green);
        Plane plane2 = new Plane();
        plane2.setOriginNormal(new Vector3f(0, 0, width/2f), new Vector3f(0,0,-1));
        wall2Geometry.addControl(new RigidBodyControl(new PlaneCollisionShape(plane2), 0));
        rootNode.attachChild(wall2Geometry);
        space.add(wall2Geometry);
        
        Box goal1 = new Box(new Vector3f(length/2f, -5, width/2f+Main.in(20)), new Vector3f(length/2f+Main.in(3), -5+Main.in(6*12+11), -width/2f-Main.in(20)));
        Geometry goal1Geometry = new Geometry("Goal", goal1);
        goal1Geometry.setMaterial(Main.green);
        goal1Geometry.addControl(new RigidBodyControl(new MeshCollisionShape(goal1), 0));
        rootNode.attachChild(goal1Geometry);
        space.add(goal1Geometry);
        
        Box goal2 = new Box(new Vector3f(-length/2f, -5, width/2f+Main.in(20)), new Vector3f(-length/2f-Main.in(3), -5+Main.in(6*12+11), -width/2f-Main.in(20)));
        Geometry goal2Geometry = new Geometry("Goal", goal2);
        goal2Geometry.setMaterial(Main.green);
        goal2Geometry.addControl(new RigidBodyControl(new MeshCollisionShape(goal2), 0));
        rootNode.attachChild(goal2Geometry);
        space.add(goal2Geometry);
        
    }
}

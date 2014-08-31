package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

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
    
    public final static float width = Main.in(24*12+8);
    public static final float length = Main.in(54*12);
    public static final GhostControl redGoalGhost = new GhostControl(new BoxCollisionShape(new Vector3f(Main.in(6)/2, Main.in(37)/2, width/2))), 
            blueGoalGhost = new GhostControl(new BoxCollisionShape(new Vector3f(Main.in(6)/2, Main.in(37)/2, width/2))); 
    private Plane[] exitPlane = new Plane[4];
    
    
    public Field(final Node rootNode, final PhysicsSpace space) {
        
        final PointLight lamp = new PointLight();
        lamp.setPosition(new Vector3f(0f, 40f, 0f));
        lamp.setRadius(100);
        rootNode.addLight(lamp); 
        
        Vector3f exitPlanePosition = new Vector3f(length/2 + Main.in(20f), 0, width/2 + Main.in(20f));
        Vector3f exitPlaneRotation = new Vector3f(-1, 0, 0);
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                exitPlane[j+i*2] = new Plane();
                exitPlane[j+i*2].setOriginNormal(exitPlanePosition, exitPlaneRotation);
                exitPlaneRotation = exitPlaneRotation.cross(Vector3f.UNIT_Y);
            }
            exitPlanePosition = exitPlanePosition.negate();
        }
        
        Geometry floorGeometry = new Geometry("Floor Box", new Box(140, 0f, 140));
        floorGeometry.setMaterial(Main.darkGray);
        floorGeometry.setLocalTranslation(0, -0, 0);
        Plane floorPlane = new Plane();
        floorPlane.setOriginNormal(new Vector3f(0, 0f, 0), Vector3f.UNIT_Y);
        RigidBodyControl floorControl = new RigidBodyControl(new PlaneCollisionShape(floorPlane), 0);
        floorGeometry.addControl(floorControl);
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);
        
        Box truss = new Box(new Vector3f(-Main.in(6), Main.in(-6), Main.in(-170.2f)), new Vector3f(Main.in(6), Main.in(6), Main.in(170.2f)));
        Geometry trussGeometry = new Geometry("truss", truss);
        trussGeometry.setMaterial(Main.sides);
        trussGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        RigidBodyControl trussControl = new RigidBodyControl(0);
        trussGeometry.addControl(trussControl);
        trussControl.setPhysicsLocation(new Vector3f(0, Main.in(65), 0));
        rootNode.attachChild(trussGeometry);
        space.add(trussGeometry);
        
        for(int i = -1; i <= 1; i+=2){
            Box northWall = new Box(length/2, Main.in(20f)/2, Main.in(20f)/2);
            Geometry northWall_geo = new Geometry("side_wall", northWall);
            northWall_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
            northWall_geo.setMaterial(Main.sides);
            rootNode.attachChild(northWall_geo);
            northWall_geo.setLocalTranslation(0, Main.in(10), i*width/2 + i*Main.in(20)/2);
            RigidBodyControl north_phy = new RigidBodyControl(0f);
            northWall_geo.addControl(north_phy);
            space.add(northWall_geo);
        }
        
        Box goal1 = new Box(Main.in(5), Main.in(6*12+10.75f)/2, width/2);
        Geometry goal1Geometry = new Geometry("Goal", goal1);
        goal1Geometry.setMaterial(Main.allianceWalls);
        goal1Geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        goal1Geometry.setLocalTranslation(length/2+Main.in(5)/2, Main.in(6*12+10.75f)/2, 0);
        goal1Geometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal1Geometry);
        space.add(goal1Geometry);
        
        Box goal1TopSouth = new Box(Main.in(1)/2, Main.in(37)/2, Main.in(4.75f)/2);
        Geometry goal1TopSouthGeometry = new Geometry("Goal", goal1TopSouth);
        goal1TopSouthGeometry.setMaterial(Main.red);
        goal1TopSouthGeometry.setLocalTranslation(length/2, Main.in(37)/2 + Main.in(6*12+10.75f), width/2 - Main.in(4.75f)/2);
        goal1TopSouthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal1TopSouthGeometry);
        space.add(goal1TopSouthGeometry);
        
        Box goalTopNorth1 = new Box(Main.in(1)/2, Main.in(37)/2, Main.in(4.75f)/2);
        Geometry goal1TopNorthGeometry = new Geometry("Goal", goalTopNorth1);
        goal1TopNorthGeometry.setMaterial(Main.red);
        goal1TopNorthGeometry.setLocalTranslation(length/2, Main.in(37)/2 + Main.in(6*12+10.75f), -width/2 + Main.in(4.75f)/2);
        goal1TopNorthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal1TopNorthGeometry);
        space.add(goal1TopNorthGeometry);
        
        Box goalTop1 = new Box(Main.in(1)/2, Main.in(4.75f)/2, width/2);
        Geometry goal1TopGeometry = new Geometry("Goal", goalTop1);
        goal1TopGeometry.setMaterial(Main.red);
        goal1TopGeometry.setLocalTranslation(length/2, Main.in(37 - 4.75f/2) + Main.in(6*12+10.75f), 0);
        goal1TopGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal1TopGeometry);
        space.add(goal1TopGeometry);
        
        Box goal2 = new Box(Main.in(5), Main.in(6*12+10.75f)/2, width/2);
        Geometry goal2Geometry = new Geometry("Goal", goal2);
        goal2Geometry.setMaterial(Main.allianceWalls);
        goal2Geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        goal2Geometry.setLocalTranslation(-length/2 - Main.in(5)/2, Main.in(6*12+10.75f)/2, 0);
        goal2Geometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal2Geometry);
        space.add(goal2Geometry);
        
         Box goal2TopSouth = new Box(Main.in(1)/2, Main.in(37)/2, Main.in(4.75f)/2);
        Geometry goal2TopSouthGeometry = new Geometry("Goal", goal2TopSouth);
        goal2TopSouthGeometry.setMaterial(Main.blue);
        goal2TopSouthGeometry.setLocalTranslation(-length/2, Main.in(37)/2 + Main.in(6*12+10.75f), width/2 - Main.in(4.75f)/2);
        goal2TopSouthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal2TopSouthGeometry);
        space.add(goal2TopSouthGeometry);
        
        Box goalTopNorth2 = new Box(Main.in(1)/2, Main.in(37)/2, Main.in(4.75f)/2);
        Geometry goal2TopNorthGeometry = new Geometry("Goal", goalTopNorth2);
        goal2TopNorthGeometry.setMaterial(Main.blue);
        goal2TopNorthGeometry.setLocalTranslation(-length/2, Main.in(37)/2 + Main.in(6*12+10.75f), -width/2 + Main.in(4.75f)/2);
        goal2TopNorthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal2TopNorthGeometry);
        space.add(goal2TopNorthGeometry);
        
        Box goalTop2 = new Box(Main.in(1)/2, Main.in(4.75f)/2, width/2);
        Geometry goal2TopGeometry = new Geometry("Goal", goalTop2);
        goal2TopGeometry.setMaterial(Main.blue);
        goal2TopGeometry.setLocalTranslation(-length/2, Main.in(37 - 4.75f/2) + Main.in(6*12+10.75f), 0);
        goal2TopGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal2TopGeometry);
        space.add(goal2TopGeometry);
        for(int j = -1; j < 2; j = j + 2){
            
        for(int i = -1; i < 2; i = i + 2){ 
        
        Material material;    
            
            if(i > 0){
                material = Main.red;
            }else{
                material = Main.blue;
            }
        
        Box lowGoalTop = new Box(Main.in(1.5f)/2, Main.in(1.5f)/2, Main.in(29)/2);
        Geometry lowGoalTopGeometry = new Geometry("Goal", lowGoalTop);
        lowGoalTopGeometry.setMaterial(Main.gray);
        lowGoalTopGeometry.setLocalTranslation((length*i)/2 - Main.in(32.5f)*i, Main.in(35), width*j/2 - Main.in(29)*j/2);
        lowGoalTopGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalTopGeometry);
        space.add(lowGoalTopGeometry);
        
        Box lowGoalBottom = new Box(Main.in(5f)/2, Main.in(1.5f)/2, Main.in(29)/2);
        Geometry lowGoalBottomGeometry = new Geometry("Goal", lowGoalBottom);
        lowGoalBottomGeometry.setMaterial(Main.gray);
        lowGoalBottomGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f- (1.5f/2))*i, Main.in(7), width*j/2 - Main.in(29)*j/2);
        lowGoalBottomGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalBottomGeometry);
        space.add(lowGoalBottomGeometry);
        
        Box lowGoalBase = new Box(Main.in(1.5f)/2, Main.in(1.5f)/2, Main.in(29)/2);
        Geometry lowGoalBaseGeometry = new Geometry("Goal", lowGoalBase);
        lowGoalBaseGeometry.setMaterial(Main.gray);
        lowGoalBaseGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i, Main.in(1.5f)/2, width*j/2 - Main.in(29)*j/2);
        lowGoalBaseGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalBaseGeometry);
        space.add(lowGoalBaseGeometry);
        
        Box lowGoalBottomNorth = new Box(Main.in(32.5f)/2, Main.in(1.5f)/2, Main.in(1.5f)/2);
        Geometry lowGoalBottomNorthGeometry = new Geometry("Goal", lowGoalBottomNorth);
        lowGoalBottomNorthGeometry.setMaterial(Main.gray);
        lowGoalBottomNorthGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i/2, Main.in(7), width*j/2 - Main.in(29)*j);
        lowGoalBottomNorthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalBottomNorthGeometry);
        space.add(lowGoalBottomNorthGeometry);
        
        Box lowGoalBottomSouth = new Box(Main.in(32.5f)/2, Main.in(1.5f)/2, Main.in(1.5f)/2);
        Geometry lowGoalBottomSouthGeometry = new Geometry("Goal", lowGoalBottomSouth);
        lowGoalBottomSouthGeometry.setMaterial(Main.gray);
        lowGoalBottomSouthGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i/2, Main.in(7), width*j/2 - Main.in(1.5f)*j/2);
        lowGoalBottomSouthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalBottomSouthGeometry);
        space.add(lowGoalBottomSouthGeometry);
        
        Box lowGoalBaseNorth = new Box(Main.in(32.5f)/2, Main.in(1.5f)/2, Main.in(1.5f)/2);
        Geometry lowGoalBaseNorthGeometry = new Geometry("Goal", lowGoalBaseNorth);
        lowGoalBaseNorthGeometry.setMaterial(Main.gray);
        lowGoalBaseNorthGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i/2, Main.in(1.5f)/2, width*j/2 - Main.in(29)*j);
        lowGoalBaseNorthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalBaseNorthGeometry);
        space.add(lowGoalBaseNorthGeometry);
        
        Box lowGoalBaseSouth = new Box(Main.in(32.5f)/2, Main.in(1.5f)/2, Main.in(1.5f)/2);
        Geometry lowGoalBaseSouthGeometry = new Geometry("Goal", lowGoalBaseSouth);
        lowGoalBaseSouthGeometry.setMaterial(Main.gray);
        lowGoalBaseSouthGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i/2, Main.in(1.5f)/2, width*j/2 - Main.in(1.5f)*j/2);
        lowGoalBaseSouthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalBaseSouthGeometry);
        space.add(lowGoalBaseSouthGeometry);
        
        Box lowGoalTopNorth = new Box(Main.in(32.5f)/2, Main.in(1.5f)/2, Main.in(1.5f)/2);
        Geometry lowGoalTopNorthGeometry = new Geometry("Goal", lowGoalTopNorth);
        lowGoalTopNorthGeometry.setMaterial(Main.gray);
        lowGoalTopNorthGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i/2, Main.in(35), width*j/2 - Main.in(29)*j);
        lowGoalTopNorthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalTopNorthGeometry);
        space.add(lowGoalTopNorthGeometry);
        
        Box lowGoalTopSouth = new Box(Main.in(32.5f)/2, Main.in(1.5f)/2, Main.in(1.5f)/2);
        Geometry lowGoalTopSouthGeometry = new Geometry("Goal", lowGoalTopSouth);
        lowGoalTopSouthGeometry.setMaterial(Main.gray);
        lowGoalTopSouthGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i/2, Main.in(35), width*j/2 - Main.in(1.5f)*j/2);
        lowGoalTopSouthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalTopSouthGeometry);
        space.add(lowGoalTopSouthGeometry);
        
        Box lowGoalMiddleNorth = new Box(Main.in(1.5f)/2, Main.in(35f)/2, Main.in(1.5f)/2);
        Geometry lowGoalMiddleNorthGeometry = new Geometry("Goal", lowGoalMiddleNorth);
        lowGoalMiddleNorthGeometry.setMaterial(Main.gray);
        lowGoalMiddleNorthGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i, Main.in(35)/2, width*j/2 - Main.in(29)*j);
        lowGoalMiddleNorthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalMiddleNorthGeometry);
        space.add(lowGoalMiddleNorthGeometry);
        
        Box lowGoalMiddleSouth = new Box(Main.in(1.5f)/2, Main.in(35f)/2, Main.in(1.5f)/2);
        Geometry lowGoalMiddleSouthGeometry = new Geometry("Goal", lowGoalMiddleSouth);
        lowGoalMiddleSouthGeometry.setMaterial(Main.gray);
        lowGoalMiddleSouthGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i, Main.in(35)/2, width*j/2 - Main.in(1.5f)*j/2);
        lowGoalMiddleSouthGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalMiddleSouthGeometry);
        space.add(lowGoalMiddleSouthGeometry);
        
        Box lowGoalMiddle = new Box(Main.in(30f)/2, Main.in(1.5f)/2, Main.in(29f)/2);
        Geometry lowGoalMiddleGeometry = new Geometry("Goal", lowGoalMiddle);
        lowGoalMiddleGeometry.setMaterial(material);
        lowGoalMiddleGeometry.setLocalTranslation(length*i/2 - Main.in(32.5f)*i/2, Main.in(3.5f), width*j/2 - Main.in(30)*j/2);
        Quaternion pitch = new Quaternion();
        pitch.fromAngleAxis((-FastMath.PI/14)*i, new Vector3f(0,0,1));
        lowGoalMiddleGeometry.setLocalRotation(pitch);
        lowGoalMiddleGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(lowGoalMiddleGeometry);
        space.add(lowGoalMiddleGeometry);
        
        }
        }
        
        Node redGoalGhostNode = new Node("a thing");
        redGoalGhostNode.addControl(redGoalGhost);
        redGoalGhostNode.setLocalTranslation(new Vector3f(length/2 + Main.in(18)/2, Main.in(37)/2  + Main.in(6*12+10.75f), 0));
        rootNode.attachChild(redGoalGhostNode);
        space.add(redGoalGhostNode);
       
        Node blueGoalGhostNode = new Node("a thing");
        blueGoalGhostNode.addControl(blueGoalGhost);
        blueGoalGhostNode.setLocalTranslation(new Vector3f(-length/2 - Main.in(18)/2, Main.in(37)/2  + Main.in(6*12+10.75f), 0));
        rootNode.attachChild(blueGoalGhostNode);
        space.add(blueGoalGhostNode);
                
        final float radius = Main.in(12+6.5f), ballRadius = .04f;        
        
        Material material = Main.red;
        
        float z = length/2;
        
        for(int j = 0; j < 2; j++){
            for(int i = 0; i < 20; i++){
                float y = (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(Main.in(i), 2));
                float x = (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(y, 2));
                    Sphere sphere = new Sphere(4, 4, ballRadius);
                Geometry sphereGeometry = new Geometry("Sphere", sphere);
                sphereGeometry.setMaterial(material);
                RigidBodyControl sphereControl = new RigidBodyControl(0f);
                sphereGeometry.setLocalTranslation(z, 
                        y + Main.in(6*12+10.75f) + Main.in(30)/2,
                        -x - width/2 + Main.in(20 + 3));
                sphereGeometry.addControl(sphereControl);
                rootNode.attachChild(sphereGeometry);
                space.add(sphereGeometry);

                sphere = new Sphere(4, 4, ballRadius);
                sphereGeometry = new Geometry("Sphere", sphere);
                sphereGeometry.setMaterial(material);
                sphereControl = new RigidBodyControl(0f);
                sphereGeometry.setLocalTranslation(z, 
                        -y + Main.in(6*12+10.75f) + Main.in(42)/2,
                        -x - width/2 + Main.in(20 + 3));
                sphereGeometry.addControl(sphereControl);
                rootNode.attachChild(sphereGeometry);
                space.add(sphereGeometry);

                sphere = new Sphere(4, 4, ballRadius);
                sphereGeometry = new Geometry("Sphere", sphere);
                sphereGeometry.setMaterial(material);
                sphereControl = new RigidBodyControl(0f);
                sphereGeometry.setLocalTranslation(z, 
                        y + Main.in(6*12+10.75f) + Main.in(30)/2,
                        x + width/2 - Main.in(20 + 3));
                sphereGeometry.addControl(sphereControl);
                rootNode.attachChild(sphereGeometry);
                space.add(sphereGeometry);

                sphere = new Sphere(4, 4, ballRadius);
                sphereGeometry = new Geometry("Sphere", sphere);
                sphereGeometry.setMaterial(material);
                sphereControl = new RigidBodyControl(0f);
                sphereGeometry.setLocalTranslation(z, 
                        -y + Main.in(6*12+10.75f) + Main.in(42)/2,
                        x + width/2 - Main.in(20 + 3));
                sphereGeometry.addControl(sphereControl);
                rootNode.attachChild(sphereGeometry);
                space.add(sphereGeometry);
            }
            material = Main.blue;
            z = -z;
        }
        
        for(int i = 0; i < 19; i++){
            float y = (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(Main.in(i), 2));
            float x = (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(y, 2));
                Sphere sphere = new Sphere(32, 32, ballRadius);
            Geometry sphereGeometry = new Geometry("Sphere", sphere);
            sphereGeometry.setMaterial(Main.red);
            RigidBodyControl sphereControl = new RigidBodyControl(0f);
            sphereGeometry.setLocalTranslation(length/2, 
                    y + Main.in(6*12+10.75f) + Main.in(30)/2,
                    -x - width/2 + Main.in(20 + 3));
            sphereGeometry.addControl(sphereControl);
            rootNode.attachChild(sphereGeometry);
            space.add(sphereGeometry);

            sphere = new Sphere(32, 32, ballRadius);
            sphereGeometry = new Geometry("Sphere", sphere);
            sphereGeometry.setMaterial(Main.red);
            sphereControl = new RigidBodyControl(0f);
            sphereGeometry.setLocalTranslation(length/2, 
                    -y + Main.in(6*12+10.75f) + Main.in(42)/2,
                    -x - width/2 + Main.in(20 + 3));
            sphereGeometry.addControl(sphereControl);
            rootNode.attachChild(sphereGeometry);
            space.add(sphereGeometry);

            sphere = new Sphere(32, 32, ballRadius);
            sphereGeometry = new Geometry("Sphere", sphere);
            sphereGeometry.setMaterial(Main.red);
            sphereControl = new RigidBodyControl(0f);
            sphereGeometry.setLocalTranslation(length/2, 
                    y + Main.in(6*12+10.75f) + Main.in(30)/2,
                    x + width/2 - Main.in(20 + 3));
            sphereGeometry.addControl(sphereControl);
            rootNode.attachChild(sphereGeometry);
            space.add(sphereGeometry);

            sphere = new Sphere(32, 32, ballRadius);
            sphereGeometry = new Geometry("Sphere", sphere);
            sphereGeometry.setMaterial(Main.red);
            sphereControl = new RigidBodyControl(0f);
            sphereGeometry.setLocalTranslation(length/2, 
                    -y + Main.in(6*12+10.75f) + Main.in(42)/2,
                    x + width/2 - Main.in(20 + 3));
            sphereGeometry.addControl(sphereControl);
            rootNode.attachChild(sphereGeometry);
            space.add(sphereGeometry);
            
            
        }
        
        
        
        
    }
    
    public boolean isBallOutOfBounds(final Ball ball){
        for(final Plane plane : exitPlane){
            if(plane.pseudoDistance(ball.getPosition()) < Main.in(0) && (ball.getPosition().y) < Main.in(14f)+.25f){
                return true;
            }
        }
        return false;
    }
    
}

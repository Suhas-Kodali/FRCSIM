package org.frogforce503.FRCSIM;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;

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
    public final static float length = Main.in(54*12);
    public final GhostControl redGoalGhost; 
    public static Integer score = 0;
    private final Vector3f humanPlayer = new Vector3f(0, 0, 0);
    private boolean isScoring = false;
    private boolean isWaiting = false;
    public boolean isInbounding = false;
    public ArrayList<Ball> scoredBalls = new ArrayList<Ball>();
    private Plane[] exitPlane = new Plane[4];
    private Vector3f exitPlanePosition = new Vector3f(length/2 + Main.in(60), 0, width/2 + Main.in(60));
    private Vector3f exitPlaneRotation = new Vector3f(1, 0, 0);
    private PhysicsSpace space;
    private Node rootNode;
    
    
    public Field(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        this.space = space;
        space.addCollisionListener((new ExitFieldListener()));
        this.rootNode = rootNode;
        AmbientLight ambient = new AmbientLight();
        //rootNode.addLight(ambient);    
        PointLight lamp = new PointLight();
        lamp.setPosition(new Vector3f(0f, 40f, 0f));
        lamp.setRadius(100);
        rootNode.addLight(lamp); 
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                exitPlane[j+i*2] = new Plane();
                exitPlane[j+i*2].setOriginNormal(exitPlanePosition, exitPlaneRotation);
                exitPlaneRotation.cross(new Vector3f(0, 0, 1));
                System.out.println(exitPlaneRotation);
                System.out.println(exitPlanePosition);
            }
            exitPlanePosition.negate();
        }
        
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
        
        
        Box northWall = new Box(length/2, Main.in(20f)/2, Main.in(20f)/2);
        Geometry northWall_geo = new Geometry("east_wall", northWall);
        northWall_geo.setMaterial(Main.green);
        rootNode.attachChild(northWall_geo);
        northWall_geo.setLocalTranslation(0, Main.in(20)/2, -width/2 - Main.in(20)/2);
        RigidBodyControl north_phy = new RigidBodyControl(0f);
        northWall_geo.addControl(north_phy);
        space.add(northWall_geo);
        
        Box southWall = new Box(length/2, Main.in(20f)/2, Main.in(20f)/2);
        Geometry southWall_geo = new Geometry("east_wall", southWall);
        southWall_geo.setMaterial(Main.green);
        rootNode.attachChild(southWall_geo);
        southWall_geo.setLocalTranslation(0, Main.in(20)/2, width/2 + Main.in(20)/2);
        RigidBodyControl south_phy = new RigidBodyControl(0f);
        southWall_geo.addControl(south_phy);
        space.add(southWall_geo);
        
        Box goal1 = new Box(Main.in(1)/2, Main.in(6*12+10.75f)/2, width/2 +Main.in(20));
        Geometry goal1Geometry = new Geometry("Goal", goal1);
        goal1Geometry.setMaterial(Main.green);
        goal1Geometry.setLocalTranslation(length/2, Main.in(6*12+10.75f)/2, 0);
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
        
        Box goal2 = new Box(Main.in(2)/2, Main.in(6*12+10.75f)/2, width/2 + Main.in(20));
        Geometry goal2Geometry = new Geometry("Goal", goal2);
        goal2Geometry.setMaterial(Main.green);
        goal2Geometry.setLocalTranslation(-length/2, Main.in(6*12+10.75f)/2, 0);
        goal2Geometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(goal2Geometry);
        space.add(goal2Geometry);
        
      
        redGoalGhost = new GhostControl(new BoxCollisionShape(new Vector3f(Main.in(6)/2, Main.in(37)/2, width/2)));
        
        Box test1 = new Box(Main.in(6)/2, Main.in(37)/2, width/2);
        Geometry testG1 = new Geometry("Goal", test1);
        testG1.setMaterial(Main.blue);
        testG1.setLocalTranslation(new Vector3f(length/2 + Main.in(18)/2, Main.in(37)/2  + Main.in(6*12+10.75f), 0));
        //testG.addControl(new RigidBodyControl(0));
        rootNode.attachChild(testG1);
        //space.add(testG);
        Node eastGoalGhostNode = new Node("a thing");
        eastGoalGhostNode.addControl(redGoalGhost);
        eastGoalGhostNode.setLocalTranslation(new Vector3f(length/2 + Main.in(18)/2, Main.in(37)/2  + Main.in(6*12+10.75f), 0));
        rootNode.attachChild(eastGoalGhostNode);
        space.add(redGoalGhost);
        
        int number; 
                
        float radius = Main.in(12+6.5f), ballRadius = .04f;
        
        double num = ((Math.PI*radius*2)/4)/ballRadius;
        
        number = (int)num;
        
        
        for(int i = 0; i < 19; i++){
            
        float y = (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(Main.in(i), 2));
        float x = (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(y, 2));
            Sphere sphere = new Sphere(32, 32, ballRadius);
        Geometry sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(Main.red);
        RigidBodyControl sphereControl = new RigidBodyControl(0f);
        sphereGeometry.setLocalTranslation(length/2, 
                y + Main.in(6*12+10.75f) + Main.in(37)/2,
                -x - width/2 + Main.in(20 + 3));
        sphereGeometry.addControl(sphereControl);
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);
        
        sphere = new Sphere(32, 32, ballRadius);
        sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(Main.red);
        sphereControl = new RigidBodyControl(0f);
        sphereGeometry.setLocalTranslation(length/2, 
                -y + Main.in(6*12+10.75f) + Main.in(37)/2,
                -x - width/2 + Main.in(20 + 3));
        sphereGeometry.addControl(sphereControl);
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);
        
        sphere = new Sphere(32, 32, ballRadius);
        sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(Main.red);
        sphereControl = new RigidBodyControl(0f);
        sphereGeometry.setLocalTranslation(length/2, 
                y + Main.in(6*12+10.75f) + Main.in(37)/2,
                x + width/2 - Main.in(20 + 3));
        sphereGeometry.addControl(sphereControl);
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);
        
        sphere = new Sphere(32, 32, ballRadius);
        sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(Main.red);
        sphereControl = new RigidBodyControl(0f);
        sphereGeometry.setLocalTranslation(length/2, 
                -y + Main.in(6*12+10.75f) + Main.in(37)/2,
                x + width/2 - Main.in(20 + 3));
        sphereGeometry.addControl(sphereControl);
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);
        
        }
       
        
    }
    
    public void update(){                
                for(int j = redGoalGhost.getOverlappingObjects().size()-1; j >=0; j--){
                    if(redGoalGhost.getOverlapping(j).getUserObject() instanceof Ball && !scoredBalls.contains(redGoalGhost.getOverlapping(j).getUserObject()) && ((Ball) redGoalGhost.getOverlapping(j).getUserObject()).alliance == Alliance.RED){
                            score = score + 1;
                            scoredBalls.add(((Ball) redGoalGhost.getOverlapping(j).getUserObject()));
                            Main.scene.updateVariables();
                            
                        }
                }
    }
    
    public static class ExitFieldListener implements PhysicsCollisionListener{

        public void collision(PhysicsCollisionEvent event) {
            
        }
        
    }
    
   
    
}

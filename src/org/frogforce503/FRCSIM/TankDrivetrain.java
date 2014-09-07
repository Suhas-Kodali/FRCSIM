package org.frogforce503.FRCSIM;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;
import java.util.EnumMap;
import static org.frogforce503.FRCSIM.Main.in;

/**
 * Class for a 8 wheeled tank drivetrain.
 * @author Bryce Paputa
 */
public class TankDrivetrain extends AbstractDrivetrain{
    private final VehicleControl vehicle;
    private final float mass = 30;
    private final float accelerationForce = 175f * mass/30f;
    private final float turningForce = 100f * mass/30f;
    private final float frictionForce = 2f * mass/30f;
    private final float maxSpeed = 17;
    private Alliance alliance;
    private final Node chassisNode, vehicleNode;
    private final Bumpers bumpers;
    private Robot robot;
    private final boolean isPlayer;
    private float lastTurn = 0;
    
    /**
     * Constructor for a tank drivetrain.
     * @param subsystems    Other subsystems of the robot
     * @param space         PhysicsSpace for the drivetrain to exist in
     * @param isPlayer      Is this drivetrain for a human player
     */
    public TankDrivetrain(final ArrayList<AbstractSubsystem> subsystems, final  PhysicsSpace space, final boolean isPlayer) {
        this.isPlayer = isPlayer;
        chassisNode = new Node("chassis Node");
        Geometry chassisGeometry = new Geometry("Chassis", new Box(new Vector3f(0, in(3), 0), in(14), in(2.5f), in(14)));
        chassisGeometry.setMaterial(Main.chassis);
        chassisGeometry.setQueueBucket(Bucket.Transparent);
        chassisNode.attachChild(chassisGeometry);
        
        
        bumpers = new Bumpers(chassisNode, in(28), in(28), in(2));
        for(final AbstractSubsystem subsystem : subsystems){
            subsystem.registerPhysics(chassisNode, space);
        }
        
        vehicleNode=new Node("vehicleNode");
        vehicleNode.attachChild(chassisNode);
        vehicle = new VehicleControl(CollisionShapeFactory.createDynamicMeshShape(chassisNode), 400);
        vehicleNode.addControl(vehicle);
        
        //setting suspension values for wheels, this can be a bit tricky
        //see also https://docs.google.com/Doc?docid=0AXVUZ5xw6XpKZGNuZG56a3FfMzU0Z2NyZnF4Zmo&hl=en
        final float stiffness = 60;//200=f1 car
        final float compValue = .3f; //(should be lower than damp)
        final float dampValue = .4f;
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setFrictionSlip(1.5f);
        vehicle.setMass(mass);
        //Create four wheels and add them at their locations
        final Vector3f wheelDirection = new Vector3f(0, -1, 0);
        final Vector3f wheelAxle = new Vector3f(-1, 0, 0);
        final float radius = in(2);
        final float width = in(1);
        final float restLength = in(2);
        final float yOff = in(3);
        final float xOff = in(14);
        final float zOff = in(11.25f);
        final float rocker = in(.5f);

        Cylinder wheelMesh = new Cylinder(16, 16, radius, width, true);

        float[][] pos = new float[8][3];
        
        for(int i = 0; i < 8; i++){
            if(i<=3){
                pos[i][0]=-xOff+width/2+in(1);
            } else {
                pos[i][0]=xOff-width/2-in(1);
            }
            switch(i%4){
                case 0:
                    pos[i][1]=zOff;
                    break;
                case 1:
                    pos[i][1]=zOff/3;
                    break;
                case 2:
                    pos[i][1]=-zOff/3;
                    break;
                case 3:
                    pos[i][1]=-zOff;                    
            }
            if(i%4 % 3 ==0){
                pos[i][2]=yOff;
            } else {
                pos[i][2]=yOff-rocker;
            }
            
            Node node = new Node("wheel node");
            Geometry wheel = new Geometry("wheel", wheelMesh);
            node.attachChild(wheel);
            wheel.rotate(0, FastMath.HALF_PI, 0);
            wheel.setMaterial(Main.black);
            
            vehicle.addWheel(node, new Vector3f(pos[i][0], pos[i][2], pos[i][1]),
                    wheelDirection, wheelAxle, restLength, radius, false);
            vehicleNode.attachChild(node);
            vehicle.setFriction(.1f);   
        }            
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPhysics(final Node rootNode, final PhysicsSpace space){
        rootNode.attachChild(vehicleNode);
        space.add(vehicle);
    }
    
    /**
     * Sends commands to the wheels using arcade control
     * @param pow   Forwards driving power
     * @param turn  Turning power
     */
    protected void updateArcade(float pow, float turn){
        pow = (pow>1? 1 : (pow<-1? -1 : pow));
        turn = (turn>1? 1 : (turn<-1? -1 : turn));
        float accelerationFactor, left, right;
        
        final float curSpeed = vehicle.getCurrentVehicleSpeedKmHour();
        if(pow*curSpeed > 0){
            accelerationFactor = (maxSpeed-Math.abs(curSpeed))/maxSpeed * accelerationForce;
        } else {
            accelerationFactor = accelerationForce;
        }
        
        left  = accelerationFactor * (pow) + turningForce * turn;
        right = accelerationFactor * (pow) - turningForce * turn;
        
        if(Math.abs(lastTurn)>.05 && Math.abs(turn)<.05){
            vehicle.setAngularVelocity(vehicle.getAngularVelocity().divide(4));
        }
        lastTurn = turn;
        
        for(int i = 0; i < 4; i++){
            vehicle.accelerate(i, left);
        }
        for(int i = 4; i < 8; i++){
            vehicle.accelerate(i, right);
        }
        
        vehicle.brake(frictionForce * 1f / (1-Math.abs(pow)));
        applyDownforce();
    }
    
    protected void updateTank(final float cleft, final float cright){
        updateArcade(cright+cleft, cright-cleft);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public VehicleControl getVehicleControl() {
        return vehicle;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerOtherSubsystems(final EnumMap<SubsystemType, AbstractSubsystem> subsystems, final Robot robot) {
        this.robot = robot;
        this.alliance = robot.alliance;
        bumpers.registerAlliance(alliance, isPlayer);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void driveToPoint(final Vector3f point, final DriveDirection direction){
        driveToPoint(point, direction, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void turnTowardsPoint(final Vector3f point){
        driveToPoint(point, DriveDirection.Towards, false);
    }
    
    private void driveToPoint(Vector3f point, DriveDirection direction, final boolean canDrive) {
        final Vector3f curPos = vehicle.getPhysicsLocation();
        final Vector3f redObstruction = Robot.getClosestRobot(curPos, Alliance.Red).getPosition(), blueObstruction = Robot.getClosestRobot(curPos, Alliance.Blue).getPosition();
        point = avoidObstructions(curPos, point, (redObstruction.distanceSquared(curPos)>blueObstruction.distanceSquared(curPos)? blueObstruction : redObstruction));
        
        float turn = 1, pow = (canDrive? 1 : 0);
        Vector3f vehicleVector = vehicle.getForwardVector(null), vectorToPoint = point.subtract(curPos);
        if(FastMath.abs(curPos.z) > in(12*7) && FastMath.abs(curPos.x) > in(12*22) && vectorToPoint.normalize().angleBetween(vehicleVector)<15){
            direction = DriveDirection.DontCare;
            if(Math.abs(vehicleVector.dot(Vector3f.UNIT_X)) > Math.abs(vehicleVector.dot(Vector3f.UNIT_Z))){
                point = new Vector3f(FastMath.sign(curPos.x)*in(12*15), 0, 0);
            } else {
                point = new Vector3f(FastMath.sign(curPos.x)*in(12*22), 0, 0);
            }
            vectorToPoint = point.subtract(curPos);
            pow = 1;
        }        
        float angle = FastMath.atan2(vehicleVector.cross(vectorToPoint).length(), vehicleVector.dot(vectorToPoint)) * -FastMath.sign(vectorToPoint.cross(vehicleVector).dot(Vector3f.UNIT_Y));
        
        if(direction == DriveDirection.Away || (direction == DriveDirection.DontCare && FastMath.abs(angle)>FastMath.HALF_PI)){
            vehicleVector = vehicleVector.negate();
            angle = -(FastMath.sign(angle) * FastMath.PI-angle);
            pow *= -1;
        }
        
        if(FastMath.abs(angle)>FastMath.QUARTER_PI){
            turn = 3;
            pow = 0;                        
        }
        
        pow *= vectorToPoint.dot(vehicleVector);
        if(vectorToPoint.dot(vehicleVector) < 0){
            pow *= 1.5;
        }
        turn *= angle * Math.min(3, vectorToPoint.length()) / 5; 
        
        if(FastMath.abs(pow) < FastMath.abs(turn) && robot.isTouchingWall()){
            turn = FastMath.sign(turn);
            pow = FastMath.sign(pow);
        }
        
        updateArcade(pow, turn);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(){
        return "TankDrivetrain";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String detailedToString(String offset) {
        return offset + "TankDrivetrain{\n"+offset+"    isPlayer: " + isPlayer +"\n"+offset+"}";
    }
}

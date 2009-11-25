package ar.edu.itba.cg;

import ar.edu.itba.cg.utils.ColladaModelLoader;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.Joint;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.RotationalJointAxis;
import com.jmex.physics.material.Material;

public class Dynamics {
	private Node rootNode;
	private PhysicsSpace physicsSpace;
	private Renderer renderer;
	private SceneParameters params;
	// Objects
	public DynamicPhysicsNode ball;
	public DynamicPhysicsNode anchor;
	public DynamicPhysicsNode arrow;
	private Joint joint;
	private DynamicPhysicsNode[] pins;
	private boolean[] pinsDown;
	
	
	public Dynamics( Node rootNode, PhysicsSpace physicsSpace, Renderer renderer, SceneParameters parameters ) {
		this.rootNode = rootNode;
		this.physicsSpace = physicsSpace;
		this.renderer = renderer;
		this.params = parameters;
	}
	
	
	public void createDynamicWorld() {
		// Ball
		this.createBall();
		// Pins
		this.createPins();	
	}
	
	
	public void createBall() {
		Sphere ballVisual = new Sphere("ball", new Vector3f(0, 0, 0), params.BALL_SAMPLES, params.BALL_SAMPLES, params.BALL_RADIUS );
		ballVisual.setModelBound( new BoundingSphere() ); 
		ballVisual.updateModelBound();
		Utils.setColor( ballVisual, ColorRGBA.green, params.HIGH_SHININESS, ColorRGBA.white, renderer );
		Utils.setTexture( ballVisual, "resources/textures/marble.jpg", renderer );
		ball = physicsSpace.createDynamicNode();
		ball.setName( "ball" );
		ball.setMaterial( Material.GRANITE );
		ball.attachChild( ballVisual );
		ball.generatePhysicsGeometry(); 
		ball.setMass( params.BALL_WEIGHT );
		rootNode.attachChild( ball );
		// The joint to make the pendulum with the ball
		anchor = physicsSpace.createDynamicNode();
		anchor.setAffectedByGravity( false );
		anchor.setIsCollidable( false );
		joint = physicsSpace.createJoint();
		RotationalJointAxis rotationalAxis = joint.createRotationalAxis();
        rotationalAxis.setDirection( new Vector3f( 1, 0, 0 ) );
        rotationalAxis.setPositionMaximum( (float)Math.PI/2 );
        rotationalAxis.setPositionMinimum( -(float)Math.PI/2 );
		// The arrow
        Quad arrowVisual = new Quad("arrow", params.BALL_DIAMETER / 8, params.BALL_DIAMETER * 2);
		arrowVisual.setModelBound( new BoundingSphere() ); 
		arrowVisual.updateModelBound();
		Utils.setColor( arrowVisual, ColorRGBA.red, params.NO_SHININESS, ColorRGBA.white, renderer );
		arrow = physicsSpace.createDynamicNode();
		arrow.setActive(false);
		arrow.setIsCollidable(false);
		arrow.setAffectedByGravity(false);
		arrow.setName( "arrow" );
		arrow.attachChild( arrowVisual );
		arrow.generatePhysicsGeometry(); 
		rootNode.attachChild( arrow );
        resetBall();
	}
	
	
	public void resetBall() {
		ball.setActive(true);
		ball.clearDynamics();
		ball.unrest();
		ball.setLocalTranslation( new Vector3f(0, params.BALL_RADIUS_EXTRA + params.BALL_RADIUS * 1.5F , params.APPROACH_LENGTH / 2) );
		ball.setLocalRotation( new Quaternion() ); // This is need for the joint to work correctly
		anchor.setLocalTranslation( 0, params.BALL_RADIUS_EXTRA + 0.8F, params.APPROACH_LENGTH / 2 );
		anchor.setLocalRotation( new Quaternion() );
		joint.attach( anchor, ball );
		arrow.setLocalRotation( new Quaternion( new float[] {(float)-Math.PI/2,0,0} ) );
		arrow.setLocalTranslation( new Vector3f(0,params.BALL_RADIUS_EXTRA + 0.001F ,params.APPROACH_LENGTH / 2) );
	}
	
	
	public void releaseBall() {
		joint.detach();
	}
	
	
	public float getBallZ() {
		return ball.getLocalRotation().z;
	}
	
	
	public void addForceZ( float z ) {
		Vector3f speed = new Vector3f( 0, 0, z );
		ball.unrest();
		ball.addForce( speed );
	}
	
	
	public void addTorqueZ( float z ) {
		Vector3f speed = new Vector3f( 0, 0, z);
		ball.addTorque( speed );
	}


	public void moveAnchorX(float x) {
		anchor.getLocalTranslation().addLocal( x, 0, 0 );
		arrow.getLocalTranslation().addLocal( x, 0, 0 );
	}
	
	
	public float getAnchorX() {
		return anchor.getLocalTranslation().x; 
	}
	
	
	public void moveAnchorZ(float z) {
		anchor.getLocalTranslation().addLocal( 0, 0, z );
		arrow.getLocalTranslation().addLocal( 0, 0, z );
	}
	
	
	public float getAnchorZ() {
		return anchor.getLocalTranslation().z; 
	}
	
	
	public void rotateAnchor(float z) {
		float []angles = anchor.getLocalRotation().toAngles(null);
		angles[1] = angles[1] + z;
		anchor.setLocalRotation( new Quaternion(angles) );
		angles = arrow.getLocalRotation().toAngles(null);
		angles[1] = angles[1] + z;
		arrow.setLocalRotation( new Quaternion(angles) );
	}
	
	
	public float getAnchorRotation() {
		return anchor.getLocalRotation().toAngles(null)[1]; 
	}

	
	public void createPins() {
		this.pinsDown = new boolean[10];
		this.pins = new DynamicPhysicsNode[10];
		for( int i = 0; i < 10; i++ ) {
			pinsDown[i] = false;
			//Cylinder pinVisual = new Cylinder("pin_"+ i, params.AXIS_SAMPLES, params.RADIAL_SAMPLES, params.PIN_RADIUS, params.PIN_HEIGHT, true);
			Spatial pinVisual = ((Spatial)(new ColladaModelLoader()).getModel("resources/birillo.dae").getChild("mesh1-geometry-material0"));
			BoundingBox pinBound = (BoundingBox)((SharedMesh)pinVisual).getModelBound();
			pinVisual.setLocalTranslation(new Vector3f().subtract(pinBound.getCenter()));
			pinVisual.setModelBound( new BoundingBox() );
			pinVisual.updateModelBound();
			pinVisual.lockMeshes();
			Utils.setColor( pinVisual, ColorRGBA.red, params.HIGH_SHININESS, ColorRGBA.white, renderer );
			pins[i] = physicsSpace.createDynamicNode();
			pins[i].setName( "pin_" + i);
			//pins[i].setCenterOfMass( new Vector3f(0,0,PIN_HEIGHT*1/8) );
			pins[i].setMaterial( Material.GRANITE );
			pins[i].attachChild(pinVisual);
			pins[i].generatePhysicsGeometry();
			pins[i].setMass( params.PIN_WEIGHT );
			rootNode.attachChild( pins[i] );
		}
		resetPins();
	}
	
	
	public Vector3f getPinPosition(int i) {
		switch( i ) {
			case 0:
				return new Vector3f( params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT );
			case 1:
				return new Vector3f( -params.PIN_WIDTHHALFDIST + params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT - params.PIN_HEIGHTDIST );
			case 2:
				return new Vector3f( params.PIN_WIDTHHALFDIST + params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT - params.PIN_HEIGHTDIST );
			case 3:
				return new Vector3f( -params.PIN_WIDTHDIST + params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT - (2 * params.PIN_HEIGHTDIST) );
			case 4:
				return new Vector3f( params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT - (2 * params.PIN_HEIGHTDIST) );
			case 5:
				return new Vector3f( params.PIN_WIDTHDIST + params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT - (2 * params.PIN_HEIGHTDIST) );
			case 6:
				return new Vector3f( -(params.PIN_WIDTHDIST + params.PIN_WIDTHHALFDIST) + params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT - (3 * params.PIN_HEIGHTDIST) );
			case 7:
				return new Vector3f( -params.PIN_WIDTHHALFDIST + params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT - (3 * params.PIN_HEIGHTDIST) );
			case 8:
				return new Vector3f( params.PIN_WIDTHHALFDIST + params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT - (3 * params.PIN_HEIGHTDIST) );
			case 9:
				return new Vector3f( (params.PIN_WIDTHDIST + params.PIN_WIDTHHALFDIST)+ params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT - (3 * params.PIN_HEIGHTDIST) );
			default:
				return new Vector3f( params.INITIAL_POS, params.BALL_RADIUS_EXTRA + (params.PIN_HEIGHT/2), params.DIST2PIT );
		}
	}
	
	
	public void resetPins() {
		for( int i = 0; i < 10; i++ ){
			pinsDown[i] = false;
			pins[i].rest();
			pins[i].clearDynamics();
			pins[i].setLocalRotation(new Quaternion( new float[]{(float)Math.PI/2,0,0} ));
			pins[i].setLocalScale(0.06f);
			pins[i].setLocalTranslation( getPinPosition(i) );
		}
	}
	
	
	public void removePins() {
		for( int i = 0; i < 10; i++ ) {
			pins[i].rest();
			pins[i].clearDynamics();
			if( isPinDown(pins[i]) ) {
				pins[i].setLocalRotation(new Quaternion( new float[]{(float)Math.PI/2,0,0} ));
				pins[i].setLocalTranslation( new Vector3f(9999,9999,9999) );
			}
		}
	}
	
	
	//Calculates if the coordinates are inside the box
	public boolean outOfBounds(float x, float z){
		if( (x > -params.LANE_WIDTH/2 && x < params.LANE_WIDTH/2) &&
			(z > -params.LANE_LENGTH && z < (-params.LANE_LENGTH + params.BOXMACHINE_LENGTH)) ) {
			return true;
		}else{
			return false;
		}
	}
	
	
	public boolean isPinDown(DynamicPhysicsNode pin){
		Double tippingCouple = Math.PI*7/36;
		if( !this.outOfBounds(pin.getLocalTranslation().x,pin.getLocalTranslation().z) ) {
			return true;
		}else if( (Math.abs( pin.getLocalRotation().toAngles(null)[0] - Math.PI/2 ) > tippingCouple) || 
        	(Math.abs( pin.getLocalRotation().toAngles(null)[2] - 0 ) > tippingCouple) ) {
            return true;           
        }
		return false;
	}
	
	
	public int numberOfPins() {
		int count = 0;
		for( int i = 0; i < 10; i++ ) {
			if( isPinDown(pins[i]) ) {
               count++;
			}
        }
		return count;
	}
	
	
}

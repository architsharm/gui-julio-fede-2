package ar.edu.itba.cg;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Cylinder;
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
	public Joint joint;
	public DynamicPhysicsNode[] pins;
	public boolean[] pinsDown;
	
	
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
		this.ball = physicsSpace.createDynamicNode();
		ball.setName( "ball" );
		this.ball.setMaterial( Material.GRANITE );
		this.ball.attachChild( ballVisual );
		this.ball.generatePhysicsGeometry(); 
		this.ball.setMass( params.BALL_WEIGHT );
		rootNode.attachChild( this.ball );
		resetBall();
	}
	
	
	public void resetBall() {
		ball.setActive(true);
		ball.clearDynamics();
		ball.unrest();
		ball.setLocalTranslation( new Vector3f(0, params.BALL_RADIUS_EXTRA + params.BALL_RADIUS * 1.5F , params.APPROACH_LENGTH / 2) );
		ball.setLocalRotation( new Quaternion() );
		if( this.joint != null ) {
			this.joint.reset();
			this.joint.detach();
		}
		this.joint = physicsSpace.createJoint();
		joint.attach( this.ball );
		joint.setAnchor( new Vector3f(0, params.BALL_RADIUS_EXTRA + params.BALL_RADIUS * 10, params.APPROACH_LENGTH / 2) );
        RotationalJointAxis rotationalAxis = joint.createRotationalAxis();
        rotationalAxis.setDirection( new Vector3f( 1, 0, 0 ) );
        rotationalAxis.setPositionMaximum( (float)Math.PI/2 );
        rotationalAxis.setPositionMinimum( -(float)Math.PI/2 );
	}
	
	
	public void createPins() {
		this.pinsDown = new boolean[10];
		this.pins = new DynamicPhysicsNode[10];
		for( int i = 0; i < 10; i++ ){
			pinsDown[i] = false;
			Cylinder pinVisual = new Cylinder("pin_"+ i, params.AXIS_SAMPLES, params.RADIAL_SAMPLES, params.PIN_RADIUS, params.PIN_HEIGHT, true);
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
			pins[i].setLocalTranslation( getPinPosition(i) );
		}
	}
	
	
	public void removePins() {
		for( int i = 0; i < 10; i++ ){
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
			if( (x > -params.LANE_WIDTH/2 && x < params.LANE_WIDTH/2)&&(z > -params.LANE_LENGTH && z < (-params.LANE_LENGTH + params.BOXMACHINE_LENGTH)) )
				return true;
			return false;
	}
	
	
	public boolean isPinDown(DynamicPhysicsNode pin){
		
		Double tippingCouple = Math.PI*7/36;
		
		if(!this.outOfBounds(pin.getLocalTranslation().x,pin.getLocalTranslation().z)){
			return true;
		}
		else if( (Math.abs( pin.getLocalRotation().toAngles(null)[0] - Math.PI/2 ) > tippingCouple) || 
        	(Math.abs( pin.getLocalRotation().toAngles(null)[2] - 0 ) > tippingCouple)) 
        {
            return true;           
        }
		return false;
		
	}
	
	
	public int numberOfPins() {
		int count = 0;
		
		for( int i = 0; i < 10; i++) {
			if(isPinDown(pins[i]))
               count++;
        }
		return count;
	}
	
	
}

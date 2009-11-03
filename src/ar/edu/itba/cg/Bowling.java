package ar.edu.itba.cg;

import java.net.MalformedURLException;
import java.net.URL;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.light.PointLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;
import com.jmex.physics.util.PhysicsPicker;
import com.jmex.physics.util.SimplePhysicsGame;

//	W	 Move Forward
//	A	 Strafe Left
//	S	 Move Backward
//	D	 Strafe Right
//	Up	 Look Up
//	Down	 Look Down
//	Left	 Look Left
//	Right	 Look Right
//	T	 Wireframe Mode on/off
//	P	 Pause On/Off
//	L	 Lights On/Off
//	C	 Print Camera Position
//	B	 Bounding Volumes On/Off
//	N	 Normals On/Off
//	F1	 Take Screenshot

public class Bowling extends SimplePhysicsGame {
	private static final String IMAGE_LOGO = "resources/logo.jpg";
	private static final String TITLE = "Bowling";
	// Parameters are in centimeters!
	// Pin Parameters
	private static final float PIN_HEIGHT = 40;
	private static final float PIN_RADIUS = 6.5F;
	private static final float PIN_WEIGHT = 2;
	private static final int AXIS_SAMPLES = 4;		// The definition of the pin
	private static final int RADIAL_SAMPLES = 10;	// The definition of the pin
	// Ball Parameters
	private static final float BALL_RADIUS = 15.0F;
	private static final float BALL_WEIGHT = 10;
	private static final int BALL_SAMPLES = 50;		// The definition of the ball
	// Gutter Parameters
	private static final float GUTTER_EXTRA = 1.20F;// How much bigger or smaller than the ball (1 is the same)
	private static final int GUTTER_SAMPLES = 100;	// The definition of the gutters
	// Lane Parameters
	// The middle of the foul line is at 0, BALL_RADIUS, 0
	// Ten Pin Bowling: http://en.wikipedia.org/wiki/Tenpin
	private static final int LANE_WIDTH = 105;
	private static final int LANE_LENGTH = 1800;
	// Approach Parameters
	// Behind the foul line is an "approach" used to gain speed
	private static final int APPROACH_LENGTH = 500;
	// Room Parameters
	private static final int ROOM_WIDTH = 800;
	private static final int ROOM_HEIGHT = 300;
	// Final box
	private static final int BOX_LENGTH = 100;
	private static final int BOX_HEIGHT = 50;
	// Calculated parameters
	private static float BALL_RADIUS_EXTRA = BALL_RADIUS * GUTTER_EXTRA;
	private static float BALL_DIAMETER = BALL_RADIUS * 2;
	private static float BALL_DIAMETER_EXTRA = BALL_RADIUS_EXTRA * 2;
	private static float ROOM_LENGTH = LANE_LENGTH + APPROACH_LENGTH + BOX_LENGTH;
	private static float ROOM_CENTER_X = 0;
	private static float ROOM_CENTER_Y = ROOM_HEIGHT / 2;
	private static float ROOM_CENTER_Z = APPROACH_LENGTH / 2 - BOX_LENGTH / 2 - LANE_LENGTH / 2;
	//Pin Positions
	//Distance between to pins (12 inches)
	private static final float PIN_WIDTHDIST = 30.5F;
	//Half a distance between to pins (6 inches)
	private static final float PIN_WIDTHHALFDIST = 15.2F; 
	//Depth distance between two rows of pins (10.39 inches)
	private static final float PIN_HEIGHTDIST = 26.4F;
	//Initial position of the pin 1
	private static float INITIAL_POS = 0F;
	//Distance between the pin 1 and the pit relative to the end of the lane
	private static float DIST2PIT = -LANE_LENGTH + 86.8F ;
	private static final Vector3f [] positions = {	new Vector3f(INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)),
													new Vector3f(-PIN_WIDTHHALFDIST + INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)- PIN_HEIGHTDIST),
													new Vector3f(PIN_WIDTHHALFDIST + INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)- PIN_HEIGHTDIST),
													new Vector3f(-PIN_WIDTHDIST + INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)- (2 * PIN_HEIGHTDIST)),
													new Vector3f(INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)- (2 * PIN_HEIGHTDIST)),
													new Vector3f(PIN_WIDTHDIST + INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)- (2 * PIN_HEIGHTDIST)),
													new Vector3f(-(PIN_WIDTHDIST + PIN_WIDTHHALFDIST)+ INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)-(3 * PIN_HEIGHTDIST)),
													new Vector3f(-PIN_WIDTHHALFDIST + INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)-(3 * PIN_HEIGHTDIST)),
													new Vector3f(PIN_WIDTHHALFDIST + INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)-(3 * PIN_HEIGHTDIST)),
													new Vector3f((PIN_WIDTHDIST+PIN_WIDTHHALFDIST)+ INITIAL_POS,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), (DIST2PIT)-(3 * PIN_HEIGHTDIST))};
	private DynamicPhysicsNode ball;
	
	
	
	public static void main(String [] args) throws MalformedURLException {
		Bowling app = new Bowling(); 
		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
		app.start();
	}
	
	
	@Override
	protected void simpleUpdate() {
		//cameraInputHandler.setEnabled( MouseInput.get().isButtonDown( 1 ) );
		if ( KeyInput.get().isKeyDown(KeyInput.KEY_SPACE)) {
			ball.setLocalTranslation(new Vector3f(0, BALL_DIAMETER_EXTRA, 0));
		}
		if ( KeyInput.get().isKeyDown(KeyInput.KEY_PGUP)) {
			Vector3f speed = new Vector3f(0,0,-50);
			ball.addForce(speed);
		}
	}
	
	
	@Override
	protected void simpleInitGame() {
		getPhysicsSpace().setAutoRestThreshold( 0.2f );
        setPhysicsSpeed( 20 );
		// Display
		this.createDisplay();
		// Room
		this.createRoom();
		// Box
		this.createBox();
		// Lane
		this.createLane();
		// Approach
		this.createApproach();
		// Ball
		this.createBall();
		// Pins
		this.createPins();
		// Gutters
		this.createGutters();
		// Lights
		this.createIlumination();
		// Camera
		this.createCamera();
		// Update
		new PhysicsPicker( input, rootNode, getPhysicsSpace() );
        MouseInput.get().setCursorVisible( true );
		rootNode.updateRenderState();
	}
	
	
	private void createDisplay() {
		display.getRenderer().setBackgroundColor( ColorRGBA.black.clone() );
		display.getRenderer().getCamera().setFrustumFar( ROOM_LENGTH * 1.1f);
		display.setTitle(TITLE);
	}
	
	
	private void createRoom() {
		Node room = new Node("room");
		// Top and bottom
		Quad wallDownVisual = new Quad("wall_down", ROOM_WIDTH, ROOM_LENGTH );
		Quad wallUpVisual =   new Quad("wall_up",   ROOM_WIDTH, ROOM_LENGTH );
		wallDownVisual.setModelBound( new BoundingBox() ); 
		wallDownVisual.updateModelBound();
		wallUpVisual.setModelBound( new BoundingBox() ); 
		wallUpVisual.updateModelBound();
		setColor( wallDownVisual, ColorRGBA.gray.clone() );
		setColor( wallUpVisual, ColorRGBA.gray.clone() );
		StaticPhysicsNode wallDown = getPhysicsSpace().createStaticNode();
		StaticPhysicsNode wallUp = getPhysicsSpace().createStaticNode();
		wallDown.attachChild( wallDownVisual );
		wallUp.attachChild( wallUpVisual );
		wallDown.setMaterial( Material.CONCRETE );
		wallUp.setMaterial( Material.CONCRETE );
		wallDown.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, 0 } ) );
		wallUp.setLocalRotation(   new Quaternion( new float[]{ (float)Math.PI/2, 0, 0 } ) );
		wallDown.setLocalTranslation( 0, 0, ROOM_CENTER_Z );
		wallUp.setLocalTranslation(   0, ROOM_HEIGHT, ROOM_CENTER_Z );
		wallDown.generatePhysicsGeometry();
		wallUp.generatePhysicsGeometry();
		// Left and right
		Quad wallLeftVisual =  new Quad("wall_left",  ROOM_LENGTH, ROOM_HEIGHT);
		Quad wallRightVisual = new Quad("wall_right", ROOM_LENGTH, ROOM_HEIGHT);
		wallLeftVisual.setModelBound( new BoundingBox() ); 
		wallLeftVisual.updateModelBound();
		wallRightVisual.setModelBound( new BoundingBox() ); 
		wallRightVisual.updateModelBound();
		setColor( wallLeftVisual, ColorRGBA.gray.clone() );
		setColor( wallRightVisual, ColorRGBA.gray.clone() );
		StaticPhysicsNode wallLeft = getPhysicsSpace().createStaticNode();
		StaticPhysicsNode wallRight = getPhysicsSpace().createStaticNode();
		wallLeft.attachChild( wallLeftVisual );
		wallRight.attachChild( wallRightVisual );
		wallLeft.setMaterial( Material.CONCRETE );
		wallRight.setMaterial( Material.CONCRETE );
		wallLeft.setLocalRotation(  new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		wallRight.setLocalRotation( new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		wallLeft.setLocalTranslation(  -ROOM_WIDTH / 2, ROOM_CENTER_Y, ROOM_CENTER_Z );
		wallRight.setLocalTranslation(  ROOM_WIDTH / 2, ROOM_CENTER_Y, ROOM_CENTER_Z );
		wallRight.generatePhysicsGeometry();
		wallLeft.generatePhysicsGeometry();
		// Back
		Quad wallBackVisual =  new Quad("wall_back", ROOM_HEIGHT, ROOM_WIDTH);
		wallBackVisual.setModelBound( new BoundingBox() ); 
		wallBackVisual.updateModelBound();
		setColor( wallBackVisual, ColorRGBA.gray.clone() );
		StaticPhysicsNode wallBack = getPhysicsSpace().createStaticNode();
		wallBack.attachChild( wallBackVisual );
		wallBack.setMaterial( Material.CONCRETE );
		wallBack.setLocalRotation(  new Quaternion( new float[]{ 0, 0, (float)Math.PI/2 } ) );
		wallBack.setLocalTranslation(  0, ROOM_CENTER_Y, APPROACH_LENGTH );
		wallBack.generatePhysicsGeometry();
		// Front
		Quad wallFrontVisual =  new Quad("wall_front", ROOM_HEIGHT, ROOM_WIDTH);
		wallFrontVisual.setModelBound( new BoundingBox() ); 
		wallFrontVisual.updateModelBound();
		setColor( wallFrontVisual, ColorRGBA.gray.clone() );
		StaticPhysicsNode wallFront = getPhysicsSpace().createStaticNode();
		wallFront.attachChild( wallFrontVisual );
		wallFront.setMaterial( Material.CONCRETE );
		wallFront.setLocalRotation(  new Quaternion( new float[]{ 0, 0, (float)Math.PI/2 } ) );
		wallFront.setLocalTranslation(  0, ROOM_CENTER_Y, -(LANE_LENGTH + BOX_LENGTH) );
		wallFront.generatePhysicsGeometry();
		// Attach
		room.attachChild( wallDown );
		room.attachChild( wallUp );
		room.attachChild( wallLeft );
		room.attachChild( wallRight );
		room.attachChild( wallBack );
		room.attachChild( wallFront );
		room.setModelBound( new BoundingBox() ); 
		room.updateModelBound();
		rootNode.attachChild( room );	
	}
	
	
	private void createBox() {
		Node box = new Node("box");
		// Top
		Quad boxTopVisual = new Quad("box_top", LANE_WIDTH + BALL_DIAMETER_EXTRA * 2, BOX_LENGTH );
		boxTopVisual.setModelBound( new BoundingBox() ); 
		boxTopVisual.updateModelBound();
		setColor( boxTopVisual, ColorRGBA.darkGray.clone() );
		StaticPhysicsNode boxTop = getPhysicsSpace().createStaticNode();
		boxTop.attachChild( boxTopVisual );
		boxTop.setMaterial( Material.CONCRETE );
		boxTop.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, 0 } ) );
		boxTop.setLocalTranslation(  0, BALL_RADIUS_EXTRA + BOX_HEIGHT, -(LANE_LENGTH + BOX_LENGTH / 2) );
		boxTop.generatePhysicsGeometry();
		// Left and right
		Quad boxLeftVisual =  new Quad("box_left",  BOX_LENGTH, BALL_RADIUS_EXTRA + BOX_HEIGHT);
		Quad boxRightVisual = new Quad("box_right", BOX_LENGTH, BALL_RADIUS_EXTRA + BOX_HEIGHT);
		boxLeftVisual.setModelBound( new BoundingBox() ); 
		boxLeftVisual.updateModelBound();
		boxRightVisual.setModelBound( new BoundingBox() ); 
		boxRightVisual.updateModelBound();
		setColor( boxLeftVisual, ColorRGBA.darkGray.clone() );
		setColor( boxRightVisual, ColorRGBA.darkGray.clone() );
		StaticPhysicsNode boxLeft = getPhysicsSpace().createStaticNode();
		StaticPhysicsNode boxRight = getPhysicsSpace().createStaticNode();
		boxLeft.attachChild( boxLeftVisual );
		boxRight.attachChild( boxRightVisual );
		boxLeft.setMaterial( Material.CONCRETE );
		boxRight.setMaterial( Material.CONCRETE );
		boxLeft.setLocalRotation(  new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		boxRight.setLocalRotation( new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		boxLeft.setLocalTranslation(  -(LANE_WIDTH / 2 + BALL_DIAMETER_EXTRA), (BALL_RADIUS_EXTRA + BOX_HEIGHT)/2, -(LANE_LENGTH + BOX_LENGTH / 2) );
		boxRight.setLocalTranslation(   LANE_WIDTH / 2 + BALL_DIAMETER_EXTRA,  (BALL_RADIUS_EXTRA + BOX_HEIGHT)/2, -(LANE_LENGTH + BOX_LENGTH / 2) );
		boxRight.generatePhysicsGeometry();
		boxLeft.generatePhysicsGeometry();
		// Attach
		box.attachChild( boxTop );
		box.attachChild( boxLeft );
		box.attachChild( boxRight );
		box.setModelBound( new BoundingBox() ); 
		box.updateModelBound();
		rootNode.attachChild( box );	
	}
	
	
	private void createLane() {
		Box laneVisual = new Box("lane", new Vector3f(0,0,0), LANE_WIDTH / 2, BALL_RADIUS_EXTRA / 2, LANE_LENGTH / 2 );
		laneVisual.setModelBound( new BoundingBox() ); 
		laneVisual.updateModelBound();
		setColor( laneVisual, ColorRGBA.brown );
		StaticPhysicsNode lane = getPhysicsSpace().createStaticNode();
		lane.setMaterial( Material.WOOD );
		lane.attachChild( laneVisual );
		lane.setLocalTranslation( new Vector3f(0, BALL_RADIUS_EXTRA / 2, -LANE_LENGTH / 2) );
		lane.generatePhysicsGeometry();
		rootNode.attachChild( lane );
	}
	
	
	private void createApproach() {
		Box approachVisual = new Box( "approach", new Vector3f(0,0,0), LANE_WIDTH/2 + BALL_DIAMETER_EXTRA, BALL_RADIUS_EXTRA / 2, APPROACH_LENGTH / 2 );
		approachVisual.setModelBound( new BoundingBox() ); 
		approachVisual.updateModelBound();
		setColor( approachVisual, ColorRGBA.brown );
		StaticPhysicsNode approach = getPhysicsSpace().createStaticNode();
		approach.setMaterial( Material.WOOD );
		approach.attachChild( approachVisual );
		approach.setLocalTranslation( new Vector3f(0, BALL_RADIUS_EXTRA / 2, APPROACH_LENGTH / 2) );
		approach.generatePhysicsGeometry();
		rootNode.attachChild( approach );
	}
	
	
	private void createBall() {
		Sphere ballVisual = new Sphere("ball", new Vector3f(0, 0, 0), BALL_SAMPLES, BALL_SAMPLES, BALL_RADIUS);
		ballVisual.setModelBound( new BoundingSphere() ); 
		ballVisual.updateModelBound();
		setColor( ballVisual, ColorRGBA.blue );
		this.ball = getPhysicsSpace().createDynamicNode();
		this.ball.setMaterial( Material.PLASTIC );
		this.ball.attachChild( ballVisual );
		this.ball.setLocalTranslation( new Vector3f(0, BALL_DIAMETER_EXTRA, 0) );
		this.ball.generatePhysicsGeometry(); 
		this.ball.setMass(BALL_WEIGHT);
		rootNode.attachChild( this.ball );
	}
	
	
	private void createPins() {
		for(int i=0; i<10;i++){
			Cylinder pinVisual = new Cylinder("pin"+ i,AXIS_SAMPLES,RADIAL_SAMPLES,PIN_RADIUS,PIN_HEIGHT,true);
			pinVisual.setModelBound( new BoundingBox() );
			pinVisual.updateModelBound();
			setColor( pinVisual, ColorRGBA.red );
			DynamicPhysicsNode pin = getPhysicsSpace().createDynamicNode();
			pin.setMaterial( Material.PLASTIC );
			pin.attachChild(pinVisual);
			pinVisual.setLocalRotation(new Quaternion(new float[]{(float)Math.PI/2,0,0}));
			pin.setLocalTranslation(positions[i]);
			pin.generatePhysicsGeometry();
			pin.setMass(PIN_WEIGHT);
			rootNode.attachChild( pin );
		}
	}
	
	
	private void createGutters() {
		float circumference = 2.0F * (float)Math.PI * BALL_RADIUS_EXTRA;
		// Node gutterLeft =  getPhysicsSpace().createStaticNode(); // new Node("gutter_left");
		// Node gutterRight = getPhysicsSpace().createStaticNode(); // new Node("gutter_right");
		Quad gutterBorderLeftVisual  = new Quad("gutter_border_left",  BALL_RADIUS_EXTRA, LANE_LENGTH);
		Quad gutterBorderRightVisual = new Quad("gutter_border_right", BALL_RADIUS_EXTRA, LANE_LENGTH);
		gutterBorderLeftVisual.setModelBound( new BoundingBox() ); 
		gutterBorderLeftVisual.updateModelBound();
		gutterBorderRightVisual.setModelBound( new BoundingBox() ); 
		gutterBorderRightVisual.updateModelBound();
		StaticPhysicsNode gutterBorderLeft = getPhysicsSpace().createStaticNode();
		StaticPhysicsNode gutterBorderRight = getPhysicsSpace().createStaticNode();
		gutterBorderLeft.setMaterial( Material.IRON );
		gutterBorderRight.setMaterial( Material.IRON );
		gutterBorderLeft.attachChild( gutterBorderLeftVisual );
		gutterBorderRight.attachChild( gutterBorderRightVisual );
		gutterBorderLeft.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, -(float)Math.PI/2 } ) );
		gutterBorderRight.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, -(float)Math.PI/2 } ) );
		gutterBorderLeft.setLocalTranslation( -(LANE_WIDTH/2 + BALL_DIAMETER_EXTRA), BALL_RADIUS_EXTRA / 2, -(LANE_LENGTH / 2) );
		gutterBorderRight.setLocalTranslation( LANE_WIDTH/2 + BALL_DIAMETER_EXTRA  , BALL_RADIUS_EXTRA / 2, -(LANE_LENGTH / 2) );
		gutterBorderLeft.generatePhysicsGeometry();
		gutterBorderRight.generatePhysicsGeometry();
		rootNode.attachChild( gutterBorderLeft );
		rootNode.attachChild( gutterBorderRight );
		for( int i = 1; i < GUTTER_SAMPLES; i++ ) {
			Quad leftVisual =  new Quad( "gutter_left_"  + String.valueOf(i), circumference / GUTTER_SAMPLES, LANE_LENGTH);
			Quad rightVisual = new Quad( "gutter_right_" + String.valueOf(i), circumference / GUTTER_SAMPLES, LANE_LENGTH);
			leftVisual.setModelBound( new BoundingBox() ); 
			leftVisual.updateModelBound();
			rightVisual.setModelBound( new BoundingBox() ); 
			rightVisual.updateModelBound();
			StaticPhysicsNode left = getPhysicsSpace().createStaticNode();
			StaticPhysicsNode right = getPhysicsSpace().createStaticNode();
			left.setMaterial( Material.IRON );
			right.setMaterial( Material.IRON );
			left.attachChild( leftVisual );
			right.attachChild( rightVisual );
			float rotation =  -(float)Math.PI/2 + (float)Math.PI / GUTTER_SAMPLES * i; 
			left.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			right.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			float angle = (float)Math.PI + (float)Math.PI / GUTTER_SAMPLES * i;
			left.setLocalTranslation( -(LANE_WIDTH/2 + BALL_RADIUS_EXTRA) + BALL_RADIUS_EXTRA * (float)Math.cos( angle ), BALL_RADIUS_EXTRA + BALL_RADIUS_EXTRA * (float)Math.sin( angle ), -(LANE_LENGTH / 2) );
			right.setLocalTranslation( LANE_WIDTH/2 + BALL_RADIUS_EXTRA + BALL_RADIUS_EXTRA * (float)Math.cos( angle ), BALL_RADIUS_EXTRA + BALL_RADIUS_EXTRA * (float)Math.sin( angle ), -(LANE_LENGTH / 2) );
			left.generatePhysicsGeometry();
			right.generatePhysicsGeometry();
			rootNode.attachChild( left );
			rootNode.attachChild( right );
		}	
	}
	
	
	private void createCamera() {
		cam.setLocation( new Vector3f(0,80,30) );
	}
	
	
	
	private void createIlumination() {
		((PointLight)lightState.get(0)).setLocation(new Vector3f(0, ROOM_HEIGHT * 0.9F, 0));
		lightState.setTwoSidedLighting(true);
		
//		// Create a point light
//		PointLight l=new PointLight();
//		// Give it a location
//		l.setLocation(new Vector3f(0,25,15));
//		// Make it a red light
//		l.setDiffuse(ColorRGBA.red);
//		// Create a LightState to put my light in
//		LightState ls=display.getRenderer().createLightState();
//		// Attach the light
//		ls.attach(l);
//		lightState.detachAll();
		
	} 

// Clear Light state
//        lightState.detachAll();
//        lightState.setEnabled( true );
//        lightState.setGlobalAmbient( ColorRGBA.white.clone() );
        // Light
//		PointLight light = new PointLight();
//        light.setAmbient( ColorRGBA.white.clone() );
//        light.setDiffuse( ColorRGBA.white.clone() );
//        light.setSpecular( ColorRGBA.white.clone() );
//        light.setAttenuate( true );
//        light.setLocation( new Vector3f( 0, ROOM_HEIGHT * 0.9F, 0 ) );
//        light.setEnabled( true );
//        lightState.attach( light );

	
	private void setColor( Spatial spatial, ColorRGBA color ) {
        final MaterialState materialState = display.getRenderer().createMaterialState();
        materialState.setDiffuse( color );
        spatial.setRenderState( materialState );
    }

}

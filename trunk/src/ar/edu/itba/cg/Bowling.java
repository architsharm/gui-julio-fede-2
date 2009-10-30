package ar.edu.itba.cg;

import java.net.MalformedURLException;
import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.light.PointLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;

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

public class Bowling extends SimpleGame {
	private static final String IMAGE_LOGO = "resources/logo.jpg";
	private static final String TITLE = "Bowling";
	// Pin Parameters
	private static final float PIN_HEIGHT = 40;
	private static final float PIN_RADIUS = 6.5F;
	private static final float PIN_WEIGHT = 7255;
	private static final int AXIS_SAMPLES = 4;
	private static final int RADIAL_SAMPLES = 10;
	// Ball Parameters
	private static final float BALL_RADIUS = 21.8F;
	private static final float BALL_WEIGHT = 7255;
	private static final int BALL_SAMPLES = 50;
	// Gutter Parameters
	private static final float GUTTER_EXTRA = 1.20F;
	private static final int GUTTER_SAMPLES = 100;
	// Lane Parameters
	// The middle of the foul line is at 0, BALL_RADIUS, 0
	// Ten Pin Bowling: http://en.wikipedia.org/wiki/Tenpin
	private static final int LANE_WIDTH = 105; // centimeters
	private static final int LANE_LENGTH = 1800; // centimeters
	// Approach Parameters
	// Behind the foul line is an “approach” used to gain speed
	private static final int APPROACH_LENGTH = 500;
	// Room Parameters
	private static final int ROOM_WIDTH = 800;
	private static final int ROOM_HEIGHT = 300;
	// Calculated parameters
	private static float BALL_RADIUS_EXTRA = BALL_RADIUS * GUTTER_EXTRA;
	private static float BALL_DIAMETER = BALL_RADIUS * 2;
	private static float BALL_DIAMETER_EXTRA = BALL_RADIUS_EXTRA * 2;
	private static float ROOM_LENGTH = LANE_LENGTH + APPROACH_LENGTH;
	private static float ROOM_CENTER_Z = -LANE_LENGTH / 2 + APPROACH_LENGTH / 2;
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

	
	public static void main(String [] args) throws MalformedURLException {
		Bowling app = new Bowling(); 
		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
		app.start();
	}
	
	
	@Override
	protected void simpleInitGame() {		
		// Display
		this.createDisplay();
		// Room
		this.createRoom();
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
		rootNode.updateRenderState();
	}
	
	
	private void createDisplay() {
		display.getRenderer().setBackgroundColor( ColorRGBA.black.clone() );
		display.setTitle(TITLE);
	}
	
	
	private void createRoom() {
		Node room = new Node("room");
		// Top and bottom
		Quad wallDown = new Quad("wall_down", ROOM_WIDTH, ROOM_LENGTH);
		Quad wallUp = new Quad("wall_up", ROOM_WIDTH, ROOM_LENGTH);
		wallDown.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, 0 } ) );
		wallUp.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, 0 } ) );
		wallDown.setLocalTranslation( 0, 0, ROOM_CENTER_Z );
		wallUp.setLocalTranslation(  0, ROOM_HEIGHT, ROOM_CENTER_Z );
		// Left and right
		Quad wallLeft =  new Quad("wall_left",  ROOM_LENGTH, ROOM_HEIGHT);
		Quad wallRight = new Quad("wall_right", ROOM_LENGTH, ROOM_HEIGHT);
		wallLeft.setLocalRotation(  new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		wallRight.setLocalRotation( new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		wallLeft.setLocalTranslation(  -ROOM_WIDTH / 2, ROOM_HEIGHT / 2, ROOM_CENTER_Z );
		wallRight.setLocalTranslation(  ROOM_WIDTH / 2, ROOM_HEIGHT / 2, ROOM_CENTER_Z );
		// Back
		Quad wallBack =  new Quad("wall_back", ROOM_HEIGHT, ROOM_WIDTH);
		wallBack.setLocalRotation(  new Quaternion( new float[]{ 0, 0, (float)Math.PI/2 } ) );
		wallBack.setLocalTranslation(  0, ROOM_HEIGHT / 2, APPROACH_LENGTH );
		// TODO: WALL FRONT!
		// Bounds
		wallDown.setModelBound( new BoundingBox() ); 
		wallDown.updateModelBound();
		wallUp.setModelBound( new BoundingBox() ); 
		wallUp.updateModelBound();
		wallLeft.setModelBound( new BoundingBox() ); 
		wallLeft.updateModelBound();
		wallRight.setModelBound( new BoundingBox() ); 
		wallRight.updateModelBound();
		wallBack.setModelBound( new BoundingBox() ); 
		wallBack.updateModelBound();
		// Attach
		room.attachChild( wallDown );
		room.attachChild( wallUp );
		room.attachChild( wallLeft );
		room.attachChild( wallRight );
		room.attachChild( wallBack );
		room.setModelBound( new BoundingBox() ); 
		room.updateModelBound();
		rootNode.attachChild( room );	
	}
	
	
	private void createLane() {
		Box lane = new Box( "lane", new Vector3f(-LANE_WIDTH/2, BALL_RADIUS_EXTRA, 0), new Vector3f(LANE_WIDTH/2, 0, -LANE_LENGTH) );
		lane.setModelBound( new BoundingBox() ); 
		lane.updateModelBound();
		MaterialState materialState = display.getRenderer().createMaterialState();
		materialState.setColorMaterial( MaterialState.ColorMaterial.Diffuse );
		materialState.setDiffuse( ColorRGBA.green.clone() );
		lane.setRenderState( materialState );
		rootNode.attachChild( lane );
	}
	
	
	private void createApproach() {
		Box approach = new Box( "approach", new Vector3f(-(LANE_WIDTH/2 + BALL_DIAMETER_EXTRA), BALL_RADIUS_EXTRA, 0), new Vector3f(LANE_WIDTH/2 + BALL_DIAMETER_EXTRA, 0, APPROACH_LENGTH) );
		approach.setModelBound( new BoundingBox() ); 
		approach.updateModelBound();
		rootNode.attachChild( approach );
	}
	
	
	private void createBall() {
		Sphere ball = new Sphere("ball", new Vector3f(0, BALL_DIAMETER, 0), BALL_SAMPLES, BALL_SAMPLES, BALL_RADIUS);
		ball.setModelBound( new BoundingSphere() ); 
		ball.updateModelBound();
		ball.setDefaultColor( ColorRGBA.red.clone() );
		ball.setSolidColor( ColorRGBA.red.clone() );
		rootNode.attachChild( ball );
	}
	
	
	private void createPins() {
		for(int i=0; i<10;i++){
			Cylinder pin = new Cylinder("pin"+ i,AXIS_SAMPLES,RADIAL_SAMPLES,PIN_RADIUS,PIN_HEIGHT,true);
			pin.setModelBound( new BoundingBox() ); 
			pin.setLocalTranslation(positions[i]);
			pin.setLocalRotation(new Quaternion(new float[]{(float)Math.PI/2,0,0}));
			pin.updateModelBound();
			pin.setDefaultColor( ColorRGBA.blue.clone() );
			pin.setSolidColor( ColorRGBA.blue.clone() );
			rootNode.attachChild( pin );
		}
	}
	
	
	private void createGutters() {
		float circumference = 2.0F * (float)Math.PI * BALL_RADIUS_EXTRA;
		Node gutterLeft =  new Node("gutter_left");
		Node gutterRight = new Node("gutter_right");
		Quad gutterBorderLeft  = new Quad("gutter_border_left",  BALL_RADIUS_EXTRA, LANE_LENGTH);
		Quad gutterBorderRight = new Quad("gutter_border_right", BALL_RADIUS_EXTRA, LANE_LENGTH);
		gutterBorderLeft.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, -(float)Math.PI/2 } ) );
		gutterBorderRight.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, -(float)Math.PI/2 } ) );
		gutterBorderLeft.setLocalTranslation( -(LANE_WIDTH/2 + BALL_DIAMETER_EXTRA), BALL_RADIUS_EXTRA / 2, -(LANE_LENGTH / 2) );
		gutterBorderRight.setLocalTranslation( LANE_WIDTH/2 + BALL_DIAMETER_EXTRA  , BALL_RADIUS_EXTRA / 2, -(LANE_LENGTH / 2) );
		gutterLeft.attachChild( gutterBorderLeft );
		gutterLeft.attachChild( gutterBorderRight );
		gutterBorderLeft.setModelBound( new BoundingBox() ); 
		gutterBorderLeft.updateModelBound();
		gutterBorderRight.setModelBound( new BoundingBox() ); 
		gutterRight.updateModelBound();
		for( int i = 1; i < GUTTER_SAMPLES; i++ ) {
			Quad left =  new Quad( "gutter_left_"  + String.valueOf(i), circumference / GUTTER_SAMPLES, LANE_LENGTH);
			Quad right = new Quad( "gutter_right_" + String.valueOf(i), circumference / GUTTER_SAMPLES, LANE_LENGTH);
			float rotation =  -(float)Math.PI/2 + (float)Math.PI / GUTTER_SAMPLES * i; 
			left.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			right.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			float angle = (float)Math.PI + (float)Math.PI / GUTTER_SAMPLES * i;
			left.setLocalTranslation( -(LANE_WIDTH/2 + BALL_RADIUS_EXTRA) + BALL_RADIUS_EXTRA * (float)Math.cos( angle ), BALL_RADIUS_EXTRA + BALL_RADIUS_EXTRA * (float)Math.sin( angle ), -(LANE_LENGTH / 2) );
			right.setLocalTranslation( LANE_WIDTH/2 + BALL_RADIUS_EXTRA + BALL_RADIUS_EXTRA * (float)Math.cos( angle ), BALL_RADIUS_EXTRA + BALL_RADIUS_EXTRA * (float)Math.sin( angle ), -(LANE_LENGTH / 2) );
			left.setModelBound( new BoundingBox() ); 
			left.updateModelBound();
			right.setModelBound( new BoundingBox() ); 
			right.updateModelBound();
			gutterLeft.attachChild( left );
			gutterLeft.attachChild( right );
		}
		gutterLeft.setModelBound( new BoundingBox() ); 
		gutterLeft.updateModelBound();
		gutterRight.setModelBound( new BoundingBox() ); 
		gutterRight.updateModelBound();
		rootNode.attachChild( gutterLeft );
		rootNode.attachChild( gutterRight );
	}
	
	
	private void createCamera() {
		cam.setLocation( new Vector3f(0,80,30) );
	}
	
	
	private void createIlumination() {
		((PointLight)lightState.get(0)).setLocation(new Vector3f(0, ROOM_HEIGHT * 0.9F, 0));
		lightState.setTwoSidedLighting(true);
		
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
	}
	
	
}

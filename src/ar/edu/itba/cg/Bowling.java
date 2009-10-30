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
	//Pin Positions
	private static final float PIN_WIDTHHALFDIST = 15.2F; 
	private static final float PIN_WIDTHDIST = 30.5F;
	private static final float PIN_HEIGHTDIST = 26.4F;
	private static final Vector3f [] positions = {	new Vector3f(0,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)),
													new Vector3f(-PIN_WIDTHHALFDIST,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)- PIN_HEIGHTDIST),
													new Vector3f(PIN_WIDTHHALFDIST,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)- PIN_HEIGHTDIST),
													new Vector3f(-PIN_WIDTHDIST,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)- (2 * PIN_HEIGHTDIST)),
													new Vector3f(0,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)- (2 * PIN_HEIGHTDIST)),
													new Vector3f(PIN_WIDTHDIST,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)- (2 * PIN_HEIGHTDIST)),
													new Vector3f(-(PIN_WIDTHDIST + PIN_WIDTHHALFDIST),BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)-(3 * PIN_HEIGHTDIST)),
													new Vector3f(-PIN_WIDTHHALFDIST,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)-(3 * PIN_HEIGHTDIST)),
													new Vector3f(PIN_WIDTHHALFDIST,BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)-(3 * PIN_HEIGHTDIST)),
													new Vector3f((PIN_WIDTHDIST+PIN_WIDTHHALFDIST),BALL_RADIUS_EXTRA + (PIN_HEIGHT/2), -(LANE_LENGTH/2)-(3 * PIN_HEIGHTDIST))};

	
	public static void main(String [] args) throws MalformedURLException {
		Bowling app = new Bowling(); 
		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
		app.start();
	}
	
	@Override
	protected void simpleInitGame() {		
		// Title
		display.setTitle(TITLE);
		// Backgournd color
		display.getRenderer().setBackgroundColor( ColorRGBA.black.clone() );
		// Room!
		Box room = new Box( "room", new Vector3f(-ROOM_WIDTH/2, ROOM_HEIGHT, APPROACH_LENGTH), new Vector3f(ROOM_WIDTH/2, 0, -LANE_LENGTH) );
		room.setModelBound( new BoundingBox() ); 
		room.updateModelBound();
		room.setIsCollidable( true );
		rootNode.attachChild( room );
		// Lane
		Box lane = new Box( "lane", new Vector3f(-LANE_WIDTH/2, BALL_RADIUS_EXTRA, 0), new Vector3f(LANE_WIDTH/2, 0, -LANE_LENGTH) );
		lane.setModelBound( new BoundingBox() ); 
		lane.updateModelBound();
		lane.setIsCollidable( true );
		MaterialState materialState = display.getRenderer().createMaterialState();
		materialState.setColorMaterial( MaterialState.ColorMaterial.Diffuse );
		materialState.setDiffuse( ColorRGBA.green.clone() );
		lane.setRenderState( materialState );
		rootNode.attachChild( lane );
		// Approach
		Box approach = new Box( "approach", new Vector3f(-(LANE_WIDTH/2 + BALL_DIAMETER_EXTRA), BALL_RADIUS_EXTRA, 0), new Vector3f(LANE_WIDTH/2 + BALL_DIAMETER_EXTRA, 0, APPROACH_LENGTH) );
		approach.setModelBound( new BoundingBox() ); 
		approach.updateModelBound();
		rootNode.attachChild( approach );
		// Ball
		Sphere ball = new Sphere("ball", new Vector3f(0, BALL_DIAMETER, 0), BALL_SAMPLES, BALL_SAMPLES, BALL_RADIUS);
		ball.setModelBound( new BoundingSphere() ); 
		ball.updateModelBound();
		ball.setDefaultColor( ColorRGBA.red.clone() );
		ball.setSolidColor( ColorRGBA.red.clone() );
		rootNode.attachChild( ball );
		
		//pins
		for(int i=0; i<10;i++){
			Cylinder pin = new Cylinder("pin"+ i,100,100,PIN_RADIUS,PIN_HEIGHT,true);
			pin.setModelBound( new BoundingBox() ); 
			pin.setLocalTranslation(positions[i]);
			pin.setLocalRotation(new Quaternion(new float[]{(float)Math.PI/2,0,0}));
			pin.updateModelBound();
			pin.setDefaultColor( ColorRGBA.blue.clone() );
			pin.setSolidColor( ColorRGBA.blue.clone() );
			rootNode.attachChild( pin );
		}
		
		
		// Gutters
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
		// Camera
		cam.setLocation( new Vector3f(0,80,30) );
		// Update
		rootNode.updateRenderState();
	}

}

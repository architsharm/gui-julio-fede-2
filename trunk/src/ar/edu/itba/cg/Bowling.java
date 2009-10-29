package ar.edu.itba.cg;

import java.net.MalformedURLException;
import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.light.PointLight;
import com.jme.math.Matrix3f;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;

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
	private static final float BALL_RADIUS = 21.8F;
	private static final float BALL_WEIGHT = 7255;
	private static final int BALL_SAMPLES = 50;
	private static final float GUTTER_EXTRA = 1.10F;
	private static final int GUTTER_SAMPLES = 100;
	// The middle of the foul line is at 0, BALL_RADIUS, 0
	// Ten Pin Bowling: http://en.wikipedia.org/wiki/Tenpin
	private static final int LANE_WIDTH = 105; // centimeters
	private static final int LANE_LENGTH = 1800; // centimeters
	// Behind the foul line is an “approach” used to gain speed
	private static final int APPROACH_LENGTH = 500;
	// Room
	private static final int ROOM_WIDTH = 800;
	private static final int ROOM_HEIGHT = 300;
	
	
	public static void main(String [] args) throws MalformedURLException {
		Bowling app = new Bowling(); 
		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
		app.start();
	}
	
	@Override
	protected void simpleInitGame() {
		float BALL_DIAMETER = BALL_RADIUS * 2;
		float BALL_RADIUS_EXTRA = BALL_RADIUS * GUTTER_EXTRA;
		float BALL_DIAMETER_EXTRA = BALL_RADIUS_EXTRA * 2;
		// Title
		display.setTitle(TITLE);
		// Backgournd color
		display.getRenderer().setBackgroundColor( ColorRGBA.black.clone() );
		// Room
		Box room = new Box( "room", new Vector3f(-ROOM_WIDTH/2, ROOM_HEIGHT, APPROACH_LENGTH), new Vector3f(ROOM_WIDTH/2, 0, -LANE_LENGTH) );
		room.setModelBound( new BoundingBox() ); 
		room.updateModelBound();
		rootNode.attachChild( room );
		// Lane
		Box lane = new Box( "lane", new Vector3f(-LANE_WIDTH/2, BALL_RADIUS_EXTRA, 0), new Vector3f(LANE_WIDTH/2, 0, -LANE_LENGTH) );
		lane.setModelBound( new BoundingBox() ); 
		lane.updateModelBound();
		rootNode.attachChild( lane );
		// Approach
		Box approach = new Box( "approach", new Vector3f(-(LANE_WIDTH/2 + BALL_DIAMETER_EXTRA), BALL_RADIUS_EXTRA, 0), new Vector3f(LANE_WIDTH/2 + BALL_DIAMETER_EXTRA, 0, APPROACH_LENGTH) );
		approach.setModelBound( new BoundingBox() ); 
		approach.updateModelBound();
		rootNode.attachChild( approach );
		// Ball
		Sphere ball = new Sphere("ball", new Vector3f(0, BALL_DIAMETER, 0), BALL_SAMPLES, BALL_SAMPLES, BALL_RADIUS);
		ball.setModelBound( new BoundingBox() ); 
		ball.updateModelBound();
		ball.setDefaultColor( ColorRGBA.red.clone() );
		ball.setSolidColor( ColorRGBA.red.clone() );
		rootNode.attachChild( ball );
		// Gutters
		float circumference = 2.0F * (float)Math.PI * BALL_RADIUS_EXTRA;
		Node gutterLeft = new Node("gutter_left");
		Node gutterRight = new Node("gutter_left");
		for( int i = 1; i < GUTTER_SAMPLES; i++ ) {
			Quad left =  new Quad( "gutter_left_"  + String.valueOf(i), circumference / GUTTER_SAMPLES, LANE_LENGTH);
			Quad right = new Quad( "gutter_right_" + String.valueOf(i), circumference / GUTTER_SAMPLES, LANE_LENGTH);
			float rotation =  -(float)Math.PI/2 + (float)Math.PI / GUTTER_SAMPLES * i; 
			left.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			right.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			float angle = (float)Math.PI + (float)Math.PI / GUTTER_SAMPLES * i;
			left.setLocalTranslation( -(LANE_WIDTH/2 + BALL_RADIUS_EXTRA) + BALL_RADIUS_EXTRA * (float)Math.cos( angle ), BALL_RADIUS_EXTRA + BALL_RADIUS_EXTRA * (float)Math.sin( angle ), -(LANE_LENGTH / 2) );
			right.setLocalTranslation( LANE_WIDTH/2 + BALL_RADIUS_EXTRA + BALL_RADIUS_EXTRA * (float)Math.cos( angle ), BALL_RADIUS_EXTRA + BALL_RADIUS_EXTRA * (float)Math.sin( angle ), -(LANE_LENGTH / 2) );
			gutterLeft.attachChild( left );
			gutterLeft.attachChild( right );
		}
		rootNode.attachChild( gutterLeft );
		rootNode.attachChild( gutterRight );
		// Light state
		lightState = display.getRenderer().createLightState();
        lightState.detachAll();
        // Light
		PointLight light = new PointLight();
        light.setAmbient( new ColorRGBA( 1f, 1f, 1f, 1.0f ) );
        light.setLocation( new Vector3f( 0, ROOM_HEIGHT * 0.9F, -LANE_LENGTH/2 ) );
        light.setEnabled( true );
        lightState.attach( light );
        rootNode.setRenderState( lightState );
		// Camera
		cam.setLocation( new Vector3f(0,80,30) );
	}

}

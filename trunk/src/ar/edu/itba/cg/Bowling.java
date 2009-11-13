package ar.edu.itba.cg;

import java.net.MalformedURLException;
import java.net.URL;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.input.ChaseCamera;
import com.jme.input.KeyInput;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Text;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Sphere;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.MusicTrackQueue;
import com.jmex.audio.MusicTrackQueue.RepeatType;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.contact.ContactCallback;
import com.jmex.physics.contact.PendingContact;
import com.jmex.physics.material.Material;
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
//  V	 Show Physics
//	F1	 Take Screenshot

public class Bowling extends SimplePhysicsGame {
	public static String IMAGE_LOGO = "resources/logo.jpg";
	private Scene scene;
	private SceneParameters params;
	public DynamicPhysicsNode ball;
	public DynamicPhysicsNode[] pins;
	public boolean[] pinsDown;
	public Text score;
	// Sounds
	private MusicTrackQueue audioQueue;
	private AudioTrack pinHit;
	// Game States
	public static enum States {SHOOTING, ROWLING};
	
	
	public static void main(String [] args) throws MalformedURLException {
		Bowling app = new Bowling(); 
		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
		app.start();
	}
	
	
	public Bowling() {
		params = new SceneParameters("resources/scene/scene.properties");
	}
	
	
	@Override
	protected void simpleInitGame() {
		// Display
		this.createDisplay();
		// Physics
		this.createPhysics();
		// Audio
		this.createAudio();
		// Static scene
		this.scene = new Scene( rootNode, getPhysicsSpace(), lightState, display.getRenderer(), this.params );
		this.scene.createStaticWorld();
		// Ball
		this.createBall();
		// Pins
		this.createPins();
		// Camera
		this.createCamera();
		// Create controls
		this.createControls();
		// Update
		rootNode.updateRenderState();
	}
	
	
	@Override
	protected void simpleUpdate() {
		if ( KeyInput.get().isKeyDown(KeyInput.KEY_SPACE)) {
			this.resetBall();
			this.resetPins();
		}
		if ( KeyInput.get().isKeyDown(KeyInput.KEY_RETURN)) {
			this.resetBall();
			this.removePins();
		}
		if( KeyInput.get().isKeyDown(KeyInput.KEY_PGUP) && ball.getLocalTranslation().z > -1 ) {
			Vector3f speed = new Vector3f(0,0,-20);
			ball.unrest();
			this.ball.addForce( speed );
		}
		this.score.print(" Pins down: " + this.numberOfPins() );
	}
	
	
	private void createDisplay() {
		display.getRenderer().setBackgroundColor( ColorRGBA.black );
		display.getRenderer().getCamera().setFrustumFar( params.ROOM_LENGTH * 1.1f);
		display.getRenderer().getCamera().update();
		display.setTitle( params.TITLE );
		score = Text.createDefaultTextLabel( "score", "" );
		score.setLocalTranslation( 0, 20, 0 );
		statNode.attachChild( score );
	}
	
	
	private void createPhysics() {
		getPhysicsSpace().setAutoRestThreshold( 2.0f );
        setPhysicsSpeed( 4 );
		// getPhysicsSpace().setWorldBounds( new Vector3f(ROOM_CENTER_X - ROOM_WIDTH, ROOM_CENTER_Y - ROOM_HEIGHT, ROOM_CENTER_Z - ROOM_LENGTH), new Vector3f(ROOM_CENTER_X + ROOM_WIDTH, ROOM_CENTER_Y + ROOM_HEIGHT, ROOM_CENTER_Z + ROOM_LENGTH) );
		// getPhysicsSpace().setWorldBounds( new Vector3f(-9999,-9999,-9999), new Vector3f(9999,9999,9999) );
		// getPhysicsSpace().setDirectionalGravity( new Vector3f(0,-9.81F,0) );
        ContactCallback myCallBack = new ContactCallback() {
			public boolean adjustContact( PendingContact c ) {
				String name1 = c.getNode1().getName();
				String name2 = c.getNode2().getName();
				if( name1 == null || name2 == null ) {
					return false;
				}
				if( name1.startsWith( "pin" ) && name2.startsWith( "pin" ) ) {
					System.out.println( "Pin Pin collition!");
                }else if( name1.startsWith( "pin" ) && name2.startsWith( "ball" ) || name1.startsWith( "ball" ) && name2.startsWith( "pin" )) {
                	playSound( pinHit );
                	System.out.println( "Pin Ball collition!");
                }
                // everything normal, continue with next callback
                return false;
            }
			
        };
        getPhysicsSpace().getContactCallbacks().add( myCallBack );
	}
	
	
	private void createAudio() {
		audioQueue = AudioSystem.getSystem().getMusicQueue();
		audioQueue.setCrossfadeinTime(0);
		audioQueue.setRepeatType(RepeatType.NONE);
		pinHit = getAudioTrack( "resources/Sounds/pinHit.ogg" );
	}
	
	
	private void createCamera() {
		//cam.setLocation( new Vector3f(0,80,30) );
		//ChaseCamera chaser = new ChaseCamera( cam, ball);
		//input = new FirstPersonHandler( cam, CAMERA_MOVE_SPEED, CAMERA_TURN_SPEED );
		// Simple chase camera
        input.removeFromAttachedHandlers( cameraInputHandler );
        cameraInputHandler = new ChaseCamera( cam, ball );
        cameraInputHandler.setActionSpeed( 0.3F );
        ((ChaseCamera)cameraInputHandler).setMaxDistance( params.CAMERA_DISTANCE_MAX );
        ((ChaseCamera)cameraInputHandler).setMinDistance( params.CAMERA_DISTANCE_MIN );
        input.addToAttachedHandlers( cameraInputHandler );
	}
	
	
	private void createControls() {
		//new PhysicsPicker( input, rootNode, getPhysicsSpace() );
        //MouseInput.get().setCursorVisible( true );
	}

	
	private void createBall() {
		Sphere ballVisual = new Sphere("ball", new Vector3f(0, 0, 0), params.BALL_SAMPLES, params.BALL_SAMPLES, params.BALL_RADIUS );
		ballVisual.setModelBound( new BoundingSphere() ); 
		ballVisual.updateModelBound();
		Utils.setColor( ballVisual, ColorRGBA.green, params.HIGH_SHININESS, ColorRGBA.white, display.getRenderer() );
		Utils.setTexture( ballVisual, "resources/textures/marble.jpg", display.getRenderer() );
		this.ball = getPhysicsSpace().createDynamicNode();
		ball.setName( "ball" );
		this.ball.setMaterial( Material.GRANITE );
		this.ball.attachChild( ballVisual );
		this.ball.generatePhysicsGeometry(); 
		this.ball.setMass( params.BALL_WEIGHT );
		rootNode.attachChild( this.ball );
		resetBall();
	}
	
	
	private void resetBall() {
		ball.setActive(true);
		ball.clearDynamics();
		ball.unrest();
		ball.setLocalTranslation( new Vector3f(0, params.BALL_RADIUS_EXTRA + params.BALL_RADIUS, params.APPROACH_LENGTH / 2) );
	}
	
	
	private void createPins() {
		this.pinsDown = new boolean[10];
		this.pins = new DynamicPhysicsNode[10];
		for( int i = 0; i < 10; i++ ){
			pinsDown[i] = false;
			Cylinder pinVisual = new Cylinder("pin_"+ i, params.AXIS_SAMPLES, params.RADIAL_SAMPLES, params.PIN_RADIUS, params.PIN_HEIGHT, true);
			pinVisual.setModelBound( new BoundingBox() );
			pinVisual.updateModelBound();
			pinVisual.lockMeshes();
			Utils.setColor( pinVisual, ColorRGBA.red, params.HIGH_SHININESS, ColorRGBA.white, display.getRenderer() );
			pins[i] = getPhysicsSpace().createDynamicNode();
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
	
	
	private Vector3f getPinPosition(int i) {
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
	
	
	private void resetPins() {
		for( int i = 0; i < 10; i++ ){
			pinsDown[i] = false;
			pins[i].rest();
			pins[i].clearDynamics();
			pins[i].setLocalRotation(new Quaternion( new float[]{(float)Math.PI/2,0,0} ));
			pins[i].setLocalTranslation( getPinPosition(i) );
		}
	}
	
	private void removePins() {
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
	private boolean outOfBounds(float x, float z){
			if( (x > -params.LANE_WIDTH/2 && x < params.LANE_WIDTH/2)&&(z > -params.LANE_LENGTH && z < (-params.LANE_LENGTH + params.BOXMACHINE_LENGTH)) )
				return true;
			return false;
	}
	
	private boolean isPinDown(DynamicPhysicsNode pin){
		
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
	
	private int numberOfPins() {
		int count = 0;
		
		for( int i = 0; i < 10; i++) {
			if(isPinDown(pins[i]))
               count++;
        }
		return count;
	}

	
	public AudioTrack getAudioTrack(String file) {
		AudioTrack track = null;
		try {
			track = AudioSystem.getSystem().createAudioTrack( new URL( "file:" + file ), false);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		track.setLooping( true );
		track.setVolume( 1.0f );
		return track;
	}
	
	
	public void playSound(AudioTrack track) {
		if( !track.isPlaying() ) {
			audioQueue.addTrack(track);
			audioQueue.play();
		}
		AudioSystem.getSystem().update();
		AudioSystem.getSystem().fadeOutAndClear(1.5f);
	}
	
	
}

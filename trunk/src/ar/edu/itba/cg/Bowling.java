package ar.edu.itba.cg;

import java.net.MalformedURLException;
import java.net.URL;

import com.jme.input.ChaseCamera;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Text;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.MusicTrackQueue;
import com.jmex.audio.MusicTrackQueue.RepeatType;
import com.jmex.physics.contact.ContactCallback;
import com.jmex.physics.contact.PendingContact;
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
	private Dynamics dynamics;
	private SceneParameters params;
	public Text score;
	// Sounds
	private MusicTrackQueue audioQueue;
	private AudioTrack[] pinHit;
	// Game States
	public static enum States {SHOOTING, ROWLING};
	
	
	public static void main(String [] args) throws MalformedURLException {
		Bowling app = new Bowling(); 
		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
		app.start();
	}
	
	
	public Bowling() {
	}
	
	
	@Override
	protected void simpleInitGame() {
		// Parameters
		this.params = new SceneParameters("resources/scene/scene.properties");
		// Display
		this.createDisplay();
		// Physics
		this.createPhysics();
		// Audio
		this.createAudio();
		// Static scene
		this.scene = new Scene( rootNode, getPhysicsSpace(), lightState, display.getRenderer(), this.params );
		this.scene.createStaticWorld();
		// Dynamic objects
		this.dynamics = new Dynamics( rootNode, getPhysicsSpace(), display.getRenderer(), this.params );
		this.dynamics.createDynamicWorld();
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
			dynamics.resetBall();
			dynamics.resetPins();
		}
		if ( KeyInput.get().isKeyDown(KeyInput.KEY_RETURN)) {
			dynamics.resetBall();
			dynamics.removePins();
		}
		if( KeyInput.get().isKeyDown(KeyInput.KEY_PGUP) && dynamics.ball.getLocalTranslation().z > -1 ) {
			Vector3f speed = new Vector3f(0,0,-20);
			dynamics.ball.unrest();
			dynamics.ball.addForce( speed );
		}
		this.score.print(" Pins down: " + dynamics.numberOfPins() );
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
				}else if( name1.startsWith( "pin" ) && name2.startsWith( "ball" ) ) {
                	playSound( pinHit[ Integer.valueOf( name1.substring(4) ) ] );
                }else if( name1.startsWith( "ball" ) && name2.startsWith( "pin" ) ) {
                	playSound( pinHit[ Integer.valueOf( name2.substring(4) ) ] );
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
		pinHit = new AudioTrack[10];
		for( int i = 0; i < 10; i++ ) {
			pinHit[i] = getAudioTrack( "resources/Sounds/bowling2.wav" );
		}
	}
	
	
	private void createCamera() {
		//cam.setLocation( new Vector3f(0,80,30) );
		//ChaseCamera chaser = new ChaseCamera( cam, ball);
		//input = new FirstPersonHandler( cam, CAMERA_MOVE_SPEED, CAMERA_TURN_SPEED );
		// Simple chase camera
        input.removeFromAttachedHandlers( cameraInputHandler );
        cameraInputHandler = new ChaseCamera( cam, dynamics.ball );
        cameraInputHandler.setActionSpeed( 0.3F );
        ((ChaseCamera)cameraInputHandler).setMaxDistance( params.CAMERA_DISTANCE_MAX );
        ((ChaseCamera)cameraInputHandler).setMinDistance( params.CAMERA_DISTANCE_MIN );
        input.addToAttachedHandlers( cameraInputHandler );
	}
	
	
	private void createControls() {
		//new PhysicsPicker( input, rootNode, getPhysicsSpace() );
        //MouseInput.get().setCursorVisible( true );
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

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
	private Dynamics dynamics;
	private StartUpMenu menu;
	private HelpMenu helpMenu;
	private SceneParameters params;
	public Text help;
	public Text score;
	// Sounds
	private MusicTrackQueue audioQueue;
	private AudioTrack[] pinDown;
	private AudioTrack ballMoving;
	// Game States
	public static enum States {MENU, SHOOTING, ROLLING, HELP, EXIT};
	private States state = States.MENU;
	
	
	
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
		this.params = new SceneParameters( "resources/scene/scene.properties" );
		//Menu
		this.menu = new StartUpMenu(statNode, display.getWidth(), display.getHeight(),this);
		this.menu.showAllOptions();
		//Help menu
		this.helpMenu = new HelpMenu(statNode, display.getWidth(), display.getHeight(),this);
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
		if( state == States.MENU ) {

			if( KeyInput.get().isKeyDown(KeyInput.KEY_UP) ) {
				menu.keyUp();
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_DOWN) ) {
				menu.keyDown();
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_RETURN) ) {
				menu.keyEnter();
			}
			
		}else if( state == States.SHOOTING ){
			if( KeyInput.get().isKeyDown(KeyInput.KEY_LEFT) && dynamics.getAnchorX() > -params.APPROACH_WIDTH/2 ) {
				dynamics.moveAnchorX( -0.01F );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_RIGHT) && dynamics.getAnchorX() < params.APPROACH_WIDTH/2 ) {
				dynamics.moveAnchorX( 0.01F );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_UP) && dynamics.getAnchorZ() > 0 ) {
				dynamics.moveAnchorZ( -0.01F );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_DOWN) && dynamics.getAnchorZ() < params.APPROACH_LENGTH ) {
				dynamics.moveAnchorZ( 0.01F );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_A) && dynamics.getAnchorRotation() < (float)(Math.PI/4) ) {
				dynamics.rotateAnchor( 0.01F );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_D) && dynamics.getAnchorRotation() > (float)(-Math.PI/4) ) {
				dynamics.rotateAnchor( -0.01F );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_W) && dynamics.getBallZ() > -1 ) {
				dynamics.addForceZ( -5 );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_S) && dynamics.getBallZ() > -1 ) {
				dynamics.addForceZ( 5 );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_Q) && dynamics.getBallZ() > -1 ) {
				dynamics.addTorqueZ( 0.5f );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_E) && dynamics.getBallZ() > -1 ) {
				dynamics.addTorqueZ( -0.5f );
			}
			if ( this.pause ) {
				score.print( "Chupala" );
			}
			if ( KeyInput.get().isKeyDown(KeyInput.KEY_SPACE)) {
				dynamics.resetBall();
				dynamics.resetPins();
			}
			if ( KeyInput.get().isKeyDown(KeyInput.KEY_RETURN)) {
				dynamics.resetBall();
				dynamics.removePins();
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_Z)) {
				dynamics.releaseBall();
			}
			this.score.print(" Pins down: " + dynamics.numberOfPins() );
		}else if( state == States.HELP ){
			
			this.helpMenu.showAllOptions();
			if( KeyInput.get().isKeyDown(KeyInput.KEY_0) ) {
				helpMenu.keyScape();
			}
		}else if( state == States.EXIT ){
			this.finish();			
		}
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
		getPhysicsSpace().setAutoRestThreshold( 0.2f );
        setPhysicsSpeed( 4 );
        getPhysicsSpace().setAccuracy( 0.01F );
        ContactCallback myCallBack = new ContactCallback() {
			public boolean adjustContact( PendingContact c ) {
				String name1 = c.getNode1().getName();
				String name2 = c.getNode2().getName();
				if( name1 == null || name2 == null ) {
					return false;
				}
				if( name1.startsWith( "pin" ) && name2.startsWith( "pin" ) ) {
					// TODO: Set a sound!
				}else if( name1.startsWith( "pin" ) && name2.startsWith( "ball" ) ) {
                	Vector3f v1 = new Vector3f();
                	Vector3f v2 = new Vector3f();
					((DynamicPhysicsNode)c.getNode1()).getLinearVelocity( v1 );
					((DynamicPhysicsNode)c.getNode2()).getLinearVelocity( v2 );
					v1.subtract( v2 );
					playSound( pinDown[ Integer.valueOf( name1.substring(4) ) ], v1.length()/2 );
                }else if( name1.startsWith( "ball" ) && name2.startsWith( "pin" ) ) {
                	Vector3f v1 = new Vector3f();
                	Vector3f v2 = new Vector3f();
					((DynamicPhysicsNode)c.getNode1()).getLinearVelocity( v1 );
					((DynamicPhysicsNode)c.getNode2()).getLinearVelocity( v2 );
					v1.subtract( v2 );
                	playSound( pinDown[ Integer.valueOf( name2.substring(4) ) ], v1.length()/2 );
                }
				
				if( name1.startsWith( "ball" ) && name2.startsWith( "lane" ) || name1.startsWith( "lane" ) && name2.startsWith( "ball" ) ) {
					DynamicPhysicsNode node;
					if( name1.startsWith( "ball" ) ) {
						node = (DynamicPhysicsNode)c.getNode1();
					}else{
						node = (DynamicPhysicsNode)c.getNode2();
					}
					Vector3f v = new Vector3f();
					node.getLinearVelocity(v);
					float length = v.lengthSquared();
					if( length > 1F ) {
						playSound( ballMoving, length / 4096 );
						if (scene.lane.getMaterial() == Material.ICE && dynamics.ball.getLocalTranslation().z < -params.LANE_LENGTH * 0.7f) {
							System.out.println("changed material");
							scene.lane.setMaterial(Material.WOOD);
						}
					}
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
		pinDown = new AudioTrack[10];
		for( int i = 0; i < 10; i++ ) {
			pinDown[i] = getAudioTrack( "resources/Sounds/pinHitLong.wav" );
		}
		ballMoving = getAudioTrack( "resources/Sounds/ballMoving.wav" );
	}
	
	
	private void createCamera() {
		//cam.setLocation( new Vector3f(0,80,30) );
		//ChaseCamera chaser = new ChaseCamera( cam, ball);
		//input = new FirstPersonHandler( cam, CAMERA_MOVE_SPEED, CAMERA_TURN_SPEED );
		// Simple chase camera
        input.removeFromAttachedHandlers( cameraInputHandler );
        cameraInputHandler = new ChaseCamera( cam, dynamics.anchor );
        cameraInputHandler.setActionSpeed( 0.3F );
        ((ChaseCamera)cameraInputHandler).setMaxDistance( 2 );
        ((ChaseCamera)cameraInputHandler).setMinDistance( 1 );
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
	
	
	public void playSound(AudioTrack track, float volume ) {
		if( !track.isPlaying() ) {
			track.setMinVolume( 0 );
			track.setVolume(volume);
			audioQueue.addTrack(track);
			audioQueue.play();
		}
		AudioSystem.getSystem().update();
		AudioSystem.getSystem().fadeOutAndClear(1.5f);
	}
	
	public void setState(States state){
		this.state = state;
	} 
	
	public void showStartUpMenu(){
		menu.showAllOptions();
	}
	
}

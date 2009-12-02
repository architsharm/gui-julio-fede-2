package ar.edu.itba.cg;

import java.net.MalformedURLException;
import java.net.URL;

import ar.edu.itba.cg.menu.HelpMenu;
import ar.edu.itba.cg.menu.StartUpMenu;

import com.jme.input.ChaseCamera;
import com.jme.input.KeyInput;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Text;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.MusicTrackQueue.RepeatType;
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
	private SoundManager soundManager;
	public Text help;
	public Text score;
	// Sounds
	
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
		this.menu = new StartUpMenu( statNode, display.getWidth(), display.getHeight(), this );
		this.menu.showAllOptions();
		//Help menu
		this.helpMenu = new HelpMenu( statNode, display.getWidth(), display.getHeight(),this );
		// Display
		this.createDisplay();
		// Physics
		this.createPhysics();
		// Audio
		this.soundManager = new SoundManager();
		// Static scene
		this.scene = new Scene( rootNode, getPhysicsSpace(), lightState, display.getRenderer(), this.params );
		this.scene.createStaticWorld();
		// Dynamic objects
		this.dynamics = new Dynamics( rootNode, getPhysicsSpace(), display.getRenderer(), this.params, this.input, this.soundManager );
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
				dynamics.addForceZ( -4 * this.timer.getTimePerFrame() * 1000 );
			}
			if( KeyInput.get().isKeyDown(KeyInput.KEY_S) && dynamics.getBallZ() > -1 ) {
				dynamics.addForceZ( 4 * this.timer.getTimePerFrame() * 1000 );
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
        setPhysicsSpeed( 1 );
        getPhysicsSpace().setAccuracy( 0.015625F / 2 );
	}
	
	private void createCamera() {
		//cam.setLocation( new Vector3f(0,80,30) );
		//ChaseCamera chaser = new ChaseCamera( cam, ball);
//		input = new FirstPersonHandler( cam, params.CAMERA_MOVE_SPEED, params.CAMERA_TURN_SPEED );
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
	
	public void setState(States state){
		this.state = state;
	} 
	
	public void showStartUpMenu(){
		menu.showAllOptions();
	}
	
}

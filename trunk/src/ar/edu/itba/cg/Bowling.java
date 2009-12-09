package ar.edu.itba.cg;

import java.net.MalformedURLException;
import java.net.URL;

import ar.edu.itba.cg.menu.GameMenu;
import ar.edu.itba.cg.menu.HelpMenu;
import ar.edu.itba.cg.menu.StartUpMenu;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.Text;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;
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
	private static ShadowedRenderPass sPass = new ShadowedRenderPass();
	private CameraManager cameraManager;
	private StartUpMenu menu;
	private HelpMenu helpMenu;
	private GameMenu gameScore;
	private SceneParameters params;
	private SoundManager soundManager;
	public Text score;
	// Game States
	public static enum States {MENU, SHOOTING, ROLLING, HELP, EXIT};
	private States state = States.MENU;
	
	private InputHandler physicsStepInputHandler;
	
	
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
		// Display
		this.createDisplay();
		// Physics
		this.createPhysics();
		//Menu
		this.menu = new StartUpMenu( statNode, display.getWidth(), display.getHeight(), this );
		this.menu.showAllOptions();
		//Help menu
		this.helpMenu = new HelpMenu( statNode, display.getWidth(), display.getHeight(),this );
		//Game menu
		this.gameScore = new GameMenu(statNode, display.getWidth(), display.getHeight());
		// Audio
		this.soundManager = new SoundManager();
		// Static scene
		this.scene = new Scene( rootNode, getPhysicsSpace(), lightState, display.getRenderer(), params );
		this.scene.createStaticWorld();
		// Dynamic objects
		this.dynamics = new Dynamics( rootNode, getPhysicsSpace(), display.getRenderer(), params, input, soundManager );
		this.dynamics.createDynamicWorld();
		// Camera
		this.cameraManager = new CameraManager( scene, dynamics, input, cameraInputHandler, params );
		cameraManager.setAnchorCamera();
		// Create controls
		this.createControls();
		// Update
		rootNode.updateRenderState();
	}
	
    
	@Override
	protected void simpleUpdate() {
		score.print(" Pins down: " + dynamics.numberOfPins() );
	}
	
	
	private void createDisplay() {      
		display.getRenderer().setBackgroundColor( ColorRGBA.black );
		
		display.getRenderer().getCamera().setFrustumFar( params.ROOM_LENGTH * 1.1f);
		display.getRenderer().getCamera().update();
		
        cam.setFrustumPerspective( 50.0f, (float) display.getWidth()/ (float) display.getHeight(), 1f, 10000 );
		
		
		display.setTitle( params.TITLE );
		
		score = Text.createDefaultTextLabel( "score", "" );
		score.setLocalTranslation( 0, 8, 0 );
		statNode.attachChild( score );
		
//        CullState cullState = display.getRenderer().createCullState();
//        cullState.setCullFace( CullState.Face.None );
//        cullState.setEnabled( true );
//        rootNode.setRenderState( cullState );
//		
//        rootNode.setCullHint( Spatial.CullHint.Never );
//		
//        rootNode.setRenderQueueMode( Renderer.QUEUE_OPAQUE );
//		
//        ZBufferState zState = display.getRenderer().createZBufferState();
//        zState.setEnabled( true );
//        rootNode.setRenderState( zState );
//        
//        rootNode.setLightCombineMode(Spatial.LightCombineMode.Off);
//        rootNode.setTextureCombineMode(TextureCombineMode.Replace);
//        
//        rootNode.updateRenderState();
//
//        rootNode.lockBounds();
//        rootNode.lockMeshes();
       
	}
	
	
	private void createPhysics() {
		getPhysicsSpace().setAutoRestThreshold( 0.2f );
        setPhysicsSpeed( 1 );
        getPhysicsSpace().setAccuracy( 0.015625F / 2 );
        physicsStepInputHandler = new InputHandler();
        getPhysicsSpace().addToUpdateCallbacks( new PhysicsUpdateCallback() {
            public void beforeStep( PhysicsSpace space, float time ) {
                physicsStepInputHandler.update( time );
            }
            public void afterStep( PhysicsSpace space, float time ) {

            }
        } );
	}
	
	
	private void createControls() {
		physicsStepInputHandler.addAction( new MyInputAction(),
				InputHandler.DEVICE_KEYBOARD, InputHandler.BUTTON_ALL, InputHandler.AXIS_NONE, true );
		input.addAction( new MyInputAction2(),
				InputHandler.DEVICE_KEYBOARD, InputHandler.BUTTON_ALL, InputHandler.AXIS_NONE, false );
	}
	
	
	private class MyInputAction2 extends InputAction {
        /**
         * This method gets invoked upon key event
         *
         * @param evt more data about the event (we don't need it)
         */
        public void performAction( InputActionEvent evt ) {
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
    			if ( pause ) {
    				score.print( " Paused" );
    			}
    			if ( KeyInput.get().isKeyDown(KeyInput.KEY_SPACE)) {
    				dynamics.resetBall();
    				dynamics.resetPins();
    				cameraManager.setAnchorCamera();
    			}
    			if ( KeyInput.get().isKeyDown(KeyInput.KEY_RETURN)) {
    				dynamics.resetBall();
    				dynamics.removePins();
    				cameraManager.setAnchorCamera();
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_Z)) {
    				dynamics.releaseBall();
    				cameraManager.setBallCamera();
    			}
    		}else if( state == States.HELP ){
    			helpMenu.showAllOptions();
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_0) ) {
    				helpMenu.keyScape();
    			}
    		}else if( state == States.EXIT ){
    			finish();			
    		}
        	if( KeyInput.get().isKeyDown(KeyInput.KEY_1) ) {
				cameraManager.setAnchorCamera();
			}
        	if( KeyInput.get().isKeyDown(KeyInput.KEY_2) ) {
				cameraManager.setBallCamera();
			}
        }
    }
	
	/**
     * An action that get's invoked on a keystroke (once per stroke).
     */
    private class MyInputAction extends InputAction {
        /**
         * This method gets invoked upon key event
         *
         * @param evt more data about the event (we don't need it)
         */
        public void performAction( InputActionEvent evt ) {
        	if( state == States.SHOOTING ){
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_LEFT) && dynamics.getAnchorX() > -params.APPROACH_WIDTH/2 ) {
    				dynamics.moveAnchorX( -0.01F );
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_RIGHT) && dynamics.getAnchorX() < params.APPROACH_WIDTH/2 ) {
    				dynamics.moveAnchorX( 0.01F );
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_UP) && dynamics.getAnchorZ() > 0 ) {
    				dynamics.moveAnchorZ( -0.01F );
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_DOWN) && dynamics.getAnchorZ() < (params.APPROACH_LENGTH - params.BALL_RADIUS) ) {
    				dynamics.moveAnchorZ( 0.01F );
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_A) && dynamics.getAnchorRotation() < (float)(Math.PI/4) ) {
    				dynamics.rotateAnchor( 0.01F );
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_D) && dynamics.getAnchorRotation() > (float)(-Math.PI/4) ) {
    				dynamics.rotateAnchor( -0.01F );
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_W) && dynamics.getBallZ() > -1 ) {
    				dynamics.addForceZ( -40 * evt.getTime() * 1000 );
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_S) && dynamics.getBallZ() > -1 ) {
    				dynamics.addForceZ( 40 * evt.getTime() * 1000 );
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_Q) ) {
    				dynamics.addTorqueZ( 0.8f );
    			}
    			if( KeyInput.get().isKeyDown(KeyInput.KEY_E) ) {
    				dynamics.addTorqueZ( -0.8f );
    			}
    		}else if( state == States.EXIT ){
    			finish();			
    		}
        }
    }
    
    
	public void setState(States state){
		this.state = state;
	} 
	
	
	public void showStartUpMenu(){
		menu.showAllOptions();
	}
	
	
	public void startGame(){
		gameScore.createScore();
	}
	
	
}

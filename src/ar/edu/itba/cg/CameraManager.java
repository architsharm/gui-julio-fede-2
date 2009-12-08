package ar.edu.itba.cg;

import com.jme.input.ChaseCamera;
import com.jme.input.InputHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.system.DisplaySystem;

public class CameraManager {
	private Scene scene;
	private Dynamics dynamics;
	private InputHandler input;
	private InputHandler actualInput;
	
	
	public CameraManager( Scene scene, Dynamics dynamics, InputHandler input, InputHandler actualInput ) {
		this.scene = scene;
		this.dynamics = dynamics;
		this.input = input;
		this.actualInput = actualInput;
	}
	

	private void changeInput( InputHandler newInput ) {
		input.removeFromAttachedHandlers( actualInput );
		input.addToAttachedHandlers( newInput );
		actualInput = newInput;
	}
	
	
	public void setBallCamera() {
		Camera cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
		InputHandler cameraInputHandler = new ChaseCamera( cam, dynamics.ball );
		cameraInputHandler.setActionSpeed( 0.3F );
		((ChaseCamera)cameraInputHandler).setDampingK( 0 );
		((ChaseCamera)cameraInputHandler).setSpringK( 0 );
        ((ChaseCamera)cameraInputHandler).setMaxDistance( 3 );
        ((ChaseCamera)cameraInputHandler).setMinDistance( 2 );
        ((ChaseCamera)cameraInputHandler).setTargetOffset( new Vector3f(0,0.4F,1) );
		changeInput( cameraInputHandler );
	}
	
	
	public void setAnchorCamera() {
		Camera cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
		InputHandler cameraInputHandler = new ChaseCamera( cam, dynamics.anchor );
        ((ChaseCamera)cameraInputHandler).setMaxDistance( 2 );
        ((ChaseCamera)cameraInputHandler).setMinDistance( 1 );
		changeInput( cameraInputHandler );		
	}
	
	
}

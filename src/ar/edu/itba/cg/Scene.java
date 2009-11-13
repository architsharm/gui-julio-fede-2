package ar.edu.itba.cg;

import com.jme.bounding.BoundingBox;
import com.jme.light.PointLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;

public class Scene {
	private Node rootNode;
	private PhysicsSpace physicsSpace;
	private LightState lightState;
	private Renderer renderer;
	private SceneParameters params;
	
	
	public Scene( Node rootNode, PhysicsSpace physicsSpace, LightState lightState, Renderer renderer, SceneParameters parameters ) {
		this.rootNode = rootNode;
		this.physicsSpace = physicsSpace;
		this.lightState = lightState;
		this.renderer = renderer;
		this.params = parameters;
	}
	
	
	public void createStaticWorld() {
		// Room
		this.createRoom();
		// Box
		this.createBox();
		// Lane
		this.createLane();
		// Approach
		this.createApproach();
		// Gutters
		this.createGutters();
		// Lights
		this.createIlumination();
	}
	
	
	public void createRoom() {
		Node room = new Node("room");
		// Top and bottom
		Quad wallDownVisual = new Quad("wall_down", params.ROOM_WIDTH, params.ROOM_LENGTH );
		Quad wallUpVisual =   new Quad("wall_up",   params.ROOM_WIDTH, params.ROOM_LENGTH );
		wallDownVisual.setModelBound( new BoundingBox() ); 
		wallDownVisual.updateModelBound();
		wallUpVisual.setModelBound( new BoundingBox() ); 
		wallUpVisual.updateModelBound();
		Utils.setColor( wallDownVisual, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setColor( wallUpVisual,   ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallDownVisual, "resources/textures/wall.jpg", renderer );
		Utils.setTexture( wallUpVisual, "resources/textures/wall.jpg", renderer );
		StaticPhysicsNode wallDown = physicsSpace.createStaticNode();
		StaticPhysicsNode wallUp = physicsSpace.createStaticNode();
		wallDown.setName( "wall_down" );
		wallUp.setName( "wall_up" );
		wallDown.attachChild( wallDownVisual );
		wallUp.attachChild( wallUpVisual );
		wallDown.setMaterial( Material.CONCRETE );
		wallUp.setMaterial( Material.CONCRETE );
		wallDown.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, 0 } ) );
		wallUp.setLocalRotation(   new Quaternion( new float[]{ (float)Math.PI/2, 0, 0 } ) );
		wallDown.setLocalTranslation( 0, 0, params.ROOM_CENTER_Z );
		wallUp.setLocalTranslation(   0, params.ROOM_HEIGHT, params.ROOM_CENTER_Z );
		wallDown.generatePhysicsGeometry();
		wallUp.generatePhysicsGeometry();
		// Left and right
		Quad wallLeftVisual =  new Quad("wall_left",  params.ROOM_LENGTH, params.ROOM_HEIGHT);
		Quad wallRightVisual = new Quad("wall_right", params.ROOM_LENGTH, params.ROOM_HEIGHT);
		wallLeftVisual.setModelBound( new BoundingBox() ); 
		wallLeftVisual.updateModelBound();
		wallRightVisual.setModelBound( new BoundingBox() ); 
		wallRightVisual.updateModelBound();
		Utils.setColor( wallLeftVisual,  ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setColor( wallRightVisual, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallLeftVisual, "resources/textures/wall.jpg", renderer );
		Utils.setTexture( wallRightVisual, "resources/textures/wall.jpg", renderer );
		StaticPhysicsNode wallLeft = physicsSpace.createStaticNode();
		StaticPhysicsNode wallRight = physicsSpace.createStaticNode();
		wallLeft.setName( "wall_left" );
		wallRight.setName( "wall_right" );
		wallLeft.attachChild( wallLeftVisual );
		wallRight.attachChild( wallRightVisual );
		wallLeft.setMaterial( Material.CONCRETE );
		wallRight.setMaterial( Material.CONCRETE );
		wallLeft.setLocalRotation(  new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		wallRight.setLocalRotation( new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		wallLeft.setLocalTranslation(  -params.ROOM_WIDTH / 2, params.ROOM_CENTER_Y, params.ROOM_CENTER_Z );
		wallRight.setLocalTranslation(  params.ROOM_WIDTH / 2, params.ROOM_CENTER_Y, params.ROOM_CENTER_Z );
		wallRight.generatePhysicsGeometry();
		wallLeft.generatePhysicsGeometry();
		// Back
		Quad wallBackVisual =  new Quad("wall_back", params.ROOM_HEIGHT, params.ROOM_WIDTH);
		wallBackVisual.setModelBound( new BoundingBox() ); 
		wallBackVisual.updateModelBound();
		Utils.setColor( wallBackVisual, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallBackVisual, "resources/textures/wall.jpg", renderer );
		StaticPhysicsNode wallBack = physicsSpace.createStaticNode();
		wallBack.setName( "wall_back" );
		wallBack.attachChild( wallBackVisual );
		wallBack.setMaterial( Material.CONCRETE );
		wallBack.setLocalRotation(  new Quaternion( new float[]{ 0, 0, (float)Math.PI/2 } ) );
		wallBack.setLocalTranslation(  0, params.ROOM_CENTER_Y, params.APPROACH_LENGTH );
		wallBack.generatePhysicsGeometry();
		// Front
		Quad wallFrontVisual =  new Quad("wall_front", params.ROOM_HEIGHT, params.ROOM_WIDTH);
		wallFrontVisual.setModelBound( new BoundingBox() ); 
		wallFrontVisual.updateModelBound();
		Utils.setColor( wallFrontVisual, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallFrontVisual, "resources/textures/wall.jpg", renderer );
		StaticPhysicsNode wallFront = physicsSpace.createStaticNode();
		wallFront.setName( "wall_front" );
		wallFront.attachChild( wallFrontVisual );
		wallFront.setMaterial( Material.CONCRETE );
		wallFront.setLocalRotation(  new Quaternion( new float[]{ 0, 0, (float)Math.PI/2 } ) );
		wallFront.setLocalTranslation(  0, params.ROOM_CENTER_Y, -(params.LANE_LENGTH + params.BOX_LENGTH) );
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
	
	
	public void createBox() {
		Node box = new Node("box");
		// Top
		Quad boxTopVisual = new Quad("box_top", params.LANE_WIDTH + params.BALL_DIAMETER_EXTRA * 2, params.BOX_LENGTH + params.BOXMACHINE_LENGTH );
		boxTopVisual.setModelBound( new BoundingBox() ); 
		boxTopVisual.updateModelBound();
		Utils.setColor( boxTopVisual, ColorRGBA.darkGray, params.NO_SHININESS, params.NO_COLOR, renderer );
		StaticPhysicsNode boxTop = physicsSpace.createStaticNode();
		boxTop.setName( "box_top" );
		boxTop.attachChild( boxTopVisual );
		boxTop.setMaterial( Material.CONCRETE );
		boxTop.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, 0 } ) );
		boxTop.setLocalTranslation(  0, params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT, -(params.LANE_LENGTH) );
		boxTop.generatePhysicsGeometry();
		// Left and right
		Quad boxLeftVisual =  new Quad("box_left",  params.BOX_LENGTH + params.BOXMACHINE_LENGTH, params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT);
		Quad boxRightVisual = new Quad("box_right", params.BOX_LENGTH + params.BOXMACHINE_LENGTH, params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT);
		boxLeftVisual.setModelBound( new BoundingBox() ); 
		boxLeftVisual.updateModelBound();
		boxRightVisual.setModelBound( new BoundingBox() ); 
		boxRightVisual.updateModelBound();
		Utils.setColor( boxLeftVisual,  ColorRGBA.darkGray, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setColor( boxRightVisual, ColorRGBA.darkGray, params.NO_SHININESS, params.NO_COLOR, renderer );
		StaticPhysicsNode boxLeft = physicsSpace.createStaticNode();
		StaticPhysicsNode boxRight = physicsSpace.createStaticNode();
		boxLeft.setName( "box_left" );
		boxRight.setName( "box_right" );
		boxLeft.attachChild( boxLeftVisual );
		boxRight.attachChild( boxRightVisual );
		boxLeft.setMaterial( Material.CONCRETE );
		boxRight.setMaterial( Material.CONCRETE );
		boxLeft.setLocalRotation(  new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		boxRight.setLocalRotation( new Quaternion( new float[]{ 0, (float)Math.PI/2, 0 } ) );
		boxLeft.setLocalTranslation(  -(params.LANE_WIDTH / 2 + params.BALL_DIAMETER_EXTRA), (params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT)/2, -(params.LANE_LENGTH ) );
		boxRight.setLocalTranslation(   params.LANE_WIDTH / 2 + params.BALL_DIAMETER_EXTRA,  (params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT)/2, -(params.LANE_LENGTH ) );
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
	
	
	public void createLane() {
		Box laneVisual = new Box("lane", new Vector3f(0,0,0), params.LANE_WIDTH / 2, params.BALL_RADIUS_EXTRA / 2, params.LANE_LENGTH / 2 );
		laneVisual.setModelBound( new BoundingBox() ); 
		laneVisual.updateModelBound();
		Utils.setColor( laneVisual, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( laneVisual, "resources/textures/wood.jpg", renderer );
		StaticPhysicsNode lane = physicsSpace.createStaticNode();
		lane.setName( "lane" );
		lane.setMaterial( Material.WOOD );
		lane.attachChild( laneVisual );
		lane.setLocalTranslation( new Vector3f(0, params.BALL_RADIUS_EXTRA / 2, -params.LANE_LENGTH / 2) );
		lane.generatePhysicsGeometry();
		rootNode.attachChild( lane );
	}
	
	
	public void createApproach() {
		Box approachVisual = new Box( "approach", new Vector3f(0,0,0), params.LANE_WIDTH/2 + params.BALL_DIAMETER_EXTRA, params.BALL_RADIUS_EXTRA / 2, params.APPROACH_LENGTH / 2 );
		approachVisual.setModelBound( new BoundingBox() ); 
		approachVisual.updateModelBound();
		Utils.setColor( approachVisual, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( approachVisual, "resources/textures/wood.jpg", renderer );
		StaticPhysicsNode approach = physicsSpace.createStaticNode();
		approach.setName( "approach" );
		approach.setMaterial( Material.WOOD );
		approach.attachChild( approachVisual );
		approach.setLocalTranslation( new Vector3f(0, params.BALL_RADIUS_EXTRA / 2, params.APPROACH_LENGTH / 2) );
		approach.generatePhysicsGeometry();
		rootNode.attachChild( approach );
	}
	
	
	public void createGutters() {
		float circumference = 2.0F * (float)Math.PI * params.BALL_RADIUS_EXTRA;
		// Node gutterLeft =  getPhysicsSpace().createStaticNode(); // new Node("gutter_left");
		// Node gutterRight = getPhysicsSpace().createStaticNode(); // new Node("gutter_right");
		Quad gutterBorderLeftVisual  = new Quad("gutter_border_left",  params.BALL_RADIUS_EXTRA, params.LANE_LENGTH);
		Quad gutterBorderRightVisual = new Quad("gutter_border_right", params.BALL_RADIUS_EXTRA, params.LANE_LENGTH);
		gutterBorderLeftVisual.setModelBound( new BoundingBox() ); 
		gutterBorderLeftVisual.updateModelBound();
		gutterBorderRightVisual.setModelBound( new BoundingBox() ); 
		gutterBorderRightVisual.updateModelBound();
		Utils.setColor( gutterBorderLeftVisual, ColorRGBA.gray, params.LOW_SHININESS, params.NO_COLOR, renderer );
		Utils.setColor( gutterBorderRightVisual,   ColorRGBA.gray, params.LOW_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( gutterBorderLeftVisual, "resources/textures/metal.jpg", renderer );
		Utils.setTexture( gutterBorderRightVisual, "resources/textures/metal.jpg", renderer );
		StaticPhysicsNode gutterBorderLeft = physicsSpace.createStaticNode();
		StaticPhysicsNode gutterBorderRight = physicsSpace.createStaticNode();
		gutterBorderLeft.setName( "gutter_border_left" );
		gutterBorderRight.setName( "gutter_border_right" );
		gutterBorderLeft.setMaterial( Material.IRON );
		gutterBorderRight.setMaterial( Material.IRON );
		gutterBorderLeft.attachChild( gutterBorderLeftVisual );
		gutterBorderRight.attachChild( gutterBorderRightVisual );
		gutterBorderLeft.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, -(float)Math.PI/2 } ) );
		gutterBorderRight.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, -(float)Math.PI/2 } ) );
		gutterBorderLeft.setLocalTranslation( -(params.LANE_WIDTH/2 + params.BALL_DIAMETER_EXTRA), params.BALL_RADIUS_EXTRA / 2, -(params.LANE_LENGTH / 2) );
		gutterBorderRight.setLocalTranslation( params.LANE_WIDTH/2 + params.BALL_DIAMETER_EXTRA  , params.BALL_RADIUS_EXTRA / 2, -(params.LANE_LENGTH / 2) );
		gutterBorderLeft.generatePhysicsGeometry();
		gutterBorderRight.generatePhysicsGeometry();
		rootNode.attachChild( gutterBorderLeft );
		rootNode.attachChild( gutterBorderRight );
		for( int i = 1; i < params.GUTTER_SAMPLES; i++ ) {
			Quad leftVisual =  new Quad( "gutter_left_"  + String.valueOf(i), circumference / params.GUTTER_SAMPLES, params.LANE_LENGTH);
			Quad rightVisual = new Quad( "gutter_right_" + String.valueOf(i), circumference / params.GUTTER_SAMPLES, params.LANE_LENGTH);
			leftVisual.setModelBound( new BoundingBox() ); 
			leftVisual.updateModelBound();
			rightVisual.setModelBound( new BoundingBox() ); 
			rightVisual.updateModelBound();
			Utils.setColor( leftVisual, ColorRGBA.gray, params.LOW_SHININESS, params.NO_COLOR, renderer );
			Utils.setColor( rightVisual,   ColorRGBA.gray, params.LOW_SHININESS, params.NO_COLOR, renderer );
			Utils.setTexture( leftVisual,  "resources/textures/metal.jpg", renderer );
			Utils.setTexture( rightVisual, "resources/textures/metal.jpg", renderer );
			StaticPhysicsNode left = physicsSpace.createStaticNode();
			StaticPhysicsNode right = physicsSpace.createStaticNode();
			left.setName( "gutter_left_" + String.valueOf(i) );
			right.setName( "gutter_right_" + String.valueOf(i) );
			left.setMaterial( Material.IRON );
			right.setMaterial( Material.IRON );
			left.attachChild( leftVisual );
			right.attachChild( rightVisual );
			float rotation =  -(float)Math.PI/2 + (float)Math.PI / params.GUTTER_SAMPLES * i; 
			left.setLocalRotation(  new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			right.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			float angle = (float)Math.PI + (float)Math.PI / params.GUTTER_SAMPLES * i;
			left.setLocalTranslation( -(params.LANE_WIDTH/2 + params.BALL_RADIUS_EXTRA) + params.BALL_RADIUS_EXTRA * (float)Math.cos( angle ), params.BALL_RADIUS_EXTRA + params.BALL_RADIUS_EXTRA * (float)Math.sin( angle ), -(params.LANE_LENGTH / 2) );
			right.setLocalTranslation( params.LANE_WIDTH/2 + params.BALL_RADIUS_EXTRA + params.BALL_RADIUS_EXTRA * (float)Math.cos( angle ), params.BALL_RADIUS_EXTRA + params.BALL_RADIUS_EXTRA * (float)Math.sin( angle ), -(params.LANE_LENGTH / 2) );
			left.generatePhysicsGeometry();
			right.generatePhysicsGeometry();
			rootNode.attachChild( left );
			rootNode.attachChild( right );
		}	
	}
	
	
	public void createIlumination() {
		((PointLight)lightState.get(0)).setLocation(new Vector3f(0, params.ROOM_HEIGHT * 0.9F, 0));
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
	
	
}

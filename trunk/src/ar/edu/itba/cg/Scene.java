package ar.edu.itba.cg;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.cg.utils.ColladaModelLoader;

import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.material.Material;

public class Scene {
        private Node rootNode;
	private PhysicsSpace physicsSpace;
	private LightState lightState;
	private Renderer renderer;
	private SceneParameters params;
	private float ROOM_WIDTH;
	public StaticPhysicsNode lane;
	
	public Scene( Node rootNode, PhysicsSpace physicsSpace, LightState lightState, Renderer renderer, SceneParameters parameters ) {
		this.rootNode = rootNode;
		this.physicsSpace = physicsSpace;
		this.lightState = lightState;
		this.renderer = renderer;
		this.params = parameters;
	}
	
	
	public void createStaticWorld() {
		Vector3f moveCenter = new Vector3f(0,0,0);
		Vector3f moveRight = new Vector3f(  params.LANE_WIDTH + params.BALL_DIAMETER_EXTRA*2 + params.SEPARATION_WIDTH, 0,0);
		Vector3f moveLeft = new Vector3f( -(params.LANE_WIDTH + params.BALL_DIAMETER_EXTRA*2 + params.SEPARATION_WIDTH),0,0);
        Vector3f moveRightRight = moveRight.mult( 2 );
        Vector3f moveLeftLeft = moveLeft.mult( 2 );
        ROOM_WIDTH = params.LANE_WIDTH * 5 + params.SEPARATION_WIDTH * 5 + params.BALL_DIAMETER_EXTRA * 5 * 2;
		// Room
		this.createRoom();
		// Box
		this.createBowling( moveCenter );
		this.createBowling( moveRight );
		this.createBowling( moveLeft );
		this.createBowling( moveRightRight );
		this.createBowling( moveLeftLeft );
		// Create bar
		this.createBar2();
		// Lights
		this.createIlumination();
		// Balls
		Vector3f ballsRight = new Vector3f( params.LANE_WIDTH/2 + params.BALL_DIAMETER_EXTRA + params.SEPARATION_WIDTH/2, 0, 0 );
		Vector3f ballsLeft = ballsRight.mult( -1 );
		Vector3f ballsRightRight = ballsRight.mult( 3 );
		Vector3f ballsLeftLeft = ballsLeft.mult( 3 );
		this.createBalls( ballsRight );
		this.createBalls( ballsLeft );
		this.createBalls( ballsRightRight );
		this.createBalls( ballsLeftLeft );
		// TV
		this.createTV( moveCenter );
		this.createTV( moveRight );
		this.createTV( moveLeft );
		this.createTV( moveRightRight );
		this.createTV( moveLeftLeft );
	}
	

	private void createBowling( Vector3f move ) {
		// Box
		this.createBox( move );
		// Lane
		this.createLane( move );
		// Approach
		this.createApproach( move );
		// Gutters
		this.createGutters( move );
		// Separation
		this.createSeparation( move );
	}


	public void createRoom() {		
		// Top
		StaticPhysicsNode wallUp = createStaticVisualBox( "wall_up" );
		Utils.setColor( wallUp, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallUp, "resources/textures/wall.jpg", renderer );
		wallUp.getLocalScale().set( ROOM_WIDTH, params.WALL_THICK, params.ROOM_LENGTH );
		wallUp.getLocalTranslation().set( params.ROOM_CENTER_X, params.ROOM_HEIGHT, params.ROOM_CENTER_Z );
		wallUp.setMaterial( Material.CONCRETE );
		// Bottom
		StaticPhysicsNode wallDown = createStaticVisualBox( "wall_down" );
		Utils.setColor( wallDown, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallDown, "resources/textures/wall.jpg", renderer );
		wallDown.getLocalScale().set( ROOM_WIDTH, params.WALL_THICK, params.ROOM_LENGTH );
		wallDown.getLocalTranslation().set( params.ROOM_CENTER_X, 0, params.ROOM_CENTER_Z );
		wallDown.setMaterial( Material.CONCRETE );
		// Left
		StaticPhysicsNode wallLeft = createStaticVisualBox( "wall_left" );
		Utils.setColor( wallLeft, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallLeft, "resources/textures/wall.jpg", renderer );
		wallLeft.getLocalScale().set( params.WALL_THICK, params.ROOM_HEIGHT, params.ROOM_LENGTH );
		wallLeft.getLocalTranslation().set( -ROOM_WIDTH/2, params.ROOM_CENTER_Y, params.ROOM_CENTER_Z );
		wallLeft.setMaterial( Material.CONCRETE );
		// Right
		StaticPhysicsNode wallRight = createStaticVisualBox( "wall_right" );
		Utils.setColor( wallRight, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallRight, "resources/textures/wall.jpg", renderer );
		wallRight.getLocalScale().set( params.WALL_THICK, params.ROOM_HEIGHT, params.ROOM_LENGTH );
		wallRight.getLocalTranslation().set( ROOM_WIDTH/2, params.ROOM_CENTER_Y, params.ROOM_CENTER_Z );
		wallRight.setMaterial( Material.CONCRETE );
		// Back
		StaticPhysicsNode wallBack = createStaticVisualBox( "wall_back" );
		Utils.setColor( wallBack, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallBack, "resources/textures/wall.jpg", renderer );
		wallBack.getLocalScale().set( ROOM_WIDTH, params.ROOM_HEIGHT, params.WALL_THICK );
		wallBack.getLocalTranslation().set( params.ROOM_CENTER_X, params.ROOM_CENTER_Y, params.ROOM_BACK_Z );
		wallBack.setMaterial( Material.CONCRETE );
		// Front
		StaticPhysicsNode wallFront = createStaticVisualBox( "wall_front" );
		Utils.setColor( wallFront, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( wallFront, "resources/textures/wall.jpg", renderer );
		wallFront.getLocalScale().set( ROOM_WIDTH, params.ROOM_HEIGHT, params.WALL_THICK );
		wallFront.getLocalTranslation().set( params.ROOM_CENTER_X, params.ROOM_CENTER_Y, params.ROOM_FRONT_Z );
		wallFront.setMaterial( Material.CONCRETE );
	}
	
	
	public void createBox( Vector3f move ) {
		// Top
		StaticPhysicsNode boxTop = createStaticVisualBox( "box_top" );
		Utils.setColor( boxTop, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( boxTop, "resources/textures/ITBA.jpg", renderer );
		boxTop.getLocalScale().set( params.LANE_WIDTH + params.BALL_DIAMETER_EXTRA*2 + params.SEPARATION_WIDTH, params.BOX_TOP_HEIGHT, params.BOX_LENGTH + params.BOXMACHINE_LENGTH );
		boxTop.getLocalTranslation().set( move.x + params.ROOM_CENTER_X, move.y + params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT + (params.BOX_TOP_HEIGHT)/2, move.z - params.LANE_LENGTH );
		boxTop.setMaterial( Material.CONCRETE );
		// Left
		StaticPhysicsNode boxLeft = createStaticVisualBox( "box_left" );
		Utils.setColor( boxLeft,  ColorRGBA.darkGray, params.NO_SHININESS, params.NO_COLOR, renderer );
		boxLeft.getLocalScale().set( params.SEPARATION_WIDTH/2, params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT, params.BOXMACHINE_LENGTH + params.BOX_LENGTH );
		boxLeft.getLocalTranslation().set( move.x - (params.LANE_WIDTH/2 + params.BALL_DIAMETER_EXTRA + params.SEPARATION_WIDTH/4), move.y + (params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT)/2, move.z - params.LANE_LENGTH );
		boxLeft.setMaterial( Material.CONCRETE );
		// Right
		StaticPhysicsNode boxRight = createStaticVisualBox( "box_right" );
		Utils.setColor( boxRight,  ColorRGBA.darkGray, params.NO_SHININESS, params.NO_COLOR, renderer );
		boxRight.getLocalScale().set( params.SEPARATION_WIDTH/2, params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT, params.BOXMACHINE_LENGTH + params.BOX_LENGTH );
		boxRight.getLocalTranslation().set( move.x + (params.LANE_WIDTH/2 + params.BALL_DIAMETER_EXTRA + params.SEPARATION_WIDTH/4), move.y + (params.BALL_RADIUS_EXTRA + params.BOX_HEIGHT)/2, move.z - params.LANE_LENGTH );
		boxRight.setMaterial( Material.CONCRETE );
	}
	
	
	public void createLane( Vector3f move ) {
		lane = createStaticVisualBox( "lane" );
		Utils.setColor( lane, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( lane, "resources/textures/wood.jpg", renderer );
		lane.getLocalScale().set( params.LANE_WIDTH, params.BALL_RADIUS_EXTRA, params.LANE_LENGTH );
		lane.getLocalTranslation().set( move.x + params.ROOM_CENTER_X, move.y + params.BALL_RADIUS_EXTRA/2, move.z - params.LANE_LENGTH/2 );
		lane.setMaterial( Material.WOOD );
	}
	
	
	public void createApproach( Vector3f move ) {
		StaticPhysicsNode approach = createStaticVisualBox( "approach" );
		Utils.setColor( approach, ColorRGBA.white, params.NO_SHININESS, params.NO_COLOR, renderer );
		Utils.setTexture( approach, "resources/textures/wood.jpg", renderer );
		approach.getLocalScale().set( params.APPROACH_WIDTH, params.BALL_RADIUS_EXTRA, params.APPROACH_LENGTH );
		approach.getLocalTranslation().set( move.x + params.ROOM_CENTER_X, move.y + params.BALL_RADIUS_EXTRA/2, move.z + params.APPROACH_LENGTH/2 );
		approach.setMaterial( Material.WOOD );
	}
	
	
	public void createGutters( Vector3f move ) {
		float circumference = 2.0F * (float)Math.PI * params.BALL_RADIUS_EXTRA;
		for( int i = 1; i < params.GUTTER_SAMPLES; i++ ) {
			float rotation =  -(float)Math.PI/2 + (float)Math.PI / params.GUTTER_SAMPLES * i; 
			float angle = (float)Math.PI + (float)Math.PI / params.GUTTER_SAMPLES * i;
			// Left
			StaticPhysicsNode left = createStaticVisualBox( "gutter_left" );
			Utils.setColor( left,  ColorRGBA.gray, params.NO_SHININESS, params.NO_COLOR, renderer );
			Utils.setTexture( left,  "resources/textures/metal.jpg", renderer );
			left.getLocalScale().set( circumference/params.GUTTER_SAMPLES, params.LANE_LENGTH, params.GUTTER_THICK );
			left.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			left.setLocalTranslation( move.x - (params.LANE_WIDTH/2 + params.BALL_RADIUS_EXTRA) + params.BALL_RADIUS_EXTRA * (float)Math.cos( angle ), move.y + params.BALL_RADIUS_EXTRA + params.BALL_RADIUS_EXTRA * (float)Math.sin( angle ) + 0.001F, move.z - params.LANE_LENGTH/2 );
			left.setMaterial( Material.IRON );
			// Right
			StaticPhysicsNode right = createStaticVisualBox( "gutter_left" );
			Utils.setColor( right,  ColorRGBA.gray, params.NO_SHININESS, params.NO_COLOR, renderer );
			Utils.setTexture( right,  "resources/textures/metal.jpg", renderer );
			right.getLocalScale().set( circumference/params.GUTTER_SAMPLES, params.LANE_LENGTH, params.GUTTER_THICK );
			right.setLocalRotation( new Quaternion( new float[]{ (float)Math.PI/2, 0, rotation } ) );
			right.setLocalTranslation( move.x + (params.LANE_WIDTH/2 + params.BALL_RADIUS_EXTRA) + params.BALL_RADIUS_EXTRA * (float)Math.cos( angle ), move.y + params.BALL_RADIUS_EXTRA + params.BALL_RADIUS_EXTRA * (float)Math.sin( angle ) + 0.001F, move.z - params.LANE_LENGTH/2 );
			right.setMaterial( Material.IRON );
		}	
	}
	
	
	public void createSeparation( Vector3f move ) {
		// Left
		StaticPhysicsNode left = createStaticVisualBox( "separation" );
		Utils.setColor( left, ColorRGBA.white, params.LOW_SHININESS, ColorRGBA.white, renderer );
		Utils.setTexture( left, "resources/textures/metal.jpg", renderer );
		left.getLocalScale().set( params.SEPARATION_WIDTH/2, params.SEPARATION_HEIGHT, params.LANE_LENGTH);
		left.getLocalTranslation().set( move.x - (params.LANE_WIDTH/2 + params.BALL_DIAMETER_EXTRA + params.SEPARATION_WIDTH/4), move.y + params.SEPARATION_HEIGHT/2, move.z - params.LANE_LENGTH/2 );
		left.setMaterial( Material.IRON );
		// Right
		StaticPhysicsNode right = createStaticVisualBox( "separation" );
		Utils.setColor( right, ColorRGBA.white, params.LOW_SHININESS, ColorRGBA.white, renderer );
		Utils.setTexture( right, "resources/textures/metal.jpg", renderer );
		right.getLocalScale().set( params.SEPARATION_WIDTH/2, params.SEPARATION_HEIGHT, params.LANE_LENGTH);
		right.getLocalTranslation().set( move.x + (params.LANE_WIDTH/2 + params.BALL_DIAMETER_EXTRA + params.SEPARATION_WIDTH/4), move.y + params.SEPARATION_HEIGHT/2, move.z - params.LANE_LENGTH/2 );
		right.setMaterial( Material.IRON );
	}
	
	
	public void createBalls( Vector3f move ) {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( new URI("file:resources/scene/") ) );
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ColladaModelLoader loader = new ColladaModelLoader();
		Node balls = loader.getModel( "resources/scene/balls.dae" );
		try {
			ResourceLocatorTool.removeResourceLocator( ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( new URI("file:resources/scene/") ) );
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		childCreation( balls );
		balls.setLocalScale( 0.015F );
		balls.setLocalRotation( new Quaternion(new float[] {(float)-Math.PI/2,(float)-Math.PI/2,0} ) );
		balls.setLocalTranslation( new Vector3f( move.x + 0, move.y + params.BALL_RADIUS_EXTRA, move.z + 0 ) );
		rootNode.attachChild( balls );
	}
	
	
	public void createTV( Vector3f move ) {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( new URI("file:resources/scene/") ) );
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ColladaModelLoader loader = new ColladaModelLoader();
		Node tv = loader.getModel( "resources/scene/sorny.dae" );
		try {
			ResourceLocatorTool.removeResourceLocator( ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( new URI("file:resources/scene/") ) );
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create this node as a static physics node! (slower)
                // childCreation( tv );
		tv.setLocalScale( 0.4F );
		tv.setLocalRotation( new Quaternion(new float[] {(float)-Math.PI/2,(float)-Math.PI/2,(float)-Math.PI/6} ) );
		tv.setLocalTranslation( new Vector3f( move.x + 0, move.y + params.ROOM_HEIGHT + 0.4F, move.z + 0 ) );
		rootNode.attachChild( tv );
	}
	
	
	public void createBar() {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( new URI("file:resources/scene/") ) );
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ColladaModelLoader loader = new ColladaModelLoader();
		Node barVisual = loader.getModel( "resources/scene/bar.dae" );
		try {
			ResourceLocatorTool.removeResourceLocator( ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( new URI("file:resources/scene/") ) );
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create this node as a static physics node! (slower)
                // childCreation( barVisual );
		barVisual.setLocalScale( 0.01F );
		barVisual.setLocalRotation( new Quaternion(new float[] {(float)-Math.PI/2,(float)Math.PI/2,0} ) );
		barVisual.setLocalTranslation( new Vector3f(0.5F, 0, params.ROOM_BACK_Z) );
		barVisual.setCullHint( CullHint.Never );
		rootNode.attachChild( barVisual );
	}
	
	
	
	public void createBar2() {
		try {
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( new URI("file:resources/scene/") ) );
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ColladaModelLoader loader = new ColladaModelLoader();
		Node barVisual = loader.getModel( "resources/scene/bar2.dae" );
		try {
			ResourceLocatorTool.removeResourceLocator( ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( new URI("file:resources/scene/") ) );
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create this node as a static physics node! (slower)
                // childCreation( barVisual );
		barVisual.setLocalScale( 0.04F );
		barVisual.setLocalRotation( new Quaternion(new float[] {(float)-Math.PI/2,(float)Math.PI,0} ) );
		barVisual.setLocalTranslation( new Vector3f(6.7F, 0.01F, 0.8F) );
		barVisual.setCullHint( CullHint.Never );
		rootNode.attachChild( barVisual );
	}
	
	
	
	private void childCreation( Node node ) {
		List<Spatial> attach = new ArrayList<Spatial>();
		List<Spatial> childs = node.getChildren();
		if( childs == null ) {
			return;
		}
		for( Spatial child : childs ) {
			if( child instanceof Node ) {
				childCreation( (Node)child );
			}else{
				attach.add( child );
			}
		}
		for( Spatial n : attach ) {
			if( n instanceof SharedMesh ) {
				node.detachChild( n );
				Vector3f center = ((SharedMesh)n).getModelBound().getCenter().clone();
				n.setLocalTranslation( new Vector3f().subtract( center )  );
//				n.setModelBound( new BoundingBox() ); 
//				n.updateModelBound();
				PhysicsNode physics = physicsSpace.createStaticNode();
				physics.setName( n.getName() );
				physics.attachChild( n );
				physics.setLocalTranslation( center );
				physics.generatePhysicsGeometry();
				node.attachChild( physics );
			}
		}
	}
	
	
	public void createIlumination() {
		((PointLight)lightState.get(0)).setLocation(new Vector3f(0, params.ROOM_HEIGHT * 0.9F, 0));
		((PointLight)lightState.get(0)).setAmbient( new ColorRGBA(1,1,1,1) );
		lightState.setTwoSidedLighting(true);
		lightState.setEnabled(true);
		lightState.setGlobalAmbient( new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f) );
		
//		lightState.detachAll();
//		for (float i = 0; i <= params.LANE_LENGTH; i+=params.LANE_LENGTH/4) {
//			SpotLight pl = new SpotLight();
//			pl.setDirection(new Vector3f(0,-1,0));
//			pl.setAngle(45);
//	        pl.setAmbient(new ColorRGBA(1,1,1,1));
//	        pl.setDiffuse(new ColorRGBA(1f,0,0,1));
//	        pl.setLocation( new Vector3f(0, params.ROOM_HEIGHT * 0.9F, -i) );
//	        pl.setShadowCaster(true);
//	        pl.setEnabled(true);
//	        lightState.attach(pl);
//		}
	}
	

    private StaticPhysicsNode createStaticVisualBox( String name ) {
        StaticPhysicsNode staticNode = physicsSpace.createStaticNode();
        staticNode.setName( name );
        rootNode.attachChild( staticNode );
        final Box visualBox = new Box( name, new Vector3f(), 0.5f, 0.5f, 0.5f );
        staticNode.attachChild( visualBox );
        staticNode.generatePhysicsGeometry();
        return staticNode;
    }
	
	
}

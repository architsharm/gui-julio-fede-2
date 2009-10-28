package ar.edu.itba.cg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.animation.Bone;
import com.jme.animation.SkinNode;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingVolume;
import com.jmex.model.collada.ColladaImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;
import com.jmex.model.converters.X3dToJme;
import com.jme.input.FirstPersonHandler;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;

// Setting up Eclipse: http://www.jmonkeyengine.com/wiki/doku.php?id=setting_up_eclipse_to_build_jme_2

public class MomentoBowling extends SimpleGame {
	private static final Logger logger = Logger.getLogger( MomentoBowling.class.getName() );
	private static final String IMAGE_LOGO = "resources/logo.jpg";
	private static final String SCENE = "resources/scene.obj";
	private static final String SCENE_MODEL = "";
	private static final ColorRGBA BACKGROUND_COLOR = ColorRGBA.red.clone();
	private static final int CAMERA_MOVE_SPEED = 80;
	private static final int CAMERA_TURN_SPEED = 1;
	
	
//	public static void main(String [] args) throws MalformedURLException {
//		MomentoBowling app = new MomentoBowling(); 
//		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
//		app.start();
//	}

	
	@Override
	protected void simpleInitGame() {
		try{
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, new SimpleResourceLocator( new URL("file:resources/scene")) );
            Spatial sceneNode;
			if( SCENE.toLowerCase().endsWith(".x3d") || SCENE.toLowerCase().endsWith(".obj") ) {
            	FormatConverter converter = null;
            	if( SCENE.toLowerCase().endsWith(".x3d") ) {
            		converter = new X3dToJme();
            	}else{
            		converter = new ObjToJme();
            		if( SCENE_MODEL != null && !SCENE_MODEL.isEmpty() ) {
                		converter.setProperty( "mtllib", SCENE_MODEL );
                	}
            	}
            	URL sceneFile = new URL( "file:" + SCENE );
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                converter.convert( sceneFile.openStream(), bo );
                sceneNode = (Spatial) BinaryImporter.getInstance().load( new ByteArrayInputStream( bo.toByteArray() ) );
            }else if( SCENE.toLowerCase().endsWith(".dae") ) {
            	InputStream in = new FileInputStream( new File( SCENE ) );
            	ColladaImporter.load( in, "model" );
            	sceneNode = ColladaImporter.getModel();
            	ColladaImporter.cleanUp();
                SkinNode sn = ColladaImporter.getSkinNode(ColladaImporter.getSkinNodeNames().get(0));
                Bone skel = ColladaImporter.getSkeleton(ColladaImporter.getSkeletonNames().get(0));
                rootNode.attachChild(sn);
                rootNode.attachChild(skel);
            	ColladaImporter.cleanUp();
            }else{
            	sceneNode = null;
            }
			//sceneNode.setLocalScale( 1F );
			// Attach sceneNode to scene graph
			rootNode.attachChild( sceneNode );
			BoundingVolume limits = sceneNode.getWorldBound();
			// Set camera ( Our model is Z up so orient the camera properly. )
            cam.setAxes( new Vector3f(-1,0,0), new Vector3f(0,0,1), new Vector3f(0,1,0) );
            cam.setLocation( new Vector3f(0,0,30) );
            // Set input
            input = new FirstPersonHandler( cam, CAMERA_MOVE_SPEED, CAMERA_TURN_SPEED );
            // Set background
            //display.getRenderer().setBackgroundColor( BACKGROUND_COLOR );
            // Set lights
            PointLight light = new PointLight();
            light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
            light.setAmbient( new ColorRGBA( 1f, 1f, 1f, 1.0f ) );
            light.setLocation( new Vector3f( 0, 0, 40 ) );
            light.setEnabled( true );
            lightState = display.getRenderer().createLightState();
            lightState.setEnabled( true );
            lightState.attach( light );
            rootNode.setRenderState( lightState );
        }catch( Exception e ) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "simpleInitGame()", "Exception", e);
        }
    }

	
//	@Override
//	protected void simpleUpdate() {
//		// TODO Auto-generated method stub
//	}
  
	
}
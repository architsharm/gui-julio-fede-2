package ar.edu.itba.cg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;
import com.jmex.model.converters.X3dToJme;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.util.export.binary.BinaryImporter;

// Setting up Eclipse: http://www.jmonkeyengine.com/wiki/doku.php?id=setting_up_eclipse_to_build_jme_2

public class MomentoBowling extends SimpleGame {
	private static final Logger logger = Logger.getLogger(MomentoBowling.class.getName());
	private static final String IMAGE_LOGO = "resources/logo.jpg";
	private static final String SCENE = "resources/simpleScene.obj";
	private static final String MODEL = "";
	
	
	public static void main(String [] args) throws MalformedURLException {
		MomentoBowling app = new MomentoBowling(); 
		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
		app.start();
	}

	
	@Override
	protected void simpleInitGame() {
		try{
			FormatConverter converter = null;
            if( SCENE.toLowerCase().endsWith(".x3d") ) {
            	converter = new X3dToJme(); 
            }else if( SCENE.toLowerCase().endsWith(".obj") ) {
            	converter = new ObjToJme();
            	if( MODEL != null && !MODEL.isEmpty() ) {
            		converter.setProperty( "mtllib", MODEL );
            	}
            }else{
            	converter = null;
            }
            URL sceneFile = new URL( "file:" + SCENE );
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            converter.convert( sceneFile.openStream(), bo );
            Spatial r = (Spatial) BinaryImporter.getInstance().load( new ByteArrayInputStream( bo.toByteArray() ) );
            r.setLocalScale( 100 );
            rootNode.attachChild(r);
            display.getRenderer().setBackgroundColor( ColorRGBA.red.clone() );
            PointLight light = new PointLight();
            light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
            light.setAmbient( new ColorRGBA( 200f, 200f, 200f, 1.0f ) );
            light.setLocation( new Vector3f( 0, 0, 0 ) );
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
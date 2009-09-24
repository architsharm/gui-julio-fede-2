package ar.edu.itba.cg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jmex.model.converters.X3dToJme;
import com.jme.scene.Node;
import com.jme.util.export.binary.BinaryImporter;

// LWJGL: A Java Game Library extension: 
// 1. Handles the graphics, sound, and input simply 
// 2. Wraps OpenGL and OpenAL 
// 3. Hires timers LWJGL currently 
// supports Linux, Mac OS X (10.3 and above) and Windows (2000 and above).

// Extends SimpleGame giving a basic framework
public class MomentoBowling extends SimpleGame {
	private static final Logger logger = Logger.getLogger(MomentoBowling.class.getName());
	private static final String IMAGE_LOGO = "resources/logo.jpg";
	private static final String SCENE = "resources/simpleScene.x3d";
	
	
	public static void main(String [] args) throws MalformedURLException {
		MomentoBowling app = new MomentoBowling(); 
		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
		app.start();
	}

	
	@Override
	protected void simpleInitGame() {
		try {
            X3dToJme converter = new X3dToJme();
            URL x3dFile = new URL("file:" + SCENE);
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            logger.info("Starting to convert .x3d to .jme");
            converter.convert( x3dFile.openStream(), bo );
            logger.info("Done converting, now watch how fast it loads!");
            long time=System.currentTimeMillis();
            Node r= (Node)BinaryImporter.getInstance().load( new ByteArrayInputStream(bo.toByteArray()) );
            logger.info( "Finished loading time is " + ( System.currentTimeMillis() - time ) );
            rootNode.attachChild(r);
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "simpleInitGame()", "Exception", e);
        }
    }

	
//	@Override
//	protected void simpleUpdate() {
//		// TODO Auto-generated method stub
//	}
  
}
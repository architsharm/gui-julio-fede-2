package ar.edu.itba.cg.momento_bowling;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;

// LWJGL: A Java Game Library extension: 
// 1. Handles the graphics, sound, and input simply 
// 2. Wraps OpenGL and OpenAL 
// 3. Hires timers LWJGL currently 
// supports Linux, Mac OS X (10.3 and above) and Windows (2000 and above).

// Extends SimpleGame giving a basic framework
public class MomentoBowling extends SimpleGame {
	private static final Logger logger = Logger.getLogger(MomentoBowling.class.getName());
	private static final String IMAGE_LOGO = "resources/logo.jpg";
	
	public static void main(String [] args) throws MalformedURLException {
		MomentoBowling app = new MomentoBowling(); 
		app.setConfigShowMode( ConfigShowMode.AlwaysShow, new URL("file:" + IMAGE_LOGO ) );
		app.start();
	}

	@Override
	protected void simpleInitGame() {
		// TODO Auto-generated method stub	
	}
	
	@Override
	protected void simpleUpdate() {
		// TODO Auto-generated method stub
	}
  
}
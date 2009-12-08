package ar.edu.itba.cg;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Properties;

import com.jme.renderer.ColorRGBA;

public class SceneParameters {
	
	public String TITLE = "Bowling";
	// Parameters are in centimeters!
	// Pin Parameters
	public float PIN_HEIGHT = 0.40F;
	public float PIN_RADIUS = 0.65F;
	public float PIN_WEIGHT = 1.750F;
	// Ball Parameters
	public float BALL_RADIUS = 0.150F;
	public float BALL_WEIGHT = 2.800F;
	public int 	 BALL_SAMPLES = 20;		// The definition of the ball
	// Gutter Parameters
	public float GUTTER_EXTRA = 1.20F;// How much bigger or smaller than the ball (1 is the same)
	public int 	 GUTTER_SAMPLES = 25;	// The definition of the gutters
	public float GUTTER_THICK = 0.01F;
	// Lane Parameters
	// The middle of the foul line is at 0, BALL_RADIUS, 0
	// Ten Pin Bowling: http://en.wikipedia.org/wiki/Tenpin
	public float LANE_WIDTH = 1.05F;
	public float LANE_LENGTH = 18.00F;
	// Lane separation
	public float SEPARATION_WIDTH = 0.30F;
	public float SEPARATION_HEIGHT = 0.30F;
	// Approach Parameters
	// Behind the foul line is an "approach" used to gain speed
	public float APPROACH_LENGTH = 5.00F;
	// Room Parameters
	public float ROOM_HEIGHT = 2.20F;
	public float ROOM_BACK = 7.00F;
	// The wall thickness
	public float WALL_THICK = 0.01F;
	// Final box
	public float BOX_LENGTH = 1.00F;
	public float BOX_HEIGHT = 0.50F;
	public float BOX_TOP_HEIGHT = 1.00F;
	public float BOXMACHINE_LENGTH = 2.10F;
	//Pin Positions
	//Distance between to pins (12 inches)
	public float PIN_WIDTHDIST = 0.305F;
	//Half a distance between to pins (6 inches)
	public float PIN_WIDTHHALFDIST = 0.152F; 
	//Depth distance between two rows of pins (10.39 inches)
	public float PIN_HEIGHTDIST = 0.264F;
	//Initial position of the pin 1
	public float INITIAL_POS = 0F;
	//Distance between the pin 1 and the pit relative to the end of the lane
	public float DIST2PIT;
	// Shading
	public float NO_SHININESS = 0.0f;
	public float LOW_SHININESS = 5.0f;
	public float HIGH_SHININESS = 100.0f;
	// Camera speed
	public float CAMERA_MOVE_SPEED = 5;
	public float CAMERA_TURN_SPEED = 1;
	public float CAMERA_DISTANCE_MIN = 0.5F;
	public float CAMERA_DISTANCE_MAX = 3;
	// Calculated parameters
	public float BALL_RADIUS_EXTRA;
	public float BALL_DIAMETER;
	public float BALL_DIAMETER_EXTRA;
	public float APPROACH_WIDTH;
	public float ROOM_LENGTH;
	public float ROOM_BACK_Z;
	public float ROOM_FRONT_Z;
	public float ROOM_CENTER_X;
	public float ROOM_CENTER_Y;
	public float ROOM_CENTER_Z;
	// Constants
	public ColorRGBA NO_COLOR = ColorRGBA.black;

	
	public SceneParameters( String sceneParametersFile ) {
		Properties props = new Properties();
		try {
			props.load( new FileInputStream( sceneParametersFile ) );
            for( Enumeration<?> properties = props.propertyNames(); properties.hasMoreElements(); ) {
            	try {
            		String property = (String)properties.nextElement();
					Field field = this.getClass().getField( property );
					Type type = field.getType();
					if( type.equals(int.class) ) {
						field.set( this, Integer.parseInt(props.getProperty(property)) );
					}else if( type.equals(float.class) ){
						field.set( this, Float.parseFloat(props.getProperty(property)) );
					}else{
						field.set( this, props.getProperty(property) );
					}
	            }catch( SecurityException e ) {
					System.err.println( "Parametro inexistente: " + e.getMessage() );
				}catch( NoSuchFieldException e ) {
					System.err.println( "Parametro inexistente: " + e.getMessage() );
				}catch( IllegalArgumentException e ) {
					System.err.println( "Parametro inexistente: " + e.getMessage() );
				}catch( IllegalAccessException e ) {
					System.err.println( "Parametro inexistente: " + e.getMessage() );
				}
            }
		}catch( IOException e ) {
			System.err.println("Properties file could not be loaded");
        }
   		BALL_RADIUS_EXTRA = BALL_RADIUS * GUTTER_EXTRA;
   		BALL_DIAMETER = BALL_RADIUS * 2;
   		BALL_DIAMETER_EXTRA = BALL_RADIUS_EXTRA * 2;
   		APPROACH_WIDTH = LANE_WIDTH + BALL_DIAMETER_EXTRA * 2 + SEPARATION_WIDTH;
   		ROOM_LENGTH = ROOM_BACK + LANE_LENGTH + APPROACH_LENGTH + BOX_LENGTH;
   		ROOM_BACK_Z = ROOM_BACK + APPROACH_LENGTH;
   		ROOM_FRONT_Z = -(LANE_LENGTH + BOX_LENGTH);
   		ROOM_CENTER_X = 0;
   		ROOM_CENTER_Y = ROOM_HEIGHT / 2;
   		ROOM_CENTER_Z = ROOM_BACK / 2 + APPROACH_LENGTH / 2 - BOX_LENGTH / 2 - LANE_LENGTH / 2;
   		DIST2PIT = -LANE_LENGTH + 0.868F ;
	}
	
	
}

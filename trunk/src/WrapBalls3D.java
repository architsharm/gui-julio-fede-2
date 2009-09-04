
// WrapBalls3D.java
// Andrew Davison, April 2006, ad@fivedots.coe.psu.ac.th

/* Create the scene, using PhyBox for the physics and 
   graphical elements of the box, and PhySpheres and PhySphere
   for the spheres. The behaviour that drives the simulation,
   StepBehavior, is also created here.

   The initialization requires a physics world, and various
   collision objects. 

   There are NUM_SPHERES spheres, which can collide with each 
   other and with the sides, floor, and ceiling of the enclosing
   box.
*/


import javax.swing.*;
import java.awt.*;
import java.util.*;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.vp.*;

import org.odejava.*;
import org.odejava.ode.*;
import org.odejava.collision.*;



public class WrapBalls3D extends JPanel
// Holds the 3D canvas where the loaded image is displayed
{
  private static final int PWIDTH = 512;   // size of panel
  private static final int PHEIGHT = 512; 

  private static final int BOUNDSIZE = 100;  // larger than world

  private static final Point3d USERPOSN = new Point3d(0,3.5,11);
    // initial user position

  private static final int NUM_SPHERES = 10;

  private SimpleUniverse su;
  private BranchGroup sceneBG;
  private BoundingSphere bounds;   // for environment nodes

  // physical elements
  private World world;
  private HashSpace collSpace;      // holds collision info
  private JavaCollision collCalcs;  // calculates collisions

  private PhySpheres spheres;     // manages the bouncing spheres
  private StepBehavior stepBeh;   // behavior to advance the simulation



  public WrapBalls3D()
  {
    setLayout( new BorderLayout() );
    setOpaque( false );
    setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

    GraphicsConfiguration config =
					SimpleUniverse.getPreferredConfiguration();
    Canvas3D canvas3D = new Canvas3D(config);
    add("Center", canvas3D);
    canvas3D.setFocusable(true);     // give focus to the canvas 
    canvas3D.requestFocus();

    su = new SimpleUniverse(canvas3D);

    createSceneGraph();
    initUserPosition();        // set user's viewpoint
    orbitControls(canvas3D);   // controls for moving the viewpoint

    su.addBranchGraph( sceneBG );
  } // end of WrapBalls3D()



  private void createSceneGraph() 
  // initilise the scene
  { 
    sceneBG = new BranchGroup();
    bounds = new BoundingSphere(new Point3d(0,0,0), BOUNDSIZE);   

    lightScene();         // add the lights
    addBackground();      // add the sky
    sceneBG.addChild( new CheckerFloor().getBG() );  // add the floor

    initPhysWorld();    // the physical world
    addPhysObjects();   // add the box and spheres
    addStepper();       // step behaviour for simulation

    sceneBG.compile();   // fix the scene
  } // end of createSceneGraph()


  private void lightScene()
  /* One ambient light, 2 directional lights */
  {
    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    // Set up the ambient light
    AmbientLight ambientLightNode = new AmbientLight(white);
    ambientLightNode.setInfluencingBounds(bounds);
    sceneBG.addChild(ambientLightNode);

    // Set up the directional lights
    Vector3f light1Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
       // left, down, backwards 
    Vector3f light2Direction  = new Vector3f(1.0f, -1.0f, 1.0f);
       // right, down, forwards

    DirectionalLight light1 = 
            new DirectionalLight(white, light1Direction);
    light1.setInfluencingBounds(bounds);
    sceneBG.addChild(light1);

    DirectionalLight light2 = 
        new DirectionalLight(white, light2Direction);
    light2.setInfluencingBounds(bounds);
    sceneBG.addChild(light2);
  }  // end of lightScene()



  private void addBackground()
  // A blue sky
  { Background back = new Background();
    back.setApplicationBounds( bounds );
    back.setColor(0.17f, 0.65f, 0.92f);    // sky colour
    sceneBG.addChild( back );
  }  // end of addBackground()



  private void orbitControls(Canvas3D c)
  /* OrbitBehaviour allows the user to rotate around the scene, and to
     zoom in and out.  */
  {
    OrbitBehavior orbit = 
		new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
    orbit.setSchedulingBounds(bounds);

    ViewingPlatform vp = su.getViewingPlatform();
    vp.setViewPlatformBehavior(orbit);	 
  }  // end of orbitControls()


  private void initUserPosition()
  // Set the user's initial viewpoint using lookAt()
  {
    ViewingPlatform vp = su.getViewingPlatform();
    TransformGroup steerTG = vp.getViewPlatformTransform();

    Transform3D t3d = new Transform3D();
    steerTG.getTransform(t3d);

    // args are: viewer posn, where looking, up direction
    t3d.lookAt( USERPOSN, new Point3d(0,0,0), new Vector3d(0,1,0));
    t3d.invert();

    steerTG.setTransform(t3d);
  }  // end of initUserPosition()


  // ------------ the physical components of the scene ----------------


  private void initPhysWorld() 
  // inialize the world and collision objects
  {
    Odejava.getInstance();

    world = new World();
    // world.setGravity(0f, -0.2f, 0);  // down y-axis (9.8 is too fast)

    // set max interactions per step (bigger is more accurate, but slower)
    world.setStepInteractions(10);

    // set step size (smaller is more accurate, but slower)
    world.setStepSize(0.05f);

    // create a collision space for the world's box and spheres
    collSpace = new HashSpace();

    collCalcs = new JavaCollision(world);  // for collision calculations
  }  // end of initPhysWorld()



  private void addPhysObjects()
  /* the physical objects are the enclosing box and its spheres */
  {
    PhyBox box = new PhyBox(6.0f, 3.0f, 6.0f, collSpace);
    sceneBG.addChild( box.getBoxBG() );   // add the box to the scene

    // create the spheres
    spheres = new PhySpheres(sceneBG, world, collSpace);
    for(int i=0; i < NUM_SPHERES; i++)
      spheres.addSphere();
   }  // end of addPhysObjects();


  private void addStepper()
  // create the behaviour that advances the simulation
  {
    stepBeh = new StepBehavior(30, spheres, world, collSpace, collCalcs);  
           // it will be triggered every 30ms (== 33 frames/sec)

    stepBeh.setSchedulingBounds( bounds );
    sceneBG.addChild( stepBeh );
  }  // end of addStepper()



  public void cleanUp()
  // called from top-level frame at close down
  {
    stepBeh.setEnable(false);

    collSpace.delete();
    collCalcs.delete();
    world.delete();
    Ode.dCloseODE();
  }  // end of cleanUp()

} // end of WrapBalls3D class

// PhySpheres.java
// Andrew Davison, April 2006, ad@fivedots.coe.psu.ac.th

/* PhySpheres manages the PhySphere objects created in the application.
   It creates spheres with random radii, positions, velocities.
   
   The redraw() method updates all the spheres held in PhySpheres.

   Uses J2SE 5 generics
*/

import java.util.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import org.odejava.*;
import org.odejava.ode.*;



public class PhySpheres
{
  private ArrayList<PhySphere> spheres; 

  private String[] planetTexs = { "earth.jpg", "moon.jpg", "mars.jpg"};
  private Texture2D[] textures;   // holds the loaded textures

  private Random rand;
  private int counter = 0;

  private BranchGroup sceneBG;    // the scene graph

  // physics elements
  private World world;
  private HashSpace collSpace;



  public PhySpheres(BranchGroup sg, World w, HashSpace cs)
  {
    sceneBG = sg;
    world = w;
    collSpace = cs;

    spheres = new ArrayList<PhySphere>();
    rand = new Random();

    loadTextures();
  }  // end of PhySpheres()


  private void loadTextures()
  /* Load all the textures listed in planetTexs[], storing them
     in textures[]. */
  {
    int numTextures = planetTexs.length;
    textures = new Texture2D[numTextures];
    for(int i=0; i < numTextures; i++)
      textures[i] = loadTexture("images/" + planetTexs[i]);
  }  // end of loadTextures()


  private Texture2D loadTexture(String fn)
  // load image from file fn as a texture
  {
    TextureLoader texLoader = new TextureLoader(fn, null);
    Texture2D texture = (Texture2D) texLoader.getTexture();
    if (texture == null)
      System.out.println("Cannot load texture from " + fn);
    else {
      System.out.println("Loaded texture from " + fn);
      texture.setEnable(true);
    }
    return texture;
  }  // end of loadTexture()



  public void addSphere()
  /* Create a sphere with a randomly selected texture, radius,
     position, and linear velocity. The sphere is also given a name
     which is "Planet" + a number.
  */
  {
    Texture2D planTex = textures[ rand.nextInt(textures.length) ];
    float radius = rand.nextFloat()/4.0f + 0.2f;   // between 0.2 and 0.45

    PhySphere s = new PhySphere(world, collSpace, "planet "+counter, 
                          planTex, radius, randomPos(), randomVel());

    sceneBG.addChild( s.getSphereTG() );
    spheres.add(s);
    counter++;
  }  // end of addSphere()



  private Vector3f randomPos()
  /* Return a random position vector. The numbers are hardwired to be
     within the confines of the box. */
  {
    Vector3f pos = new Vector3f();
    pos.x = rand.nextFloat()*5.0f - 2.5f;   // -2.5 to 2.5
    pos.y = rand.nextFloat()*2.0f + 0.5f;   // 0.5 to 2.5
    pos.z = rand.nextFloat()*5.0f - 2.5f;   // -2.5 to 2.5
    return pos;
  }  // end of randomPos()


  private Vector3f randomVel()
  /* Return a velocity vector. The numbers were chosen to produce
     a reasonable looking speed. */
  {
    Vector3f vel = new Vector3f();
    vel.x = rand.nextFloat()*6.0f - 3.0f;   // -3.0 to 3.0
    vel.y = rand.nextFloat()*6.0f - 3.0f;   
    vel.z = rand.nextFloat()*6.0f - 3.0f;
    return vel;
  }  // end of randomVel()


  // -------------------------- access methods -----------------------
  // Called from StepBehavior

  public void redraw()
  /* Redraw all the spheres, which will change their positions
     and orientations. */
  {
    for(PhySphere ps: spheres)
      ps.redraw();
  }  // end of redraw()

}  // end of PhySpheres class
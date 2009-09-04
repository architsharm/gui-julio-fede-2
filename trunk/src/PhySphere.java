
// PhySphere.java
// Andrew Davison, April 2006, ad@fivedots.coe.psu.ac.th

/* PhySphere defines the graphical and physics-based elements
   of a sphere.

   The graphical sphere is textured and lit, and hangs below
   two TransformGroups (TGs) which are used to control its
   position and orientation.
   (NOTE: I could have used one TG for both the position and
    orientation, but this approach is easier to understand.)

   The physics-based sphere is a combined GeomSphere and body.
   The GeomSphere is for collision detection with other spheres
   and the sides of the box. 
   The body has a mass, position, linear, and angular velocity.
   The mass is based on the radius*MASS_FACTOR. The angular
   velocity is a clockwise spin around the y-axis.

   The sphere's visual position and orientation are updated via
   a call to update(), which uses the current position, and
   orientation of the physics sphere.
*/

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;
import javax.vecmath.*;

import org.odejava.*;
import org.odejava.ode.*;


public class PhySphere
{
  // sphere colours
  private static final Color3f BLACK = new Color3f(0.0f, 0.0f, 0.0f);
  private static final Color3f GRAY = new Color3f(0.6f, 0.6f, 0.6f);
  private static final Color3f WHITE = new Color3f(0.9f, 0.9f, 0.9f);

  // radius --> mass conversion
  private static final float MASS_FACTOR = 5.0f;


  // TGs which the sphere hangs off: 
  //       moveTG-->rotTG-->sphere
  private TransformGroup moveTG, rotTG;    
  private Transform3D t3d;           // used for accessing a TG's transform

  private Body sphereBody;

  private DecimalFormat df;  // for printing data during testing



  public PhySphere(World world, HashSpace collSpace, String name,
                          Texture2D tex, float radius, 
	                      Vector3f posVec, Vector3f velVec)
  {
    t3d = new Transform3D();
    df = new DecimalFormat("0.##");  // 2 dp

    makeSphere3D(tex, radius, posVec);     // makes the graphical parts
    makeSphereBody(world, collSpace, name, radius, posVec, velVec);  // physics parts
  }  // end of PhySphere()


  private void makeSphere3D(Texture2D tex, float radius, Vector3f posVec)
  /* A sphere with the specfied radius. 
     It's appearance is a combination of a gray lighted
     material and the texture loaded from tex.

     The TGs are for applying moves and rotations to the sphere.
        moveTG-->rotTG-->sphere
  */
  {
    Appearance app = new Appearance();

    // combine texture with material and lighting of underlying surface
    TextureAttributes ta = new TextureAttributes();
    ta.setTextureMode( TextureAttributes.MODULATE );
    app.setTextureAttributes( ta );

    // assign gray material with lighting
    Material mat= new Material(GRAY, BLACK, GRAY, WHITE, 25.0f);
       // sets ambient, emissive, diffuse, specular, shininess
    mat.setLightingEnable(true);
    app.setMaterial(mat);

    // apply texture to shape
    if (tex != null)
      app.setTexture(tex);

    // make the sphere with normals for lighting, and texture support
    Sphere sphere = new Sphere(radius, 
                           Sphere.GENERATE_NORMALS |
                           Sphere.GENERATE_TEXTURE_COORDS,
                           15, app);   // default divs == 15

    // create a transform group for rotating the sphere
    rotTG = new TransformGroup();
    rotTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    rotTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    rotTG.addChild(sphere);

    // create a transform group for moving the sphere
    t3d.set(posVec); 
    moveTG = new TransformGroup(t3d);
    moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    moveTG.addChild(rotTG);
   }  // end of makeSphere3D()




  private void makeSphereBody(World world, HashSpace collSpace, String name,
                         float radius, Vector3f posVec, Vector3f velVec) 
  // initialize sphere dynamics (a GeomSphere and a Body)
  {
    sphereBody = new Body(name, world, new GeomSphere(radius));
    sphereBody.adjustMass(MASS_FACTOR*radius); 
    sphereBody.setPosition(posVec);  // same as graphical sphere
    sphereBody.setLinearVel(velVec);
    sphereBody.setAngularVel(0, 2.0f, 0);  // clockwise around y-axis
    collSpace.addBodyGeoms( sphereBody );
  } // end of makeSphereBody()


  // -------------------- access methods ------------------------

  public TransformGroup getSphereTG()
  {  return moveTG;  }


  public void redraw()
  /* Redraw the sphere's on-screen position and orientation,
     by copying over the physics sphere's position and orientation.
     These will have been updated in the last simulation step 
     before redraw() was called.

     redraw() is called from PhySpheres (which calls every sphere's
     redraw()). The update() method in PhySphere's is called
     periodically from StepBehavior.
  */
  {
    // get position and orientation from the physics sphere 
    Vector3f posVec = sphereBody.getPosition();
    Quat4f quat = sphereBody.getQuaternion();
    // float angle = sphereBody.getAxisAngle(axisAng);
/*
    System.out.println("Pos: (" +
                 df.format(posVec.x) + ", " + df.format(posVec.y) + ", " +
                 df.format(posVec.z) + "), angle: " +
                 df.format( Math.toDegrees(angle)) );
                 // ", quat: " + ball.getQuaternion() );
*/

    // update the TGs in the graphical sphere
    t3d.set(posVec); 
    moveTG.setTransform(t3d);    // translate the sphere

    t3d.set(quat); 
    rotTG.setTransform(t3d);    // rotate the sphere
  }  // end of redraw()


}  // end of PhySphere class
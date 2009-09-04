
// StepBehavior.java
// Andrew Davison, April 2006, ad@fivedots.coe.psu.ac.th

/* StepBehaviour is periodically fired, every timeDelay ms.
   
   The physics simulation is advanced, taking account of any 
   collisions, and then the graphical spheres are updated
   using the physics spheres new positions/orientations.
*/


import java.util.Enumeration;
import java.text.DecimalFormat;

import javax.media.j3d.*;
import javax.vecmath.*;

import org.odejava.*;
import org.odejava.ode.*;
import org.odejava.collision.*;


public class StepBehavior extends Behavior
{
  private WakeupCondition timeOut;

  private PhySpheres spheres;

  private World world;
  private HashSpace collSpace;      // holds collision info
  private JavaCollision collCalcs;  // calculates collisions

  // private DecimalFormat df;   // for reporting position info
  // private Vector3f posVec;

  private Contact contactInfo;      // for accessing contact details


  public StepBehavior(int timeDelay, PhySpheres ps,
                   World w, HashSpace cs, JavaCollision cc)
  { 
    timeOut = new WakeupOnElapsedTime(timeDelay);
    spheres = ps;
    world = w;
    collSpace = cs;
    collCalcs = cc;

    // posVec = new Vector3f();
    // df = new DecimalFormat("0.##");  // 2 dp

    contactInfo = new Contact( collCalcs.getContactIntBuffer(),
                               collCalcs.getContactFloatBuffer());
  } // end of StepBehaviour()


  public void initialize()
  { wakeupOn( timeOut ); }


  public void processStimulus( Enumeration criteria )
  /* Called every timeDelay ms. The criteria argument is ignored. 

     Contact points are found, converted to joints, and the physics
     simulation is advanced, thereby changing the position and
     orientation of the physics spheres. redraw() is called in PhySpheres
     to use those changes to modify the _graphical_ sphere's
     position and orientation.
   */
  { 
    // step through the simulation
    collCalcs.collide(collSpace);   // find collisions
    examineContacts();              // examine contact points
    collCalcs.applyContacts();      // add contacts to contactInfo jointGroup
    world.stepFast();               // advance the simulation

    spheres.redraw();   // redraw the graphical spheres

    wakeupOn( timeOut );
  } // end of processStimulus()


  private void examineContacts() 
 // make sphere contacts bounce
  {
    for (int i = 0; i < collCalcs.getContactCount(); i++) {
      contactInfo.setIndex(i);     // look at the ith contact point

      // if the contact involves a sphere, then make the contact bounce
      if ((contactInfo.getGeom1() instanceof GeomSphere) ||
          (contactInfo.getGeom2() instanceof GeomSphere)) {
        contactInfo.setMode(Ode.dContactBounce);
        contactInfo.setBounce(1.0f);     // 1 is max bounciness
        contactInfo.setBounceVel(0.1f);  // min velocity for a bounce
        contactInfo.setMu(0);            // 0 is friction-less
        // contactInfo.getPosition(posVec);
        // System.out.println("  Contact: (" +  df.format(posVec.x) + ", " + 
        //         df.format(posVec.y) + ", " + df.format(posVec.z) + ")");
      }
    }
  } // end of examineContacts()

}  // end of StepBehavior class

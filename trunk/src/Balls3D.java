
// Balls3D.java
// Andrew Davison, April 2006, ad@fivedots.coe.psu.ac.th

/* A variant of the scene in Checkers3D: a checkboard floor
   two lights, a blue sky, and OrbitBehaviour to allow the 
   user to move the camera.

   The new visual elements are a translucent box with yellow 
   edges, filled with textured bouncing balls. The balls bounce
   off each other and the sides of the box.

   Each ball is a PhySphere object, and are manged by PhySpheres.
   The box is created using the PhyBox class.

   The physics simulation is advanced by StepBehavior, which is
   periodically triggered based on elapsed time. 

   NOTE: the Java collections used here employ generics, so this 
   program will only compile under J2SE 5, or later.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Balls3D extends JFrame
{
  private WrapBalls3D w3d;


  public Balls3D() 
  {
    super("Balls3D");
    Container c = getContentPane();
    c.setLayout( new BorderLayout() );
    w3d = new WrapBalls3D();     // panel holding the 3D canvas
    c.add(w3d, BorderLayout.CENTER);

    addWindowListener( new WindowAdapter() {
      public void windowClosing(WindowEvent e)
      { w3d.cleanUp();    // stop odejava
        System.exit(0);
      }
    });

    pack();
    setResizable(false);    // fixed size display
    setVisible(true);
  } // end of Balls3D()


// -----------------------------------------

  public static void main(String[] args)
  { new Balls3D(); }

} // end of Balls3D class


// PhyBox.java
// Andrew Davison, April 2006, ad@fivedots.coe.psu.ac.th

/* PhyBox manages the graphical and physics elements of the box.

   The box has dimensions specified by width, height, and depth 
   supplied in the constructor. It is centered on the origin, 
   with its base on the XZ plane. 

   The graphical box is translucent, but its edges are highlighted with 
   thick yellow lines (apart from the edges resting on the floor.)

   The physics box is defined by a floor on the XZ plane and 5 geom
   boxes for the 4 walls and ceiling.
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
import org.odejava.collision.*;


public class PhyBox
{
  // thickness of the box's geom walls and ceiling
  private static final float THICKNESS = 1.0f; 

  private BranchGroup boxBG;  // for holding the box's graphical parts



  public PhyBox(float width, float height, float depth, HashSpace collSpace)
  {
    makeBox(width, height, depth);        // makes the graphical parts
    makeBoxGeom(width, height, depth, collSpace);  // physics parts
  }  // end of PhyBox()


  private void makeBox(float width, float height, float depth)
  /* A transparent box resting on the floor
  */
  {
    float xDim = width/2.0f;
    float yDim = height/2.0f;
    float zDim = depth/2.0f;

    Appearance app = new Appearance();

    // switch off face culling
    PolygonAttributes pa = new PolygonAttributes();
    pa.setCullFace(PolygonAttributes.CULL_NONE);
    app.setPolygonAttributes(pa);

    // semi-transparent appearance
    TransparencyAttributes ta = new TransparencyAttributes();
    ta.setTransparencyMode( TransparencyAttributes.BLENDED );
    ta.setTransparency(0.7f);     // 1.0f is totally transparent
    app.setTransparencyAttributes(ta);

    // position the box: centered, resting on the XZ plane
    Transform3D t3d = new Transform3D();
    t3d.set( new Vector3f(0, yDim+0.01f,0));  
      /* the box is a bit above the floor, so it doesn't visual 
         interact with the floor. */
    TransformGroup boxTG = new TransformGroup(t3d);
    boxTG.addChild( new com.sun.j3d.utils.geometry.Box(xDim, yDim, zDim, app));   
             // set the box's dimensions and appearance

    Shape3D edgesShape =  makeBoxEdges(xDim, height, zDim);  // box edges

    // collect the box and edges together under a single BranchGroup
    boxBG = new BranchGroup();
    boxBG.addChild(boxTG);
    boxBG.addChild(edgesShape);
  }  // end of makeBox()


  private Shape3D makeBoxEdges(float x, float y, float z)
  /* Only 8 edges are needed, since the four edges
     of the box resting on the floor are not highlighted.
     8 edges are 8 lines, requiring 16 points in a LineArray.
  */
  {
    LineArray edges = new LineArray(16, LineArray.COORDINATES | 
                                        LineArray.COLOR_3);

    Point3f pts[] = new Point3f[16];
    // front edges
    pts[0] = new Point3f(-x, 0, z);   // edge 1 (left)
    pts[1] = new Point3f(-x, y, z); 

    pts[2] = new Point3f(-x, y, z);   // edge 2 (top)
    pts[3] = new Point3f( x, y, z); 

    pts[4] = new Point3f( x, y, z);   // edge 3 (right)
    pts[5] = new Point3f( x, 0, z); 

    // back edges
    pts[6] = new Point3f(-x, 0,-z);   // edge 4 (left)
    pts[7] = new Point3f(-x, y,-z); 

    pts[8] = new Point3f(-x, y,-z);   // edge 5 (top)
    pts[9] = new Point3f( x, y,-z); 

    pts[10] = new Point3f( x, y,-z);   // edge 6 (right)
    pts[11] = new Point3f( x, 0,-z); 

    // top edges, running front to back
    pts[12] = new Point3f(-x, y, z);   // edge 7 (left)
    pts[13] = new Point3f(-x, y,-z); 

    pts[14] = new Point3f( x, y, z);   // edge 8 (right)
    pts[15] = new Point3f( x, y,-z); 

    edges.setCoordinates(0, pts);

    // set the edges colour to yellow
    for(int i = 0; i < 16; i++) 
      edges.setColor(i, new Color3f(1, 1, 0));

    Shape3D edgesShape = new Shape3D(edges);

    // make the edges (lines) thicker
    Appearance app = new Appearance();
    LineAttributes la = new LineAttributes();
    la.setLineWidth(4);
    app.setLineAttributes(la);
    edgesShape.setAppearance(app);

    return edgesShape;
  }  // end of makeBoxEdges()



  private void makeBoxGeom(float width, float height, float depth, 
                                                     HashSpace collSpace) 
  /* The box geom consists of a floor, 4 walls, and a ceiling.
     The floor is a plane, while the walls and ceiling are boxes.
     The boxes are THICKNESS thick, and surround the space occupied
     by visual translucent box.
  */
  {
    float xDim = width/2.0f;
    float yDim = height/2.0f;
    float zDim = depth/2.0f;
    float midWall = THICKNESS/2.0f;

    collSpace.add( new GeomPlane(0, 1.0f, 0, 0));   // floor, facing upwards

   // the four walls
   GeomBox rightWall = new GeomBox(THICKNESS, height, depth);
   rightWall.setPosition(xDim+midWall, yDim, 0);
   collSpace.add(rightWall);

   GeomBox leftWall = new GeomBox(THICKNESS, height, depth);
   leftWall.setPosition(-(xDim+midWall), yDim, 0);
   collSpace.add(leftWall);

   GeomBox frontWall = new GeomBox(width, height, THICKNESS);
   frontWall.setPosition(0, yDim, zDim+midWall);
   collSpace.add(frontWall);

   GeomBox backWall = new GeomBox(width, height, THICKNESS);
   backWall.setPosition(0, yDim, -(zDim+midWall));
   collSpace.add(backWall);

   // the ceiling
   GeomBox ceiling = new GeomBox(width, THICKNESS, depth);
   ceiling.setPosition(0, height+midWall, 0);
   collSpace.add(ceiling);
  }  // end of makeBoxGeom()


  // -------------------- access methods ------------------------

  public BranchGroup getBoxBG()
  // return a reference to the graphical elements of the box
  {  return boxBG;  }


}  // end of PhyBox class

Chapter 5. When Worlds Collide

From:
  Pro Java 6 3D Game Development
  Andrew Davison
  Apress, April 2007
  ISBN: 1590598172 
  http://www.apress.com/book/bookDisplay.html?bID=10256
  Web Site for the book: http://fivedots.coe.psu.ac.th/~ad/jg2


Contact Address:
  Dr. Andrew Davison
  Dept. of Computer Engineering
  Prince of Songkla University
  Hat Yai, Songkhla 90112, Thailand
  E-mail: ad@fivedots.coe.psu.ac.th


If you use this code, please mention my name, and include a link
to the book's Web site.

Thanks,
  Andrew


==================================
Balls3D

A simple Java 3D world containing a box full of bouncing balls.
The collision detection is handled by Odejava.

You can move the camera around the scene by using the Java 3D
OrbitBehavior controls
  -- try dragging the mouse while holding down 
     different mouse buttons

==================================
Files and directories here:

  * Balls3D.java, CheckerFloor.java, ColouredTiles.java,
    PhyBox.java, PhySphere.java, PhySpheres.java
    StepBehavior.java, WrapBalls3D.java
       // 8 Java files

  * images/	// a directory holding 3 textures
      - earth.jpg, mars.jpg, moon.jpg

==================================
Requirements:

* J2SE 5, Java 3D, Odejava
  -- see readme.txt in the parent directory above this one
     for installation details

==================================
Compilation: 
  $ javac *.java

Execution: 
  $ java Balls3D

-----------
Last updated: 3rd March 2007
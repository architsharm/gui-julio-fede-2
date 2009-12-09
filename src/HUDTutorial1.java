 import com.jme.app.SimpleGame;
 import com.jme.bounding.BoundingBox;
 import com.jme.math.Quaternion;
 import com.jme.math.Vector3f;
 import com.jme.renderer.ColorRGBA;
 import com.jme.renderer.Renderer;
 import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.shape.Arrow;
 import com.jme.scene.shape.Cylinder;
 import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
 import com.jme.scene.state.LightState;
 import com.jme.scene.state.MaterialState;
import com.jme.scene.Spatial;


 public class HUDTutorial1 extends SimpleGame {

	 private Quaternion rotQuat = new Quaternion();
     private Vector3f axis = new Vector3f(1, 1, 0);
     private Cylinder cylinder;
     private float angle = 0;
     private Node hudNode;
     private int textureWidth;
     private int textureHeight;

     public static void main(String[] args) {
         HUDTutorial1 app = new HUDTutorial1();
         app.setConfigShowMode(SimpleGame.ConfigShowMode.AlwaysShow);
         app.start();
     }
     protected void simpleInitGame() {
         display.setTitle("HUD Tutorial 1");
 
         /* create a rotating cylinder so we have something in the background */
         cylinder = new Cylinder("Cylinder", 6, 18, 5, 10);
         cylinder.setModelBound(new BoundingBox());
         cylinder.updateModelBound();
 
         MaterialState ms = display.getRenderer().createMaterialState();
         ms.setAmbient(new ColorRGBA(1f, 0f, 0f, 1f));
         ms.setDiffuse(new ColorRGBA(1f, 0f, 0f, 1f));
 
         /* has been depricated */
         //ms.setAlpha(1f);
 
         ms.setEnabled(true);
         cylinder.setRenderState(ms);
         cylinder.updateRenderState();
 
         rootNode.attachChild(cylinder);
         hudNode = new Node("hudNode");
         Quad border_bigbox = new Quad("border_bigbox", 40f, 40f);
         Quad bigbox = new Quad("bigbox",38f, 38f);
         border_bigbox.setRenderQueueMode(Renderer.QUEUE_ORTHO);     
         bigbox.setRenderQueueMode(Renderer.QUEUE_ORTHO);

         int height = display.getHeight();
         int width = display.getWidth();
         
         System.out.println(width*1/6);
         System.out.println(height*6/7);
         
//         border_bigbox.setLocalTranslation(new Vector3f(40 ,height-40,0));
//         bigbox.setLocalTranslation(new Vector3f(40 ,height-40,0));
         Text opt = Text.createDefaultTextLabel("");
         opt.print("100");
         opt.setLocalTranslation(-opt.getWidth()/2, -opt.getHeight()/2, 0);
         opt.setLightCombineMode(Spatial.LightCombineMode.Off);
         opt.setTextColor(ColorRGBA.red);
         
 
         border_bigbox.setLightCombineMode(Spatial.LightCombineMode.Off);
         border_bigbox.updateRenderState();
         bigbox.setLightCombineMode(Spatial.LightCombineMode.CombineClosestEnabled);
         bigbox.updateRenderState();
 
         hudNode.attachChild(border_bigbox);
         hudNode.attachChild(bigbox);
         hudNode.attachChild(opt);
         hudNode.setLocalTranslation(new Vector3f(40 ,height-40,0));
         rootNode.attachChild(hudNode);
 
     }
     protected void simpleUpdate() {
         /* recalculate rotation for the cylinder */
         if (timer.getTimePerFrame() < 1) {
             angle = angle + timer.getTimePerFrame();
         }
 
         rotQuat.fromAngleAxis(angle, axis);
         cylinder.setLocalRotation(rotQuat);
     }
     
}

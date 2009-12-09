 import java.nio.FloatBuffer;

import com.jme.image.Texture;
 import com.jme.math.Quaternion;
 import com.jme.math.Vector2f;
 import com.jme.math.Vector3f;
 import com.jme.renderer.ColorRGBA;
 import com.jme.scene.Node;
import com.jme.scene.TexCoords;
 import com.jme.scene.shape.Cylinder;
 import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
 import com.jme.scene.state.LightState;
 import com.jme.scene.state.MaterialState;
 import com.jme.scene.state.TextureState;
 import com.jme.util.TextureManager; 
import com.jme.util.geom.BufferUtils;
 import com.jme.app.SimpleGame;
 import com.jme.bounding.BoundingBox;
 import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
 
public class HudTutorial3 extends SimpleGame{
    private static final float MAXIMUM = 100f;
    private Quaternion rotQuat = new Quaternion();
    private Vector3f axis = new Vector3f(1, 1, 0);
    private Cylinder cylinder;
    private float angle = 0;

    private Node hudNode;
    private Quad gauge;

    private int textureWidth;
    private int textureHeight;
    
    public static void main(String[] args) {
        HudTutorial3 app = new HudTutorial3();
        app.setConfigShowMode(SimpleGame.ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle("HUD Tutorial 3");

        /* create a rotating cylinder so we have something in the background */
        cylinder = new Cylinder("Cylinder", 6, 18, 5, 10);
        cylinder.setModelBound(new BoundingBox());
        cylinder.updateModelBound();

        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setAmbient(new ColorRGBA(1f, 0f, 0f, 1f));
        ms.setDiffuse(new ColorRGBA(1f, 0f, 0f, 1f));

        ms.setEnabled(true);
        cylinder.setRenderState(ms);
        cylinder.updateRenderState();

        rootNode.attachChild(cylinder);

        hudNode = new Node("hudNode");
        Quad hudQuad = new Quad("hud", 34f, 10f);
        gauge = new Quad("gauge", 32f, 8f);
        hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        
        hudNode.setLocalTranslation(new Vector3f(display.getWidth()/2,display.getHeight()/2,0));        
 
        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(getClass().getClassLoader()
                .getResource("data/images/hud/hudtutorial3.png"),Texture.MinificationFilter.Trilinear, // of no use for the quad
                Texture.MagnificationFilter.Bilinear, // of no use for the quad
                1.0f, true));
        textureWidth = ts.getTexture().getImage().getWidth();
        textureHeight = ts.getTexture().getImage().getHeight();
        ts.setEnabled(true);
     // correct texture application:
        final FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);
        // coordinate (0,0) for vertex 0
        texCoords.put(getUForPixel(0)).put(getVForPixel(0));
        // coordinate (0,40) for vertex 1
        texCoords.put(getUForPixel(0)).put(getVForPixel(40));
        // coordinate (40,40) for vertex 2
        texCoords.put(getUForPixel(40)).put(getVForPixel(40));
        // coordinate (40,0) for vertex 3
        texCoords.put(getUForPixel(40)).put(getVForPixel(0));
        // assign texture coordinates to the quad
        hudQuad.setTextureCoords(new TexCoords(texCoords));
        // apply the texture state to the quad
        hudQuad.setRenderState(ts);

 
        BlendState as = display.getRenderer().createBlendState();
       
        as.setBlendEnabled(true);
 
        as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        as.setTestEnabled(false);
        as.setEnabled(true);      
        
        hudNode.setLightCombineMode(Spatial.LightCombineMode.Off);        
        hudNode.updateRenderState();
 
        hudNode.attachChild(hudQuad);
        hudNode.attachChild(gauge);
 
        hudNode.setRenderState(ts);
        hudNode.setRenderState(as);
        hudNode.updateRenderState();
        setGauge(0);
        rootNode.attachChild(hudNode);
     }
    protected void simpleUpdate() {
        /* recalculate rotation for the cylinder */
        if (timer.getTimePerFrame() < 1) {
            angle = angle + timer.getTimePerFrame();
        }
        rotQuat.fromAngleAxis(angle, axis);
        cylinder.setLocalRotation(rotQuat);
        setGauge((int)cam.getLocation().length());
   }
    private float getUForPixel(int xPixel) {
        return (float) xPixel / textureWidth;
    }

    private float getVForPixel(int yPixel) {
        return 1f - (float) yPixel / textureHeight;
    }
    
    private void setGauge(int value) {
        value %= (int)MAXIMUM;
        FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);
        float relCoord = 0.5f - ((float)value / MAXIMUM) * 0.5f;
        texCoords.put(relCoord).put(getVForPixel(56));
        texCoords.put(relCoord).put(getVForPixel(63));
        texCoords.put(relCoord + 0.5f).put(getVForPixel(63));
        texCoords.put(relCoord + 0.5f).put(getVForPixel(56));     
        gauge.setTextureCoords(new TexCoords(texCoords));
   }


}

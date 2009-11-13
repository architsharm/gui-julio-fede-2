package ar.edu.itba.cg;

import java.net.MalformedURLException;
import java.net.URL;

import com.jme.image.Texture;
import com.jme.image.Texture.WrapMode;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class Utils {

	public static void setColor( Spatial spatial, ColorRGBA diffuseColor, float shininess, ColorRGBA specularColor, Renderer renderer ) {
        final MaterialState materialState = renderer.createMaterialState();
        materialState.setDiffuse( diffuseColor );
        materialState.setSpecular( specularColor );
        materialState.setShininess( shininess );
        materialState.setEmissive( ColorRGBA.black );
        materialState.setEnabled( true );
        spatial.setRenderState( materialState );
    }
	
	
	public static void setTexture( Spatial spatial, String image, Renderer renderer ) {
		TextureState textureState = renderer.createTextureState();
		Texture texture = null;
		try {
			texture = TextureManager.loadTexture(
				new URL( "file:" + image ),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear 
			);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		texture.setWrap( WrapMode.Repeat );
		textureState.setTexture( texture );
	    spatial.setRenderState(textureState);
	}
	
}

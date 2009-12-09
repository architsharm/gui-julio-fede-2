package ar.edu.itba.cg.menu;

import java.util.ArrayList;

import ar.edu.itba.cg.Bowling;
import ar.edu.itba.cg.Bowling.States;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.shape.Quad;
import com.jmex.font2d.Text2D;

public class GameMenu {

	private Node gameMenu;
	private int screenWidth;
	private int screenHeight;
	private Node statNode;
	private ArrayList<Text[]> score = new ArrayList();

	public GameMenu(Node statNode, int screenWidth, int screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.statNode = statNode;
	}

	private Text createBox(float boxHeight,float BoxWeight,Vector3f location, Node parent) {
		Node hudNode = new Node();
		hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		//Creation of the box
		Quad border_box = new Quad(Double.toString(Math.random()),BoxWeight, boxHeight);
		Quad box = new Quad(Double.toString(Math.random()), BoxWeight-2, boxHeight-2);
		
		//Setting the boxes to be orthogonal
//		box.setLightCombineMode(Spatial.LightCombineMode.Inherit);
//		border_box.setLightCombineMode(Spatial.LightCombineMode.Off);
		//Setting the lighting to all the boxes
		border_box.setDefaultColor(ColorRGBA.red);
		border_box.updateRenderState();
		box.setDefaultColor(ColorRGBA.cyan);
		box.updateRenderState();
		
		Text opt = Text.createDefaultTextLabel("sdf");
		opt.setTextColor(ColorRGBA.green);
//		opt.setDefaultColor(ColorRGBA.red);
//		opt.setSolidColor(ColorRGBA.blue);
		opt.setLocalScale(0.75f);
		opt.print("Hola mundo!");
		opt.setLocalTranslation(-opt.getWidth() / 2, -opt.getHeight() / 2, 0);

		hudNode.attachChild(border_box);
		hudNode.attachChild(box);
		hudNode.attachChild(opt);
		hudNode.setLocalTranslation(location);
		hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		parent.attachChild(hudNode);
		
		return opt;
	}
	
	private Text[] createBoxCombination(Vector3f location, int amountSubBoxes, Node parent){
		
		Node node = new Node();
		Text[] options = new Text[amountSubBoxes+1];
		float smallBoxSize = 20f;
		
		for(int i= 0; i<amountSubBoxes;i++){
			Vector3f subLocation = new Vector3f( (smallBoxSize*i)+(smallBoxSize/2)-(smallBoxSize*amountSubBoxes)/2,smallBoxSize*1.5f, 0 );
			options[i]=createBox(smallBoxSize, smallBoxSize, subLocation, node);
		}

		if(amountSubBoxes == 3)
			options[amountSubBoxes]=createBox(smallBoxSize*(amountSubBoxes-1),smallBoxSize*amountSubBoxes,new Vector3f(0,0,0),node);
		else
			options[amountSubBoxes]=createBox(smallBoxSize*amountSubBoxes,smallBoxSize*amountSubBoxes,new Vector3f(0,0,0),node);
		
		node.setLocalTranslation(location);
		node.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		parent.attachChild(node);
		return options;		
	}
	
	public void createScore(){
		
		float delta = 40f;
		
		Node node = new Node();
		for(int i=0 ; i <10; i++)
		{
			Vector3f location = new Vector3f(delta + delta*i ,screenHeight-delta*1.25f,0);
			if(i!=9){
				score.add(createBoxCombination(location,2,node));
			}else{
				location.setX(location.getX() + delta/4);
				score.add(createBoxCombination(location,3,node));
			}
		}
		node.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		statNode.attachChild(node);
	}

}

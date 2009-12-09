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
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.RenderState.StateType;
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

	private Text createBox(float boxHeight,float boxwidth,Vector3f location, Node parent) {
		Node hudNode = new Node();
		//Setting the lighting to all the boxes
		hudNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		//Creation of the box
		Quad border_box = new Quad(Double.toString(Math.random()),boxwidth, boxHeight);
		Quad box = new Quad(Double.toString(Math.random()), boxwidth-2, boxHeight-2);
		
		Text opt = Text.createDefaultTextLabel(Double.toString(Math.random()));
		opt.setTextColor(ColorRGBA.black);
		

		border_box.setDefaultColor(ColorRGBA.red);
		border_box.updateRenderState();
		box.setDefaultColor(ColorRGBA.white);
		box.updateRenderState();

		hudNode.attachChild(border_box);
		hudNode.attachChild(box);
		hudNode.attachChild(opt);
		hudNode.setLocalTranslation(location);
		parent.attachChild(hudNode);
		opt.print(" ");
		opt.updateRenderState();
		opt.setLocalTranslation(-opt.getWidth() / 2, -opt.getHeight() / 2, 0);
		
		return opt;
	}
	
	private Text[] createBoxCombination(Vector3f location, int amountSubBoxes, float smallBoxSize,Node parent){
		
		Node node = new Node();
		Text[] options = new Text[amountSubBoxes+1];
		
		for(int i= 0; i<amountSubBoxes;i++){
			Vector3f subLocation = new Vector3f( (smallBoxSize*i)+(smallBoxSize/2)-(smallBoxSize*amountSubBoxes)/2,smallBoxSize*1.5f, 0 );
			options[i]=this.createBox(smallBoxSize, smallBoxSize, subLocation, node);
		}

		if(amountSubBoxes == 3) {
			options[amountSubBoxes]=this.createBox(smallBoxSize*(amountSubBoxes-1),smallBoxSize*amountSubBoxes,new Vector3f(0,0,0),node);
		} else {
			options[amountSubBoxes]=this.createBox(smallBoxSize*amountSubBoxes,smallBoxSize*amountSubBoxes,new Vector3f(0,0,0),node);
		}
		
		node.setLocalTranslation(location);
		parent.attachChild(node);
		return options;
	}
	
	public void createScore(){

		float smallBoxSize = 20f;
		float delta = smallBoxSize * 2;
		
		Node node = new Node();
		for(int i=0 ; i <10; i++)
		{
			Vector3f location = new Vector3f(delta + delta*i ,screenHeight-delta*1.25f,0);
			if(i!=9){
				score.add(createBoxCombination(location,2,smallBoxSize,node));
			}else{
				location.setX(location.getX() + delta/4);
				score.add(createBoxCombination(location,3,smallBoxSize,node));
			}
		}
		statNode.attachChild(node);
		
		for (int i=0;i<10;i++) {
			this.setScore(i, 0, -1);
			this.setScore(i, 1, -1);
			this.setScore(i, 2, -1);
		}
	}
	
	public void setScore(int num, int shot, int value) {
		String strValue;
		if (value == -1) {
			strValue = "";
		} else {
			strValue = Integer.toString(value);
		}
		Text node = score.get(num)[shot];
		node.print(strValue);
		node.updateRenderState();
	}

}

package ar.edu.itba.cg.menu;

import ar.edu.itba.cg.Bowling;
import ar.edu.itba.cg.Bowling.States;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;

public class HelpMenu {

	private Node help;
//	private Text opt2;
//	private Text opt3;
//	private Text[] options = new Text[3];
	private int screenWidth;
	private int screenHeight;
	private Node statNode;
	private Bowling game;
	private int internalState = 0;
	private long lastUpdatedTime = System.currentTimeMillis();
	//milliseconds to await after updating the menu status
	private long delta = 100;
	
	
	public HelpMenu(Node statNode,int screenWidth, int screenHeight, Bowling game) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.statNode = statNode;
		this.game = game;
	}

	public void showAllOptions() {
		
		if (help == null || !statNode.hasChild(this.help)) {
			help = this.createText("Con las flechas te moves\nCon W y S le das fuerza para adelante y para atras\nCon Z soltas la bola\nCon Q y E le das efecto\n\nPara salir de este menu presione 0 (cero)\n");
			statNode.attachChild(help);
		}
//		opt2.print("Help");
//		statNode.attachChild(opt2);
//		opt3.print("Quit game");
//		statNode.attachChild(opt3);
	}
	
	public void hideAllOptions(){
		
		if(statNode.hasChild(help)){
			statNode.detachChild(help);
//			statNode.detachChild(opt2);
//			statNode.detachChild(opt3);
		}
	}

	
	private Node createText(String text) {
		Node node = new Node();
		String[] parts = text.split("\n");
		float height = 0;
		for (String part : parts) {
			Text opt = Text.createDefaultTextLabel("", part);
			opt.print(part);
			opt.setLocalTranslation(screenWidth/2 - opt.getWidth()/2, -height, 0);
			height += opt.getHeight();
			node.attachChild(opt);
		}
		node.setLocalTranslation(0, screenHeight/2 - height/2, 0);
		return node;
	}

	public void keyScape(){
		
		game.setState(States.MENU);
		this.hideAllOptions();
		game.showStartUpMenu();
	}

}

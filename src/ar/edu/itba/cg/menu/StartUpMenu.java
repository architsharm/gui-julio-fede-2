package ar.edu.itba.cg.menu;

import ar.edu.itba.cg.Bowling;
import ar.edu.itba.cg.Bowling.States;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Text;
import com.jme.scene.Node;

public class StartUpMenu {

	private Text opt1;
	private Text opt2;
	private Text opt3;
	private Text[] options = new Text[3];
	private int screenWidth;
	private int screenHeight;
	private Node statNode;
	private Bowling game;
	private int internalState = 0;
	private long lastUpdatedTime = System.currentTimeMillis();
	//milliseconds to await after updating the menu status
	private long delta = 100;
	
	
	public StartUpMenu(Node statNode,int screenWidth, int screenHeight, Bowling game) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.statNode = statNode;
		this.game = game;
		this.setMenu();
	}

	public void showAllOptions() {
		
		opt1.print("New game");
		statNode.attachChild(opt1);
		opt2.print("Help");
		statNode.attachChild(opt2);
		opt3.print("Quit game");
		statNode.attachChild(opt3);
	}
	
	public void hideAllOptions(){
		
		if(statNode.hasChild(opt1)){
			statNode.detachChild(opt1);
			statNode.detachChild(opt2);
			statNode.detachChild(opt3);
		}
	}

	private void setMenu() {

		opt1 = Text.createDefaultTextLabel("opt1");
		opt2 = Text.createDefaultTextLabel("opt2");
		opt3 = Text.createDefaultTextLabel("opt3");
		opt1.setLocalScale(1.2f);
		opt2.setLocalScale(1.2f);
		opt3.setLocalScale(1.2f);
		opt1.setLocalTranslation(screenWidth / 2 - 30, (screenHeight / 2) + 40,	0);
		opt2.setLocalTranslation(screenWidth / 2 - 30, (screenHeight / 2), 0);
		opt3.setLocalTranslation(screenWidth / 2 - 30, (screenHeight / 2) - 40,	0);
		opt1.setTextColor(ColorRGBA.red);
		opt2.setTextColor(ColorRGBA.white);
		opt3.setTextColor(ColorRGBA.white);
		options[0]= opt1;
		options[1]= opt2;
		options[2]= opt3;
		

	}
	
	public void keyUp(){
		
		long now = System.currentTimeMillis();
		if((now - lastUpdatedTime)> delta)
		{
			//change the old selected option to white
			options[internalState].setTextColor(ColorRGBA.white);
			
			if(internalState > 0)
				internalState--;
			else
				internalState = 2;
			
			//change the new selected option to red
			options[internalState].setTextColor(ColorRGBA.red);
			lastUpdatedTime = now;				
		}
			
	}
	
	public void keyDown(){
	
		long now = System.currentTimeMillis();
		if((now - lastUpdatedTime)> delta)
		{
			//change the old selected option to white
			options[internalState].setTextColor(ColorRGBA.white);
			
			if(internalState < 2)
				internalState++;
			else
				internalState = 0;
			
			//change the new selected option to red
			options[internalState].setTextColor(ColorRGBA.red);
			lastUpdatedTime = now;	
		}
			
	}
	
	public void keyEnter(){
		
		if(internalState == 0)
			game.setState(States.SHOOTING);
		else if (internalState == 1)
			game.setState(States.HELP);
		else
			game.setState(States.EXIT);
		this.hideAllOptions();
	}

}

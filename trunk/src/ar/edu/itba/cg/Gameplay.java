package ar.edu.itba.cg;

import com.jme.scene.Node;

import ar.edu.itba.cg.Bowling.States;
import ar.edu.itba.cg.menu.GameMenu;

public class Gameplay {
	private int shoot = 0;
	private int frame = 0;
	private int[][] pinsDown = new int[10][];
	private GameMenu gameMenu;
	private int screenWidth;
	private int screenHeight;
	private Node statNode;
	private Dynamics dynamics;
	private Bowling game;
		
	public Gameplay(Node statNode, int screenWidth, int screenHeight,Dynamics dynamics, Bowling game) {
		
		gameMenu = new GameMenu(statNode, screenWidth, screenHeight);
		this.dynamics = dynamics;
		this.game =game;
		
		for(int i=0; i<9; i++){
			pinsDown[i]= new int[2];
		}
		this.pinsDown[9]= new int[3];
	}
	
	
	public void updateScore(int pinsDown){
		
		int amountPinsDown = 0;
				
		if(shoot == 0)
			this.pinsDown[frame][shoot] = pinsDown;
		else
			this.pinsDown[frame][shoot] = pinsDown - this.pinsDown[frame][shoot-1];
		
		amountPinsDown = this.pinsDown[frame][shoot];
		
		if(pinsDown == 10 && shoot == 0){
			gameMenu.setScore(frame, shoot, "X");
			
		}else if(pinsDown == 10 ){
			gameMenu.setScore(frame, shoot, "/");
			
		}else
			gameMenu.setScore(frame, shoot, amountPinsDown);
		
		if (this.pinsDown[frame][shoot] == 10 || shoot == 1){
			if(frame!=9){
			shoot = 0;
			frame ++;
			}else if(shoot == 2){
				shoot=0;
				frame=0;
				game.setState(States.HELP);
				
			}
			else
				shoot++;
			
			dynamics.resetPins();
		}else
			shoot++;
		
	}
	
	public int getScore() {
		return 0;
	}
	
	
	public int getScore(int shoot, int frame) {
		return pinsDown[frame][shoot];
	}
	
	
}

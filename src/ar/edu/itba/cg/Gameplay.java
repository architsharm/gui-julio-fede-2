package ar.edu.itba.cg;

import java.util.ArrayList;

import ar.edu.itba.cg.Bowling.States;
import ar.edu.itba.cg.menu.GameMenu;

public class Gameplay {
	private int shoot = 0;
	private int frame = 0;
	private int[][] pinsDown = new int[10][];
	private GameMenu gameMenu;
	private Dynamics dynamics;
	private Bowling game;
	private ArrayList<Integer> spares = new ArrayList<Integer>();
	private ArrayList<Integer> strikes = new ArrayList<Integer>();
	private int[] frameTotal;
		
	public Gameplay(Dynamics dynamics, Bowling game, GameMenu gameMenu) {
		
		this.gameMenu = gameMenu;
		this.dynamics = dynamics;
		this.game = game;
		
		this.resetScore();
	}
	
	public void resetScore() {
		for(int i=0; i<9; i++){
			pinsDown[i]= new int[2];
		}
		this.pinsDown[9]= new int[3];
		shoot = 0;
		frame = 0;
		frameTotal = new int[10];
		this.strikes.clear();
		this.spares.clear();
		gameMenu.resetScore();
	}
	
	public void updateScore(int pinsDown){
		
		int amountPinsDown = 0;
				
		if(shoot==3){
			shoot=0;
			frame=0;
			game.setState(States.MENU);
			game.showStartUpMenu();
		}
		
		if(shoot == 0)
			this.pinsDown[frame][shoot] = pinsDown;
		else if (frame == 9) {
			if (this.pinsDown[frame][shoot - 1] == 10) {
				this.pinsDown[frame][shoot] = pinsDown;
			} else if (shoot == 2) {
				this.pinsDown[frame][shoot] = pinsDown - this.pinsDown[frame][shoot-1];
			}
		}
		else
			this.pinsDown[frame][shoot] = pinsDown - this.pinsDown[frame][shoot-1];
		
		amountPinsDown = this.pinsDown[frame][shoot];
		
		if(pinsDown == 10 && shoot == 0 || (frame == 9 && this.pinsDown[frame][shoot -1] == 10)){
			this.strikes.add(frame);
			gameMenu.setScore(frame, shoot, "X");
		} else if(pinsDown == 10 ){
			this.spares.add(frame);
			gameMenu.setScore(frame, shoot, "/");
		} else {
			gameMenu.setScore(frame, shoot, amountPinsDown);
		}
		if (this.pinsDown[frame][shoot] == 10 || shoot == 1){
			if(frame!=9){
				this.updateFrameScore();
				shoot = 0;
				frame ++;
			}else if((shoot == 1 && this.pinsDown[frame][0] + amountPinsDown < 10) || shoot == 2){
				this.updateFrameScore();
				shoot=0;
				frame=0;
				game.setState(States.MENU);
				game.showStartUpMenu();
			} else {
				shoot++;
			}
			dynamics.resetPins();
		} else {
			shoot++;
		}
	}
	
	private void updateFrameScore() {
		if (this.strikes.contains(frame - 1) && this.strikes.contains(frame - 2)) {
			int score = this.getFrameTotal(frame - 3);
			score += this.pinsDown[frame-2][0] + this.pinsDown[frame-1][0];
			frameTotal[frame - 2] = score + this.pinsDown[frame][0];
			gameMenu.setFrameScore(frame-2, frameTotal[frame - 2]);
			this.strikes.remove((Object)new Integer(frame - 2));
		}
		if (this.spares.contains(frame - 1)) {
			int score = this.getFrameTotal(frame - 2);
			score += this.pinsDown[frame-1][0] + this.pinsDown[frame-1][1];
			frameTotal[frame - 1] = score + this.pinsDown[frame][0];
			gameMenu.setFrameScore(frame-1, frameTotal[frame - 1]);
			this.spares.remove((Object)new Integer(frame - 1));
		} else if (this.strikes.contains(frame - 1) && (!this.strikes.contains(frame) || (this.strikes.contains(frame) && frame == 9))) {
			int score = this.getFrameTotal(frame - 2);
			score += this.pinsDown[frame-1][0] + this.pinsDown[frame-1][1];
			frameTotal[frame - 1] = score + this.pinsDown[frame][0] + this.pinsDown[frame][1];
			gameMenu.setFrameScore(frame-1, frameTotal[frame - 1]);
			this.strikes.remove((Object)new Integer(frame - 1));
		}
		if (!this.spares.contains(frame) && !this.strikes.contains(frame)) {
			int score = this.getFrameTotal(frame - 1);
			frameTotal[frame] = score + this.pinsDown[frame][0] + this.pinsDown[frame][1];
			gameMenu.setFrameScore(frame, frameTotal[frame]);
		}
		if (this.frame == 9) {
			int score = this.getFrameTotal(frame - 1);
			gameMenu.setFrameScore(frame, score + this.pinsDown[frame][0]+ this.pinsDown[frame][1] + this.pinsDown[frame][2]);
		}
	}
	
	private int getFrameTotal(int frame) {
		if (frame < 0) {
			return 0;
		} else {
			return frameTotal[frame];
		}
	}
	
	public int getScore() {
		return 0;
	}
	
	
	public int getScore(int shoot, int frame) {
		return pinsDown[frame][shoot];
	}
	
	
}

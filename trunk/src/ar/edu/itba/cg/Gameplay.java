package ar.edu.itba.cg;

public class Gameplay {
	private int shoot = 0;
	private int[] pinsDown = new int[10];
	
	
	public Gameplay() {
		
	}
	
	
	public void nextShoot(int pinsDown) {
		if( resetPins() ) {
			
		}else{
			
		}
		shoot++;
		
	}
	
	
	public boolean resetPins() {
		if( shoot % 2 == 0 ) {
			return true;
		}else{
			return false;
		}
	}
	
	
	public boolean shootsLeft() {
		if( shoot < 19 ) {
			return true;
		}else{
			return false;
		}
	}
	
	
	public int getScore() {
		return 0;
	}
	
	
	public int getScore(int shoot) {
		return pinsDown[shoot];
	}
	
	
}

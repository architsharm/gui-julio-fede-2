package ar.edu.itba.cg.gjf2.app;

import java.lang.Thread.UncaughtExceptionHandler;

import com.jme.system.GameSettings;
import com.jmex.game.StandardGame;

public class BowlingGame extends StandardGame {


	public BowlingGame(String arg0, GameType arg1, GameSettings arg2, UncaughtExceptionHandler arg3) {
		super(arg0, arg1, arg2, arg3);
	}
	
	
	@Override
	protected void render(float arg0) {
          display.getRenderer().clearBuffers();
          super.render(arg0);
	}
}
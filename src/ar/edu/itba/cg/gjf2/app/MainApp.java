package ar.edu.itba.cg.gjf2.app;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ar.edu.itba.cg.gjf2.state.GameState;

import com.jme.system.DisplaySystem;
import com.jmex.game.StandardGame.GameType;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.TransitionGameState;

public class MainApp {

	public static void main(String [] args) throws InterruptedException, ExecutionException
	{
		Logger.getLogger("").setLevel(Level.WARNING);
		
		BowlingGame game = new BowlingGame("Bowling", GameType.GRAPHICAL, null, Thread
				.getDefaultUncaughtExceptionHandler());
		game.getSettings().setWidth(320);
		game.getSettings().setHeight(200);
		game.getSettings().setFramerate(70);
		game.getSettings().setVerticalSync(true);
		game.getSettings().setMusic(false);
		game.getSettings().setSFX(false);

		game.getSettings().setStencilBits(4);
		game.start();
		TransitionGameState trans = new TransitionGameState(5,null);
		GameStateManager.getInstance().attachChild(trans);
		
		trans.setActive(true);
		trans.setProgress(0, "Initializing Game ...");
		
		trans.increment("Initializing Game...");
		
        DisplaySystem disp = DisplaySystem.getDisplaySystem(); 
        disp.getRenderer().getCamera().setFrustumPerspective( 45.0f,
                    (float) disp.getWidth() / (float) disp.getHeight(), 1f, 100000 );
        disp.getRenderer().getCamera().update();

        trans.increment("Initializing physics ...");

		trans.increment("Initializing menu ...");
        
        trans.increment("Done !!");

        trans.setProgress(1.0f, "Finished Loading");
        trans.setActive(false);
        
        trans.increment("Initializing menu ...");
        GameState gameState = new GameState(game, "Main", true);
        
        trans.increment("Done !!");
        GameStateManager.getInstance().attachChild(gameState);
        
        GameStateManager.getInstance().activateChildNamed("Main");
	}
  
}
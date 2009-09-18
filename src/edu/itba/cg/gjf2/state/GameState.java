package ar.edu.itba.cg.gjf2.state;

import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.StandardGame;

public class GameState extends BasicGameState {

	protected InputHandler input;

	protected WireframeState wireState;

	protected LightState lightState;

	protected Camera firstCam;

	protected Text pauseObject;

	protected Node pauseNode;

	protected Text gameOverObject;

	protected Node gameOverNode;

	protected BasicPassManager pManager = new BasicPassManager();

	protected StandardGame game;

	public GameState(StandardGame game) {
		this(game, "GameState", true);
	}

	public GameState(StandardGame game, String name, boolean handleInput) {

		super(name);
		this.game = game;

		int height = DisplaySystem.getDisplaySystem().getHeight();
		int width = DisplaySystem.getDisplaySystem().getWidth();

		pauseObject = Text.createDefaultTextLabel("Pause", "PAUSE");
		pauseObject.setLocalScale(3.0f);
		pauseObject.setTextColor(ColorRGBA.green);

		Vector3f pausePosition = new Vector3f(height / 2.0f, width / 2.0f, 0f);
		pauseObject.setLocalTranslation(pausePosition);
		pauseObject.setTextureCombineMode(TextureState.REPLACE);

		pauseNode = new Node("PauseNode");
		pauseNode.attachChild(pauseObject);
		pauseNode.updateGeometricState(0.0f, true);
		pauseNode.updateRenderState();

		gameOverObject = Text.createDefaultTextLabel("Pause", "GAME OVER");
		gameOverObject.setLocalScale(4.0f);
		gameOverObject.setTextColor(ColorRGBA.red);

		Vector3f gameOverPosition = new Vector3f(height / 4.0f, width / 4.0f,
				0f);
		gameOverObject.setLocalTranslation(gameOverPosition);
		gameOverObject.setTextureCombineMode(TextureState.REPLACE);

		gameOverNode = new Node("GameOverNode");
		gameOverNode.attachChild(gameOverObject);
		gameOverNode.updateRenderState();

		// create the Hud
		init(handleInput);
	}

	private void init(boolean handleInput) {
		rootNode = new Node("RootNode");

		// Create a wirestate to toggle on and off. Starts disabled with default
		// width of 1 pixel.
		wireState = DisplaySystem.getDisplaySystem().getRenderer()
				.createWireframeState();
		wireState.setEnabled(false);
		rootNode.setRenderState(wireState);

		// Create ZBuffer for depth
		ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer()
				.createZBufferState();
		zbs.setEnabled(true);
		zbs.setFunction(ZBufferState.CF_LEQUAL);
		rootNode.setRenderState(zbs);

		// Initial InputHandler
		if (handleInput) {
			input = new FirstPersonHandler(DisplaySystem.getDisplaySystem()
					.getRenderer().getCamera(), 15.0f, 0.5f);
			initKeyBindings();
		}
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		float width = display.getWidth();
		float height = display.getHeight();
		Text pruebaText = Text.createDefaultTextLabel("pruebaText", "Prueba");
		pruebaText.setLocalScale(1.2f);
		pruebaText.setTextColor(ColorRGBA.lightGray);
		pruebaText.setLocalTranslation(width - 50 - pruebaText.getWidth(),
				height - 15 - pruebaText.getHeight(), 0);
		rootNode.attachChild(pruebaText);
	}

	public LightState getLightState() {
		return lightState;
	}

	private void initKeyBindings() {
		/** Assign key P to action "toggle_pause". */
		KeyBindingManager.getKeyBindingManager().set("toggle_pause",
				KeyInput.KEY_P);
		/** Assign key T to action "toggle_wire". */
		KeyBindingManager.getKeyBindingManager().set("toggle_wire",
				KeyInput.KEY_T);
		/** Assign key B to action "toggle_bounds". */
		KeyBindingManager.getKeyBindingManager().set("toggle_bounds",
				KeyInput.KEY_B);
		/** Assign key N to action "toggle_normals". */
		KeyBindingManager.getKeyBindingManager().set("toggle_normals",
				KeyInput.KEY_N);
		/** Assign key C to action "camera_out". */
		KeyBindingManager.getKeyBindingManager().set("toggle_camera",
				KeyInput.KEY_C);
		KeyBindingManager.getKeyBindingManager().set("screen_shot",
				KeyInput.KEY_F1);
		KeyBindingManager.getKeyBindingManager().set("parallel_projection",
				KeyInput.KEY_F2);
		KeyBindingManager.getKeyBindingManager().set("mem_report",
				KeyInput.KEY_R);
		KeyBindingManager.getKeyBindingManager().set("toggle_mouse",
				KeyInput.KEY_M);
		KeyBindingManager.getKeyBindingManager().set("toggle_physics",
				KeyInput.KEY_V);
		KeyBindingManager.getKeyBindingManager().set("show_menu",
				KeyInput.KEY_ESCAPE);
		KeyBindingManager.getKeyBindingManager().set("tilt_left",
				KeyInput.KEY_2);
		KeyBindingManager.getKeyBindingManager().set("tilt_right",
				KeyInput.KEY_9);
		KeyBindingManager.getKeyBindingManager().set("quit", KeyInput.KEY_Q);
	}

	public void update(float tpf) {

		input.update(tpf);
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("quit",
				false)) {

		}

		// Update the geometric state of the rootNode
		rootNode.updateGeometricState(tpf, true);
	}

	public void render(float tpf) {

		Renderer r = DisplaySystem.getDisplaySystem().getRenderer();

		r.draw(rootNode);
	}

	public void cleanup() {
	}

	public InputHandler getInput() {
		return input;
	}

}

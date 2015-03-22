package uk.mmi.gaming.net.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.mmi.gaming.ApplicationCommonAdapter;
import uk.mmi.gaming.GameListener;
import uk.mmi.gaming.net.client.lobby.LobbyScreen;
import uk.mmi.gaming.net.client.lobby.LobbyScreenController;
import uk.mmi.gaming.net.client.play.GamePlayController;
import uk.mmi.gaming.net.client.play.GamePlayScreen;
import uk.mmi.gaming.net.model.ModelRegistrar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.esotericsoftware.kryonet.Client;

public class NetPongRenderer extends ApplicationCommonAdapter {
	private OrthographicCamera camera;
	private ShapeRenderer renderer;
	private SpriteBatch spriteBatch;
	private LobbyScreen lobbyScreen;

	private boolean inLobby = false;
	private LobbyScreenController lobbyController;
	private Client client;
	private ExecutorService threadPool;
	private GamePlayController gamePlayController;
	private GamePlayScreen gamePlayScreen;

	public NetPongRenderer(GameListener gameListener) {
		super(gameListener);
	}

	@Override
	public void internalCreate() {
		Gdx.gl.glClearColor(0, 0.2f, 0.2f, 1);
		Gdx.input.setCursorCatched(false);

		// UI:
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);
		spriteBatch = new SpriteBatch();
		renderer = new ShapeRenderer();

		threadPool = Executors.newCachedThreadPool();

		client = createClientConnection();
		lobbyScreen = new LobbyScreen(spriteBatch, renderer);
		lobbyController = new LobbyScreenController(client, lobbyScreen, threadPool);

		gamePlayScreen = new GamePlayScreen(spriteBatch, renderer, screenWidth, screenHeight);
		gamePlayController = new GamePlayController(client, gamePlayScreen, threadPool);
	}

	private Client createClientConnection() {
		Client cl = new Client();
		cl.start();
		ModelRegistrar.registerDataObjects(cl.getKryo());
		return cl;
	}

	@Override
	public void internalRender() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		renderer.setProjectionMatrix(camera.combined);
		spriteBatch.setProjectionMatrix(camera.combined);

		if (inLobby) {
			lobbyScreen.renderFrame();
			if (lobbyController.receivedGameStartEvent()) {
				inLobby = false;
				disposeLobby();
			}
			return;
		}
		gamePlayScreen.renderFrame();
	}

	@Override
	public void dispose() {
		disposeLobby();
		disposeGamePlay();
		threadPool.shutdownNow();
		client.stop();
	}

	private void disposeLobby() {
		if (lobbyScreen != null) {
			lobbyScreen.dispose();
			lobbyScreen = null;
			lobbyController = null;
		}
	}

	private void disposeGamePlay() {
		if (gamePlayScreen != null) {
			gamePlayScreen.dispose();
			gamePlayScreen = null;
			gamePlayController = null;
		}
	}
}

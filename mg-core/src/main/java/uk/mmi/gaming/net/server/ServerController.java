package uk.mmi.gaming.net.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.mmi.gaming.net.model.Lobby;
import uk.mmi.gaming.net.model.ModelRegistrar;
import uk.mmi.gaming.net.model.NetPongStatics;
import uk.mmi.gaming.net.server.game.InputEventBuffer;
import uk.mmi.gaming.net.server.game.ServerGameEngine;
import uk.mmi.gaming.net.server.lobby.LobbyObservable;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class ServerController extends Listener {
	private Server server;
	private LobbyObservable lobby;
	private ServerCommListener listener;
	private Map<Integer, ServerClientHandler> clientHandles;
	private boolean running;
	private ExecutorService threadPool;
	private ServerGameEngine gameEngine;
	private int lastPlayerId = 0;
	private InputEventBuffer buffer;

	public ServerController(ServerCommListener lst) {
		listener = lst;
		clientHandles = new HashMap<>();
	}

	public void startup() {
		try {
			lobby = new LobbyObservable(new Lobby());

			server = new Server();
			server.start();
			server.bind(NetPongStatics.TCP_PORT, NetPongStatics.UDP_PORT);

			ModelRegistrar.registerDataObjects(server.getKryo());
			threadPool = Executors.newCachedThreadPool();
			server.addListener(new ThreadedListener(this, threadPool));

			buffer = new InputEventBuffer();
			gameEngine = new ServerGameEngine(lobby, clientHandles, listener, buffer);
			threadPool.submit(gameEngine);
			listener.serverStarted();
			running = true;
			listener.log("[MAIN] Server started.");
		} catch (IOException e) {
			listener.log("[MAIN] Error: " + e.getMessage());
		}
	}

	public void shutdown() {
		running = false;
		if (server != null) {
			server.stop();
		}
		gameEngine.shutdown();

		if (threadPool != null) {
			threadPool.shutdownNow();
		}
		listener.serverStopped();
		listener.log("[MAIN] Server stopped.");
	}

	@Override
	public void connected(Connection connection) {
		Integer connId = connection.getID();
		ServerClientHandler handle = clientHandles.get(connId);
		if (handle != null) {
			handle.unexpectedError("Connection ID already in use!");
			return;
		}
		handle = new ServerClientHandler(++lastPlayerId, connection, listener, lobby, buffer);
		clientHandles.put(connId, handle);
		threadPool.submit(handle);
	}

	@Override
	public void disconnected(Connection connection) {
		int connId = connection.getID();
		ServerClientHandler handle = clientHandles.get(connId);
		if (handle != null) {
			clientHandles.remove(connId);
			handle.stopProcessing();
		}
	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof KeepAlive) {
			return;
		}
		try {
			ServerClientHandler handle = clientHandles.get(connection.getID());
			handle.received(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isRunning() {
		return running;
	}
}

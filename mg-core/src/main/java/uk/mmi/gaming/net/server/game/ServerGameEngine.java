package uk.mmi.gaming.net.server.game;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;

import uk.mmi.gaming.net.model.PlayField;
import uk.mmi.gaming.net.model.Player;
import uk.mmi.gaming.net.model.ServerMessage;
import uk.mmi.gaming.net.model.ServerMessage.ServerMessageType;
import uk.mmi.gaming.net.server.ServerClientHandler;
import uk.mmi.gaming.net.server.ServerCommListener;
import uk.mmi.gaming.net.server.lobby.LobbyObservable;
import uk.mmi.gaming.net.server.lobby.LobbyObservable.LobbyEvent;

public class ServerGameEngine implements Callable<Object>, Observer {

	private final LobbyObservable lobbyObservable;
	private final Collection<ServerClientHandler> clientHandles;
	private final InputEventBuffer inputBuffer;
	private final ServerCommListener listener;

	private GeometryEngine geometryEngine;

	private boolean allPlayersReady = false;
	private boolean isRunning;
	private float deltaTime = 0;
	private long lastTime = System.nanoTime();

	public ServerGameEngine(LobbyObservable lobby, Map<Integer, ServerClientHandler> clientHandles, ServerCommListener listener, InputEventBuffer buffer) {
		this.lobbyObservable = lobby;
		this.listener = listener;
		this.inputBuffer = buffer;

		lobby.addObserver(this);
		this.clientHandles = clientHandles.values();
		isRunning = true;
	}

	@Override
	public Object call() throws Exception {
		while (!allPlayersReady) {
			Thread.sleep(50);
		}
		PlayField field = new PlayField();
		field.setPlayerCount(clientHandles.size());
		geometryEngine = new GeometryEngine(inputBuffer, field);

		Thread.sleep(500);
		sendCountdownToStart();
		sendGameStart(field);

		while (isRunning && !Thread.currentThread().isInterrupted()) {
			updateTime();
			geometryEngine.calculateNextState(deltaTime);

			sendBallPosition(field);
			sendPlayerPositionDiff(field);
			Thread.sleep(16);
		}
		lobbyObservable.deleteObserver(this);
		return null;
	}

	private void updateTime() {
		long time = System.nanoTime();
		deltaTime = (time - lastTime) / 1000000000.0f;
		lastTime = time;
	}

	public void shutdown() {
		isRunning = false;
	}

	@Override
	public void update(Observable o, Object event) {
		if (event.equals(LobbyEvent.PLAYER_UPDATED)) {
			List<Player> players = lobbyObservable.getPlayers();
			boolean playersReady = true;
			for (Player player : players) {
				playersReady &= player.isReady();
			}
			allPlayersReady = playersReady;
		}
	}

	private void sendCountdownToStart() throws InterruptedException {
		int countdown = 5;
		while (countdown >= 0) {
			listener.log("Game will start in %s seconds...", countdown);
			notifyClients(new ServerMessage(ServerMessageType.COUNTDOWN, countdown));
			countdown--;
			Thread.sleep(1000);
		}
	}

	private void sendGameStart(PlayField field) {
		notifyClients(new ServerMessage(ServerMessageType.GAME_START, field));
	}

	private void sendPlayerPositionDiff(PlayField field) {
	}

	private void sendBallPosition(PlayField field) {
	}

	private void notifyClients(ServerMessage message) {
		for (ServerClientHandler client : clientHandles) {
			client.update(null, message);
		}
	}

}

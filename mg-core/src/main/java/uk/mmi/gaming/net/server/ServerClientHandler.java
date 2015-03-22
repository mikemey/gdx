package uk.mmi.gaming.net.server;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import uk.mmi.gaming.net.model.InputEvent;
import uk.mmi.gaming.net.model.Player;
import uk.mmi.gaming.net.model.ServerMessage;
import uk.mmi.gaming.net.server.game.InputEventBuffer;
import uk.mmi.gaming.net.server.lobby.LobbyObservable;
import uk.mmi.gaming.net.server.lobby.LobbyObservable.LobbyEvent;

import com.esotericsoftware.kryonet.Connection;

public class ServerClientHandler implements Callable<Object>, Observer {

	private final int playerId;
	private final Connection connection;
	private final ServerCommListener listener;
	private final LobbyObservable lobbyObservable;
	private final InputEventBuffer inputBuffer;

	private BlockingQueue<Object> received = new LinkedBlockingQueue<>();
	private boolean connectionOpen;
	private Player player;
	private String message;

	public ServerClientHandler(int pid, Connection conn, ServerCommListener listener, LobbyObservable lobbyWrapper, InputEventBuffer buffer) {
		this.playerId = pid;
		this.connection = conn;
		this.listener = listener;
		this.lobbyObservable = lobbyWrapper;
		this.inputBuffer = buffer;
		lobbyWrapper.addObserver(this);
		connectionOpen = conn.isConnected();
	}

	public void received(Object object) throws InterruptedException {
		received.put(object);
	}

	public void unexpectedError(String msg) {
		message = msg;
		connection.close();
	}

	public void stopProcessing() {
		lobbyObservable.deleteObserver(this);
		connectionOpen = false;
	}

	@Override
	public void update(Observable o, Object event) {
		received.offer(event);
	}

	@Override
	public Object call() throws Exception {
		while (connectionOpen) {
			Object obj = received.poll(16, TimeUnit.MILLISECONDS);
			if (obj == null) continue;

			if (obj instanceof Player) {
				receivedPlayer((Player) obj);
			}
			if (obj instanceof LobbyEvent) {
				receivedLobbyDataEvent();
			}
			if (obj instanceof ServerMessage) {
				sendServerMessage((ServerMessage) obj);
			}
			if (obj instanceof InputEvent) {
				storeInputMessage((InputEvent) obj);
			}
		}
		if (message != null) {
			listener.log("[%s] %s", connection.getID(), message);
		}
		if (player != null) {
			lobbyObservable.removePlayer(player);
			listener.log("[%s] Connection to player <%s> closed.", connection.getID(), player.getName());
		}
		return null;
	}

	private void receivedPlayer(Player pl) {
		if (player != null) { // update on the player
			player.setName(pl.getName());
			player.setReady(pl.isReady());
			listener.log("[%s] Player updated: %s", connection.getID(), player);
			lobbyObservable.updatePlayer(player);
			return;
		}
		player = pl;
		listener.log("[%s] New player: <%s>", connection.getID(), player.getName());
		lobbyObservable.addPlayer(player);
	}

	private void receivedLobbyDataEvent() {
		connection.sendTCP(lobbyObservable.getLobby());
	}

	private void sendServerMessage(ServerMessage serverMessage) {
		connection.sendUDP(serverMessage);
	}

	private void storeInputMessage(InputEvent inputEvent) {
		inputBuffer.add(playerId, inputEvent);
	}
}

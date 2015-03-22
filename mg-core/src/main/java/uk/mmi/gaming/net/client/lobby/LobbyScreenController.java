package uk.mmi.gaming.net.client.lobby;

import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import uk.mmi.gaming.net.client.lobby.LobbyScreenModel.LobbyScreenEvent;
import uk.mmi.gaming.net.model.Lobby;
import uk.mmi.gaming.net.model.NetPongStatics;
import uk.mmi.gaming.net.model.Player;
import uk.mmi.gaming.net.model.ServerMessage;
import uk.mmi.gaming.net.model.ServerMessage.ServerMessageType;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class LobbyScreenController extends Listener implements Observer {
	// external resources - do not shutdown/dispose:
	final Client client;
	private final ExecutorService threadPool;

	Player localPlayer;
	LobbyScreenModel model;
	private boolean gameStartReceived = false;

	public LobbyScreenController(Client client, LobbyScreen lobbyScreen, ExecutorService threadPool) {
		this.client = client;
		this.threadPool = threadPool;

		client.addListener(this);
		model = new LobbyScreenModel();
		model.addObserver(this);
		model.showWaitingScreen(true);
		lobbyScreen.setModel(model);
	}

	@Override
	public void update(Observable o, Object event) {
		LobbyScreenEvent dataEvent = (LobbyScreenEvent) event;
		switch (dataEvent) {
		case PLAYER_READY:
			if (localPlayer != null) {
				submitCallable(new PlayerReady());
			}
			break;
		case NAME_SET:
			submitCallable(new InitialCommunication());
			break;
		}
	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof Lobby) {
			Lobby l = (Lobby) object;
			model.setPlayers(l.getPlayers());
		}
		if (object instanceof ServerMessage) {
			ServerMessage servMessage = (ServerMessage) object;
			if (servMessage.getType() == ServerMessageType.COUNTDOWN) {
				// work of this class is done:
				gameStartReceived = true;
				client.removeListener(this);
				model.deleteObserver(this);
			}
		}
	}

	void submitCallable(Callable<?> callable) {
		threadPool.submit(callable);
	}

	void setErrorMessage(String msg) {
		model.setMessage(null);
		model.setErrorMessage(msg);
	}

	void setMessage(String msg) {
		model.setMessage(msg);
		model.setErrorMessage(null);
	}

	void removeWaitingScreen() {
		model.showWaitingScreen(false);
	}

	public boolean receivedGameStartEvent() {
		return gameStartReceived;
	}

	class InitialCommunication implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			setMessage("Searching NetPong server...");
			InetAddress serverHost = client.discoverHost(NetPongStatics.UDP_PORT, 1500);
			if (serverHost == null) {
				setErrorMessage("No NetPong server found! Retry in 2 seconds...");
				Thread.sleep(2000);
				submitCallable(new InitialCommunication());
				return null;
			}
			setMessage("Server found: " + serverHost);
			Thread.sleep(500);
			client.connect(1000, serverHost, NetPongStatics.TCP_PORT, NetPongStatics.UDP_PORT);
			localPlayer = new Player();
			localPlayer.setName(model.getPlayerName());
			client.sendTCP(localPlayer);
			setMessage(null);
			removeWaitingScreen();
			return null;
		}
	}

	class PlayerReady implements Callable<Object> {

		@Override
		public Object call() {
			localPlayer.setReady(true);
			client.sendTCP(localPlayer);
			return null;
		}
	}
}

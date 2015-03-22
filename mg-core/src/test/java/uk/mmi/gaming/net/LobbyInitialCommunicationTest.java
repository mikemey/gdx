package uk.mmi.gaming.net;

import java.util.List;
import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.mmi.gaming.net.client.lobby.LobbyScreenController;
import uk.mmi.gaming.net.client.lobby.LobbyScreen;
import uk.mmi.gaming.net.client.lobby.LobbyScreenModel;
import uk.mmi.gaming.net.model.ModelRegistrar;
import uk.mmi.gaming.net.model.Player;
import uk.mmi.gaming.net.server.ServerCommListener;
import uk.mmi.gaming.net.server.ServerController;

import com.esotericsoftware.kryonet.Client;

public class LobbyInitialCommunicationTest {

	private ServerController serverComm;
	private LobbyScreenModel model;
	private Client client;

	@Before
	public void setup() {
		serverComm = new ServerController(new ConsoleLogger());
		serverComm.startup();

		LobbyScreenInspector lobbyScreenInspector = new LobbyScreenInspector();
		client = createClient();
		new LobbyScreenController(client, lobbyScreenInspector, Executors.newCachedThreadPool());
		model = lobbyScreenInspector.getModel();
	}

	private Client createClient() {
		Client cl = new Client();
		cl.start();
		ModelRegistrar.registerDataObjects(cl.getKryo());
		return cl;
	}

	@Test
	public void testInitiallyNoServer() throws InterruptedException {
		serverComm.shutdown();
		Assert.assertTrue(model.shouldShowWaitingScreen());
		String playerName = "Bladibla";
		model.setPlayerName(playerName);

		Thread.sleep(6000);
		Assert.assertEquals("No NetPong server found! Retry in 2 seconds...", model.getErrorMessage());
		Assert.assertTrue(model.shouldShowWaitingScreen());

		serverComm.startup();
		// wait long enough for server startup and reconnect:
		Thread.sleep(10000);
		assertOwnPlayerInLobby(playerName);
	}

	@Test
	public void testExchangeLobby() throws InterruptedException {
		Assert.assertTrue(model.shouldShowWaitingScreen());
		String playerName = "Bladibla";
		model.setPlayerName(playerName);

		// wait for communication threads to do their things:
		Thread.sleep(10000);
		assertOwnPlayerInLobby(playerName);
	}

	@Test
	public void testExchangeLobby2Players() throws InterruptedException {
		Assert.assertTrue(model.shouldShowWaitingScreen());
		String player1Name = "Bladibla";
		model.setPlayerName(player1Name);

		Thread.sleep(6000);
		Client client2 = null;
		try {
			client2 = createClient();
			LobbyScreenInspector lobbyScreenInspector = new LobbyScreenInspector();
			new LobbyScreenController(createClient(), lobbyScreenInspector, Executors.newCachedThreadPool());
			LobbyScreenModel model2 = lobbyScreenInspector.getModel();
			String player2Name = "bbbabab";
			model2.setPlayerName(player2Name);
			Thread.sleep(12000);

			List<Player> players = model2.getPlayers();
			Assert.assertEquals(2, players.size());
			boolean p1Found = false;
			boolean p2Found = false;
			for (Player player : players) {
				if (player1Name.equals(player.getName())) {
					p1Found = true;
				}
				if (player2Name.equals(player.getName())) {
					p2Found = true;
				}
				Assert.assertFalse(player.isReady());
			}
			Assert.assertTrue(p1Found);
			Assert.assertTrue(p2Found);
		} finally {
			if (client2 != null) {
				client2.stop();
			}
		}
	}

	@Test
	public void testExchangeLobby2PlayersOneReady() throws InterruptedException {
		Assert.assertTrue(model.shouldShowWaitingScreen());
		String player1Name = "Bladibla";
		model.setPlayerName(player1Name);

		Thread.sleep(6000);
		Client client2 = null;
		try {
			client2 = createClient();
			LobbyScreenInspector lobbyScreenInspector = new LobbyScreenInspector();
			new LobbyScreenController(createClient(), lobbyScreenInspector, Executors.newCachedThreadPool());
			LobbyScreenModel model2 = lobbyScreenInspector.getModel();
			String player2Name = "bbbabab";
			model2.setPlayerName(player2Name);
			Thread.sleep(8000);

			List<Player> players = model2.getPlayers();
			Assert.assertEquals(2, players.size());
			boolean p1Found = false;
			boolean p2Found = false;
			for (Player player : players) {
				if (player1Name.equals(player.getName())) {
					p1Found = true;
				}
				if (player2Name.equals(player.getName())) {
					p2Found = true;
				}
				Assert.assertFalse(player.isReady());
			}
			Assert.assertTrue(p1Found);
			Assert.assertTrue(p2Found);

			model.setPlayerReady();
			// waiting for propagation of new status:
			Thread.sleep(7000);
			players = model2.getPlayers();
			Assert.assertTrue(players.get(0).isReady());
			Assert.assertFalse(players.get(1).isReady());

		} finally {
			if (client2 != null) {
				client2.stop();
			}
		}
	}

	private void assertOwnPlayerInLobby(String playerName) {
		List<Player> playersInLobby = model.getPlayers();
		Assert.assertEquals(1, playersInLobby.size());
		Assert.assertEquals(playerName, playersInLobby.get(0).getName());
		Assert.assertFalse(playersInLobby.get(0).isReady());
		Assert.assertNull(model.getMessage());
		Assert.assertNull(model.getErrorMessage());
		Assert.assertFalse(model.shouldShowWaitingScreen());
	}

	@After
	public void tearDown() {
		serverComm.shutdown();
		client.stop();
	}

	class LobbyScreenInspector extends LobbyScreen {
		private LobbyScreenModel inspectedModel;

		@Override
		public void setModel(LobbyScreenModel model) {
			this.inspectedModel = model;
		}

		public LobbyScreenModel getModel() {
			return inspectedModel;
		}
	}

	class ConsoleLogger implements ServerCommListener {

		@Override
		public void serverStarted() {
			System.out.println("server started");
		}

		@Override
		public void serverStopped() {
			System.out.println("server stopped");
		}

		@Override
		public void log(String string, Object... params) {
			System.out.println(String.format(string, params));
		}

	}
}

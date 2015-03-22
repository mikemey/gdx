package uk.mmi.gaming.net.client.play;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import uk.mmi.gaming.net.model.NetPongStatics;
import uk.mmi.gaming.net.model.Player;
import uk.mmi.gaming.net.model.ServerMessage;
import uk.mmi.gaming.net.model.ServerMessage.ServerMessageType;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class GamePlayController extends Listener {

	final Client client;
	private final ExecutorService threadPool;
	private final PlayScreenModel model;

	public GamePlayController(Client client, GamePlayScreen playScreen, ExecutorService threadPool) {
		this.client = client;
		this.threadPool = threadPool;

		client.addListener(this);
		model = new PlayScreenModel();
		playScreen.setModel(model);

		threadPool.submit(new FakePlayerReadySender());
	}

	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof ServerMessage) {
			ServerMessage servMessage = (ServerMessage) object;
			if (servMessage.getType() == ServerMessageType.COUNTDOWN) {
				model.setCountdownMessage((Integer) servMessage.getData());
			}
			if (servMessage.getType() == ServerMessageType.GAME_START) {
				model.setCountdownMessage(null);
			}
			if (servMessage.getType() == ServerMessageType.INITAL_MODEL) {
				setInitialModel(servMessage.getData());
			}
		}
	}

	private void setInitialModel(Object data) {
	}

	private void submitCallable(Callable<?> callable) {
		threadPool.submit(callable);
	}

	class EventSender implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			return null;
		}
	}

	class FakePlayerReadySender implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			Thread.sleep(1000);

			client.connect(500, "127.0.0.1", NetPongStatics.TCP_PORT, NetPongStatics.UDP_PORT);
			Player player = new Player();
			player.setName("bladibla");
			player.setReady(true);
			client.sendTCP(player);
			return null;
		}
	}
}

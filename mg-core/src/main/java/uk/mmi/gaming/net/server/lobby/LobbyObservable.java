package uk.mmi.gaming.net.server.lobby;

import java.util.List;
import java.util.Observable;

import uk.mmi.gaming.net.model.Lobby;
import uk.mmi.gaming.net.model.Player;

public class LobbyObservable extends Observable {
	private final Lobby lobby;

	public enum LobbyEvent {
		PLAYER_UPDATED, PLAYER_REMOVED;
	}

	public LobbyObservable(Lobby lobby) {
		this.lobby = lobby;
	}

	public Lobby getLobby() {
		return lobby;
	}

	public List<Player> getPlayers() {
		return lobby.getPlayers();
	}

	public void addPlayer(Player player) {
		lobby.addPlayer(player);
		event(LobbyEvent.PLAYER_UPDATED);
	}

	public void updatePlayer(Player player) {
		if (lobby.getPlayers().contains(player)) {
			event(LobbyEvent.PLAYER_UPDATED);
		}
	}

	public void removePlayer(Player player) {
		lobby.removePlayer(player);
		event(LobbyEvent.PLAYER_REMOVED);

	}

	private void event(LobbyEvent ev) {
		this.setChanged();
		notifyObservers(ev);
	}
}

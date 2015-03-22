package uk.mmi.gaming.net.model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
	private List<Player> players = new ArrayList<>();

	public List<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public void removePlayer(Player player) {
		players.remove(player);
	}

	public int playerCount() {
		return players.size();
	}

	public int gamesStarted() {
		return 0;
	}
}
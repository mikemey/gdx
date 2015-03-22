package uk.mmi.gaming.net.client.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import uk.mmi.gaming.net.model.Player;

public class LobbyScreenModel extends Observable {
	enum LobbyScreenEvent {
		PLAYER_READY, NAME_SET;
	}

	private boolean playerReady;
	private List<Player> players = new ArrayList<>();
	private String name;
	private String errorMessage;
	private String message;
	private boolean showWaitScreen;
	public boolean hasPlayerName() {
		return name != null;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public boolean isPlayerReady() {
		return playerReady;
	}

	public void setPlayerReady() {
		playerReady = true;
		event(LobbyScreenEvent.PLAYER_READY);
	}

	public void setPlayerName(String name) {
		this.name = name;
		event(LobbyScreenEvent.NAME_SET);
	}

	public String getPlayerName() {
		return name;
	}

	public boolean shouldShowWaitingScreen() {
		return showWaitScreen;
	}

	public void showWaitingScreen(boolean value) {
		showWaitScreen = value;
	}

	public boolean hasErrorMessage() {
		return errorMessage != null;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean hasMessage() {
		return message != null;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		message = msg;
	}

	private void event(LobbyScreenEvent ev) {
		this.setChanged();
		notifyObservers(ev);
	}
}

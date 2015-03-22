package uk.mmi.gaming.net.client.play;

import java.util.ArrayList;
import java.util.List;

import uk.mmi.gaming.net.model.NetPongStatics;

public class PlayScreenModel {
	public enum GameEvent {
		MOVE_RIGHT, MOVE_LEFT
	}

	private float xCoordinate = 100;
	private float[] allPlayerXCoordinates = new float[] { 300, 1000, 1300, 2100, 2700, 3300 };
	private int playerCount = 6;
	private int playerIndex = 2;
	private List<GameEvent> events = new ArrayList<>(100);
	private boolean isInitialised = false;
	private Integer countdown = 5;

	public boolean isInitialised() {
		return isInitialised;
	}

	public void addGameEvent(GameEvent event) {
		events.add(event);
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public float getPlayerXOffset() {
		return xCoordinate;
	}

	public void setPlayerXOffset(float newX) {
		xCoordinate = newX;
		allPlayerXCoordinates[playerIndex] = newX + playerIndex * NetPongStatics.PLAYFIELD_WIDTH;
	}

	public float[] getAllPlayersXCoordinates() {
		return allPlayerXCoordinates;
	}

	public boolean showCountdown() {
		return countdown != null;
	}

	public void setCountdownMessage(Integer num) {
		this.countdown = num;
	}

	public int getCountdown() {
		return countdown;
	}
}

package uk.mmi.gaming.net.model;

public class PlayField {

	private float ballX;
	private float ballY;
	private int playerCount;
	private float[] playerX;

	public float getBallX() {
		return ballX;
	}

	public void setBallX(float ballX) {
		this.ballX = ballX;
	}

	public float getBallY() {
		return ballY;
	}

	public void setBallY(float ballY) {
		this.ballY = ballY;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public float[] getPlayerX() {
		return playerX;
	}

	public void setPlayerX(float[] playerX) {
		this.playerX = playerX;
	}
}
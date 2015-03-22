package uk.mmi.gaming.singlepong;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class ActiveEngine {
	private static final int PLAYER_SPEED_PPS = 350;
	private static final int BALL_SPEED_PPS = 350;

	private String message = null;
	private final Rectangle playfield;
	private final Rectangle[] rectangles;

	private final int maxPlayerX;
	private final int maxBallY;
	private final int maxBallX;

	private final Rectangle ballCollisionRect;
	private final float ballQuarterWidth;
	private final float playerHalfWidth;

	//	private float ballAngle = MathUtils.random(30, 330);
	private float ballAngle = 90;
	private final float maxAngleDistortion = 35.0f;
	private double controlAngle;

	public ActiveEngine(Rectangle playfield, Rectangle[] rectangles) {
		this.playfield = playfield;
		this.rectangles = rectangles;

		maxPlayerX = (int) (playfield.x + playfield.width - rectangles[0].getWidth());
		maxBallX = (int) (playfield.x + playfield.width - rectangles[1].getWidth());
		maxBallY = (int) (playfield.y + playfield.height - rectangles[1].getHeight());

		ballQuarterWidth = rectangles[1].width / 4.0f;
		playerHalfWidth = rectangles[0].width / 2.0f;
		ballCollisionRect = new Rectangle(rectangles[1].x + ballQuarterWidth, rectangles[1].y, rectangles[1].width / 2.0f, rectangles[1].height);
	}

	public String getMessage() {
		return message;
	}

	public void calculateNextFrame() {
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			rectangles[0].x = Math.max(playfield.x, rectangles[0].x - PLAYER_SPEED_PPS * Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			rectangles[0].x = Math.min(maxPlayerX, rectangles[0].x + PLAYER_SPEED_PPS * Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			rectangles[2].x = Math.max(playfield.x, rectangles[2].x - PLAYER_SPEED_PPS * Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			rectangles[2].x = Math.min(maxPlayerX, rectangles[2].x + PLAYER_SPEED_PPS * Gdx.graphics.getDeltaTime());
		}
		updateBallCollision();
		moveBall();
	}

	private void moveBall() {
		rectangles[1].x += MathUtils.cosDeg(ballAngle) * BALL_SPEED_PPS * Gdx.graphics.getDeltaTime();
		rectangles[1].y += MathUtils.sinDeg(ballAngle) * BALL_SPEED_PPS * Gdx.graphics.getDeltaTime();
		ballCollisionRect.setX(rectangles[1].x + ballQuarterWidth);
		ballCollisionRect.setY(rectangles[1].y);

		controlAngle = Math.abs(ballAngle % 360);
		if ((rectangles[1].x <= playfield.x && controlAngle > 90 && controlAngle < 270)//
				|| (rectangles[1].x >= maxBallX && (controlAngle < 90 || controlAngle > 270))) {
			ballAngle = 540 - ballAngle;
		}
		if (rectangles[1].y > maxBallY) {
			message = "Player 1 wins!!";
			//			message = "You win!!";
		} else if (rectangles[1].y < playfield.y) {
			message = "Player 2 wins!";
			//			message = "You lost!";
		}
	}

	private void updateBallCollision() {
		checkBallCollision(rectangles[0], 1, rectangles[0].y + rectangles[0].height);
		checkBallCollision(rectangles[2], -1, rectangles[2].y - rectangles[1].height);
	}

	private void checkBallCollision(Rectangle player, int sign, float correctionY) {
		if (ballCollisionRect.overlaps(player)) {
			float dist = (ballCollisionRect.x + ballQuarterWidth) - (player.x + playerHalfWidth);
			ballAngle = 360 - ballAngle - (maxAngleDistortion * dist / playerHalfWidth) * sign;
			rectangles[1].y = correctionY;
			ballCollisionRect.y = correctionY;
		}
	}
}

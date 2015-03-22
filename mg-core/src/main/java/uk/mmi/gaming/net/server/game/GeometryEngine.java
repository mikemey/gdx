package uk.mmi.gaming.net.server.game;

import uk.mmi.gaming.net.model.NetPongStatics;
import uk.mmi.gaming.net.model.PlayField;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class GeometryEngine {
	private static final int PLAYER_SPEED_PPS = 350;
	private static final int BALL_SPEED_PPS = 350;

	private final InputEventBuffer inputBuffer;
	private final PlayField field;
	private final Rectangle playfield;
	private final Rectangle[] players;
	private final Circle ball;

	private final int maxPlayerX;
	private final int maxBallY;
	private final int maxBallX;
	private final int minBallY;
	private final int minBallX;

	//	private final float ballQuarterWidth;
	//	private final float playerHalfWidth;

	private final float maxAngleDistortion = 35.0f;
	private float ballAngle;
	private double controlAngle;

	public GeometryEngine(InputEventBuffer inputBuffer, PlayField field) {
		this.inputBuffer = inputBuffer;
		this.field = field;
		this.players = new Rectangle[field.getPlayerCount()];
		// TODO: initialise X offsets of each player
		this.playfield = new Rectangle(0, 0, NetPongStatics.PLAYFIELD_WIDTH * field.getPlayerCount(), NetPongStatics.PLAYFIELD_HEIGHT);

		float ballX = (int) (Math.random() * playfield.width);
		ballX = Math.min(playfield.width - NetPongStatics.BALL_RADIUS, ballX);
		ballX = Math.max(NetPongStatics.BALL_RADIUS, ballX);
		ball = new Circle(ballX, NetPongStatics.PLAYFIELD_HEIGHT / 2, NetPongStatics.BALL_RADIUS);

		maxPlayerX = NetPongStatics.PLAYFIELD_WIDTH - NetPongStatics.BAT_WIDTH;
		maxBallX = (int) (playfield.width - NetPongStatics.BALL_RADIUS);
		maxBallY = (int) (playfield.height - NetPongStatics.BALL_RADIUS);
		minBallX = NetPongStatics.BALL_RADIUS;
		minBallY = NetPongStatics.BALL_RADIUS;
		ballAngle = MathUtils.random(30, 330);

		// TODO what type of collision should we implement?
		//		ballQuarterWidth = rectangles[1].width / 4.0f;
		//		playerHalfWidth = rectangles[0].width / 2.0f;
		//		ballCollisionRect = new Rectangle(rectangles[1].x + ballQuarterWidth, rectangles[1].y, rectangles[1].width / 2.0f, rectangles[1].height);
	}

	public void calculateNextState(float delta) {
		//		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
		//			rectangles[0].x = Math.max(playfield.x, rectangles[0].x - PLAYER_SPEED_PPS * Gdx.graphics.getDeltaTime());
		//		}
		//		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
		//			rectangles[0].x = Math.min(maxPlayerX, rectangles[0].x + PLAYER_SPEED_PPS * Gdx.graphics.getDeltaTime());
		//		}
		//		if (Gdx.input.isKeyPressed(Keys.A)) {
		//			rectangles[2].x = Math.max(playfield.x, rectangles[2].x - PLAYER_SPEED_PPS * Gdx.graphics.getDeltaTime());
		//		}
		//		if (Gdx.input.isKeyPressed(Keys.D)) {
		//			rectangles[2].x = Math.min(maxPlayerX, rectangles[2].x + PLAYER_SPEED_PPS * Gdx.graphics.getDeltaTime());
		//		}
		updateBallCollision();
		moveBall();
	}

	private void moveBall() {
		//		rectangles[1].x += MathUtils.cosDeg(ballAngle) * BALL_SPEED_PPS * Gdx.graphics.getDeltaTime();
		//		rectangles[1].y += MathUtils.sinDeg(ballAngle) * BALL_SPEED_PPS * Gdx.graphics.getDeltaTime();
		//		ballCollisionRect.setX(rectangles[1].x + ballQuarterWidth);
		//		ballCollisionRect.setY(rectangles[1].y);
		//
		//		controlAngle = Math.abs(ballAngle % 360);
		//		if ((rectangles[1].x <= playfield.x && controlAngle > 90 && controlAngle < 270)//
		//				|| (rectangles[1].x >= maxBallX && (controlAngle < 90 || controlAngle > 270))) {
		//			ballAngle = 540 - ballAngle;
		//		}
		//		if (rectangles[1].y > maxBallY) {
		//			
		//		} else if (rectangles[1].y < playfield.y) {
		//			
		//		}
	}

	private void updateBallCollision() {
		//		checkBallCollision(rectangles[0], 1, rectangles[0].y + rectangles[0].height);
		//		checkBallCollision(rectangles[2], -1, rectangles[2].y - rectangles[1].height);
	}

	private void checkBallCollision(Rectangle player, int sign, float correctionY) {
		//		if (ballCollisionRect.overlaps(player)) {
		//			float dist = (ballCollisionRect.x + ballQuarterWidth) - (player.x + playerHalfWidth);
		//			ballAngle = 360 - ballAngle - (maxAngleDistortion * dist / playerHalfWidth) * sign;
		//			rectangles[1].y = correctionY;
		//			ballCollisionRect.y = correctionY;
		//		}
	}
}

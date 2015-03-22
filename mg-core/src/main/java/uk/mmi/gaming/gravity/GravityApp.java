package uk.mmi.gaming.gravity;

import uk.mmi.gaming.ApplicationCommonAdapter;
import uk.mmi.gaming.GameListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GravityApp extends ApplicationCommonAdapter {

	public GravityApp(GameListener gameListener) {
		super(gameListener);
	}

	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private ShapeRenderer renderer;
	private BitmapFont bmFont;

	private Texture ballTex;
	boolean waitForStart = true;

	private final Vector2 gravityVector = new Vector2(0, -9.81f);

	// Pendulum:
	private Vector2 fixPoint;
	private double moveRadius;
	// x, y -> center of circle
	private Circle ballCenter;
	private Vector2 nextCenter;
	private Vector2 pendularMoveVector;
	private double deltaTime;

	// free fall ball:
	private Circle freeFallBall;
	private Vector2 freeFallMoveVector;
	private float deltaCounter;
	private Array<Vector2> freeTrace;

	@Override
	public void internalCreate() {
		Gdx.gl.glClearColor(0, 0.2f, 0.2f, 1);
		Gdx.input.setCursorCatched(false);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);
		renderer = new ShapeRenderer();

		spriteBatch = new SpriteBatch();
		bmFont = new BitmapFont();

		ballTex = new Texture(Gdx.files.internal("ball.png"));
		Gdx.input.setInputProcessor(new GravityInputProcessor());

		resetAllValues();
	}

	void resetAllValues() {
		resetPendulumValues();
		resetFreeFallBallValues();
	}

	private void resetPendulumValues() {
		fixPoint = new Vector2(screenWidth / 2.0f, screenHeight * (3 / 4.0f));

		ballCenter = new Circle(fixPoint.x + 200, fixPoint.y - 10, ballTex.getWidth() / 2.0f);
		moveRadius = fixPoint.dst(ballCenter.x, ballCenter.y);

		nextCenter = new Vector2(0, 0);
		pendularMoveVector = new Vector2(0, 0);
	}

	private void resetFreeFallBallValues() {
		freeFallBall = new Circle(10, fixPoint.y, ballCenter.radius);
		freeFallMoveVector = new Vector2(3.0f, 7.0f);
		freeTrace = new Array<>(300);
	}

	@Override
	public void internalRender() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();

		Gdx.gl.glLineWidth(2);
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.FilledCircle);
		renderer.setColor(0.9f, 0.2f, 0.2f, 1);
		renderer.filledCircle(fixPoint.x, fixPoint.y, 5);
		renderer.end();

		Gdx.gl.glLineWidth(1);
		renderer.begin(ShapeType.Line);
		renderer.setColor(0.196f, 1.0f, 0.759f, 1);
		renderer.line(fixPoint.x, fixPoint.y, ballCenter.x, ballCenter.y);
		renderer.end();
		renderer.begin(ShapeType.Line);
		renderer.line(ballCenter.x, ballCenter.y, ballCenter.x + pendularMoveVector.x, ballCenter.y + pendularMoveVector.y);
		renderer.end();

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(ballTex, ballCenter.x - ballCenter.radius, ballCenter.y - ballCenter.radius);
		spriteBatch.draw(ballTex, freeFallBall.x - freeFallBall.radius, freeFallBall.y - freeFallBall.radius);
		spriteBatch.end();

		for (Vector2 freeTr : freeTrace) {
			renderer.begin(ShapeType.Point);
			renderer.setColor(0.696f, 1.0f, 0.959f, 1);
			renderer.point(freeTr.x, freeTr.y, 0);
			renderer.end();
		}

		if (waitForStart) {
			printMessage("Press any Key to start...");
			return;
		}
		deltaTime = Gdx.graphics.getDeltaTime();

		moveBallPosition();
		moveFreeBall();

		deltaCounter += deltaTime;
		if (deltaCounter > 0.005) {
			deltaCounter = 0;
			freeTrace.add(new Vector2(freeFallBall.x, freeFallBall.y));
		}
	}

	public void moveFreeBall() {
		if (freeFallBall.y <= 15) {
			resetFreeFallBallValues();
			return;
		}
		freeFallMoveVector.x += gravityVector.x * deltaTime;
		freeFallMoveVector.y += gravityVector.y * deltaTime;
		freeFallBall.x += freeFallMoveVector.x;
		freeFallBall.y += freeFallMoveVector.y;
	}

	public void moveBallPosition() {
		pendularMoveVector.x += gravityVector.x * deltaTime;
		pendularMoveVector.y += gravityVector.y * deltaTime;

		float dx = ballCenter.x + pendularMoveVector.x - fixPoint.x;
		float dy = ballCenter.y + pendularMoveVector.y - fixPoint.y;

		double gamma = Math.atan2(dy, dx);

		nextCenter.x = (float) (fixPoint.x + moveRadius * Math.cos(gamma));
		nextCenter.y = (float) (fixPoint.y + moveRadius * Math.sin(gamma));

		pendularMoveVector.x = nextCenter.x - ballCenter.x;
		pendularMoveVector.y = nextCenter.y - ballCenter.y;

		ballCenter.x = nextCenter.x;
		ballCenter.y = nextCenter.y;
	}

	private void printMessage(String msg) {
		spriteBatch.begin();
		bmFont.draw(spriteBatch, msg, screenWidth / 2 - (msg.length() * 3.0f), screenHeight / 2 - 10);
		spriteBatch.end();
	}

	@Override
	public void dispose() {
		ballTex.dispose();
		spriteBatch.dispose();
		bmFont.dispose();
		renderer.dispose();
	}

	class GravityInputProcessor extends InputAdapter {

		@Override
		public boolean keyUp(int keycode) {
			if (waitForStart) {
				waitForStart = false;
				return true;
			}
			if (keycode == Keys.ENTER) {
				moveBallPosition();
			}
			if (keycode == Keys.BACKSPACE) {
				resetAllValues();
			}
			return true;
		}
	}
}

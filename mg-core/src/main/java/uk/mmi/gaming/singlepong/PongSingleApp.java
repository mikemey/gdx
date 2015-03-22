package uk.mmi.gaming.singlepong;

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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PongSingleApp extends ApplicationCommonAdapter {

	public PongSingleApp(GameListener gameListener) {
		super(gameListener);
	}

	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private ShapeRenderer renderer;
	private BitmapFont bmFont;
	// index:
	// 0 - player
	// 1 - ball
	// 2 - opponent
	private final Texture[] textures = new Texture[3];
	private final Rectangle[] rectangles = new Rectangle[3];

	private float playerHalfWidth;
	private float ballHalfWidth;

	private Rectangle playfield;
	float rotationSpeedDPS = 25f;

	private ActiveEngine activeEngine;
	private String message;
	boolean rotate = false;
	boolean waitForStart = true;

	@Override
	public void internalCreate() {
		Gdx.gl.glClearColor(0, 0.2f, 0.2f, 1);
		Gdx.input.setCursorCatched(true);

		camera = new OrthographicCamera();
		playfield = new Rectangle(screenWidth / 2 - 300, screenHeight / 2 - 400, 600, 800);

		camera.setToOrtho(false, screenWidth, screenHeight);

		textures[0] = new Texture(Gdx.files.internal("bat.png"));
		textures[1] = new Texture(Gdx.files.internal("ball.png"));
		textures[2] = new Texture(Gdx.files.internal("bat_op.png"));
		playerHalfWidth = textures[0].getWidth() / 2;
		rectangles[0] = new Rectangle(playfield.x + (playfield.width / 2) - playerHalfWidth, playfield.y + 10, textures[0].getWidth(), textures[0].getHeight());

		ballHalfWidth = textures[1].getWidth() / 2;
		rectangles[1] = new Rectangle(playfield.x + (playfield.width / 2) - ballHalfWidth, playfield.y + (playfield.height / 2), textures[1].getWidth(), textures[1].getHeight());
		rectangles[2] = new Rectangle(playfield.x + (playfield.width / 2) - playerHalfWidth, playfield.y + playfield.height - 10 - textures[2].getHeight(), textures[2].getWidth(), textures[2].getHeight());

		spriteBatch = new SpriteBatch();
		bmFont = new BitmapFont();
		//		bmFont.setScale(1.f);

		renderer = new ShapeRenderer();
		activeEngine = new ActiveEngine(playfield, rectangles);

		Gdx.input.setInputProcessor(new GameControlInputProcessor());
	}

	@Override
	public void internalRender() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();

		Gdx.gl.glLineWidth(3);
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Rectangle);
		renderer.setColor(0.9f, 0.2f, 0.2f, 1);
		renderer.rect(playfield.x, playfield.y, playfield.width, playfield.height);
		renderer.end();

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(textures[0], rectangles[0].x, rectangles[0].y);
		spriteBatch.draw(textures[1], rectangles[1].x, rectangles[1].y);
		spriteBatch.draw(textures[2], rectangles[2].x, rectangles[2].y);
		spriteBatch.end();

		if (waitForStart) {
			printMessage("Press any Key to start...");
			return;
		}
		if (message == null) {
			if (rotate) {
				camera.rotate(rotationSpeedDPS * Gdx.graphics.getDeltaTime());
			}
			cheaterOpponent();
			activeEngine.calculateNextFrame();
			message = activeEngine.getMessage();
		} else {
			restRotation();
			printMessage(message);
		}
	}

	private void printMessage(String msg) {
		spriteBatch.begin();
		bmFont.draw(spriteBatch, msg, screenWidth / 2 - (msg.length() * 3.0f), screenHeight / 2 - 10);
		spriteBatch.end();
	}

	private void restRotation() {
		if (rotate) {
			double degree = Math.abs(MathUtils.radiansToDegrees * Math.atan2(camera.up.y, camera.up.x) - 90);
			if (degree > 2) {
				camera.rotate(809 * Gdx.graphics.getDeltaTime());
			} else {
				camera.up.y = 1.0f;
				camera.up.x = 0.0f;
				rotate = false;
			}
		}
	}

	private void cheaterOpponent() {
		rectangles[2].x = rectangles[1].x + ballHalfWidth - playerHalfWidth;
		rectangles[2].x = Math.max(playfield.x, rectangles[2].x);
		rectangles[2].x = Math.min(playfield.x + playfield.width - rectangles[2].width, rectangles[2].x);
	}

	@Override
	public void dispose() {
		textures[0].dispose();
		textures[1].dispose();
		textures[2].dispose();
		spriteBatch.dispose();
		bmFont.dispose();
		renderer.dispose();
	}

	class GameControlInputProcessor extends InputAdapter {

		@Override
		public boolean keyUp(int keycode) {
			if (waitForStart) {
				waitForStart = false;
				return true;
			}
			if (keycode == Keys.R) {
				rotate = !rotate;
			}

			return true;
		}

		@Override
		public boolean keyTyped(char ch) {
			if (ch == '+') {
				rotationSpeedDPS += 2.0f;
			}
			if (ch == '-') {
				rotationSpeedDPS -= 2.0f;
			}
			return true;
		}
	}
}

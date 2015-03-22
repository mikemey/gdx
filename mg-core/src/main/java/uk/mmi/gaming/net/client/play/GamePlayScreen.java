package uk.mmi.gaming.net.client.play;

import uk.mmi.gaming.net.model.NetPongStatics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GamePlayScreen {
	private static final float MINIATURE_SCALE = 0.2f;
	private static final int PLAYER_Y_OFFSET = 10;
	private static final int countdownWidth = 102;
	private static final int countdownHeight = 132;

	// external resources - do not dispose!
	private final SpriteBatch spriteBatch;
	private final ShapeRenderer renderer;
	private final int screenWidth;
	private final int screenHeight;
	private PlayScreenModel model;

	// local resources:
	private final Array<Texture> textures = new Array<Texture>(false, 20);
	private final TextureRegion[] countdownNumbers = new TextureRegion[6];

	// coordinates:
	private Vector2 fieldBottomLeft;
	private Vector2 fieldBottomRight;
	private Vector2 fieldTopLeft;
	private Vector2 fieldTopRight;
	private Vector2 miniFieldBottomLeft;
	private Vector2 miniFieldBottomRight;
	private Vector2 miniFieldTopLeft;
	private Vector2 miniFieldTopRight;
	private boolean miniatureCoordinatesReady;
	private float maxPlayerX;
	private float playerY;
	private float playerOppositeY;
	private float miniPlayerY;
	private float miniPlayerOppositeY;
	private float tempX;
	private float miniPlayerWidth;
	private float miniPlayerHeight;
	private Vector2 countdownLocation;

	public GamePlayScreen(SpriteBatch spriteBatch, ShapeRenderer renderer, int screenWidth, int screenHeight) {
		this.spriteBatch = spriteBatch;
		this.renderer = renderer;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		createResources();
		createCoordinates();
	}

	private void createResources() {
		textures.add(new Texture(Gdx.files.internal("bat.png")));
		textures.add(new Texture(Gdx.files.internal("ball.png")));
		textures.add(new Texture(Gdx.files.internal("bat_op.png")));

		Texture goTex = new Texture(Gdx.files.internal("go.png"));
		textures.add(goTex);
		countdownNumbers[0] = new TextureRegion(goTex);
		Texture countdownTex = new Texture(Gdx.files.internal("countdown.png"));
		textures.add(countdownTex);
		for (int i = 0; i < 5; i++) {
			countdownNumbers[i + 1] = new TextureRegion(countdownTex, i * countdownWidth, 0, countdownWidth, countdownHeight);
		}
	}

	private void createCoordinates() {
		fieldTopLeft = new Vector2(screenWidth / 2 - NetPongStatics.PLAYFIELD_WIDTH / 2,// 
				screenHeight - 50);
		fieldTopRight = new Vector2(fieldTopLeft.x + NetPongStatics.PLAYFIELD_WIDTH, fieldTopLeft.y);
		fieldBottomLeft = new Vector2(fieldTopLeft.x, fieldTopLeft.y - NetPongStatics.PLAYFIELD_HEIGHT);
		fieldBottomRight = new Vector2(fieldTopRight.x, fieldBottomLeft.y);

		miniFieldTopLeft = new Vector2(-1, fieldBottomLeft.y - 70);
		miniFieldTopRight = new Vector2(100, miniFieldTopLeft.y);
		miniFieldBottomLeft = new Vector2(-1, miniFieldTopLeft.y - NetPongStatics.PLAYFIELD_HEIGHT * MINIATURE_SCALE);
		miniFieldBottomRight = new Vector2(200, miniFieldBottomLeft.y);

		maxPlayerX = NetPongStatics.PLAYFIELD_WIDTH - textures.get(2).getWidth();
		playerY = fieldBottomLeft.y + PLAYER_Y_OFFSET;
		playerOppositeY = fieldTopLeft.y - PLAYER_Y_OFFSET - textures.get(2).getHeight();

		countdownLocation = new Vector2(fieldBottomLeft.x + NetPongStatics.PLAYFIELD_WIDTH / 2 - countdownWidth / 2, //
				fieldBottomLeft.y + NetPongStatics.PLAYFIELD_HEIGHT / 2 - countdownHeight / 2);
	}

	private void calculateMiniatureFieldCoordinates() {
		float fieldWidth = model.getPlayerCount() * NetPongStatics.PLAYFIELD_WIDTH * MINIATURE_SCALE;
		miniFieldTopLeft.x = (int) (fieldTopLeft.x + NetPongStatics.PLAYFIELD_WIDTH / 2 - fieldWidth / 2);
		miniFieldBottomLeft.x = miniFieldTopLeft.x;

		miniFieldTopRight.x = miniFieldTopLeft.x + fieldWidth;
		miniFieldBottomRight.x = miniFieldTopRight.x;

		miniPlayerWidth = textures.get(2).getWidth() * MINIATURE_SCALE;
		miniPlayerHeight = textures.get(2).getHeight() * MINIATURE_SCALE;
		miniPlayerY = miniFieldBottomLeft.y + PLAYER_Y_OFFSET * MINIATURE_SCALE;
		miniPlayerOppositeY = miniFieldTopLeft.y - PLAYER_Y_OFFSET * MINIATURE_SCALE - miniPlayerHeight;
	}

	public void setModel(PlayScreenModel model) {
		this.model = model;
	}

	public void renderFrame() {
		captureInput();

		renderFixedContent();
		if (model.isInitialised()) {
			spriteBatch.begin();
			renderLocalPlayer();
			spriteBatch.end();
		}

		if (model.showCountdown()) {
			renderGreyOverlay();
			spriteBatch.begin();
			showCountdown(model.getCountdown());
			spriteBatch.end();
		}
	}

	private void renderFixedContent() {
		// red top/bottom lines:
		Gdx.gl.glLineWidth(3);
		renderer.begin(ShapeType.Line);
		renderer.setColor(0.4f, 0.2f, 0.2f, 1);
		renderer.line(0, fieldBottomLeft.y, screenWidth, fieldBottomRight.y);
		renderer.line(0, fieldTopLeft.y, screenWidth, fieldTopRight.y);
		renderer.setColor(0.9f, 0.2f, 0.2f, 1);
		renderer.line(fieldBottomLeft.x, fieldBottomLeft.y, fieldBottomRight.x, fieldBottomRight.y);
		renderer.line(fieldTopLeft.x, fieldTopLeft.y, fieldTopRight.x, fieldTopRight.y);
		renderer.end();

		// grey side lines:
		Gdx.gl.glLineWidth(1);
		renderer.begin(ShapeType.Line);
		renderer.setColor(0.4f, 0.4f, 0.4f, 1);
		renderer.line(fieldBottomLeft.x, fieldBottomLeft.y, fieldTopLeft.x, fieldTopLeft.y);
		renderer.line(fieldBottomRight.x, fieldBottomRight.y, fieldTopRight.x, fieldTopRight.y);
		// line width 1:
		if (model.isInitialised()) {
			if (!miniatureCoordinatesReady) {
				calculateMiniatureFieldCoordinates();
				miniatureCoordinatesReady = true;
			}
			renderer.setColor(0.9f, 0.2f, 0.2f, 1);
			renderer.line(miniFieldBottomLeft.x, miniFieldBottomLeft.y, miniFieldBottomRight.x, miniFieldBottomRight.y);
			renderer.line(miniFieldTopLeft.x, miniFieldTopLeft.y, miniFieldTopRight.x, miniFieldTopRight.y);
			renderer.setColor(0.4f, 0.4f, 0.4f, 1);
			// grey side lines:
			renderer.setColor(0.4f, 0.4f, 0.4f, 1);
			for (int i = 0; i <= model.getPlayerCount(); i++) {
				int xoffset = (int) (i * NetPongStatics.PLAYFIELD_WIDTH * MINIATURE_SCALE);
				renderer.line(miniFieldTopLeft.x + xoffset, miniFieldTopLeft.y, miniFieldBottomLeft.x + xoffset, miniFieldBottomLeft.y);
			}
		}
		renderer.end();
	}

	private void renderLocalPlayer() {
		tempX = fieldBottomLeft.x + model.getPlayerXOffset();
		spriteBatch.draw(textures.get(0), tempX, playerY);
		spriteBatch.draw(textures.get(2), tempX, playerOppositeY);

		for (float miniX : model.getAllPlayersXCoordinates()) {
			tempX = miniFieldBottomLeft.x + miniX * MINIATURE_SCALE;
			spriteBatch.draw(textures.get(0), tempX, miniPlayerY, miniPlayerWidth, miniPlayerHeight);
			spriteBatch.draw(textures.get(2), tempX, miniPlayerOppositeY, miniPlayerWidth, miniPlayerHeight);
		}
	}

	private void captureInput() {
		//		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
		//			model.addGameEvent(GameEvent.MOVE_LEFT);
		//		}
		//		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
		//			model.addGameEvent(GameEvent.MOVE_RIGHT);
		//		}
	}

	private void showCountdown(int countdown) {
		spriteBatch.draw(countdownNumbers[countdown], countdownLocation.x, countdownLocation.y);
	}

	private void renderGreyOverlay() {
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		renderer.begin(ShapeType.FilledRectangle);
		renderer.setColor(0.2f, 0.2f, 0.2f, 0.7f);
		renderer.filledRect(0, 0, screenWidth, screenHeight);
		renderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);
	}

	public void dispose() {
		for (Texture tex : textures) {
			if (tex != null) {
				tex.dispose();
			}
		}
	}
}

package uk.mmi.gaming.net.client.lobby;

import org.apache.commons.lang.StringUtils;

import uk.mmi.gaming.net.model.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class LobbyScreen {

	// external resources - do not dispose!
	private final SpriteBatch spriteBatch;
	private final ShapeRenderer renderer;

	// local resources:
	private Texture bgPlayerList;
	private Texture buttonTexture;
	private TextureRegion readyButton;
	private TextureRegion readyButtonHover;
	private TextureRegion readyButtonSelected;
	private TextureRegion exitButton;
	private TextureRegion exitButtonHover;
	private Texture readyLight;
	private Texture waitingLight;
	private BitmapFont playerFont;
	private BitmapFont messageFont;
	private Texture waitingWheel;
	private Animation waitingWheelAnime;
	private TextureRegion currentWheelFrame;

	// coordinates:
	private int screenWidth;
	private int screenHeight;
	private Vector2 bgPlayerListPoint;
	private Vector2 playerListPoint;
	private int playerLightOffset;
	private int playerNameOffset;
	private final int buttonWidth = 263;
	private final int buttonHeight = 128;
	private Rectangle readyButtonArea;
	private Rectangle exitButtonArea;
	private Vector2 errorMessagePoint;

	// control:
	boolean showNameDialog = true;
	LobbyScreenModel model;
	private float stateTime;
	private Vector2 wheelPoint;
	private boolean greyOverlayVisible;

	// testing purposes only!
	public LobbyScreen() {
		spriteBatch = null;
		renderer = null;
	}

	public LobbyScreen(SpriteBatch spriteBatch, ShapeRenderer renderer) {
		this.spriteBatch = spriteBatch;
		this.renderer = renderer;
		playerFont = new BitmapFont(Gdx.files.internal("fonts/players.fnt"), Gdx.files.internal("fonts/players.png"), false);
		playerFont.setColor(0.443f, 0.333f, 0.290f, 1.0f);
		messageFont = new BitmapFont(Gdx.files.internal("fonts/messages.fnt"), Gdx.files.internal("fonts/messages.png"), false);
		bgPlayerList = new Texture(Gdx.files.internal("playerlist_bg.png"));

		buttonTexture = new Texture(Gdx.files.internal("buttons.png"));
		readyButton = new TextureRegion(buttonTexture, 0, 0, buttonWidth, buttonHeight);
		readyButtonHover = new TextureRegion(buttonTexture, 0, buttonHeight, buttonWidth, buttonHeight);
		readyButtonSelected = new TextureRegion(buttonTexture, 0, 2 * buttonHeight, buttonWidth, buttonHeight);
		exitButton = new TextureRegion(buttonTexture, buttonWidth, 0, buttonWidth, buttonHeight);
		exitButtonHover = new TextureRegion(buttonTexture, buttonWidth, buttonHeight, buttonWidth, buttonHeight);

		readyLight = new Texture(Gdx.files.internal("led_green.png"));
		waitingLight = new Texture(Gdx.files.internal("led_red.png"));

		waitingWheel = new Texture(Gdx.files.internal("waiting_wheel.png"));
		TextureRegion[] wwRegion = TextureRegion.split(waitingWheel, waitingWheel.getWidth() / 8, waitingWheel.getHeight())[0];
		waitingWheelAnime = new Animation(0.125f, wwRegion);

		calculateScreenCoordinates();
	}

	public void setModel(LobbyScreenModel model) {
		this.model = model;
	}

	private void calculateScreenCoordinates() {
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();

		bgPlayerListPoint = new Vector2(50, screenHeight - 20 - bgPlayerList.getHeight());
		playerListPoint = new Vector2(80, bgPlayerListPoint.y + 375);
		playerLightOffset = -readyLight.getHeight() / 2;
		playerNameOffset = -31; // trial and error value...

		// buttonX, buttonY -> bottom left corner of first (top) button:
		float buttonX = bgPlayerListPoint.x + bgPlayerList.getWidth() + 10;
		float buttonY = bgPlayerListPoint.y + bgPlayerList.getHeight() - buttonHeight - 32;
		readyButtonArea = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
		exitButtonArea = new Rectangle(readyButtonArea.x, readyButtonArea.y - buttonHeight - 10, buttonWidth, buttonHeight);

		errorMessagePoint = new Vector2(screenWidth / 2, screenHeight / 2 - 30);
		wheelPoint = new Vector2(screenWidth / 2 - waitingWheel.getWidth() / 16, screenHeight / 2);
	}

	public void renderFrame() {
		greyOverlayVisible = false;
		spriteBatch.begin();
		renderFixedContent();
		spriteBatch.end();

		if (!model.hasPlayerName()) {
			showNameInput();
			return;
		}

		if (model.shouldShowWaitingScreen()) {
			showWaitScreen();
		}

		if (model.hasMessage()) {
			showMessage(model.getMessage(), false);
			return;
		}

		if (model.hasErrorMessage()) {
			showMessage(model.getErrorMessage(), true);
			return;
		}

		spriteBatch.begin();
		renderPlayerList();
		renderButtonSelection();
		spriteBatch.end();
	}

	private void renderFixedContent() {
		spriteBatch.draw(bgPlayerList, bgPlayerListPoint.x, bgPlayerListPoint.y);

		if (model.isPlayerReady()) {
			spriteBatch.draw(readyButtonSelected, readyButtonArea.x, readyButtonArea.y);
		} else {
			spriteBatch.draw(readyButton, readyButtonArea.x, readyButtonArea.y);
		}
		spriteBatch.draw(exitButton, exitButtonArea.x, exitButtonArea.y);
		messageFont.setColor(0.8f, 0.8f, 0.5f, 1.0f);
		messageFont.drawMultiLine(spriteBatch, "Doesn't work\nPress ESC instead!", exitButtonArea.x, exitButtonArea.y + 100);
	}

	private void renderPlayerList() {
		int index = 0;
		for (Player player : model.getPlayers()) {
			float lineY = playerListPoint.y - index++ * 40;
			if (player.isReady()) {
				spriteBatch.draw(readyLight, playerListPoint.x, lineY + playerLightOffset);
			} else {
				spriteBatch.draw(waitingLight, playerListPoint.x, lineY + playerLightOffset);
			}
			playerFont.draw(spriteBatch, player.getName(), playerListPoint.x + 45, lineY - playerNameOffset);
		}
	}

	private void renderButtonSelection() {
		int mouseX = Gdx.input.getX();
		int mouseY = screenHeight - Gdx.input.getY();
		if (!model.isPlayerReady()) {
			if (readyButtonArea.contains(mouseX, mouseY)) {
				if (Gdx.input.justTouched()) {
					model.setPlayerReady();
				}
				spriteBatch.draw(readyButtonHover, readyButtonArea.x, readyButtonArea.y);
			}
		}
		if (exitButtonArea.contains(mouseX, mouseY)) {
			spriteBatch.draw(exitButtonHover, exitButtonArea.x, exitButtonArea.y);
		}
	}

	private void showNameInput() {
		renderGreyOverlay();
		if (showNameDialog) {
			MyTextInputListener listener = new MyTextInputListener();
			Gdx.input.getTextInput(listener, "What is your screen name?", "");
			showNameDialog = false;
		}
	}

	private void showMessage(String errorMessage, boolean error) {
		renderGreyOverlay();
		if (error) {
			messageFont.setColor(1.0f, 0.1f, 0.1f, 1.0f);
		} else {
			messageFont.setColor(0.8f, 0.8f, 0.1f, 1.0f);
		}
		TextBounds bounds = playerFont.getBounds(errorMessage);
		spriteBatch.begin();
		// (bounds.width  / 2.5) -> trial & error value
		messageFont.draw(spriteBatch, errorMessage, errorMessagePoint.x - (int) (bounds.width / 2.5), errorMessagePoint.y);
		spriteBatch.end();
	}

	private void showWaitScreen() {
		renderGreyOverlay();
		stateTime += Gdx.graphics.getDeltaTime();
		currentWheelFrame = waitingWheelAnime.getKeyFrame(stateTime, true);
		spriteBatch.begin();
		spriteBatch.draw(currentWheelFrame, wheelPoint.x, wheelPoint.y);
		spriteBatch.end();
	}

	private void renderGreyOverlay() {
		if (greyOverlayVisible) {
			return;
		}
		greyOverlayVisible = true;
		Gdx.gl.glEnable(GL10.GL_BLEND);
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		renderer.begin(ShapeType.FilledRectangle);
		renderer.setColor(0.2f, 0.2f, 0.2f, 0.7f);
		renderer.filledRect(0, 0, screenWidth, screenHeight);
		renderer.end();
		Gdx.gl.glDisable(GL10.GL_BLEND);
	}

	public void dispose() {
		playerFont.dispose();
		messageFont.dispose();
		bgPlayerList.dispose();
		buttonTexture.dispose();
		readyLight.dispose();
		waitingLight.dispose();

		waitingWheel.dispose();
	}

	class MyTextInputListener implements TextInputListener {
		@Override
		public void input(String text) {
			String cleanText = text.trim();
			if (StringUtils.isEmpty(cleanText)) {
				canceled();
				return;
			}
			model.setPlayerName(cleanText);
		}

		@Override
		public void canceled() {
			showNameDialog = true;
		}
	}
}

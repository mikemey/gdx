package uk.mmi.gaming;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public abstract class ApplicationCommonAdapter extends ApplicationAdapter {
	private final GameListener gameListener;
	protected int screenWidth;
	protected int screenHeight;

	public ApplicationCommonAdapter(GameListener gameListener) {
		this.gameListener = gameListener;
	}

	@Override
	public final void create() {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		internalCreate();
	}

	protected abstract void internalCreate();

	@Override
	public final void render() {
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			gameListener.hintExit();
		}
		internalRender();
	}

	protected abstract void internalRender();

}

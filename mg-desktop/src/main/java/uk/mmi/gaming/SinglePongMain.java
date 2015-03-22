package uk.mmi.gaming;

import uk.mmi.gaming.singlepong.PongSingleApp;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class SinglePongMain implements GameListener {
	public static void main(String[] args) {
		new SinglePongMain().startApp();
	}

	private LwjglApplication app;

	private void startApp() {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Net pong";
		cfg.useGL20 = true;
		cfg.width = 1000;
		cfg.height = 1000;

		app = new LwjglApplication(new PongSingleApp(this), cfg);
	}

	@Override
	public void hintExit() {
		if (app != null) {
			app.exit();
		}
	}
}

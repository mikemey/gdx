package uk.mmi.gaming;

import uk.mmi.gaming.gravity.GravityApp;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GravityMain implements GameListener {
	public static void main(String[] args) {
		new GravityMain().startApp();
	}

	private LwjglApplication app;

	private void startApp() {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Gravity";
		cfg.useGL20 = true;
		cfg.width = 600;
		cfg.height = 600;
		//		cfg.fullscreen = true;

		app = new LwjglApplication(new GravityApp(this), cfg);
	}

	@Override
	public void hintExit() {
		if (app != null) {
			app.exit();
		}
	}
}

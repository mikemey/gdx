package uk.mmi.gaming;

import uk.mmi.gaming.bucketdrops.CatchDrop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class BucketDropsMain implements GameListener {
	public static void main(String[] args) {
		new BucketDropsMain().startApp();
	}

	private LwjglApplication app;

	private void startApp() {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Drop";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 600;

		app = new LwjglApplication(new CatchDrop(this), cfg);
	}

	@Override
	public void hintExit() {
		if (app != null) {
			app.exit();
		}
	}
}

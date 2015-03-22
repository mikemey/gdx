package uk.mmi.gaming;

import uk.mmi.gaming.net.client.NetPongRenderer;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class NetPongMain implements GameListener {
	public static void main(String[] args) {
		new NetPongMain().startApp();
	}

	private LwjglApplication app;

	private void startApp() {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Net pong";
		cfg.useGL20 = true;
		cfg.width = 1000;
		cfg.height = 900;
		app = new LwjglApplication(new NetPongRenderer(this), cfg);
	}

	@Override
	public void hintExit() {
		if (app != null) {
			app.exit();
		}
	}
}

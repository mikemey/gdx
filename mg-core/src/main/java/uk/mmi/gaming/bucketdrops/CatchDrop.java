package uk.mmi.gaming.bucketdrops;

import java.util.Iterator;

import uk.mmi.gaming.ApplicationCommonAdapter;
import uk.mmi.gaming.GameListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class CatchDrop extends ApplicationCommonAdapter {

	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;

	private Texture bucketTex;
	private Rectangle bucket;
	private int maxX;

	private Texture dropTex;
	private int dropWidth;
	private int dropHeight;
	private int dropStartHeight;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private long nextDropTime = 1000000000;
	private ShapeRenderer renderer;

	public CatchDrop(GameListener gameListener) {
		super(gameListener);
	}

	@Override
	public void internalCreate() {
		dropTex = new Texture(Gdx.files.internal("droplet.png"));
		dropWidth = dropTex.getWidth();
		dropHeight = dropTex.getHeight();
		dropStartHeight = screenHeight;

		bucketTex = new Texture(Gdx.files.internal("bucket.png"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);
		spriteBatch = new SpriteBatch();

		bucket = new Rectangle();
		int bucketWidth = bucketTex.getWidth();
		bucket.x = screenWidth / 2 - bucketWidth / 2;
		bucket.y = 20;
		bucket.width = bucketWidth;
		bucket.height = bucketTex.getHeight();
		maxX = screenWidth - bucketWidth;

		raindrops = new Array<>();
		spawnRaindrop();
		renderer = new ShapeRenderer();
		Gdx.gl.glClearColor(0, 0.3f, 0.2f, 1);
	}

	float boxDepth = -100.0f;

	@Override
	public void internalRender() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(bucketTex, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			spriteBatch.draw(dropTex, raindrop.x, raindrop.y);
		}
		spriteBatch.end();

		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Box);
		renderer.setColor(1.0f, 0.4f, 0.56f, 1.0f);
		renderer.identity();
		renderer.translate(50, 50, 5);
		renderer.rotate(1.0f, 1.0f, 0, 34.0f);
		renderer.box(100, 100, -20, 20, 20, 10);
		boxDepth += 0.1f;
		renderer.end();

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			bucket.x = Math.max(0, bucket.x - 200 * Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			bucket.x = Math.min(maxX, bucket.x + 200 * Gdx.graphics.getDeltaTime());
		}

		if (TimeUtils.nanoTime() - lastDropTime > nextDropTime) {
			spawnRaindrop();
		}
		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 48 < 0 || raindrop.overlaps(bucket)) {
				iter.remove();
			}
		}
	}

	@Override
	public void dispose() {
		System.out.println("CatchDrop.dispose()");
		System.out.println(boxDepth);
		dropTex.dispose();
		bucketTex.dispose();
		spriteBatch.disableBlending();
		renderer.dispose();
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, maxX);
		raindrop.y = dropStartHeight;
		raindrop.width = dropWidth;
		raindrop.height = dropHeight;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
}

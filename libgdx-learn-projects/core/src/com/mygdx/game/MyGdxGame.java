package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;


public class MyGdxGame extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	
	//This is me
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private Rectangle bucket;
	
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	
	private int health;
	private int score;
	private String gameOverText;
	
	BitmapFont font;
	
	@Override
	public void create() {
		// load drop bucket classes
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		
		// load sound effects
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		
		// start playback of BGM
		rainMusic.setLooping(true);
		rainMusic.play();
		
		// camera stuff
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		font = new BitmapFont();
		
		// sprite loading
		batch = new SpriteBatch();
		
		// bucket rectangle stuff
		bucket = new Rectangle();
		bucket.x = 368;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;
		
		// spawn a raindrop and init array
		raindrops = new Array<Rectangle>();
		spawnRainDrop();
		
		// more game logic init.
		score = 0;
		health = 3;
		gameOverText = "";
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// update camera matrixes
		camera.update();
		
		// render bucket, raindrops
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		font.draw(batch, "Score: " + score, 20, 100);
		font.draw(batch, "Health: " + health, 20, 120);
		font.draw(batch, gameOverText, 20, 140);
		batch.end();
		
		// move bucket with various input
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			bucket.x += 200 * Gdx.graphics.getDeltaTime();
		}
		
		// bound bucket
		if (bucket.x < 0) {
			bucket.x = 0;
		}
		if (bucket.x > 800 - 64) {
			bucket.x = 800 - 64;
		}
		
		// spawn a raindrop if the elapsed time is great enough.
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
			spawnRainDrop();
		}
		
		// handle creation and destroy of raindrop
		Iterator<Rectangle> iter = raindrops.iterator();
		while(iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();

			// raindrop collision
			if (raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
				score++;
			}

			if(raindrop.y + 64 < 0) {
				iter.remove();
				health--;
			}
		}
		
		// game over?
		if (health < 1) {
			gameOverText = "You lost this game."; 
		}
		
 	}
	
	private void spawnRainDrop () {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
	
	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}

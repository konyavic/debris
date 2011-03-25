package com.victor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Body;

public class Debris extends InputAdapter implements ApplicationListener {

	SpriteBatch spriteBatch;
	BitmapFont font;

	DebrisWorld world;

	Vector2 lastPosition = null;
	Vector2 moveVector = new Vector2();

	Matrix4 transform = new Matrix4();
	int translation_x = 0;

	int width;
	int height;	

	boolean jumping = false;
	
	float countDown = 60000;	// 60 secs
	
	public static final float DEBRIS_INTERVAL = 1000f;	// msecs per fall 
	float timeFall = DEBRIS_INTERVAL;
	
	public static float MOVE_INTERVAL = 100f;	// apply force for msecs
	float timeMove = 0;
	
	int countDebris = 30;
	boolean stopped = false;
	boolean isWin = false;
	boolean isLose = false;

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		// texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
		spriteBatch = new SpriteBatch();

		world = new DebrisWorld();
		world.reset();
		Gdx.input.setInputProcessor(this);

		translation_x = (int) (DebrisWorld.WIDTH / 2f);
		transform.setToTranslation(translation_x, 0f, 0f);
	}

	@Override
	public void render() {
		//
		// Model
		//	
		if (!isWin && !isLose) {
			float delta = Gdx.graphics.getDeltaTime() * 1000; // in msecs
			countDown -= delta;
			world.tick((long) (delta * 6), 6);

			if (timeMove >= 0) {
				timeMove -= delta;
				world.movePlayer(moveVector);
			}

			// debris fall
			timeFall += delta;
			if (timeFall >= DEBRIS_INTERVAL && countDebris > 0) {
				timeFall = 0f;
				world.createDebrisRandom();
				countDebris -= 1;
			}

			if (countDebris == 0 && !stopped) {
				stopped = true;
				world.createDoor();
			}
			
			if (world.isWin()) {
				isWin = true;
			}
			
			if (countDown <= 0) {
				isLose = true;
			}
		}
		 
		//
		// View
		//
		
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		// center to player
		Vector2 position = world.getPlayerPosition();
		float translation = (float) -position.x + width / 2f;
		transform.setToTranslation(translation, 0f, 0f);
		spriteBatch.setTransformMatrix(transform);

		// render
		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);
		Vector2 v = world.getPlayerVelocity();
		font.draw(spriteBatch, "v = " + v.len(), (int) -translation + 50,
				(int) height - 50);
		font.draw(spriteBatch, "fps = " + Gdx.graphics.getFramesPerSecond(),
				(int) -translation + 50, (int) height - 70);
		font.draw(spriteBatch, "time = " + countDown,
				(int) -translation + 50, (int) height - 90);
		if (isWin) {
			font.draw(spriteBatch, "YOU WIN!",
					(int) -translation + 50, (int) height - 110);
		} else if (isLose) {
			font.draw(spriteBatch, "YOU LOST!",
					(int) -translation + 50, (int) height - 110);
		}
		/*
		 * for (Body d : world.getDebrisList()) { Vector2 pos = d.getPosition();
		 * font.draw(spriteBatch, "D", (int) pos.x, (int) pos.y); }
		 */
		spriteBatch.end();

		// render debug shape
		world.renderDebug();
		
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		if (!world.isPlayerJumpable()) {
			jumping = false;

		} else {
			jumping = true;
			if (lastPosition != null) {
				lastPosition.x = (float) x;
				lastPosition.y = (float) y;
			} else {
				lastPosition = new Vector2((float) x, (float) y);
			}

		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (jumping) {			
			moveVector.x = (x - lastPosition.x); 
			moveVector.y = -(y - lastPosition.y);
			 
			moveVector.x *= 250f;
			moveVector.y *= 250f;
			
			timeMove = MOVE_INTERVAL;
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

}

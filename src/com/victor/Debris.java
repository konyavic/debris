package com.victor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	float tps;	// tick per second

	boolean jumping = false;
	public static final float DEBRIS_SPEED = 1;	// secs per fall 
	float debrisCount = DEBRIS_SPEED;
	
	Lock lock = new ReentrantLock();

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

		new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						System.err.println("Caught interrupt in tick-thread");
					}
					lock.lock();
					world.tick();
					lock.unlock();
				}
			}
		}.start();
	}

	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		// sync problem
		lock.lock();
		debrisCount += Gdx.graphics.getDeltaTime();
		if (debrisCount >= DEBRIS_SPEED) {
			debrisCount = 0f;
			world.createDebrisRandom();
		}
		lock.unlock();
		
		
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
		font.draw(spriteBatch, "fps =: " + Gdx.graphics.getFramesPerSecond(),
				(int) -translation + 50, (int) height - 70);
		/*
		 * for (Body d : world.getDebrisList()) { Vector2 pos = d.getPosition();
		 * font.draw(spriteBatch, "D", (int) pos.x, (int) pos.y); }
		 */
		spriteBatch.end();

		// render debug shape
		lock.lock();
		world.renderDebug();
		lock.unlock();
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
			 
			moveVector.x *= 1000f;
			moveVector.y *= 1000f;
			
			world.movePlayer(moveVector);
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

}

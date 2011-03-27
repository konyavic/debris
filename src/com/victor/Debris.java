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
import com.badlogic.gdx.physics.box2d.BodyDef;

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
	
	public static float MOVE_INTERVAL = 200f;	// apply force for msecs
	float timeMove = 0;
	
	int countDebris = 30;
	int countFreeze = -5;
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

		translation_x = (int) (DebrisParam.STAGE_WIDTH / 2f);
		transform.setToTranslation(translation_x, 0f, 0f);
	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime() * 1000; // in milliseconds
		float sleep = 1000f/DebrisParam.FPS - delta;
		
		//
		// Model
		//
		if (!isWin && !isLose) {
			
			countDown -= delta;
			world.tick(delta);

			/*
			if (timeMove >= 0) {
				timeMove -= delta; // wrong?
				world.movePlayer(moveVector);
			}
			*/

			// debris fall
			timeFall += delta;
			if (timeFall >= DEBRIS_INTERVAL && countDebris > 0) {
				timeFall = 0f;
				world.createDebrisRandom();
				countDebris -= 1;
				if (countFreeze >= 0) {
					world.getDebrisList().get(countFreeze).setType(BodyDef.BodyType.StaticBody);
				}
				countFreeze += 1;
			}

			if (countDebris == 0 && !stopped) {
				stopped = true;
				world.createDoor();
			}
			
			if (world.getState() == DebrisWorld.State.WIN) {
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
		font.draw(spriteBatch, "delta = " + String.format("%.2f", delta), 
				(int) -translation + 50, (int) height - 50);
		font.draw(spriteBatch, "sleep = " + String.format("%.2f", sleep), 
				(int) -translation + 150, (int) height - 50);
		font.draw(spriteBatch, "fps = " + Gdx.graphics.getFramesPerSecond(),
				(int) -translation + 50, (int) height - 70);
		font.draw(spriteBatch, "time = " + String.format("%.2f", countDown/1000f),
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
		
		// sleep if current fps runs too fast
		if (sleep > 0.0f) {
			try {
				Thread.sleep((long) sleep);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
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
			
			//moveVector.x *= 1000f;
			//moveVector.y *= 1000f;
			world.movePlayer(moveVector);
			
			timeMove = MOVE_INTERVAL;
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

}

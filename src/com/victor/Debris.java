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
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Debris extends InputAdapter implements ApplicationListener {

	SpriteBatch batch;
	BitmapFont font;

	DebrisWorld world;

	Vector2 lastPosition = null;
	Vector2 moveVector = new Vector2();

	Matrix4 transform = new Matrix4();
	int translation_x = 0;
	
	OrthographicCamera camera;

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
		batch = new SpriteBatch();
		
		camera = new OrthographicCamera(30, 50);
		camera.position.set(0, 0, 0);

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
		
		GL10 gl = Gdx.app.getGraphics().getGL10();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        camera.apply(gl);

		// center to player
		Vector2 position = world.getPlayerPosition();
		float translation = (float) -position.x + width / 2f;
		transform.setToTranslation(translation, 0f, 0f);
		//spriteBatch.setTransformMatrix(transform);
		camera.position.set(position.x, 15f, 0);
		// render
		//batch.getProjectionMatrix().set(camera.combined);
		//batch.begin();
		
		
		
		/*
		 * for (Body d : world.getDebrisList()) { Vector2 pos = d.getPosition();
		 * font.draw(spriteBatch, "D", (int) pos.x, (int) pos.y); }
		 */
		//batch.end();

		// render debug shape
		world.renderDebug();

		batch.getProjectionMatrix().setToOrtho2D(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.begin();
		batch.setColor(Color.WHITE);
		Vector2 v = world.getPlayerVelocity();
		font.draw(batch, "delta = " + String.format("%.2f", delta), 
				10, (int) height - 50);
		font.draw(batch, "sleep = " + String.format("%.2f", sleep), 
				200, (int) height - 50);
		font.draw(batch, "fps = " + Gdx.graphics.getFramesPerSecond(),
				10, (int) height - 70);
		font.draw(batch, "time = " + String.format("%.2f", countDown/1000f),
				10, (int) height - 90);
		if (isWin) {
			font.draw(batch, "YOU WIN!",
					(int) 10, (int) height - 110);
		} else if (isLose) {
			font.draw(batch, "YOU LOST!",
					(int) 10, (int) height - 110);
		}
		batch.end();
		
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
			 
			//moveVector.x *= 10f;
			//moveVector.y *= 10f;
			
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

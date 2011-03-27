package com.victor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.MathUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Debris extends InputAdapter implements ApplicationListener {

	SpriteBatch batch;
	BitmapFont font;

	DebrisWorld world;

	Vector2 lastPosition = null;
	Vector2 impulse = new Vector2();

	OrthographicCamera camera;

	int width;
	int height;

	boolean jumping = false;
	
	TextureRegion textureRegion;

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		// texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
		textureRegion = new TextureRegion(new Texture(Gdx.files.internal("data/tmp.jpg")));
		
		batch = new SpriteBatch();

		camera = new OrthographicCamera(30, 50);
		camera.position.set(0, 0, 0);

		world = new DebrisWorld();
		world.reset();
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime() * 1000; // in milliseconds
		float sleep = 1000f / DebrisParam.FPS - delta;

		world.tick(delta);

		//
		// render
		//

		GL10 gl = Gdx.app.getGraphics().getGL10();
		camera.update();
		camera.apply(gl);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// center to player
		Vector2 position = world.getPlayerPosition();
		camera.position.set(position.x, 10f, 0);
		batch.getProjectionMatrix().set(camera.combined);
		batch.begin();
		
		for (Body d : world.getDebrisList()) {
			Vector2 bodyPosition = d.getPosition(); // that's the box's center position
			float angle = MathUtils.radiansToDegrees * d.getAngle(); // the rotation angle around the center
			batch.draw(textureRegion, 
					bodyPosition.x - 1, bodyPosition.y - 1, // the bottom left corner of the box, unrotated
						  1f, 1f, // the rotation center relative to the bottom left corner of the box
						  2, 2, // the width and height of the box
						  1, 1, // the scale on the x- and y-axis
						  angle); // the rotation angle
		}
		batch.end();

		// render debug shape
		world.renderDebug();

		// render text
		batch.getProjectionMatrix().setToOrtho2D(
				0, 0, 
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.begin();
		batch.setColor(Color.WHITE);
		font.draw(batch, "FPS = " + Gdx.graphics.getFramesPerSecond(), 10,
				(int) height - 10);
		font.draw(batch, "time = " + String.format("%.2f", world.getTimeLeft() / 1000f),
				10, (int) height - 30);
		DebrisWorld.State state = world.getState();
		if (state == DebrisWorld.State.WIN) {
			font.draw(batch, "YOU WIN!", 
					10, (int) height - 50);
		} else if (state == DebrisWorld.State.DEAD) {
			font.draw(batch, "YOU ARE DEAD!", 
					10, (int) height - 50);
		}
		batch.end();

		// sleep if current FPS runs too fast
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
			impulse.x = (x - lastPosition.x);
			impulse.y = -(y - lastPosition.y);
			impulse.x = impulse.x * (
					DebrisParam.SCREEN_TO_WORLD / Gdx.graphics.getWidth());
			impulse.y = impulse.y * (
					DebrisParam.SCREEN_TO_WORLD / Gdx.graphics.getHeight());
			world.movePlayer(impulse);
		}

		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

}

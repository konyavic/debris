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
	Vector2 textPosition = new Vector2(100, 100);
	Vector2 textDirection = new Vector2(1, 1);
	DebrisWorld world;
	Vector2 lastPosition = null;
	Matrix4 transform = new Matrix4();
	int translation_x = 0;

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		// texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
		spriteBatch = new SpriteBatch();

		world = new DebrisWorld();
		world.reset();
		world.start();
		Gdx.input.setInputProcessor(this);
		
		//transform.setToTranslation(-DebrisWorld.WIDTH/4f, 0f, 0f);
	}

	@Override
	public void render() {
		world.tick((long) (Gdx.graphics.getDeltaTime() * 6000), 6);

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

		// more fun but confusing :)
		// textPosition.add(textDirection.tmp().mul(Gdx.graphics.getDeltaTime()).mul(60));
		textPosition.x += textDirection.x * Gdx.graphics.getDeltaTime() * 60;
		textPosition.y += textDirection.y * Gdx.graphics.getDeltaTime() * 60;

		if (textPosition.x < 0) {
			textDirection.x = -textDirection.x;
			textPosition.x = 0;
		}
		if (textPosition.x > Gdx.graphics.getWidth()) {
			textDirection.x = -textDirection.x;
			textPosition.x = Gdx.graphics.getWidth();
		}
		if (textPosition.y < 0) {
			textDirection.y = -textDirection.y;
			textPosition.y = 0;
		}
		if (textPosition.y > Gdx.graphics.getHeight()) {
			textDirection.y = -textDirection.y;
			textPosition.y = Gdx.graphics.getHeight();
		}

		spriteBatch.setTransformMatrix(transform);
		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);
		font.draw(spriteBatch, "Debris", (int) textPosition.x,
				(int) textPosition.y);
		for (Body d : world.getDebrisList()) {
			Vector2 pos = d.getPosition();
			font.draw(spriteBatch, "D", (int) pos.x, (int) pos.y);
		}
		spriteBatch.end();

		world.renderDebug();
	}

	@Override
	public void resize(int width, int height) {
		textPosition.set(0, 0);
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
		if (lastPosition == null) {
			lastPosition = new Vector2((float) x, (float) y);
		} else {
			lastPosition.x = (float) x;
			lastPosition.y = (float) y;
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		
		translation_x += x - (int) lastPosition.x;
		transform.setToTranslation((float) translation_x, 0f, 0f);
		lastPosition.x = (float) x;
		lastPosition.y = (float) y;

		return false;
	}

}

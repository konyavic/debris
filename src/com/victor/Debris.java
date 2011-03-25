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

	@Override
	public void create() {
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		// texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
		spriteBatch = new SpriteBatch();

		world = new DebrisWorld();
		world.reset();
		Gdx.input.setInputProcessor(this);
		
		
		translation_x = (int) (DebrisWorld.WIDTH/2f);
		transform.setToTranslation(translation_x, 0f, 0f);
	}

	@Override
	public void render() {
		// 
		// model
		//
		world.tick((long) (Gdx.graphics.getDeltaTime() * 6000), 6);

		//
		// view
		//
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// center to player
		Vector2 position = world.getPlayerPosition();
		transform.setToTranslation((float) -position.x + width/2f, 0f, 0f);
		spriteBatch.setTransformMatrix(transform);
		
		// render
		spriteBatch.begin();
		spriteBatch.setColor(Color.WHITE);
		Vector2 v = world.getPlayerVelocity();
		font.draw(spriteBatch, "(" + v.x + "," + v.y + ")", 
				(int) 10, (int) height - 100);
		/*
		for (Body d : world.getDebrisList()) {
			Vector2 pos = d.getPosition();
			font.draw(spriteBatch, "D", (int) pos.x, (int) pos.y);
		}
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
		if (lastPosition != null) {
			lastPosition.x = (float) x;
			lastPosition.y = (float) y;
		} else {
			lastPosition = new Vector2((float) x, (float) y);
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (!world.isPlayerJumpable()) {
			return false;
		}
		
		moveVector.x = (x - lastPosition.x) * 100000;
		moveVector.y = -(y - lastPosition.y) * 100000;
		world.movePlayer(moveVector);
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

}

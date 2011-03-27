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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.MathUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Debris extends InputAdapter implements ApplicationListener {

	// members for rendering
	protected SpriteBatch batch;
	protected BitmapFont font;
	protected OrthographicCamera camera;

	// the Box2D world
	protected DebrisWorld world;

	// player control
	protected Vector2 lastPosition = null;
	protected Vector2 impulse = new Vector2();
	protected boolean jumping = false;
	
	// textures
	protected TextureRegion textDebris;
	protected TextureRegion textWall;
	protected TextureRegion textPlayer;
	protected TextureRegion textDoor;

	@Override
	public void create() {
		font = new BitmapFont();
		font.setScale(2.0f);
		font.setColor(Color.WHITE);
		
		textDebris = new TextureRegion(new Texture(Gdx.files.internal("data/tmp.jpg")));
		textWall = new TextureRegion(new Texture(Gdx.files.internal("data/brick.jpg")));
		textPlayer = new TextureRegion(new Texture(Gdx.files.internal("data/player.jpg")));
		textDoor = new TextureRegion(new Texture(Gdx.files.internal("data/exit.jpg")));
		
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
		Body player = world.getPlayer();
		Vector2 playerPosition = player.getPosition();
		camera.position.set(playerPosition.x, playerPosition.y, 0);
		batch.getProjectionMatrix().set(camera.combined);
		batch.begin();
		
		// draw player
		float playerAngle = MathUtils.radiansToDegrees * player.getAngle();
		batch.draw(textPlayer, 
				playerPosition.x - DebrisParam.PLAYER_SIZE,
				playerPosition.y - DebrisParam.PLAYER_SIZE,
				DebrisParam.PLAYER_SIZE, DebrisParam.PLAYER_SIZE,
				DebrisParam.PLAYER_SIZE * 2, DebrisParam.PLAYER_SIZE * 2, 
				1, 1,
				playerAngle);
		
		// draw walls
		float x, y;
		float brickSize = (float) DebrisParam.WALL_WIDTH*2;
		for(x = 0f; x < DebrisParam.WALL_WIDTH*2; x += brickSize) {
			for(y = 0f; y < DebrisParam.WALL_HEIGHT*2; y += brickSize) {
				batch.draw(textWall, 
						-DebrisParam.STAGE_WIDTH/2f - DebrisParam.WALL_WIDTH*2 + x, 
						y, 
						brickSize, brickSize);
				batch.draw(textWall, 
						DebrisParam.STAGE_WIDTH/2f + x, 
						y, 
						brickSize, brickSize);
			}
		}
		for(x = -DebrisParam.STAGE_WIDTH/2f; x < DebrisParam.STAGE_WIDTH/2f; x += brickSize) {
			batch.draw(textWall, 
					x, 0f, 
					brickSize, brickSize);
		}
		
		// draw debris
		for (Body d : world.getDebrisList()) {
			Vector2 bodyPosition = d.getPosition(); // that's the box's center position
			float size = (Float) d.getUserData();
			float debrisAngle = MathUtils.radiansToDegrees * d.getAngle(); // the rotation angle around the center
			batch.draw(textDebris, 
					bodyPosition.x - size, bodyPosition.y - size, // the bottom left corner of the box, unrotated
						  size, size, // the rotation center relative to the bottom left corner of the box
						  size*2, size*2, // the width and height of the box
						  1, 1, // the scale on the x- and y-axis
						  debrisAngle); // the rotation angle
		}
		
		// draw exit door
		Body door = world.getDoor();
		if (door != null) {
			Vector2 doorPosition = door.getPosition();
			float doorAngle = MathUtils.radiansToDegrees * door.getAngle();
			batch.draw(textDoor, 
					doorPosition.x - DebrisParam.DOOR_WIDTH,
					doorPosition.y - DebrisParam.DOOR_HEIGHT,
					DebrisParam.DOOR_WIDTH, DebrisParam.DOOR_HEIGHT,
					DebrisParam.DOOR_WIDTH * 2, DebrisParam.DOOR_HEIGHT * 2, 
					1, 1,
					doorAngle);
		}

		batch.end();

		// render debug shape
		//world.renderDebug();

		// render text
		int height = Gdx.graphics.getHeight();
		batch.getProjectionMatrix().setToOrtho2D(
				0, 0, 
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.begin();
		
		batch.setColor(Color.WHITE);
		font.draw(batch, "FPS = " + Gdx.graphics.getFramesPerSecond(), 10,
				(int) height - 10);
		font.draw(batch, "time = " + String.format("%.2f", world.getTimeLeft() / 1000f),
				10, (int) height - 35);
		
		DebrisWorld.State state = world.getState();
		if (state == DebrisWorld.State.WIN) {
			font.draw(batch, "YOU WIN!", 
					10, (int) height - 60);
		} else if (state == DebrisWorld.State.DEAD) {
			font.draw(batch, "YOU ARE DEAD!", 
					10, (int) height - 60);
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
			
			if (world.getState() != DebrisWorld.State.DEAD)
				world.movePlayer(impulse);
		}

		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		return false;
	}

}

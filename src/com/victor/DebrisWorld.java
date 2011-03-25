package com.victor;

import java.util.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class DebrisWorld {
	World world;
	Vector<Body> debrisList = new Vector<Body>();
	Body player;
	Box2DDebugRenderer debug = new Box2DDebugRenderer();
	float debrisCount = DEBRIS_SPEED;
	
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	public static final int MAX_DEBRIS_SIZE = 25;
	public static final int MIN_DEBRIS_SIZE = 15;
	public static final float DEBRIS_SPEED = 2f; // secs per fall 

	public void reset() {
		Vector2 gravity = new Vector2(0.0f, -8.0f);
		world = new World(gravity, true);
		// world.setContactListener(this);
		createFloor();
		createLeftWall();
		createRightWall();
		
		createPlayer();
	}

	public void tick(long msecs, int iters) {
		float dt = (msecs / 1000.0f) / iters;
		for (int i = 0; i < iters; i++) {
			world.step(dt, 10, 10);
		}
		
		debrisCount += dt;
		if (debrisCount >= DEBRIS_SPEED) {
			debrisCount = 0f;
			createDebrisRandom();
		}
	}

	public Vector<Body> getDebrisList() {
		return debrisList;
	}
	
	public void createPlayer() {
		player = createDebrisBox(0, 100, 5, 10, 0, 0, 0);
	}
	
	public void movePlayer(Vector2 force) {
		player.applyForce(force, player.getWorldCenter());
	}
	
	public Vector2 getPlayerPosition() {
		return player.getPosition();
	}
	
	public boolean isPlayerStopped() {
		Vector2 v = player.getLinearVelocity();
		return (Math.abs(v.x) <= 100 && Math.abs(v.y) <= 0);
	}
	
	public void createDebrisRandom() {
		int size = MIN_DEBRIS_SIZE 
			+ (int) (Math.random() * (MAX_DEBRIS_SIZE - MIN_DEBRIS_SIZE));
		int x = (-WIDTH/2 + 30)
			+ (int) (Math.random() * (WIDTH - 60));
		
		int type = (int) (Math.random() * 3);
		
		if (type == 0) {
			createDebrisBox(x, HEIGHT, size, size, 0, 0, 0);
		} else if (type == 1) {
			createDebrisTriangle(x, HEIGHT, size);
		} else if (type == 2) {
			createDebrisCircle(x, HEIGHT, size);
		}
	}

	public Body createDebrisBox(int x, int y, int w, int h, int cx, int cy,
			float angle) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox((float) w, (float) h,
				new Vector2((float) cx, (float) cy), angle);

		return createDebris(x, y, shape);
	}

	public Body createDebrisTriangle(int x, int y, int e) {
		PolygonShape shape = new PolygonShape();
		Vector2[] vlist = { new Vector2(0f, 0f),
				new Vector2(e * 0.5f, -e * 0.866f), new Vector2((float) e, 0f) };
		shape.set(vlist);

		return createDebris(x, y, shape);
	}

	public Body createDebrisCircle(int x, int y, int radius) {
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);

		return createDebris(x, y, shape);
	}

	public Body createDebris(int x, int y, Shape shape) {
		BodyDef bd = new BodyDef();
		bd.position.set((float) x, (float) y);

		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 1.0f;
		fd.friction = 0.3f;
		fd.restitution = 0f;

		Body body = world.createBody(bd);
		body.createFixture(fd);
		body.setType(BodyDef.BodyType.DynamicBody);

		return body;
	}
	
	public void createFloor() {
		PolygonShape wallshape = new PolygonShape();
		wallshape.setAsBox(WIDTH/2f, 15f, new Vector2(0f, 0f), 0f);

		FixtureDef fd = new FixtureDef();
		fd.shape = wallshape;
		fd.density = 1.0f;

		BodyDef bd = new BodyDef();
		bd.position.set(0f, 0f);
		
		Body wall = world.createBody(bd);
		wall.createFixture(fd);
		wall.setType(BodyDef.BodyType.StaticBody);
	}
	
	public void createLeftWall() {
		PolygonShape wallshape = new PolygonShape();
		wallshape.setAsBox(15f, HEIGHT/2f, new Vector2(0f, HEIGHT/2f), 0f);

		FixtureDef fd = new FixtureDef();
		fd.shape = wallshape;
		fd.density = 1.0f;

		BodyDef bd = new BodyDef();
		bd.position.set(-WIDTH/2f, 0f);
		
		Body wall = world.createBody(bd);
		wall.createFixture(fd);
		wall.setType(BodyDef.BodyType.StaticBody);		
	}
	
	public void createRightWall() {
		PolygonShape wallshape = new PolygonShape();
		wallshape.setAsBox(15f, HEIGHT/2f, new Vector2(0f, HEIGHT/2f), 0f);

		FixtureDef fd = new FixtureDef();
		fd.shape = wallshape;
		fd.density = 1.0f;

		BodyDef bd = new BodyDef();
		bd.position.set(WIDTH/2f, 0f);
		
		Body wall = world.createBody(bd);
		wall.createFixture(fd);
		wall.setType(BodyDef.BodyType.StaticBody);		
	}

	public void renderDebug() {
		debug.render(world);
	}
}

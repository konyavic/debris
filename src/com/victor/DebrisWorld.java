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
	Body wall;
	Box2DDebugRenderer debug = new Box2DDebugRenderer();
	
	
	public final int WIDTH = 960;
	public final int HEIGHT = 800;

	public void reset() {
		Vector2 gravity = new Vector2(0.0f, -10.0f);
		world = new World(gravity, true);
		// world.setContactListener(this);
	}

	public void start() {
		PolygonShape debrisshape = new PolygonShape();
		Vector2[] vlist = { new Vector2(0f, 0f), new Vector2(2f, -10f),
				new Vector2(10f, 0f) };
		debrisshape.set(vlist);

		FixtureDef fd = new FixtureDef();
		fd.shape = debrisshape;
		fd.density = 1.0f;
		fd.friction = 0.3f;
		fd.restitution = 0.6f;

		BodyDef bd = new BodyDef();
		// bd.isBullet = true;
		bd.allowSleep = true;
		bd.position.set(50.0f, 200.0f);
		Body body = world.createBody(bd);
		body.createFixture(fd);

		// body.setType(BodyDef.BodyType.StaticBody);
		body.setType(BodyDef.BodyType.DynamicBody);
		debrisList.add(body);

		createFloor();

		createDebrisBox(100, 200, 15, 15, 0, 0, 0);
		createDebrisTriangle(150, 200, 30);
		createDebrisCircle(200, 200, 12);
	}

	public void tick(long msecs, int iters) {
		float dt = (msecs / 1000.0f) / iters;

		for (int i = 0; i < iters; i++) {
			world.step(dt, 10, 10);
		}

	}

	public Vector<Body> getDebrisList() {
		return debrisList;
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
		fd.restitution = 0.6f;

		Body body = world.createBody(bd);
		body.createFixture(fd);
		body.setType(BodyDef.BodyType.DynamicBody);

		return body;
	}
	
	public void createFloor() {
		PolygonShape wallshape = new PolygonShape();
		wallshape.setAsBox(600f, 10f, new Vector2(0f, 0f), 0f);

		FixtureDef fd = new FixtureDef();
		fd.shape = wallshape;
		fd.density = 1.0f;

		BodyDef bd = new BodyDef();
		bd.position.set(0f, 0f);
		
		Body wall = world.createBody(bd);
		wall.createFixture(fd);
		wall.setType(BodyDef.BodyType.StaticBody);
	}

	public void renderDebug() {
		debug.render(world);
	}
}

package com.victor;

public final class DebrisParam {
	// parameters in Box2D (meters)
	public static final float STAGE_WIDTH = 40f;
	public static final float STAGE_HEIGHT = 50f;
	public static final float PLAYER_SIZE = 1f;
	public static final float DOOR_WIDTH = 1f;
	public static final float DOOR_HEIGHT = 2f;
	public static final float WALL_WIDTH = 1f;
	public static final float WALL_HEIGHT = 50f;
	public static final float[] DEBRIS_SIZE = {1f, 3f, 5f};
	public static final int[] DEBRIS_PROB = {8, 2, 1};
	public static final int DEBRIS_PROB_MAX = 12;
	public static final float GRAVITY = -9.8f;
	public static final float SCREEN_TO_WORLD = 300f;
	
	public static final float FPS = 60f;
	public static final float DROP_INTERVAL = 1000f;
	public static final int MAX_DEBRIS = 60;
	public static final float TIME_LIMIT = 100000f;
}

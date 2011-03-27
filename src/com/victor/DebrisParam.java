package com.victor;

public final class DebrisParam {
	// parameters in Box2D (meters)
	public static final float STAGE_WIDTH = 40f;
	public static final float STAGE_HEIGHT = 50f;
	public static final float PLAYER_WIDTH = 0.4f;
	public static final float PLAYER_HEIGHT = 1.6f;
	public static final float DOOR_WIDTH = 1f;
	public static final float DOOR_HEIGHT = 2f;
	public static final float WALL_WIDTH = 1f;
	public static final float WALL_HEIGHT = 100f;
	public static final float[] DEBRIS_SIZE = {1f, 3f, 5f};
	public static final int[] DEBRIS_PROB = {6, 2, 2};
	public static final int DEBRIS_PROB_MAX = 10;
	public static final float GRAVITY = -9.8f;
	public static final float SCREEN_TO_WORLD = 300f;
	
	public static final float FPS = 60f;
	public static final float DROP_INTERVAL = 1000f;
	public static final int MAX_DEBRIS = 30;
	public static final float TIME_LIMIT = 60000f;
}

package com.victor;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class DebrisDesktop {
	public static void main(String[] argv) {
		new JoglApplication(new Debris(), "Debris", 360, 600, false);
	}
}

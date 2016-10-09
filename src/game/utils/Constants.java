package game.utils;

import java.awt.Dimension;

public class Constants {

//	public static final int STATS_HEIGHT = 11;
//	public static final int STATS_WIDTH = 34;
	public static final int FONT_PT = 12;


	/*
	 * Units of time in ticks.
	 */
	public static final int ONE_SECOND = 60;
	public static final int ONE_SIXTH_SECOND = 10;
	public static final int MESSAGE_DISAPPEAR_RATE = 420;

	
	/**
	 * Gravity Constant Measured in some arbitrary units.
	 */
	public static final double GACCEL = .1;

	private Constants() {
		throw new AssertionError("You cannot create this class!");
	}

}

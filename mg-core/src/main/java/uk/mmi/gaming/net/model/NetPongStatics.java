package uk.mmi.gaming.net.model;

public class NetPongStatics {
	private NetPongStatics() {
		// no intantiation
	}

	public static final int UDP_PORT = 45455;
	public static final int TCP_PORT = 45454;
	
	public static final int PLAYFIELD_WIDTH = 600;
	public static final int PLAYFIELD_HEIGHT = 600;
	
	public static final int PLAYER_SPEED_PPS = 350;
	public static final int BALL_SPEED_PPS = 350;
	
	
	public static final int BAT_WIDTH = 90;
	public static final int BAT_HEIGHT = 14;
	public static final int BALL_RADIUS = 16;
}

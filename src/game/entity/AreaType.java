package game.entity;

import java.awt.Color;

public enum AreaType {
	NORMAL(255,255,255),
	PLATFORM(136, 0 ,21),
	NODRAW(255,174,201),
	GOAL(0,162,232),
	BALL(163,73,164),
	UPSWITCH(181, 230,29),
	DOWNSWITCH(255,201,14),
	DEATH(127,127,127);
	
	private int r, g, b;
	AreaType(int r, int g, int b){
		this.r = r;
		this.g = g;
		this.b = b;
	}
	public static AreaType getAreaType(Color c){
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();
		
		if (red == 255 && green == 255 && blue == 255)
			return AreaType.NORMAL;
		else if (red == 136 && green == 0 && blue == 21)
			return AreaType.PLATFORM;
		else if (red == 255 && green == 174 && blue == 201)
			return AreaType.NODRAW;
		else if (red == 0 && green == 162 && blue == 232)
			return AreaType.GOAL;
		else if (red == 163 && green == 73 && blue == 164)
			return AreaType.BALL;
		else if (red == 181 && green == 230 && blue == 29)
			return AreaType.UPSWITCH;
		else if (red == 255 && green == 201 && blue == 14)
			return AreaType.DOWNSWITCH;
		else if (red == 127 && green == 127 && blue == 127)
			return AreaType.DEATH;
		
		else return AreaType.NORMAL;
		
	}
	public Color getColor(){ 
		if(r == 255 && g == 174 && b==201)
			return new Color(229,132,123);
		else if(r == 255 && g == 255 && b==255)
			return new Color(250,240,230);
		else if(r == 136 && g == 0 && b==21)
			return new Color(167, 50, 77);
			
		return new Color(r,g,b); }
}

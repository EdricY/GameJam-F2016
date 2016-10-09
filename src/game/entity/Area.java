package game.entity;

import java.awt.Graphics;

import game.gfx.Screen;

public class Area {
	
	AreaType type;
	int x, y, w, h;
	public Area(AreaType a, int x, int y, int w, int h){
		type = a;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	public AreaType getType() { return type;}
	public boolean contains(int x, int y){
		return (x >= this.x && x <= this.x + this.w
				&& y > this.y && y <= this.y + this.h);
	}
	public void draw(Graphics g)
	{ 
		g.setColor(type.getColor());
		g.fillRect(x, y, w, h);
	}

}

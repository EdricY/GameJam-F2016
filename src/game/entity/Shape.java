package game.entity;

import java.awt.Graphics;
import java.awt.Point;


public abstract class Shape{
		public abstract void draw(Graphics g);
		public abstract boolean collision(double x1, double y1, double x2, double y2);
		public abstract double collisionAngle(double x1, double y1, double x2, double y2);
}

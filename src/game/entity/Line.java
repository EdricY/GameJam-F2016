package game.entity;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Line2D;

import game.Game;

public class Line extends Shape{
	double x1;
	double x2;
	double y1;
	double y2;
	double m, b;
	boolean v;
	public Line(double x1,double y1, double x2, double y2, boolean visible)
	{
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.m = (double)(y2-y1)/(double)(x2-x1);
		this.b = y1 - (m*x1);
		v = visible;
		
		//System.out.println("l created! x1:" + x1 + " y1:" + y1 + " x2:" + x2 + " y2:" + y2 + " m: " + m + " b: " + b);
	}
	public void draw(Graphics g){ 
		if (v)
			g.drawLine((int)(.5+x1 * Game.SCALE), (int)(.5+y1* Game.SCALE), (int)(.5+x2* Game.SCALE), (int)(.5+y2* Game.SCALE));
		}
	public boolean collision(double x1, double y1, double x2, double y2){
		if (Line2D.linesIntersect(this.x1, this.y1, this.x2, this.y2, x1, y1, x2, y2)){
			//System.out.println("lintersect! x: " + x1 + "y : " + y1 );
			return true;
		}
		else return false;

//		System.out.println("lintersect!: x: " + (int)(.5+x1) + " y: " + (int)(.5+y1));
//		return new Point((int)(.5+x1), (int)(.5+y1));
//		if (x1 == x2) {
//			System.out.println("setting point: x: " + (int)(.5+x1) + " y: " + (int)(.5+ ((m)*x2 + b)));
//			return new Point((int)(.5+x1), (int)(.5+y1));
//			//return new Point((int)(.5+x1), (int)(.5+ ((m)*x2 + b)));
//		}
//		else{
//		double m1 = (y2-y1)/(x2-x1); //y=ax+b
//		double b1 = y1 + m1*x1;
//		double x = (double)(b1-b)/(double)(m-m1);
//		System.out.println("setting pointt: x: " + (int)(.5+x1) + " y: " + (int)(.5+ ((m)*x2 + b)));
//		return new Point((int)(.5+ x1), (int)(.5+y1));
//		
////		System.out.println(a1);
////		System.exit(0);
////		
////		
////		double b1 = y1 - a1*x1;
////		double a2 = (this.y2-this.y1)/(this.x2-this.x1);
////		double b2 = this.y1 - a2*this.x1;
////		System.out.println("C3");
//		
//		//return new Point((int)(.5+(-(b1-b2)/(a1-a2))), (int)(.5+ a2*(-(b1-b2)/(a1-a2) + b2)));
//		}
	}
	public double collisionAngle(double x1, double y1, double x2, double y2){
		return Math.atan((double)((this.y2-this.y1)/(double)(this.x2-this.x1)));
	}
}

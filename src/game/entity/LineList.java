package game.entity;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.util.Arrays;
import java.util.Collections;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import game.Game;

public class LineList{

	ArrayList<Line> lines = new ArrayList<>();
	/**
	 * collision lines
	 */
	ArrayList<Shape> clines = new ArrayList<>(); 
	public LineList(){}
	
	public void add(int x1, int y1, int x2, int y2, boolean visible) { 
		lines.add(new Line(x1, y1, x2, y2, visible)); 
//		if (clines.isEmpty()){
//			clines.add(new Circle(x1, y1, Game.radius));
//		}
//		clines.add(new Circle(x2, y2, Game.radius));
		double slope = ((double)(y1-y2)/(double)(x2-x1));

		double angle = Math.atan(slope);
		
		double invslope = ((double)(x2-x1))/((double)(y1-y2));
		double ratio = Math.sqrt(1 + 1/(invslope*invslope))/Game.radius;

		clines.add(new Line(x1-Game.radius*Math.sin(angle), 
						y1-Game.radius*Math.cos(angle), 
						x2-Game.radius*Math.sin(angle), 
						y2-Game.radius*Math.cos(angle), true));
		clines.add(new Line(Game.radius*Math.sin(angle)+x1, 
						Game.radius*Math.cos(angle) +y1, 
						Game.radius*Math.sin(angle)+x2, 
						Game.radius*Math.cos(angle) +y2, true));
	}
	
	public void reset() { lines = new ArrayList<>(); 
	 clines = new ArrayList<>(); }
	
	public ArrayList<Line> getLines() { return lines; }
	
	public void draw(Graphics g){ //using graphics, so not rendering :( sorry
		for (Line l: lines)
		{
			g.setColor(new Color(222,184,135));
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(5));
			l.draw(g);
		}
		
	}
	
	public void drawCollision(Graphics g, Ball b){ //this is for debugging only
		for (Shape s: clines)
		{
			g.setColor(Color.RED);
			s.draw(g);
			//g.drawLine((int)(.5+b.getX()), (int)(.5+b.getY()), (int)(.5+b.getX()-b.getVelX()), (int)(.5+b.getY()-b.getVelY()));
		}
	}
//	public Point getCollision(Ball b, Line l) {
//		int x1 = (int)(.5+ b.getCenterX() - b.getVelX());
//		int y1 = (int)(.5+ b.getCenterY() - b.getVelY());
//		int x2 = (int)(b.getCenterY());
//		int y2 = (int)(b.getCenterY());
//		
//		
//		
//		
//		return null; 
//	}
//	public Point getCollision(Ball b, Circle c) {
//		
//		
//		return null; 
//	}

	public double collides(Ball b) {
		int x1 = (int)(.5+ b.getX() - b.getVelX());
		int y1 = (int)(.5+ b.getY() - b.getVelY());
		int x2 = (int)(b.getX());
		int y2 = (int)(b.getY());
//		ArrayList<Point> pts = new ArrayList<>();
//		ArrayList<Shape> shapes = new ArrayList<>();

		
		for(Shape s : clines)
		{
			if(s.collision(x1, y1, x2, y2)){
				b.setX((int)(.5+b.lastX));
				b.setY((int)(.5+b.lastY));
				return s.collisionAngle(x1, y1, x2, y2);
			}
			//System.out.println(cpoint.getX());
			
		}
		return -10;
//		if (pts.isEmpty()) return -1;
//		
//		int closest = 0;
//		for(int i = 0; i < pts.size(); i++)
//			if(Math.sqrt((pts.get(i).getX()-x1)*pts.get(i).getX()-x1 + (pts.get(i).getY()-y1)+(pts.get(i).getY()-y1)) < 
//					(Math.sqrt((pts.get(closest).getX()-x1)*pts.get(closest).getX()-x1 + (pts.get(closest).getY()-y1)+(pts.get(closest).getY()-y1))))
//				closest = i;
//		b.setX(pts.get(closest).x);
//		b.setY(pts.get(closest).y);
//		System.out.println("point set" + " x:" + b.getX() + " y:" + b.getY());
//		return shapes.get(closest).collisionAngle(x1, y1, x2, y2);
		
	}

}

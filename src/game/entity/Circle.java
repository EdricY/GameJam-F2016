package game.entity;

import java.awt.Graphics;
import java.awt.Point;

import game.Game;

public class Circle extends Shape{
		int x;
		int y;
		int r;
		public Circle(int x,int y, int r)
		{
			this.x = x;
			this.y = y;
			this.r = r;
		}
		public void draw(Graphics g){ 
			g.drawOval((x-r) * Game.SCALE, (y-r) * Game.SCALE, 2*r* Game.SCALE, 2*r* Game.SCALE);
		} 
		public boolean collision(double x1, double y1, double x2, double y2){ //I am so sorry about this
			
			if(Math.sqrt((x-x1)*(x-x1) + (y-y1)*(y-y1)) >= r && Math.sqrt((x-x2)*(x-x2) + (y-y2)*(y-y2)) <= r ){
				System.out.println("cintersect! x: " + x1 + "y : " + y1 );
				return true;
			}
			return false;
				
			
			
//			if(CircleLine.getCircleLineIntersectionPoint(new game.utils.CircleLine.Point(x1, y1),
//	                new game.utils.CircleLine.Point(x2, y2), new game.utils.CircleLine.Point(this.x, this.y), this.r).equals(Collections.emptyList()))
//				return null;
//			else if ((CircleLine.getCircleLineIntersectionPoint(new game.utils.CircleLine.Point(x1, y1),
//	                new game.utils.CircleLine.Point(x2, y2), new game.utils.CircleLine.Point(this.x, this.y), this.r).size() == 1)){ System.out.println("C1");
//				return new Point((int) (.5+ CircleLine.getCircleLineIntersectionPoint(new game.utils.CircleLine.Point(x1, y1),
//		                new game.utils.CircleLine.Point(x2, y2), new game.utils.CircleLine.Point(this.x, this.y), this.r).get(0).getX()),
//						(int) (.5+ CircleLine.getCircleLineIntersectionPoint(new game.utils.CircleLine.Point(x1, y1),
//				                new game.utils.CircleLine.Point(x2, y2), new game.utils.CircleLine.Point(this.x, this.y), this.r).get(0).getY()));
//				
//			}else{System.out.println("C2");
//			Point p1 = new Point((int) (.5+ CircleLine.getCircleLineIntersectionPoint(new game.utils.CircleLine.Point(x1, y1),
//	                new game.utils.CircleLine.Point(x2, y2), new game.utils.CircleLine.Point(this.x, this.y), this.r).get(0).getX()),
//					(int) (.5+ CircleLine.getCircleLineIntersectionPoint(new game.utils.CircleLine.Point(x1, y1),
//			                new game.utils.CircleLine.Point(x2, y2), new game.utils.CircleLine.Point(this.x, this.y), this.r).get(0).getY()));
//			Point p2 = new Point((int) (.5+ CircleLine.getCircleLineIntersectionPoint(new game.utils.CircleLine.Point(x1, y1),
//	                new game.utils.CircleLine.Point(x2, y2), new game.utils.CircleLine.Point(this.x, this.y), this.r).get(1).getX()),
//					(int) (.5+ CircleLine.getCircleLineIntersectionPoint(new game.utils.CircleLine.Point(x1, y1),
//			                new game.utils.CircleLine.Point(x2, y2), new game.utils.CircleLine.Point(this.x, this.y), this.r).get(1).getY()));
//			double distp1 = Math.sqrt((x1-p1.getX())*(x1-p1.getX()) + (y1-p1.getY())*(y1-p1.getY()));
//			double distp2 = Math.sqrt((x1-p2.getX())*(x1-p2.getX()) + (y1-p2.getY())*(y1-p2.getY()));
//			if (distp1 >= distp2) return p1;
//			return p2;
//			}
			
		}
		public double collisionAngle(double x1, double y1, double x2, double y2){
			double m = (double)(y2-y1)/(double)(x2-x1);
			return Math.atan(-1/m);
		}
	}

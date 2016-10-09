package game.entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.CopyOnWriteArrayList;

import game.gfx.FontJump;
import game.gfx.Screen;
import game.utils.Constants;
import game.utils.Renderable;
import game.utils.Tickable;
import game.Game;
import game.entity.Area;
import game.entity.AreaType;

public class Ball implements Renderable, Tickable{

	private double x, y; //these are the center
	private double velX = 0, velY = 0;
	double lastX;
	double lastY;
	private boolean justCollided = false;
	private boolean activated = false;
	private boolean moveDown = true;
	
	
	
	final double MAX_X_VEL = 7;
	final double MAX_Y_VEL = 7;
	
	private int r;
	
	private String imgFile;

	private final CopyOnWriteArrayList<FontJump> jumping = new CopyOnWriteArrayList<>();
	
	private boolean justDied = false;

    public Ball(double x, double y, int radius)
    {
        this.x = x;
        this.y = y;
        this.r = radius;
        this.imgFile = "/circle.png";
    }
	public void render(Screen screen) { //not using right now
		
		screen.render((int)(getX() - r +.5), (int)(getY() - r +.5), imgFile);
		
	}
	
	public void draw(Graphics g){
		
		g.fillOval((int)(.5+x - r), (int)(.5+y - r), r*2, r*2);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));
		Color oldc = new Color(g.getColor().getRed(),
				g.getColor().getGreen(), 
				g.getColor().getBlue());
		Color c = new Color(Math.min(255, oldc.getRed()+20),
				Math.min(255, oldc.getGreen()+20), 
				Math.min(255, oldc.getBlue()+20));
		g.setColor(c);
		g.drawOval((int)(.5+x-r), (int)(.5+y-r), r*2, r*2);
		g.setColor(oldc);
        
		g.setColor(Color.blue);
		//g.drawLine((int)(.5+getX()), (int)(.5+getY()), (int)(.5+getX()+getVelX()), (int)(.5+getY()+getVelY()));
		
	}

	public void tick() {
//		if(activated)
//		{
//			double lastX = x-velX;
//			double lastY = y-velY;
//			
//			
//			x+=velX;
//			y+=velY;
//			velY += Constants.GACCEL;
//			
//			
//	        if (velY > 0)      velY = Math.min(MAX_Y_VEL, velY);
//	        else if (velY < 0) velY = Math.max(-1*MAX_Y_VEL, velY);
//	        if (velX > 0)      velX = Math.min(MAX_X_VEL, velX);
//	        else if (velX < 0) velX = Math.max(-1*MAX_X_VEL, velX);
//		}
	}
	public void tick(LineList lines, Area[][] areas) {
		if(activated)
		{
			lastX = x-velX;
			lastY = y-velY;
			if(!justCollided){
				bounce(lines.collides(this));
			}
			else justCollided = false;
			
			Area cArea;
			if(x >= 0 && x < 960 && y >= 0 && y < 540){
				cArea = areas[(int)(y/30)][(int)(x/30)];
				if (cArea.type == AreaType.DOWNSWITCH  && !moveDown){
					velY = 0;
					this.moveDown = true;
				}
				else if (cArea.type == AreaType.UPSWITCH && moveDown){
					velY = 0;
					this.moveDown = false;
				}
				else if (cArea.type == AreaType.DEATH)
					Game.restart = true;
			}
			else Game.restart = true;
			
//			for(Area[] a : areas)
//				for (Area area : a)
//				{
//					if ()
//				}
		
			x+=velX;
			y+=velY;
			if(moveDown)
				velY += Constants.GACCEL;
			else
				velY -= Constants.GACCEL;
			
	        if (velY > 0)      velY = Math.min(MAX_Y_VEL, velY);
	        else if (velY < 0) velY = Math.max(-1*MAX_Y_VEL, velY);
	        if (velX > 0)      velX = Math.min(MAX_X_VEL, velX);
	        else if (velX < 0) velX = Math.max(-1*MAX_X_VEL, velX);
		}
	}

	public double getX() {return x;}
	public double getY() {return y;}
	public void setX(int x) {this.x = x;
	justCollided = true;}
	public void setY(int y) {this.y = y; 
	justCollided = true;}

	public double getVelX() {return velX;}
	public double getVelY() {return velY;}
	public int getR() {return r;}
	public void activate() { activated = true; }
//	public Point getCollision(LineList lines) {
//		int x1 = (int)(.5+ this.getCenterX() - this.getVelX());
//		int y1 = (int)(.5+ this.getCenterY() - this.getVelY());
//		int x2 = (int)(this.getCenterY());
//		int y2 = (int)(this.getCenterY());
//		
//		if(lines.collides(this) != -1);
//		
//		return null; 
//	}
	public void bounce(double angle) { //angle of the wall in radians
		if (angle != -10)
		{
			System.out.println("angle:" + angle);

			//angle = angle + Math.PI;
			double vel = Math.sqrt(velX*velX + velY*velY);
				
			System.out.println("sin: " +  Math.sin(angle));
			if (velY >= 0){
				velY = .9* vel* -Math.cos(angle);
				velX = .9* vel* Math.sin(angle);
			}
			else {
				velY = .9* vel* Math.cos(angle);
				velX = .9* vel* -Math.sin(Math.PI-angle);
			}
			System.out.println("cos: " +  Math.cos(angle));
					
		}
		
	}
	
}
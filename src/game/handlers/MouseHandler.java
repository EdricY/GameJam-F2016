package game.handlers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game.Game;
import game.utils.Constants;

public class MouseHandler implements MouseListener, MouseMotionListener {

	private int x = Integer.MIN_VALUE;
	private int y = Integer.MIN_VALUE;
	
	private int state = 0; //0 = released, 1 = pressed
	
	private int lastClickedX = Integer.MIN_VALUE;
	private int lastClickedY = Integer.MIN_VALUE;
	private int currentX = 0;
	private int currentY = 0;

	private final Game game;

	public MouseHandler(Game game) {
		game.addMouseListener(this);
		game.addMouseMotionListener(this);
		this.game = game;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		
		lastClickedX = x;
		lastClickedY = y;
		
		//System.out.println(x + " " + y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		x = Integer.MIN_VALUE;
		y = Integer.MIN_VALUE;
		game.csearch = true;
	}

	public int getX() {
		return x / Game.SCALE;
	}

	public int getY() {
		return y / Game.SCALE;
	}

	public int getLastClickedX() {
		return lastClickedX;
	}
	public int getLastClickedY() {
		return lastClickedY;
	}
	public int getCurrentX() {
		return currentX;
	}
	public int getCurrentY() {
		return currentY;
	}
	
	public void resetActions() {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		currentX = e.getX();
		currentY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		currentX = e.getX();
		currentY = e.getY();
	}
}

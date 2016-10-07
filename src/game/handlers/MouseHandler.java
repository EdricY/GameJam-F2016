package game.handlers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game.Game;
import game.utils.Constants;

public class MouseHandler implements MouseListener, MouseMotionListener {

	private int x = Integer.MIN_VALUE;
	private int y = Integer.MIN_VALUE;

	private int[] actions = new int[Constants.MOBS_PER_LANE * Constants.LANES];
	private int lastClicked;

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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		x = Integer.MIN_VALUE;
		y = Integer.MIN_VALUE;
	}

	public int getX() {
		return x / Game.SCALE;
	}

	public int getY() {
		return y / Game.SCALE;
	}

	public int[] getActions() {
		return actions;
	}

	public int getLastClicked() {
		return lastClicked;
	}

	public void resetActions() {
		actions = new int[Constants.MOBS_PER_LANE * Constants.LANES];
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}

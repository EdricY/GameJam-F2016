import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;



/**
 * The main class that instantiates all other variables.
 */
@SuppressWarnings("restriction")
public class Game extends JPanel implements Runnable {

	private static final long serialVersionUID = -8332966360668015263L;
	
	public Stage stage = Stage.LOADING;
	public final JFrame frame;
	public int lane;
	private int ticksPassed = 0;
	
	public double x = 10;

	/**
	 * Creates the Game class
	 */
	public Game() {
		setMinimumSize(new Dimension(640 * 1, 480 * 1));
		setMaximumSize(new Dimension(640 * 1, 480 * 1));
		setPreferredSize(new Dimension(640 * 1, 480 * 1));
		frame = new JFrame("Name");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.setResizable(false);
		final ArrayList<Image> icons = new ArrayList<Image>();
		//icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/smallIcon.png")));
		//icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
		frame.setIconImages(icons);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	/**
	 * Starts up the game thread.
	 */
	private void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime(); // long time in nanoseconds.
		final double nsPerTick = 1000000000D / 60D; // limits updates to 60 ups
		int frames = 0; // frames drawn
		long lastTimer = System.currentTimeMillis();
		double wait = 0; // unprocessed nanoseconds.
		int ticks = 0;

		init();

		while (true) {
			final long now = System.nanoTime();
			wait += (now - lastTime) / nsPerTick;
			lastTime = now;
			while (wait >= 1D) {
				ticks++; // Update drawn ticks.
				tick();
				wait = wait - 1D;
			}
			frames++;
			render();
			try {
				Thread.sleep(6);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			if ((System.currentTimeMillis() - lastTimer) > 1000) {
				lastTimer += 1000;
				frame.setTitle("Name" + " - " + ticks + " ticks, " + frames + " frames.");
				frames = 0;
				ticks = 0;
			}
		}
	}

	/**
	 * Code that is run to draw objects to the frame.
	 */
	private void render() {
		//paintComponent(frame.getGraphics());
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(new Color(200,10,10));
		g.fillOval(0, 0, (int)x, (int)x);
	}

	/**
	 * Code that is updated once every nsPerTick nanoseconds.
	 *
	 * @see Game#run()
	 */
	private void tick() {
		
		x+=1;
		
	}


	/**
	 * Completely restarts the game from the bottom up, showing an error message to the user.
	 * @param reason Reason given for the sudden restart.
	 */
	public void restart(String reason) {
	}

	/**
	 * @param args
	 *            Arguments used to start the game with.
	 */
	public static void main(String[] args) {
		for (final String s : args) {
			System.out.println(s);
		}
		new Game().start();
	}

	/**
	 * Code that runs ONE TIME when the game is started up.
	 */
	private void init() {
		frame.setVisible(true); // After init, show the frame.
		final int BUTTON_WIDTH = 10;
		final int BUTTON_HEIGHT = 4;
		final int BUTTON_PIX_WIDTH = 9;
		final int BUTTON_PIX_HEIGHT = 9;
	}

	/**
	 * State of the game.
	 * @author AJ
	 *
	 */
	public enum Stage {
		/**
		 * The game is loading and just restarted.
		 */
		LOADING, 
		
		/**
		 * The game is at the main menu.
		 */
		MENU, 
		
		/**
		 * The game is showing the connect screen.
		 */
		CONNECT, 
		
		/**
		 * The game is running one-time code to set up a connection to a server.
		 */
		CONNECT_SETUP, 
		
		/**
		 * The game is showing the play screen.
		 */
		PLAY, 
		
		/**
		 * The game is running one-time code to set up the game as a host.
		 */
		PLAY_SETUP,
		
		/**
		 * The game is running one-time code to set up both the game and the server connections.
		 */
		GENERAL_SETUP, 
		
		/**
		 * The game is running.
		 */
		RUNNING
	}

	/**
	 * Available button names (Unique)(Self explanatory).
	 * @author AJ
	 *
	 */
	public enum BN {
		MENU_PLAY, MENU_CONNECT, MENU_QUIT, CONNECT_CONNECT, MESSAGE_SEND, NEXT_LANE, PREV_LANE
	}

	/**
	 * Available text box names (Unique)(Self explanatory).
	 * @author AJ
	 *
	 */
	public enum TN {
		CONNECT_USERNAME, CONNECT_IP, MESSAGE_BOX
	}
}

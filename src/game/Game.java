package game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import javax.swing.JFrame;

import game.gfx.Button;
import game.gfx.ChatBox;
import game.gfx.FontJump;
import game.gfx.ProgressBar;
import game.gfx.Screen;
import game.gfx.TextBox;
import game.handlers.InputHandler;
import game.handlers.MouseHandler;
import game.handlers.WindowHandler;
import game.utils.ButtonList;
import game.utils.Debug;
import game.utils.Out;
import game.utils.TextBoxList;

/**
 * The main class that instantiates all other variables.
 *
 * @author AJ
 */
public class Game extends Canvas implements Runnable {

	/**
	 * I have no idea what this black magic is.
	 */
	private static final long serialVersionUID = 1212454230410587523L;

	/**
	 * The {@link String} of the game. Starts with a v. This is used when
	 * logging on to a server, so update it often to prevent out dated clients
	 * from connecting.
	 */
	public static final String VERSION = "v0";

	/**
	 * The {@link String} of the class. This is displayed in the title.
	 */
	public static final String NAME = "Line Bounce";

	/**
	 * The minimum {@link Out} of debug that is output. Should be
	 * {@link Out#INFO} or higher on release. NEVER SET TO {@link Out#TRACE} ON
	 * RELEASE!
	 */
	public static final Out debugLevel = Out.INFO;

	/**
	 * Integer representing the dimensions of the JFrame.
	 */
	public static final int WIDTH = 480, HEIGHT = (Game.WIDTH / 16) * 9, SCALE = 2;

	/**
	 * The {@link JFrame} window the game is rendered to.
	 */
	public final JFrame frame;

	/**
	 * A list of all of the {@link Button}s (Enabled or hidden) in the game.
	 */
	public final ButtonList buttons = new ButtonList();

	/**
	 * A list of all of the {@link TextBox}s (Enabled or hidden) in the game.
	 */
	public final TextBoxList textboxes = new TextBoxList();

	/**
	 * Progress bar that shows clicks Remaining.
	 */
	public final ProgressBar clicksRemaining = new ProgressBar((Game.WIDTH / 2) - ((100 * 3) / 2), (Game.HEIGHT / 2)
			- ((8 * 3) / 2), 100 * 3, 8 * 3);

	/**
	 * The {@link BufferedImage} that is rendered to the JFrame.
	 */
	private static final BufferedImage image = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);

	/**
	 * Error stream for the game. Also Game messages.
	 */
	private final ChatBox err;

	/**
	 * Used for updating and pressing the mouse.
	 */
	private final MouseHandler mouse;

	/**
	 * Screen that is being rendered.
	 */
	private final Screen screen;

	/**
	 * An array of ints that is used to draw the picture on the JFrame.
	 */
	private final int[] pixels = ((DataBufferInt) Game.image.getRaster().getDataBuffer()).getData();

	/**
	 * State of the running game
	 */
	public Stage stage = Stage.MENU;
	

	FontJump fj = new FontJump(50, 100, "foo", 100, 45.0, 60, false);
	
	/**
	 * Creates the Game class
	 */
	public Game() {
		Debug.out(Out.INFO, getClass().getName(), "You are running " + Game.VERSION);

		setMinimumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
		setMaximumSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
		setPreferredSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
		frame = new JFrame(Game.NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.setResizable(false);
		final ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/smallIcon.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
		frame.setIconImages(icons);
		frame.pack();
		frame.setLocationRelativeTo(null);
		screen = new Screen(Game.WIDTH, Game.HEIGHT);
		err = new ChatBox(0, 200, 50, 3);
		mouse = new MouseHandler(this);
		
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
				frame.setTitle(Game.NAME + " - " + ticks + " ticks, " + frames + " frames.");
				frames = 0;
				ticks = 0;
			}
		}
	}

	/**
	 * Code that is run to draw objects to the frame.
	 */
	private void render() {
		final BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3); // Triple buffering!
			return;
		}

		switch (stage) {
		default:
		case MENU:
			fj.render(screen);
			break;
		}
		for (final Button b : buttons.getAll()) {
			b.render(screen);
		}
		for (final TextBox t : textboxes.getAll()) {
			t.render();
		}
		for (int pix = 0; pix < screen.pixels.length; pix++) {
			pixels[pix] = screen.pixels[pix];
		}
		final Graphics g = bs.getDrawGraphics();
		g.drawImage(Game.image, 0, 0, getWidth(), getHeight(), null);
		
		g.dispose();
		bs.show();
	}

	/**
	 * Code that is updated once every nsPerTick nanoseconds.
	 *
	 * @see Game#run()
	 */
	private void tick() {
		switch (stage) {
		default:
		case MENU:
			fj.tick();
			if (buttons.get(BN.PLAY).isClicked())
				stage = Stage.LV1;
			if (buttons.get(BN.QUIT).isClicked())
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					
			break;
		case LV1:
			break;
		}
		err.tick();
		for (final Button b : buttons.getAll()) {
			if ((b.state == Button.States.ENABLED) || (b.state == Button.States.PRESSED)
					|| (b.state == Button.States.OUT)) {
				final boolean within = (mouse.getX() >= b.getX())
						&& (mouse.getX() <= ((b.getW() * b.getTileWidth()) + b.getX())) && (mouse.getY() >= b.getY())
						&& (mouse.getY() <= ((b.getH() * b.getTileHeight()) + b.getY()));
				if (within && (mouse.getX() >= 0) && (mouse.getY() >= 0)) {
					b.state = Button.States.PRESSED;
				} else if (!within && (mouse.getX() >= 0) && (mouse.getY() >= 0)) {
					b.state = Button.States.OUT;
				} else {
					b.state = Button.States.ENABLED;
				}
			}
		}
		for (final TextBox t : textboxes.getAll()) {
			if (((mouse.getX() >= 0) && (mouse.getY() >= 0))
					&& ((t.state == TextBox.States.ENABLED) || (t.state == TextBox.States.SELECTED))) {
				final boolean within = (mouse.getX() >= t.getX()) && (mouse.getX() <= (t.getW() + t.getX()))
						&& (mouse.getY() >= t.getY()) && (mouse.getY() <= (t.getH() + t.getY()));
				if (within) {
					for (final TextBox sett : textboxes.getAll()) {
						if (sett.state == TextBox.States.SELECTED) {
							sett.state = TextBox.States.ENABLED;
						}
					}
					t.state = TextBox.States.SELECTED;
				} else {
					t.state = TextBox.States.ENABLED;
				}
			}
		}
	}

	private void hideAll() {
		for (final Button b : buttons.getAll()) {
			b.state = Button.States.HIDDEN;
		}
		for (final TextBox t : textboxes.getAll()) {
			t.state = TextBox.States.HIDDEN;
		}
	}

	/**
	 * Completely restarts the game from the bottom up, showing an error message to the user.
	 * @param reason Reason given for the sudden restart.
	 */
	public void restart(String reason) {
		stage = Stage.MENU;
		hideAll();
		Debug.out(Out.WARNING, "game.Game", "Error: " + reason);
		err.addString(reason, 0xFFFF0000);
	}

	/**
	 * Gets the currently selected text box.
	 * @return The selected text box.
	 */
	public TextBox getSelectedTextBox() {
		for (final TextBox t : textboxes.getAll()) {
			if (t.state == TextBox.States.SELECTED) {
				return t;
			}
		}
		return null;
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
		new InputHandler(this);
		new WindowHandler(this);
		frame.setVisible(true); // After init, show the frame.
		final int BUTTON_WIDTH = 10;
		final int BUTTON_HEIGHT = 4;
		final int BUTTON_PIX_WIDTH = 9;
		final int BUTTON_PIX_HEIGHT = 9;
//		buttons.add(new Button(screen, Game.WIDTH/3 - (9/2)*BUTTON_PIX_WIDTH, (Game.HEIGHT / 2)- (((BUTTON_HEIGHT + 1) * BUTTON_PIX_HEIGHT) / 2), 
//				9, 3, "/button_disabled.png", "/button_enabled.png", "/button_pressed.png") , BN.PLAY);
//		buttons.get(BN.PLAY).text = "Play!";
//		buttons.get(BN.PLAY).state = Button.States.ENABLED;
//		
//		buttons.add(new Button(screen, (Game.WIDTH / 2) + 10, (Game.HEIGHT / 2) - (((BUTTON_HEIGHT + 1) * BUTTON_PIX_HEIGHT) / 2),
//				9, 3, "/button_disabled.png", "/button_enabled.png", "/button_pressed.png") , BN.QUIT);
//		buttons.get(BN.QUIT).text = "Quit";
//		buttons.get(BN.QUIT).state = Button.States.ENABLED;

//		textboxes.add(new TextBox(screen, (Game.WIDTH / 2) - (256 / 2), 30, 256, false, "Username"),
//				TN.CONNECT_USERNAME);
//		textboxes.add(new TextBox(screen, (Game.WIDTH / 2) - (256 / 2), 70, 256, false, "IP Address"), TN.CONNECT_IP);
//		textboxes.add(new TextBox(screen, 0, 223, 240, false, "Message"), TN.MESSAGE_BOX);
	}

	/**
	 * State of the game.
	 * @author AJ
	 *
	 */
	public enum Stage {
		
		/**
		 * The game is at the main menu.
		 */
		MENU, 
		/** Tutorial Level for .*/
		LV1 
	}

	/**
	 * Available button names (Unique)(Self explanatory).
	 * @author AJ
	 *
	 */
	public enum BN {
		PLAY, QUIT
	}

	/**
	 * Available text box names (Unique)(Self explanatory).
	 * @author AJ
	 *
	 */
	public enum TN {
		TEXTBOX
	}
}

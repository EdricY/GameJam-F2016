package game;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JFrame;

import game.entity.Area;
import game.entity.AreaType;
import game.entity.Ball;
import game.entity.LineList;
import game.gfx.Button;
import game.gfx.Button.States;
import game.gfx.ChatBox;
import game.gfx.FontJump;
import game.gfx.ProgressBar;
import game.gfx.Screen;
import game.gfx.Sprite;
import game.gfx.TextBox;
import game.handlers.InputHandler;
import game.handlers.MouseHandler;
import game.handlers.WindowHandler;
import game.utils.ButtonList;
import game.utils.Debug;
import game.utils.MP3;
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
	public static final String VERSION = "v1.0";

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
	public static final int WIDTH = 960, HEIGHT = (Game.WIDTH / 16) * 9, SCALE = 1;

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
	private ChatBox err;

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
	
	/**
	 * FontJump for awesome menu effects!
	 */
	FontJump fj;
	
	/**
	 * Main Ball for the game
	 */
	Ball ball;
	
	public static int radius = 15;
	
	/**
	 * Main Ball for the game
	 */
	LineList lines = new LineList();
	int currentX = -1, currentY = -1;
	Area testA = new Area(AreaType.NODRAW, 50,100, 100, 150);
	Area[][] levelAreas = new Area[18][32];
	BufferedImage mapimg = null;
	int currentLevel = 0;
	public static boolean restart = false;
	public boolean backspace = false;
	private boolean start = false;
	public boolean skip1 = false;
	public boolean skip2 = false;
	public boolean skip3 = false;
	private int mistakes = 0;
	private long warntime;
	private int messagesent = 0;
	
	MP3 mp3player = new MP3();
	MP3 soundeffects = new MP3();
	
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
		err = new ChatBox(0, 350, 100, 9);
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
			screen.render(0, 0, "/background.png");
			if(mouse.getX() > 0 || start)
				screen.render(0, 0, "/altbackground.png");
			break;
		case INSTRUCTIONS:
			screen.render(0, 0, "/instructions.png");
			break;
		case WARNING:		
			screen.render(0, 0, "/warningbackground.png");
			err.render(screen);
			break;
		case CREDITS:
			screen.render(0, 0, "/credits.png");
			break;
		case LV:
			break;
		}
		//if (ball != null) ball.render(screen);
		
		if (fj != null) fj.render(screen);

		
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
		//use graphics here
		switch (stage) {
		default:
		case MENU:
			break;
		case INSTRUCTIONS:
			break;
		case LV:
			for (Area[] a: levelAreas)
				for (Area area: a)
					if (area != null){
						area.draw(g);
					}
			g.setColor(new Color(222,184,135));
			lines.draw(g);
			g.setColor(new Color(117,68,133));
			ball.draw(g);
					
			//lines.drawCollision(g, ball);
			
			g.setColor(new Color(222,184,135));
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
			if (currentX != -1 && currentY != -1){
				for (Area[] a: levelAreas)
					for (Area area: a)
						if (area.getType() == AreaType.NODRAW && area.contains(mouse.getCurrentX(), mouse.getCurrentY()))
							g.setColor(Color.RED);
				g.setColor(new Color(222,184,135, 100));
				g.drawLine(SCALE * currentX, SCALE * currentY, mouse.getCurrentX(), mouse.getCurrentY());
			}
			g.setColor(new Color(10, 10, 10, 50));
			g.setFont(new Font("Sylfaen", Font.PLAIN, 400));
			g.drawString(Integer.toString(currentLevel), Game.WIDTH/2 - 95, Game.HEIGHT/2 + 120);
			g.setFont(new Font("Sylfaen", Font.PLAIN, 15));
			g.drawString("mistakes: " + Integer.toString(mistakes), Game.WIDTH/2 - 60, Game.HEIGHT/2 + 145);

			g2.setStroke(new BasicStroke(1));
			break;
		}
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
			if (mp3player.isIdle()) mp3player.play();
			if (buttons.get(BN.PLAY).isClicked()){
				messagesent = 0;
				hideAll();
				fj = new FontJump(buttons.get(BN.PLAY).getX(), buttons.get(BN.PLAY).getY(), "Play!", 100, 45.0, 30, false);
				
				start = true;
				
			}
			if (start && fj.isDone()) {
				//mp3player.changeMusic("/SOUND_main_theme.mp3"); mp3player.play();
				warntime = System.currentTimeMillis();
				mp3player.stopMusic();
				soundeffects.stopMusic();
				soundeffects.changeMusic("/computer.mp3");
				soundeffects.play();
				
				readMap(currentLevel);
				stage = Stage.WARNING;
			}
			if (buttons.get(BN.INSTRUCTIONS).isClicked()){
				hideAll();
				fj = new FontJump(buttons.get(BN.INSTRUCTIONS).getX(), buttons.get(BN.INSTRUCTIONS).getY(), "Instructions", 100, 45.0, 30, false);
				readMap(currentLevel);
				stage = Stage.INSTRUCTIONS;
			}
			if (buttons.get(BN.CREDITS).isClicked()){
				hideAll();
				fj = new FontJump(buttons.get(BN.CREDITS).getX(), buttons.get(BN.CREDITS).getY(), "Credits", 100, 45.0, 30, false);
				readMap(currentLevel);
				stage = Stage.CREDITS;
			}
			if (buttons.get(BN.QUIT).isClicked())
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			break;
		case WARNING:
			if (System.currentTimeMillis() > warntime + 1000 &&  messagesent == 0){
				messagesent++;
				err.addString("Attempting to connect to the server...", 0xFF00FF00);
			}
			if (warntime + 3000 < System.currentTimeMillis() &&  messagesent == 1){
				messagesent++;
				err.addString("Server Warning:", 0xFFFFFF00);
			}
			if (warntime + 3500 < System.currentTimeMillis() &&  messagesent == 2){
				messagesent++;
				err.addString("Engine Compromised. There's a mistake on our end.", 0xFFFFFF00);
			}
			if (warntime + 4000 < System.currentTimeMillis() &&  messagesent == 3){
				messagesent++;
				err.addString("We're working on fixing this as soon as possible.", 0xFFFFFF00);
			}
			if (warntime + 4500 < System.currentTimeMillis() &&  messagesent == 4){
				messagesent++;
				err.addString("Meanwhile, collision detection may act unexpectedly.", 0xFFFFFF00);
			}
			if (warntime + 5000 < System.currentTimeMillis() &&  messagesent == 5){
				messagesent++;
				err.addString("Reach the goal to help resolve the issue.", 0xFFFF0000);
			}
			if (warntime + 7000 < System.currentTimeMillis() && messagesent == 6){
				mp3player.changeMusic("/SOUND_main_theme.mp3");
				mp3player.play();
				stage = Stage.LV;
			}
			 
			
			break;
		case INSTRUCTIONS:
			if (mp3player.isIdle()) mp3player.play();
			if (backspace){
				backspace = false;
				stage = Stage.MENU;
				buttons.get(BN.INSTRUCTIONS).state = States.ENABLED;
				buttons.get(BN.PLAY).state = States.ENABLED;
				buttons.get(BN.QUIT).state = States.ENABLED;
				buttons.get(BN.CREDITS).state = States.ENABLED;
			}
			break;
		case CREDITS:
			if (mp3player.isIdle()) mp3player.play();
			if (backspace){
				backspace = false;
				stage = Stage.MENU;
				buttons.get(BN.INSTRUCTIONS).state = States.ENABLED;
				buttons.get(BN.PLAY).state = States.ENABLED;
				buttons.get(BN.QUIT).state = States.ENABLED;
				buttons.get(BN.CREDITS).state = States.ENABLED;
			}
			break;
		case LV:
			int mouseX = mouse.getX();
			int mouseY = mouse.getY();
			if (mp3player.isIdle()) mp3player.play();
			if (restart) restart(null);
			
			if (mouseX >= -1 && mouseY >= -1 && (currentX != mouseX || currentY != mouseY )) //when clicked
			{
				
//				soundeffects.stopMusic();
//				soundeffects.changeMusic("/Blip.mp3");
//				soundeffects.play();
				
				
				
				if(currentX == -1 && currentY == -1){ //if first time
					currentX = mouseX;
					currentY = mouseY;
				}
				
				else{ //otherwise do this on click
					clicksearch:
					for (Area[] a: levelAreas)
						for (Area area: a)
							if (area.getType() == AreaType.NODRAW && area.contains(mouse.getCurrentX(), mouse.getCurrentY())){
								Toolkit.getDefaultToolkit().beep();
								break clicksearch;
							}
				
					ball.activate();
					lines.add(currentX, currentY, mouse.getX(), mouse.getY(), true);
					currentX = mouse.getX();
					currentY = mouse.getY();
					}
			
				}
			//run all the time duringlevel
			
			for (Area[] a: levelAreas)
				for (Area area: a)
					if (area.getType() == AreaType.GOAL && area.contains((int)ball.getX(), (int)ball.getY())){ // if in the goal
						playSound("/Randomize3.wav");
						currentLevel++;
						if (currentLevel > 26){
							stage = Stage.CREDITS;
							currentLevel = 0;
						}
						lines.reset();
						readMap(currentLevel);
						currentX = -1; currentY = -1;
					}
			if(ball != null) ball.tick(lines, levelAreas);
			//Super secret level passing technique
			if (skip1 && skip2){
				System.out.println("Skip!");
				skip1 = false;
				skip2 = false;
				skip3 = false;
				currentLevel++;
				if (currentLevel > 26){
					stage = Stage.CREDITS;
					currentLevel = 0;
				}
				lines.reset();
				readMap(currentLevel);
				currentX = -1; currentY = -1;
			}
			if (skip1 && skip3){
				System.out.println("Skip back!");
				skip1 = false;
				skip2 = false;
				skip3 = false;
				currentLevel=currentLevel - 2;
				lines.reset();
				readMap(currentLevel);
				currentX = -1; currentY = -1;
			}
			
			break;
		//if(ball != null) ball.tick();
		}
		if(fj != null && !fj.isDone()) fj.tick();
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
		if (currentX != -1 && currentY != -1){
			restart = false;
			lines.reset();
			readMap(currentLevel);
			currentX = -1; currentY = -1;
			mistakes++;
		}
//		stage = Stage.MENU;
//		hideAll();
//		Debug.out(Out.WARNING, "game.Game", "Error: " + reason);
//		err.addString(reason, 0xFFFF0000);
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

	public void readMap(int level){
		for (int i = 0; i < 18; i++)
			for (int j = 0; j < 32; j++){
				Area a = new Area(AreaType.getAreaType(new Color(mapimg.getRGB(j, i+(18*currentLevel)))), j*30, i*30, 30, 30);
				levelAreas[i][j] = a;
				if (a.getType() == AreaType.BALL)
					ball = new Ball(j*30 + 15, i*30 + 15,radius);
				if (a.getType() == AreaType.PLATFORM){
					lines.add(j*30   , i*30   , j*30+30, i*30, false);
					lines.add(j*30   , i*30   , j*30   , i*30+30, false);
					lines.add(j*30   , i*30+30, j*30+30, i*30+30, false);
					lines.add(j*30+30, i*30   , j*30+30, i*30+30, false);
				}
			}
	}
	
	public void playSound(String fileName) 
   {
        try {
            InputStream yourFile = Game.class.getResourceAsStream(fileName);
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;
            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        }
        catch (Exception e) {
        	e.printStackTrace();
            System.out.println("failed to play sound: " + fileName);
        }
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
		
		try {
		    mapimg = ImageIO.read(Game.class.getResourceAsStream("/map.png"));
		} catch (IOException e) { e.printStackTrace();}

		
		new InputHandler(this);
		new WindowHandler(this);
		frame.setVisible(true); // After init, show the frame.
		final int BUTTON_WIDTH = 10;
		final int BUTTON_HEIGHT = 4;
		final int BUTTON_PIX_WIDTH = 9;
		final int BUTTON_PIX_HEIGHT = 9;
		
		
		mp3player.changeMusic("/SOUND_menu_theme.mp3"); mp3player.play();
		
		buttons.add(new Button(screen, Game.WIDTH/2 - (18/2)*BUTTON_PIX_WIDTH, (Game.HEIGHT / 2)-75, 
				17, 4, "/button_disabled.png", "/button_enabled.png", "/button_pressed.png") , BN.PLAY);
		buttons.get(BN.PLAY).text = "Play!";
		buttons.get(BN.PLAY).state = Button.States.ENABLED;
		
		buttons.add(new Button(screen, (Game.WIDTH/2) - (18/2)*BUTTON_PIX_WIDTH, (Game.HEIGHT / 2)-75 + 4*45,
				17, 4, "/button_disabled.png", "/button_enabled.png", "/button_pressed.png") , BN.QUIT);
		buttons.get(BN.QUIT).text = "Quit";
		buttons.get(BN.QUIT).state = Button.States.ENABLED;
		
		
		buttons.add(new Button(screen, Game.WIDTH/2 - (18/2)*BUTTON_PIX_WIDTH, (Game.HEIGHT / 2) -75+4*15, 
				17, 4, "/button_disabled.png", "/button_enabled.png", "/button_pressed.png") , BN.INSTRUCTIONS);
		buttons.get(BN.INSTRUCTIONS).text = "Instructions";
		buttons.get(BN.INSTRUCTIONS).state = Button.States.ENABLED;
		
		buttons.add(new Button(screen, (Game.WIDTH/2) - (18/2)*BUTTON_PIX_WIDTH, (Game.HEIGHT / 2)-75 + 4*30,
				17, 4, "/button_disabled.png", "/button_enabled.png", "/button_pressed.png") , BN.CREDITS);
		buttons.get(BN.CREDITS).text = "Credits";
		buttons.get(BN.CREDITS).state = Button.States.ENABLED;

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
		
		/** Levels .*/
		LV,

		/**
		 * Instruction menu
		 */
		INSTRUCTIONS,
		
		/**
		 * Credits
		 */
		CREDITS,
		
		/**
		 * Credits
		 */
		WARNING,
	}

	/**
	 * Available button names (Unique)(Self explanatory).
	 * @author AJ
	 *
	 */
	public enum BN {
		PLAY, QUIT, INSTRUCTIONS, CREDITS;
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

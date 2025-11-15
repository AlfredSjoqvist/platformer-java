package se.liu.alfsj019.main;

import se.liu.alfsj019.game_state.GameStateManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.logging.Logger;

import javax.swing.*;

/**
 * The canvas on which the game runs which handles in game time,
 * forwards key inputs and renders the display.
 */
public class GameCanvas extends JPanel implements Runnable, KeyListener
{
    /**
     * Thread fields
     */
    private Thread thread = null;
    private volatile boolean running;
    private static final int FPS = 60;
    private static final long TIME_TARGET = 1000 / FPS;
    private long frames = 0;


    /**
     * This is the height of the screen which has to be accessed by
     * numerous other classes which handles the display.
     */
    public static final int HEIGHT = 540;

    /**
     * This is the width of the screen which also has to be accessed by
     * numerous other classes which handles the display.
     */
    public static final int WIDTH = 940;

    /**
     * Zoom factor
     */
    private static final int ZOOM_FACTOR = 2;

    /**
     * Image fields
     */
    private BufferedImage image = null;
    private Graphics2D graphic = null;

    /**
     * Game state manager
     */
    private GameStateManager gsm = null;

    public GameCanvas() {
	super();

	/**
	 * Set the size of the game window in accordance
	 * with the constants that was defined above.
	 */
	setPreferredSize(new Dimension(WIDTH * ZOOM_FACTOR, HEIGHT * ZOOM_FACTOR));
	setFocusable(true);
	requestFocusInWindow();
    }

    /**
     * Add thread and keylistener to class.
     */
    public void addNotify() {
	super.addNotify();

	if (thread == null) {
	    thread = new Thread(this);
	    addKeyListener(this);
	    thread.start();
	}
    }

    /**
     * Initialize the game state.
     */
    private void init() {
	image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    	running = true;
	graphic = (Graphics2D) image.getGraphics();
	gsm = new GameStateManager();
    }


    @Override public void run() {

	init();

	Logger logger = Logger.getLogger(getClass().getSimpleName());
	FileHandlerCreator.attachFileHandler(logger, getClass().getSimpleName());

	/**
	 * Main game loop
	 */
	while(running) {
	    frames++;
	    long timeStampStart = System.nanoTime();

	    /**
	     * Update screen
	     */
	    update();
	    render();
	    renderDisplay();

	    /**
	     * Update timers
	     */
	    long timeElapsed = System.nanoTime() - timeStampStart;

	    long timeDelayDuration = TIME_TARGET - timeElapsed / 1000000;

	    if (timeDelayDuration < 0) timeDelayDuration = 5;

	    try {
		/**
		 * This is the timing loop that adjusts the number of frames painted
		 * per second in the game. The code inspection reacts on the line below
		 * but this is a false positive.
		 */
		Thread.sleep(timeDelayDuration);
	    } catch (InterruptedException e) {
		logger.log(logger.getLevel(), "Game loop timing not working properly", e);
		signalGameLoopError();
		e.printStackTrace();
	    }

	}
    }

    public void signalGameLoopError() {
	if (JOptionPane.showConfirmDialog(null, "The game loop timing is not working properly, continue playing anyways?", "Error", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
	    System.exit(1);
	}
    }

    private void update() {
	gsm.update();
    }

    private void render() {
	gsm.draw(graphic);
    }

    /**
     * Create the graphics object and render the game image upon it
     */
    private void renderDisplay() {
	Graphics g2 = getGraphics();
	g2.drawImage(image, 0 , 0, WIDTH*ZOOM_FACTOR, HEIGHT*ZOOM_FACTOR, null);
	g2.dispose();
    }

    /**
     * Key listeners:
     * @param keyEvent the event to be processed
     */
    @Override public void keyTyped(final KeyEvent keyEvent) {

    }

    @Override public void keyPressed(final KeyEvent keyEvent) {
	gsm.keyPressed(keyEvent.getKeyCode());
    }

    @Override public void keyReleased(final KeyEvent keyEvent) {
	gsm.keyReleased(keyEvent.getKeyCode());
    }


}
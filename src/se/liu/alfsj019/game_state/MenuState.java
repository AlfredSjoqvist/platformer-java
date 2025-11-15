package se.liu.alfsj019.game_state;

import se.liu.alfsj019.tile_map.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the menu screen
 * Contains functionality for choosing between buttons and confirming choice.
 * <p>
 * OBS!
 * The code inspection yields a SequientalConstants warning for this class.
 * These are used to avoid magic numbers in the button selection function
 * to improve the readability of the code. I do not think it is appropriate
 * to use an enum instead in this case because it will not improve the code.
 */
public class MenuState extends GameState
{
    private int currentChoice = 0;
    private String[] buttons = {
	    "New Game",
	    "Load Game",
	    "Settings",
	    "Quit",
    };

    private Color titleColor = new Color(0, 0, 0);
    private Font titleFont = new Font("Century Gothic", Font.PLAIN, 28);
    private Font font = new Font("Arial", Font.PLAIN, 12);
    private Point titleTextPosition = new Point(80, 70);
    private Point titleButtonPosition = new Point(145, 140);
    private int frameCounter = 0;
    private final static int NEW_GAME_CHOICE = 0;
    private final static int LOAD_GAME_CHOICE = 1;
    private final static int SETTINGS_CHOICE = 2;
    private final static int QUIT_GAME_CHOICE = 3;
    private static final int TOTAL_NUMBER_OF_FRAMES = 26;
    private final List<Background> backGroundFrames;


    public MenuState(GameStateManager gsm) {

	setGameStateManager(gsm);

	backGroundFrames = new ArrayList<>();
	initBackgrounds();

	setBackground(new Background("images" + File.separator + "menu_background" + File.separator + "frame_0.gif", 1));
	background.setVector(0, 0);

    }

    public void initBackgrounds() {

	for (int frame = 0; frame < TOTAL_NUMBER_OF_FRAMES; frame++) {
	    String frameString = Integer.toString(frame);
	    String filePathString = "images" + File.separator + "menu_background" + File.separator + "frame_" + frameString + ".gif";
	    backGroundFrames.add(new Background(filePathString, 1));
	}

    }

    /**
     * Updates the menu state.
     */
    public void update() {
	background.update();
    }

    /**
     * Draws the menu state.
     *
     * @param g The Graphics2D object to draw on.
     */
    public void draw(Graphics2D g) {

	final double frameSpeedFactor = 0.35;

	int frame = (int)(frameCounter * frameSpeedFactor) % TOTAL_NUMBER_OF_FRAMES;
	frameCounter++;

	setBackground(backGroundFrames.get(frame));

	/**
	 * Render background GIF
	 */
	background.draw(g);

	/**
	 * Render title text
	 */
	g.setColor(titleColor);
	g.setFont(titleFont);
	g.drawString("Thrones", (int)titleTextPosition.getX(), (int)titleTextPosition.getY());

	/**
	 * Render menu buttons
	 */
	g.setFont(font);
	for(int i = 0; i < buttons.length; i++) {
	    if(i == currentChoice) {
		g.setColor(Color.WHITE);
	    } else {
		g.setColor(Color.BLACK);
	    }
	    final int titleButtonOffset = 15;
	    g.drawString(buttons[i],
			 (int)titleButtonPosition.getX(),
			 (int)titleButtonPosition.getY() + i * titleButtonOffset);
	}
    }

    /**
     * Handles the selection of menu options.
     */
    private void select() {

	switch (currentChoice) {
	    case NEW_GAME_CHOICE:
		/**
		 * New game
		 */
		this.gameStateManager.setCurrentState(1);
		break;
	    case LOAD_GAME_CHOICE:
		/**
		 * Load game
		 */
		break;
	    case SETTINGS_CHOICE:
		/**
		 * Settings
		 */
		break;
	    case QUIT_GAME_CHOICE:
		/**
		 * Quit game
		 */
		System.exit(0);
		break;
	    default:
		break;

	}
    }

    /**
     * Handles the key press events in the menu state.
     *
     * @param k The ID of the key pressed.
     */
    public void keyPressed(int k) {

	if (k == KeyEvent.VK_ENTER) {
	    select();
	}

	if (k == KeyEvent.VK_UP) {
	    currentChoice--;
	    if (currentChoice == -1) {
		currentChoice = buttons.length - 1;
	    }
	}

	if(k == KeyEvent.VK_DOWN) {
	    currentChoice++;
	    if(currentChoice == buttons.length) {
		currentChoice = 0;
	    }
	}
    }

    public void keyReleased(int k) {}
}



package se.liu.alfsj019.tile_map;

import se.liu.alfsj019.main.FileHandlerCreator;
import se.liu.alfsj019.main.GameCanvas;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * The Background class represents a background image in the game.
 * It handles loading the image, setting its position and movement, and rendering it.
 */
public class Background
{
    private BufferedImage image = null;
    private double x;
    private double y;
    private double dx;
    private double dy;
    private double moveScale;

    public Background(String s, double ms) {

	Logger logger = Logger.getLogger(getClass().getSimpleName());
	FileHandlerCreator.attachFileHandler(logger, getClass().getSimpleName());

	try {

	    URL imageResource = ClassLoader.getSystemResource(s);

	    if (imageResource == null) {
		signalSpriteError();
		logger.log(logger.getLevel(), "Background image not found", new NullPointerException("GetSystemResource returned null"));
	    } else {
		image = ImageIO.read(imageResource);
		/**
		 * ImageIO.read() returns null if the file does not exist so
		 * the below if-statement checks for this error.
		 */
		if (image == null) {
		    signalSpriteError();
		    logger.log(logger.getLevel(), "Background image not found", new NullPointerException("ImageIO.read returned null"));
		}
	    }

	    moveScale = ms;

	} catch (IOException e) {
	    /**
	     * OBS! README - CatchFallThrough in the root folder
	     */
	    logger.log(logger.getLevel(), "Background image not found", e);
	    signalSpriteError();
	    e.printStackTrace();
	}
    }

    private void signalSpriteError() {

	if (JOptionPane.showConfirmDialog(null, "Background image not found, continue anyway?", "Error", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
	    System.exit(1);
	}
	image = new BufferedImage(10000, 10000, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Sets the position of the background image.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public void setPosition(double x, double y) {
	this.x = (x * moveScale) % GameCanvas.WIDTH;
	this.y = (y * moveScale) % GameCanvas.HEIGHT;
    }

    /**
     * Sets the movement vector of the background.
     *
     * @param dx The x-component of the movement vector.
     * @param dy The y-component of the movement vector.
     */
    public void setVector(double dx, double dy) {
	this.dx = dx;
	this.dy = dy;
    }

    /**
     * Updates the background's position based on the movement vector.
     */
    public void update() {
	x += dx;
	y += dy;
    }

    /**
     * Renders the background image onto the specified Graphics2D object.
     *
     * @param g The Graphics2D object to render onto.
     */
    public void draw(Graphics2D g) {

	g.drawImage(image, (int)x, (int)y, GameCanvas.WIDTH, GameCanvas.HEIGHT, null);

	if(x != 0) {
	    int xPos = (int)x + (x < 0 ? GameCanvas.WIDTH : -GameCanvas.WIDTH);
	    g.drawImage(
		    image,
		    xPos,
		    (int)y,
		    GameCanvas.WIDTH,
		    GameCanvas.HEIGHT,
		    null
	    );
	}
    }
}

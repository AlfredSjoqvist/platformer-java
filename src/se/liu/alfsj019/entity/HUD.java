
package se.liu.alfsj019.entity;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import se.liu.alfsj019.main.FileHandlerCreator;

/**
 * Represents the Heads-Up Display (HUD) in the game.
 */
public class HUD
{
    private BufferedImage emptyBarSprite = null;
    private BufferedImage fullBarSprite = null;
    private Font font;
    private Point barSpritePosition = new Point(10, 10);


    public HUD() {
	font = new Font("Arial", Font.PLAIN, 14);

	Logger logger = Logger.getLogger(getClass().getSimpleName());
	FileHandlerCreator.attachFileHandler(logger, getClass().getSimpleName());

	try {
	    URL fullBarSpriteResource = ClassLoader.getSystemResource("images/Bars/health_full.png");
	    URL emptyBarSpriteResource = ClassLoader.getSystemResource("images/Bars/health_empty.png");


	    if (fullBarSpriteResource == null || emptyBarSpriteResource == null) {
		signalSpriteError();
		logger.log(logger.getLevel(), "Health bar sprite not found", new NullPointerException("Health bar sprite loaded as null"));
	    } else {
		fullBarSprite = ImageIO.read(fullBarSpriteResource);
		emptyBarSprite = ImageIO.read(emptyBarSpriteResource);
		/**
		 * ImageIO.read() returns null if the file does not exist so
		 * the below if-statement checks for this error.
		 */
		if (fullBarSprite == null || emptyBarSprite == null) {
		    logger.log(logger.getLevel(), "Health bar sprite not found", new NullPointerException("Health bar sprite loaded as null"));
		}
	    }
	} catch (IOException e) {
	    /**
	     * OBS! README - CatchFallThrough in the root folder
	     */
	    logger.log(logger.getLevel(), "Health bar sprite not found", e);
	    signalSpriteError();
	    e.printStackTrace();
	}
    }

    private void signalSpriteError() {

	if (JOptionPane.showConfirmDialog(null, "Health bar sprite not found, continue anyway?", "Error", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
	    System.exit(1);
	}
	fullBarSprite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
	emptyBarSprite = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Draws the HUD on the specified graphics context.
     *
     * @param g the graphics context on which to draw the HUD
     */
    public void draw(Graphics2D g) {
	g.drawImage(emptyBarSprite, (int) barSpritePosition.getX(), (int) barSpritePosition.getY(), null);
	g.drawImage(fullBarSprite, (int) barSpritePosition.getX(), (int) barSpritePosition.getY(), null);
	g.setFont(font);
    }


}

package se.liu.alfsj019.entity.enemies;

import se.liu.alfsj019.entity.Animation;
import se.liu.alfsj019.entity.Enemy;
import se.liu.alfsj019.entity.ObjectState;
import se.liu.alfsj019.main.FileHandlerCreator;
import se.liu.alfsj019.tile_map.TileMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The Yellow enemy class.
 * An enemy that walks forward until it hits a wall and then turns around and walks the other way.
 */
public class Yellow extends Enemy
{

    public Yellow(TileMap tm) {

	super(tm);

	Logger logger = Logger.getLogger(getClass().getSimpleName());
	FileHandlerCreator.attachFileHandler(logger, getClass().getSimpleName());

	setMoveSpeed(1);
	setMaxSpeed(1);
	setFallSpeed(0.2);
	setMaxFallSpeed(10.0);

	setWidth(34);
	setHeight(34);
	setCollisionBoxWidth(20);
	setCollisionBoxHeight(34);

	setHealth(2);
	List<BufferedImage[]> sprites = new ArrayList<>();

	/**
	 * MAGIC NUMBERS:
	 * This array of magic numbers indicate the number of frames
	 * for the animations of the different states in order
	 */
	final int[] numberOfFrames = {
		4, 6, 6, 4, 6
	};

	/**
	 * Load the sprites
	 */
	for(int i = 0; i < numberOfFrames.length; i++) {
	    BufferedImage[] bi = new BufferedImage[numberOfFrames[i]];
	    for(int j = 0; j < numberOfFrames[i]; j++) {
		try {
		    Map<Integer, String> pathMap = Map.of(0, "Idle", 1, "Walk", 2, "Attack", 3, "Hurt", 4, "Death");
		    URL spriteSheetResource = ClassLoader.getSystemResource("images/Enemy/" + pathMap.get(i) + ".png");
		    BufferedImage spriteSheet;
		    if (spriteSheetResource == null) {
			spriteSheet = createDefaultSprite();
		    } else {
			spriteSheet = ImageIO.read(spriteSheetResource);
			/**
			 * ImageIO.read() returns null if the file does not exist so
			 * the below if-statement checks for this error.
			 */
			if (spriteSheet == null) {
			    spriteSheet = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
			    logger.log(logger.getLevel(), "Yellow enemy sprite frame not found", new NullPointerException("Frame not found"));
			}
		    }

		    bi[j] = spriteSheet.getSubimage(
			    j * width,
			    0,
			    width,
			    height
		    );

		} catch (IOException e) {
		    /**
		     * OBS! README - CatchFallThrough in the root folder
		     */
		    logger.log(logger.getLevel(), "Yellow enemy sprite not found", e);
		    createDefaultSprite();
		    e.printStackTrace();
		}
	    }
	    sprites.add(bi);
	}

	setAnimation(new Animation());
	setCurrentAction(ObjectState.WALKING);
	final int walkingID = 1;
	animation.setFrames(sprites.get(walkingID));
	animation.setDelay(75);

	setMoveRight(true);
	setFacingRight(false);

    }

    private BufferedImage createDefaultSprite() {
	if (JOptionPane.showConfirmDialog(null, "Yellow enemy sprite not found, continue anyway?", "Error", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
	    System.exit(1);
	}
	return new BufferedImage(3000, 3000, BufferedImage.TYPE_INT_ARGB);

    }

    /**
     * Moves the enemy to the next position based on its movement.
     */
    private void moveToNextPosition() {


	if (moveLeft || moveRight) {
	    int direction = moveLeft ? -1 : 1;
	    dx += direction * moveSpeed;
	    dx = Math.max(-maxSpeed, Math.min(maxSpeed, dx));
	}

	if (falling) {
	    dy += fallSpeed;
	}
    }

    /**
     * Updates the enemy.
     */
    public void update() {

	if (isNotOnScreen()) {return;}

	moveToNextPosition();
	calculateCollisions();
	setPosition(xCurrent, yCurrent);

	if (flinching) {
	    final int timeDenominator = 1000000;
	    long timeElapsed = (System.nanoTime() - flinchingTimer) / timeDenominator;
	    final int flinchDuration = 400;
	    if (timeElapsed > flinchDuration) {
		flinching = false;
	    }
	}

	if (moveRight && dx == 0) {
	    moveRight = false;
	    moveLeft = true;
	    facingRight = true;
	} else if (moveLeft && dx == 0) {
	    moveRight = true;
	    moveLeft = false;
	    facingRight = false;
	}

	animation.update();

    }

    /**
     * Draws the enemy
     *
     * @param g the graphics context on which to draw the map object
     */
    public void draw(Graphics2D g) {

	setMapPosition();

	super.draw(g);
    }


}


package se.liu.alfsj019.entity;

import se.liu.alfsj019.main.GameCanvas;
import se.liu.alfsj019.tile_map.TileMap;
import se.liu.alfsj019.tile_map.TileType;

import java.awt.*;

/**
 * Represents an object within the game map which is not a tile.
 * All objects that can be moved or interacted with in some way inherits from this class.
 * <p>
 * OBS!
 * This class contains a lot of fields and I consider this to be a necessary outcome
 * because of the large amounts of information that has to be contained in this class.
 */
public abstract class MapObject
{
    /**
      * Tile properties
      */
    protected TileMap tileMap;
    protected int tileSize;
    protected double xMap;
    protected double yMap;

    /**
     * Position properties
     */
    protected Point position;
    protected double dx;
    protected double dy;

    /**
     * Dimension properties
     */
    protected int width;
    protected int height;

    /**
     * Collision properties
     */
    protected int collisionBoxWidth;
    protected int collisionBoxHeight;
    protected int currentRow;
    protected int currentCol;
    protected double xDestination;
    protected double yDestination;
    protected double xCurrent;
    protected double yCurrent;

    /**
     * The code inspection reacts on the similarity of the 4 field names below
     * and thinks I should combine them. The reason I chose not to anyway is because
     * the collision handler would be unnecessarly complex and less readable.
     */
    protected boolean topLeftBlocked;
    protected boolean topRightBlocked;
    protected boolean bottomLeftBlocked;
    protected boolean bottomRightBlocked;

    /**
     * Animation properties
     */
    protected Animation animation = null;
    protected ObjectState currentAction = null;
    protected boolean facingRight;

    /**
     * Movement properties
     */
    protected boolean jumping;
    protected boolean falling;
    protected double moveSpeed;
    protected double maxSpeed;
    protected double stopSpeed;
    protected double fallSpeed;
    protected double maxFallSpeed;
    protected double jumpStart;
    protected double stopJumpSpeed;

    /**
     * Similarly the code inspection thinks I should combine the 3 fields below into
     * one by creating an enum for example but I chose not to because it would also
     * unneccesarily increase complexity.
     */
    protected boolean moveLeft;
    protected boolean moveRight;
    protected boolean moveDown;

    protected MapObject (TileMap tm) {
	tileMap = tm;
	tileSize = tm.getTileSize();
	position = new Point(0, 0);
    }


    /**
     * Returns the rectangular bounds of this map object.
     *
     * @return the rectangle representing the bounds of this map object
     */

    /**
     * Calculates the blocked status of the four corners of this map object.
     *
     * @param x the x-coordinate to calculate corners from
     * @param y the y-coordinate to calculate corners from
     */
    public void calculateCorners(double x, double y) {
	int leftTile = (int)(x - collisionBoxWidth / 2) / tileSize;
	int rightTile = (int)(x + collisionBoxWidth / 2 - 1) / tileSize;
	int topTile = (int)(y - collisionBoxHeight / 2) / tileSize;
	int bottomTile = (int)(y + collisionBoxHeight / 2 - 1) / tileSize;

	TileType topLeftType = tileMap.getCollisionType(topTile, leftTile);
	TileType topRightType = tileMap.getCollisionType(topTile, rightTile);
	TileType bottomLeftType = tileMap.getCollisionType(bottomTile, leftTile);
	TileType bottomRightType = tileMap.getCollisionType(bottomTile, rightTile);


	topLeftBlocked = topLeftType == TileType.BLOCKED;
	topRightBlocked = topRightType == TileType.BLOCKED;
	bottomLeftBlocked = bottomLeftType == TileType.BLOCKED;
	bottomRightBlocked = bottomRightType == TileType.BLOCKED;
    }

    /**
     * Calculates the collisions between this map object and the tile map.
     */
    public void calculateCollisions() {
	currentCol = (int) position.getX() / tileSize;
	currentRow = (int) position.getY() / tileSize;

	xDestination = position.getX() + dx;
	yDestination = position.getY() + dy;

	xCurrent = position.getX();
	yCurrent = position.getY();

	calculateCorners(position.getX(), yDestination);
	if (dy < 0) {
	    if(topLeftBlocked || topRightBlocked) {
		dy = 0;
		yCurrent = currentRow * tileSize + collisionBoxHeight / (float)2;
	    } else {
		yCurrent += dy;
	    }
	}

	if (dy > 0) {
	    if(bottomLeftBlocked || bottomRightBlocked) {
		dy = 0;
		falling = false;
		yCurrent = (currentRow + 1) * tileSize - collisionBoxHeight / (float)2;
	    } else {
		yCurrent += dy;
	    }
	}

	calculateCorners(xDestination, position.getY());


	boolean leftMovementBlocked = dx < 0 && (topLeftBlocked || bottomLeftBlocked);
	boolean rightMovementBlocked = dx > 0 && (topRightBlocked || bottomRightBlocked);

	if (leftMovementBlocked || rightMovementBlocked) {
	    dx = 0;
	    xCurrent = (leftMovementBlocked ? currentCol : (currentCol + 1)) * tileSize + (leftMovementBlocked ? collisionBoxWidth / 2.0 : -collisionBoxWidth / 2.0);
	} else {
	    xCurrent += dx;
	}

	if (!falling) {
	    calculateCorners(position.getX(), yDestination + 1);
	    if (!bottomLeftBlocked && !bottomRightBlocked) {
		falling = true;
	    }
	}

    }

    public Point getPosition() {
	return position;
    }

    public void setPosition(double x, double y) {
	this.position.setLocation(x, y);
    }

    public void setMapPosition() {
	this.xMap = tileMap.getPosition().getX();
	this.yMap = tileMap.getPosition().getY();
    }

    /**
     * The 3 methods below are marked as RelatedMethodNames by the code inspection.
     * I think it is relevant to separate the fields as stated by comments higher up
     * in the file.
     */
    public void setMoveLeft(final boolean moveLeft) {
	this.moveLeft = moveLeft;
    }

    public void setMoveRight(final boolean moveRight) {
	this.moveRight = moveRight;
    }

    public void setMoveDown(final boolean moveDown) {
	this.moveDown = moveDown;
    }



    public void setJumping(final boolean jumping) {
	this.jumping = jumping;
    }

    public void setWidth(final int width) {
	this.width = width;
    }

    public void setHeight(final int height) {
	this.height = height;
    }

    public void setCollisionBoxWidth(final int collisionBoxWidth) {
	this.collisionBoxWidth = collisionBoxWidth;
    }

    public void setCollisionBoxHeight(final int collisionBoxHeight) {
	this.collisionBoxHeight = collisionBoxHeight;
    }

    public void setAnimation(final Animation animation) {
	this.animation = animation;
    }

    public void setCurrentAction(final ObjectState currentAction) {
	this.currentAction = currentAction;
    }

    public void setFacingRight(final boolean facingRight) {
	this.facingRight = facingRight;
    }

    public void setMoveSpeed(final double moveSpeed) {
	this.moveSpeed = moveSpeed;
    }

    public void setJumpStart(final double jumpStart) {
	this.jumpStart = jumpStart;
    }

    public void setMaxSpeed(final double maxSpeed) {
	this.maxSpeed = maxSpeed;
    }

    public void setStopSpeed(final double stopSpeed) {
	this.stopSpeed = stopSpeed;
    }

    public void setFallSpeed(final double fallSpeed) {
	this.fallSpeed = fallSpeed;
    }

    public void setMaxFallSpeed(final double maxFallSpeed) {
	this.maxFallSpeed = maxFallSpeed;
    }

    public void setStopJumpSpeed(final double stopJumpSpeed) {
	this.stopJumpSpeed = stopJumpSpeed;
    }

    /**
     * Check if the map object is on the screen currently
     */
    public boolean isNotOnScreen() {
	return position.getX() + xMap + width < 0 ||
	       position.getX() + xMap - width > GameCanvas.WIDTH ||
	       position.getY() + yMap + height < 0 ||
	       position.getY() + yMap - height > GameCanvas.HEIGHT;
    }

    /**
     * Draws the map object on the specified graphics context.
     *
     * @param g the graphics context on which to draw the map object
     */
    public void draw(Graphics2D g) {

	if (facingRight) {
	    g.drawImage(animation.getImage(),
			(int)(position.getX() + xMap - width / 2),
			(int)(position.getY() + yMap - height / 2),
			null);
	} else {
	    g.drawImage(animation.getImage(),
			(int)(position.getX() + xMap - width / 2 + width),
			(int)(position.getY() + yMap - height / 2),
			-width,
			height,
			null);
	}
    }
}

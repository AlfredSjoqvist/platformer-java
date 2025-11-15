package se.liu.alfsj019.entity;

import se.liu.alfsj019.main.FileHandlerCreator;
import se.liu.alfsj019.tile_map.TileMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;
import java.util.List;

/**
 * Represents the playable character in the game.
 * This is a lumberjack that can run, jump, roll and swing his axe.
 */
public class Player extends MapCreature
{

    /**
     * Player properties
     */
    private boolean running;
    private double runSpeed;
    private double maxRunSpeed;


    /**
     * Melee attack properties
     */
    private boolean meleeing;
    private int meleeDamage;
    private int meleeRange;


    /**
     * Animation properties
     */
    private List<BufferedImage[]> sprites;

    /**
     * MAGIC NUMBERS:
     * The magic numbers in the map represents the ID:s of the ObjectStates
     * which are used for referring to the correct index in the array that
     * contains the animation frames.
     */
    private Map<ObjectState, Integer> stateIntegerMap = Map.ofEntries(Map.entry(ObjectState.IDLE, 0), Map.entry(ObjectState.WALKING, 1),
                                                                      Map.entry(ObjectState.RUNNING, 2), Map.entry(ObjectState.JUMPING, 3),
                                                                      Map.entry(ObjectState.MELEEING, 4), Map.entry(ObjectState.HURTING, 5),
                                                                      Map.entry(ObjectState.DYING, 6), Map.entry( ObjectState.FALLING,7),
                                                                      Map.entry(ObjectState.WALK_MELEEING, 8), Map.entry(ObjectState.RUN_MELEEING, 9),
                                                                      Map.entry(ObjectState.ROLLING, 10), Map.entry(ObjectState.CROUCHING, 11));


    public Player(TileMap tm) {
        super(tm);

        Logger logger = Logger.getLogger(getClass().getSimpleName());
        FileHandlerCreator.attachFileHandler(logger, getClass().getSimpleName());

        setHeight(48);
        setWidth(48);
        setCollisionBoxWidth(24);
        setCollisionBoxHeight(48);

        setMoveSpeed(0.3);
        runSpeed = 0.5;
        maxRunSpeed = 2.6;
        setMaxSpeed(1.6);
        setStopSpeed(0.4);
        setFallSpeed(0.15);
        setMaxFallSpeed(4.0);
        setJumpStart(-7.8);
        setStopJumpSpeed(0.3);

        setFacingRight(true);
        setHealth(5);

        meleeDamage = 8;
        meleeRange = 40;
        sprites = new ArrayList<>();

        /**
         * MAGIC NUMBERS:
         * This array of magic numbers indicate the number of frames
         * for the animations of the different states in order
         */
        int[] numFrames = { 4, 6, 6, 4, 6, 3, 6, 1, 6, 6, 6, 3 };

        for(int i = 0; i < numFrames.length; i++) {
            BufferedImage[] bi = new BufferedImage[numFrames[i]];
            for(int j = 0; j < numFrames[i]; j++) {
                try {
                    /**
                     * MAGIC NUMBERS:
                     * This array of magic numbers represents
                     * the corresponding index for each folder path.
                     */
                    Map<Integer, String> pathMap =
                            Map.ofEntries(Map.entry(0, "idle"), Map.entry(1, "walk"), Map.entry(2, "run"), Map.entry(3, "jump"),
                                          Map.entry(4, "melee"), Map.entry(5, "hurt"), Map.entry(6, "death"), Map.entry(7, "fall"),
                                          Map.entry(8, "walk_melee"), Map.entry(9, "run_melee"), Map.entry(10, "roll"),
                                          Map.entry(11, "crouch"));
                    BufferedImage spriteSheet = ImageIO.read(
                            ClassLoader.getSystemResource("images/Lumberjack/" + pathMap.get(i) + ".png")
                    );
                    /**
                     * ImageIO.read() returns null if the file does not exist so
                     * the below if-statement checks for this error.
                     */
                    if (spriteSheet == null) {
                        spriteSheet = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
                        logger.log(logger.getLevel(), "Player sprite frame not found", new NullPointerException("Frame not found"));
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
                    logger.log(logger.getLevel(), "Player sprite not found", e);
                    createDefaultSprite();
                    e.printStackTrace();
                }
            }
            sprites.add(bi);
        }

        setAnimation(new Animation());
        setCurrentAction(ObjectState.IDLE);
        animation.setFrames(sprites.get(stateIntegerMap.get(ObjectState.IDLE)));
        animation.setDelay(200);

    }

    private BufferedImage createDefaultSprite() {
        if (JOptionPane.showConfirmDialog(null, "Player sprite not found, continue anyway?", "Error", JOptionPane.YES_NO_OPTION) ==
            JOptionPane.NO_OPTION) {
            System.exit(1);
        }
        return new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
    }

    public void setMeleeing() {
        meleeing = true;
    }

    public void setRunning(boolean r) {
        running = r;
    }

    /**
     * Deals melee damage to enemies within the player's melee range.
     *
     * @param enemies the list of enemies to check for melee damage.
     */
    public void dealMeleeDamage(List<Enemy> enemies) {

        for (Enemy enemy : enemies) {

            if (meleeing) {
                if (ifEnemyStruck(enemy)) {
                    enemy.hit(meleeDamage);
                }
            }
        }
    }

    /**
     * Check if a specfic enemy was struck.
     * @param enemy
     * @return true if an enemy was struck, if not return false.
     */
    private boolean ifEnemyStruck(final Enemy enemy) {

        /**
         * I believe the repeated expression warning in the code inspection here
         * is a false positive since the all the booleans check for different things.
         */
        return (facingRight
                && enemy.getPosition().getX() > position.getX()
                && enemy.getPosition().getX() < position.getX() + meleeRange
                && enemy.getPosition().getY() > position.getY() - height / 2.0
                && enemy.getPosition().getY() < position.getY() + height / 2.0)
                || !facingRight
                   && enemy.getPosition().getX() < position.getX()
                   && enemy.getPosition().getX() > position.getX() - meleeRange
                   && enemy.getPosition().getY() > position.getY() - height / 2.0
                   && enemy.getPosition().getY() < position.getY() + height / 2.0;
    }

    /**
     * Updates the position of the player based on key inputs.
     */
    private void updatePosition() {

        /**
         * === Movement ===
         */

        /**
         * Calculate the velocity change based on the running state
         */
        double speedChange = (running ? runSpeed : moveSpeed) * (moveLeft ? -1 : 1);

        /**
         * Ensure the speed is within the limit
         */
        double maxSpeedLimit = (running ? maxRunSpeed : maxSpeed) * (moveLeft ? -1 : 1);

        if ((moveLeft && dx < maxSpeedLimit) || (moveRight && dx > maxSpeedLimit)) {
            dx = maxSpeedLimit;
        }
        /**
         * Adjust speed based on movement status
         */
        else if (!moveLeft && !moveRight) {
            if (dx > 0) {
                dx = Math.max(0, dx - stopSpeed);
            } else if (dx < 0) {
                dx = Math.min(0, dx + stopSpeed);
            }
        } else {
            dx += speedChange;
        }

        /**
         * Jumping
         */
        if (jumping && !falling) {
            dy = jumpStart;
            falling = true;
        }

        /**
         * Falling
         */
        if (falling) {

            dy += fallSpeed;

            if (dy > 0) {
                jumping = false;
            }

            if (dy < 0 && !jumping) dy += stopJumpSpeed;

            if (dy > maxFallSpeed) dy = maxFallSpeed;

        }
    }


    private void setCurrentAnimation(ObjectState newAction, int delay, boolean stopAfterPlayed) {
        if (currentAction != newAction) {
            currentAction = newAction;
            animation.setFrames(sprites.get(stateIntegerMap.get(newAction)));
            animation.setDelay(delay);
            animation.setStopAfterPlayedOnce(stopAfterPlayed);
        }
    }

    public void update() {

        /**
         * Position update
         */
        updatePosition();
        calculateCollisions();
        setPosition(xCurrent, yCurrent);

        /**
         * Check if attack or rolling animation should stop
         */
        if (currentAction == ObjectState.MELEEING || currentAction == ObjectState.WALK_MELEEING || currentAction == ObjectState.RUN_MELEEING) {
            if (animation.hasPlayedOnce()) meleeing = false;
        }

        if (currentAction == ObjectState.ROLLING) {
            if (animation.hasPlayedOnce()) currentAction = ObjectState.IDLE;
        }

        /**
         * Set animation based on player state
         * Each animation is set by a function that takes in three parameters:
         *      -   The state the animation should emulate (newAction)
         *      -   The length of the animation in milliseconds (delay)
         *      -   If the animation should stop after player once or not (stopAfterPlayed)
         *
         */
        if (meleeing) {

                if (currentAction != ObjectState.MELEEING && currentAction != ObjectState.WALK_MELEEING && currentAction != ObjectState.RUN_MELEEING) {
                    /**
                     * The animation delays are marked as magic numbers.
                     * I personally think this is a viable solution in this case
                     * since it doesn't affect the code's readability but if this
                     * is wrong please let me know.
                     */

                    /**
                     * The code inspection marks almost all of the branches below as
                     * dry::SimilarBranches. I strongly believe this is a false positive
                     * but if there is any way this part of the code can be improved
                     * I would really like to know.
                     */

                    if (running || falling) {setCurrentAnimation(ObjectState.RUN_MELEEING, 75, true);}
                    else if (dx != 0) {setCurrentAnimation(ObjectState.WALK_MELEEING, 75, true);}
                    else {setCurrentAnimation(ObjectState.MELEEING, 75, true);}


                    }
        } else if (dy != 0) {
            if (jumping && dy < 0) {
                setCurrentAnimation(ObjectState.JUMPING, 50, true);
            } else if (falling && dy > 0) {
                setCurrentAnimation(ObjectState.FALLING, 50, true);
            }
        } else if (moveLeft || moveRight) {
            if (running) {

                if (moveDown) {
                    setCurrentAnimation(ObjectState.ROLLING, 60, true);
                }

                if (currentAction != ObjectState.RUNNING && currentAction != ObjectState.ROLLING) {
                    setCurrentAnimation(ObjectState.RUNNING, 50, false);
                }
            } else {
                setCurrentAnimation(ObjectState.WALKING, 75, false);
            }
        } else {
            if (moveDown) {
                setCurrentAnimation(ObjectState.CROUCHING, 100, true);
            } else {
                setCurrentAnimation(ObjectState.IDLE, 200, false);
            }
        }

        animation.update();

        /**
         * Update facing direction based on player state
         */
        if (currentAction != ObjectState.MELEEING) {
            if (moveRight) facingRight = true;
            if (moveLeft) facingRight = false;
        }
    }


    /**
     * Draws the player on the screen.
     *
     * @param g the graphics object to draw with
     */
    public void draw(Graphics2D g) {

        setMapPosition();

        /**
         * Draw the player:
         */
        if(flinching) {
            final int timeDenominator = 1000000;
            long timeElapsed =
                    (System.nanoTime() - flinchingTimer) / timeDenominator;
            if (timeElapsed / 100 % 2 == 0) {
                return;
            }
        }
        super.draw(g);
    }
}

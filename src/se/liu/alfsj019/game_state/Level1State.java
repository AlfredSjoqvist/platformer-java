package se.liu.alfsj019.game_state;

import se.liu.alfsj019.entity.enemies.Yellow;
import se.liu.alfsj019.entity.Enemy;
import se.liu.alfsj019.entity.HUD;
import se.liu.alfsj019.entity.Player;
import se.liu.alfsj019.main.FileHandlerCreator;
import se.liu.alfsj019.main.GameCanvas;
import se.liu.alfsj019.tile_map.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The Level1State class represents the game state for level 1.
 */
public class Level1State extends GameState
{

    private TileMap tileMap = null;
    private Player player = null;
    private List<Enemy> enemies = null;
    private HUD hud = null;
    private Point playerStartPosition = new Point(100, 500);

    public Level1State(GameStateManager gsm) {
	setGameStateManager(gsm);
        init();
    }

    /**
     * Initializes the object
     */
    public void init() {

        tileMap = new TileMap(32, 14);
        tileMap.loadAllTiles("level1" + File.separator + "csv");
        tileMap.loadAllMaps("level1" + File.separator + "csv");
        tileMap.updatePosition(0, 0);
        tileMap.setTween(0.07);

        background = new Background("images" + File.separator + "Background.png", 0.2);
        player = new Player(tileMap);
        player.setPosition(playerStartPosition.getX(), playerStartPosition.getY());

        populateEnemies();

        hud = new HUD();
    }

    /**
     * Reads the positions of all the enemies from a CSV-file
     * and then populates the map according to this.
     */
    private void populateEnemies() {

        Logger logger = Logger.getLogger(getClass().getSimpleName());
        FileHandlerCreator.attachFileHandler(logger, getClass().getSimpleName());

        enemies = new ArrayList<>();

        try {

            List<Point> points = new ArrayList<>();
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("level1/csv/enemies.csv")) {

                /**
                 * getResourceAsStream() returns null if the file does not exist so
                 * the below if-statement checks for this error.
                 */
                if (in == null) {
                    logger.log(logger.getLevel(), "File enemies.csv not found", new NullPointerException("File enemies.csv not found"));
                    signalCsvError();
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                final int numCols = Integer.parseInt(br.readLine());
                final int numRows = Integer.parseInt(br.readLine());
                String deliMeters = ",";

                for (int row = 0; row < numRows; row++) {
                    String line = br.readLine();
                    String[] tokens = line.split(deliMeters);
                    for(int col = 0; col < numCols; col++) {
                        if (Integer.parseInt(tokens[col]) == 0) {
                            Point point = new Point(col*tileMap.getTileSize(), row*tileMap.getTileSize());
                            points.add(point);
                        }
                    }
                }
            }

            for(Point point : points) {
                Yellow y = new Yellow(tileMap);
                y.setPosition(point.x, point.y);
                enemies.add(y);
            }

        } catch (IOException e) {
            /**
             * OBS! README - CatchFallThrough in the root folder
             */
            logger.log(logger.getLevel(), "File enemies.csv not found", e);
            signalCsvError();
            e.printStackTrace();
        }
    }

    private void signalCsvError() {

        if (JOptionPane.showConfirmDialog(null, "File enemies.csv not found, continue anyway?", "Error", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            System.exit(1);
        }

    }

    /**
     * Update the level
     */
    @Override public void update() {

        /**
         * Update player
         */
        player.update();

        tileMap.updatePosition(
                GameCanvas.WIDTH / 2.0 - player.getPosition().getX(),
                GameCanvas.HEIGHT / 2.0 - player.getPosition().getY()
        );

        /**
         * Set background
         */
        background.setPosition(tileMap.getPosition().getX(), tileMap.getPosition().getY());

        /**
         * Attack enemies
         */
        player.dealMeleeDamage(enemies);

        /**
         * Update enemies
         */
        for (Enemy enemy : enemies) {
            enemy.update();
        }

    }

    /**
     * Draw the level
     *
     * @param g the graphics context
     */
    @Override public void draw(final Graphics2D g) {

        background.draw(g);
        tileMap.draw(g);
        player.draw(g);

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            enemy.draw(g);
            if (enemy.isDead()) {
                enemies.remove(enemy);
                i--;
            }
        }

        hud.draw(g);

    }


    /**
     * Update player state according to keys pressed
     *
     * @param k the ID of the key pressed
     */
    @Override public void keyPressed(final int k) {
        /**
         * The code inspection marks this as SimilarConditionals and ChainOfIfStatements
         * I believe this to be a false positive since the conditions should NOT be
         * mutually exclusive as well as the statements doing completely different actions.
         */
        if (k == KeyEvent.VK_LEFT)  player.setMoveLeft(true);
        if (k == KeyEvent.VK_RIGHT)  player.setMoveRight(true);
        if (k == KeyEvent.VK_DOWN)  player.setMoveDown(true);
        if (k == KeyEvent.VK_SPACE)  player.setJumping(true);
        if (k == KeyEvent.VK_SHIFT) player.setRunning(true);
        if (k == KeyEvent.VK_A)  player.setMeleeing();
    }

    /**
     * Update player state according to keys released
     *
     * @param k the ID of the key pressed
     */
    @Override public void keyReleased(final int k) {
        /**
         * The code inspection also marks this as SimilarConditionals and ChainOfIfStatements
         * For the same reasons as stated above, I believe these to be false positives.
         */
        if (k == KeyEvent.VK_LEFT)  player.setMoveLeft(false);
        if (k == KeyEvent.VK_RIGHT)  player.setMoveRight(false);
        if (k == KeyEvent.VK_DOWN)  player.setMoveDown(false);
        if (k == KeyEvent.VK_SPACE)  player.setJumping(false);
        if (k == KeyEvent.VK_SHIFT) player.setRunning(false);
    }
}

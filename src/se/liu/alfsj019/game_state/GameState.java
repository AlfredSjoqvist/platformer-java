package se.liu.alfsj019.game_state;

import se.liu.alfsj019.tile_map.Background;

import java.awt.*;

/**
 * The abstract class representing a game state.
 * All different states of the game inherits from this class.
 */
public abstract class GameState
{
    protected Background background = null;
    protected GameStateManager gameStateManager = null;
    public abstract void update();
    public abstract void draw(Graphics2D g);
    public abstract void keyPressed(int k);
    public abstract void keyReleased(int k);

    public void setBackground(final Background background) {
        this.background = background;
    }

    public void setGameStateManager(final GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
    }
}

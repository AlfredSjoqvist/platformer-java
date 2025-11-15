package se.liu.alfsj019.game_state;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;



/**
 * The GameStateManager class manages the game states.
 */
public class GameStateManager {

    private List<GameState> gameStates;
    private int currentState;


    public GameStateManager() {

	gameStates = new ArrayList<>();

	final int menuStateID = 0;
	currentState = menuStateID;

	gameStates.add(new MenuState(this));
	gameStates.add(new Level1State(this));

    }

    public void setCurrentState(int state) {
	currentState = state;
    }

    /**
     * Updates the current state.
     */
    public void update() {
	gameStates.get(currentState).update();
    }

    /**
     * Draws the current state.
     *
     * @param g the graphics context
     */
    public void draw(Graphics2D g) {
	gameStates.get(currentState).draw(g);
    }

    /**
     * Handles key press event.
     *
     * @param k the key code
     */
    public void keyPressed(int k) {
	gameStates.get(currentState).keyPressed(k);
    }

    /**
     * Handles key release event.
     *
     * @param k the key code
     */
    public void keyReleased(int k) {
	gameStates.get(currentState).keyReleased(k);
    }

}
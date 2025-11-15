package se.liu.alfsj019.main;

import javax.swing.JFrame;

/**
 * The main class that starts the game.
 * This is supposed to be the only runnable class in the project.
 */
public class Game {

    /**
     * The main method that starts the game.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {

	JFrame window = new JFrame("Thrones");
	window.setContentPane(new GameCanvas());
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setResizable(true);
	window.pack();
	window.setVisible(true);

    }

}
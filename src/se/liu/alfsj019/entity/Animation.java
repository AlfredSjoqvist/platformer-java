package se.liu.alfsj019.entity;

import java.awt.image.BufferedImage;

/**
 * Represents an animation consisting of multiple frames.
 */
public class Animation
{
    private BufferedImage[] frames = null;
    private int currentFrame;
    private long startTime;
    private long delay;
    private boolean playedOnce;
    private boolean stopAfterPlayedOnce;

    public Animation() {
	playedOnce = false;
	stopAfterPlayedOnce = false;
    }


    /**
     * Sets the frames for the animation.
     *
     * @param frames the array of BufferedImages representing the frames of the animation
     */
    public void setFrames(BufferedImage[] frames) {
	this.frames = frames;
	currentFrame = 0;
	startTime = System.nanoTime();
	playedOnce = false;
    }

    public void setDelay(final long delay) {
	this.delay = delay;
    }

    /**
     * Updates the animation by advancing to the next frame if the delay has passed.
     */
    public void update() {

	if (delay == -1) return;

	final int timeDenominator = 1000000;

	long timeElapsed = (System.nanoTime() - startTime) / timeDenominator;
	if (timeElapsed > delay) {
	    currentFrame++;
	    startTime = System.nanoTime();
	}
	if (currentFrame == frames.length) {
	    currentFrame = 0;
	    playedOnce = true;
	}

	/**
	 * Special for the jump and fall
	 */
	if (stopAfterPlayedOnce && playedOnce) {
	    currentFrame = frames.length-1;
	}

    }

    public BufferedImage getImage() {
	return frames[currentFrame];
    }

    public boolean hasPlayedOnce() {
	return playedOnce;
    }

    public void setStopAfterPlayedOnce(final boolean stopAfterPlayedOnce) {
	this.stopAfterPlayedOnce = stopAfterPlayedOnce;
    }
}

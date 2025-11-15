
package se.liu.alfsj019.entity;

import se.liu.alfsj019.tile_map.TileMap;

/**
 * Represents an enemy creature in the game.
 * All moving objects that can hurt the player inherits from this class.
 */
public class Enemy extends MapCreature
{
    public Enemy (TileMap tm) {
	super(tm);
    }


    /**
     * Inflicts damage to the enemy.
     *
     * @param damage the amount of damage to inflict
     */
    public void hit(int damage) {
	if (dead || flinching) return;
	health -= damage;
	if (health <0) health = 0;
	if (health == 0) dead = true;
	flinching = true;
	flinchingTimer = System.nanoTime();
    }

    /**
     * Updates the enemy's state. (Overridden by subclasses)
     */
    public void update() {

    }
}

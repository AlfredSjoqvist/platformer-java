
package se.liu.alfsj019.entity;

import se.liu.alfsj019.tile_map.TileMap;

/**
 * Represents a living creature in the game that exists within the map.
 */
public abstract class MapCreature extends MapObject
{
    protected int health;
    protected boolean dead;
    protected boolean flinching;
    protected long flinchingTimer;

    protected MapCreature(final TileMap tm) {
	super(tm);
    }

    public boolean isDead() {return dead;}

    public void setHealth(final int health) {
        this.health = health;
    }

}

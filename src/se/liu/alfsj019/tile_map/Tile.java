package se.liu.alfsj019.tile_map;

import java.awt.image.BufferedImage;

/**
 * The Tile class represents a tile in the tile map.
 * It stores the image, ID, and type of the tile.
 */
public class Tile
{
    private BufferedImage image;

    public Tile(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

}

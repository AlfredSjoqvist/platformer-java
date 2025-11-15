package se.liu.alfsj019.tile_map;


import se.liu.alfsj019.main.FileHandlerCreator;
import se.liu.alfsj019.main.GameCanvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The class which represents the map of tiles where the game takes place.
 * <p>
 * OBS!
 * This class contains a lot of fields and I consider this to be a necessary outcome
 * because of the large amounts of information that has to be contained in this class.
 */
public class TileMap
{

    /**
     * Logger
     */
    private Logger logger;

    /**
     * Position properties
     */
    private Point position;

    /**
     * Boundary properties
     */
    private int xMinimum;
    private int yMinimum;
    private int xMaximum;
    private int yMaximum;
    private double tween;

    /**
     * Map properties
     */
    private List<int[][]> allLayers;
    private int tileSize;
    private int numRows;
    private int numCols;

    private List<Tile[]> allTiles;
    private int tileLayers;

    /**
     * Drawing properties
     */
    private int rowOffset;
    private int colOffset;
    private int numRowsToDraw;
    private int numColsToDraw;
    private int renderOffset;


    public TileMap(int tileSize, int tileLayers) {
	this.tileSize = tileSize;
	this.tileLayers = tileLayers;
	allTiles = new ArrayList<>();
	allLayers = new ArrayList<>();
	position = new Point(0, 0);
	renderOffset = 4;

	logger = Logger.getLogger(getClass().getSimpleName());
	FileHandlerCreator.attachFileHandler(logger, getClass().getSimpleName());

	/**
	 * (might lag the game a lot with this render fix)
	 */
	numRowsToDraw = GameCanvas.HEIGHT / tileSize + renderOffset;
	numColsToDraw = GameCanvas.WIDTH / tileSize + renderOffset;
	tween = 0.07;
    }

    private void signalSpriteError(String s) {

	if (JOptionPane.showConfirmDialog(null, "Tile sprites with name: " + s + " not found, load game anyway?", "Error", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
	    System.exit(1);
	}
    }

    private void signalSpriteError() {

	if (JOptionPane.showConfirmDialog(null, "Some tile sprites were not found, load game anyway?", "Error", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
	    System.exit(1);
	}
    }

    /**
     * Load all images for the tiles contained in the map
     *
     * @param s the filepath where the map resources are located
     */
    public void loadAllTiles(String s){

	URL res = getClass().getClassLoader().getResource(s);

	File[] filePaths = fetchFilesFromFolder(res, s);

	if (filePaths == null) {
	    logger.log(logger.getLevel(), "Tile sprites with name: " + s + " not found", new NullPointerException("Tile sprites not found"));
	    signalSpriteError();
	    return;
	}

	List<String> tileImagePaths = new ArrayList<>();

	/**
	 * The reason for the line below is for the tiles
	 * to align with the layers since layer 0 is collisions
	 */
	allTiles.add(null);

	for (final File filePath : filePaths) {
	    if (filePath.isFile()) {
		final int filePathBeginCharacter = 3;
		tileImagePaths.add(filePath.getName().substring(filePathBeginCharacter));
	    }
	}

	for (String tileImagePath: tileImagePaths) {
	    if (tileImagePath.endsWith("_sheet.csv")) {
		loadTilesFromSheet("tiles" + File.separator + tileImagePath.split(".cs")[0] + File.separator + "tileset.png");
	    } else {
		loadTilesFromCollection("tiles" + File.separator +  tileImagePath.split(".cs")[0]);
		}
	    }
	}

    /**
     * Crop a tile sheet image and load them to the map
     *
     * @param s the filepath to the tile sheet
     */
    public void loadTilesFromSheet(String s) {

	try {
	    /**
	     * Tileset properties
	     */
	    BufferedImage tileSheet = ImageIO.read(ClassLoader.getSystemResource(s));
	    /**
	     * ImageIO.read() returns null if the file does not exist so
	     * the below if-statement checks for this error.
	     */
	    if (tileSheet == null) {
		tileSheet = new BufferedImage(3000, 3000, BufferedImage.TYPE_INT_ARGB);
		logger.log(logger.getLevel(), "Tile sheet frame not found", new NullPointerException("Tile image not found"));
		signalSpriteError(s);
	    }
	    int numTilesWidth = tileSheet.getWidth() / tileSize;
	    int numTilesHeight = tileSheet.getHeight() / tileSize;

	    Tile[] tiles = new Tile[numTilesHeight * numTilesWidth];

	    for (int row = 0; row < numTilesHeight; row++) {
		for (int col = 0; col < numTilesWidth; col++) {
		    BufferedImage subImage = tileSheet.getSubimage(col * tileSize, row * tileSize, tileSize, tileSize);
		    int tileID = row * numTilesWidth + col;
		    tiles[tileID] = new Tile(subImage);
		}
	    }

	    allTiles.add(tiles);

	} catch (IOException e) {
	    /**
	     * OBS! README - CatchFallThrough in the root folder
	     */
	    logger.log(logger.getLevel(), "Tile sprites with name: " + s + " not found", e);
	    signalSpriteError(s);
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param res URL to the folder path
     * @param s filename to be able to log errors correctly
     * @return
     */
    public File[] fetchFilesFromFolder(URL res, String s) {

	if (res == null) {
	    logSpriteError(s);
	    signalSpriteError(s);
	    return null;
	}

	File folder;

	try {
	    folder = Paths.get(res.toURI()).toFile();
	} catch (URISyntaxException e) {
	    logger.log(logger.getLevel(), "Tile sprites with name: " + s + " not found", e);
	    signalSpriteError(s);
	    e.printStackTrace();
	    /**
	     * The code inspection reacts on the "return null;" statement in the catch clause.
	     * This return value is correctly handled at every instance this function is called
	     * and therefore this is a false positive.
	     */
	    return null;
	}

	return folder.listFiles();
    }

    /**
     * Load multiple tile images from a folder at once
     *
     * @param s the filepath to the folder
     */
    public void loadTilesFromCollection(String s) {

	URL res = getClass().getClassLoader().getResource(s);

	if (s.equals("tiles" + File.separator + "lision") || s.equals("tiles" + File.separator + "mies")) {
	    return;
	}

	File[] filePaths = fetchFilesFromFolder(res, s);

	if (filePaths == null) {
	    logSpriteError(s);
	    signalSpriteError(s);
	    return;
	}

	Tile[] tiles = new Tile[filePaths.length];

	for (int i = 0; i < filePaths.length; i++) {

	    if (filePaths[i].isFile()) {

		try {

		    URL tileImageResource = ClassLoader.getSystemResource(s + "/" + i + ".png");

		    if (tileImageResource == null) {
			logSpriteError(s);
			signalSpriteError(s);
			return;
		    }

		    BufferedImage tileImage = ImageIO.read(tileImageResource);

		    /**
		     * ImageIO.read() returns null if the file does not exist so
		     * the below if-statement checks for this error.
		     */
		    if (tileImage == null) {
			logSpriteError(s);
			signalSpriteError(s);
			return;
		    }

		    tiles[i] = new Tile(tileImage);


		} catch (IOException e) {
		    /**
		     * OBS! README - CatchFallThrough in the root folder
		     */
		    logger.log(logger.getLevel(), "Tile sprites with name: " + s + " not found", e);
		    signalSpriteError(s);
		    e.printStackTrace();
		    return;
		}
	    }
	}

	allTiles.add(tiles);
    }

    /**
     * Load all tile layers
     *
     * @param s filepath to the folder with the layers
     */
    public void loadAllMaps(String s) {

	File folder;

	URL res = getClass().getClassLoader().getResource(s);

	if (res == null) {
	    logSpriteError(s);
	    signalSpriteError();
	    return;
	}

	try {
	    folder = Paths.get(res.toURI()).toFile();
	} catch (URISyntaxException e) {
	    logger.log(logger.getLevel(), "Tile sprites folder not found", e);
	    signalSpriteError();
	    e.printStackTrace();
	    return;
	}

	File[] csvFiles = folder.listFiles();

	if (csvFiles == null) {
	    logger.log(logger.getLevel(), "Tile sprites with name: " + s + " not found", new NullPointerException("Tile sprites not found"));
	    signalSpriteError();
	    return;
	}

	loadMapFromCsv(s + File.separator + "collision.csv");

	for (File csvFile: csvFiles) {
	    if (csvFile.isFile()) {
		loadMapFromCsv(s + File.separator + csvFile.getName());
	    }
	}

    }

    /**
     * Load a specific layer from a CSV file
     *
     * @param s filepath to the CSV-files with the maps
     */
    public void loadMapFromCsv(String s) {

	try {

	    int[][] layer;
	    try (InputStream in = getClass().getClassLoader().getResourceAsStream(s)) {

		/**
		 * getResourceAsStream() returns null if the file does not exist so
		 * the below if-statement checks for this error.
		 */
		if (in == null) {
		    logger.log(logger.getLevel(), "Map CSV-files not found", new NullPointerException("Map CSV-file not found"));
		    signalSpriteError("Map CSV-files not found, continue anyway? (not recommended)");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		numCols = Integer.parseInt(br.readLine());
		numRows = Integer.parseInt(br.readLine());

		layer = new int[numRows][numCols];

		int width = numCols * tileSize;
		int height = numRows * tileSize;

		xMinimum = GameCanvas.WIDTH - width;
		xMaximum = 0;
		yMinimum = GameCanvas.HEIGHT - height;
		yMaximum = 0;

		String deliMeters = ",";
		for (int row = 0; row < numRows; row++) {
		    String line = br.readLine();
		    String[] tokens = line.split(deliMeters);
		    for(int col = 0; col < numCols; col++) {
			layer[row][col] = Integer.parseInt(tokens[col]);
		    }
		}
	    }
	allLayers.add(layer);

	} catch (IOException e) {
	    /**
	     * OBS! README - CatchFallThrough in the root folder
	     */
	    logger.log(logger.getLevel(), "Map CSV-files not found", e);
	    signalSpriteError("Map CSV-files not found, continue anyway? (not recommended)");
	    e.printStackTrace();
	}
    }

    public void logSpriteError(String s) {
	logger.log(logger.getLevel(), "Tile sprites with name: " + s + " not found", new NullPointerException("Tile sprites not found"));
    }

    public Point getPosition() {
	return position;
    }

    public int getTileSize() {
	return tileSize;
    }

    /**
     * Retrieves the collision type of the tile at the specified row and column coordinates.
     *
     * @param row The row index of the tile.
     * @param col The column index of the tile.
     * @return The collision type of the tile.
     */
    public TileType getCollisionType(int row, int col) {

	if (row >= numRows || col >= numCols || row < 0 || col < 0) {
	    return TileType.NORMAL;
	}

	int rc = allLayers.get(0)[row][col];

	if (rc == 0) {
	    return TileType.BLOCKED;
	} else if (rc == 1) {
	    return TileType.CLIMB;
	} else {
	    return TileType.NORMAL;
	}

	}



    /**
     * Sets the position of the tile map to the specified coordinates.
     *
     * @param x The x-coordinate to set.
     * @param y The y-coordinate to set.
     */
    public void updatePosition(double x, double y) {

	this.position.setLocation(this.position.getX() + (x - this.position.getX()) * tween,
				  this.position.getY() + (y - this.position.getY()) * tween);

	fixBounds();

	colOffset = (int)-this.position.getX() / tileSize;
	rowOffset = (int)-this.position.getY() / tileSize;

    }

    public void setTween(final double tween) {
	this.tween = tween;
    }

    /**
     * Adjusts the tile map bounds to ensure it stays within the specified limits.
     */
    private void fixBounds() {

	double newPositionX = Math.max(xMinimum, Math.min(xMaximum, position.getX()));
	double newPositionY = Math.max(yMinimum, Math.min(yMaximum, position.getY()));

	position.setLocation(newPositionX, newPositionY);
    }

    /**
     * Draws the tile map onto the specified Graphics2D object.
     *
     * @param g The Graphics2D object to draw on.
     */
    public void draw(Graphics2D g) {

	for(
		int row = rowOffset;
		row < rowOffset + numRowsToDraw + renderOffset;
		row++
	) {
	    for(
		    int col = colOffset - renderOffset;
		    col < colOffset + numColsToDraw;
		    col++)
	    {
		    if(col >= numCols) break;

		for (int i = this.tileLayers-1; i > 0; i--) {
			int[][] layer = allLayers.get(i);
			Tile[] tiles = allTiles.get(i);

		    if (!(row >= numRows || col >= numCols || row < 0 || col < 0)) {
			    int rc = layer[row][col];
			    if (rc >= 0 && rc < tiles.length) {
				BufferedImage image = tiles[rc].getImage();
				g.drawImage(
					image,
					(int) position.getX() + col * tileSize,
					(int) position.getY() + row * tileSize - image.getHeight() + tileSize,
					null
				);
			    }
			}
			}
	    	}
	    }
	}
    }


# Java Platformer Project

This repository contains a handcrafted 2D platformer built in Java, built to explore game development and for creative expression. It features a custom level, pixel art environments, a state driven game loop, and a responsive player controller.

<p align="center">
  <img src="resources/images/Background.png" width="480" alt="Platformer game screenshot">
</p>

## Skills Demonstrated

- Object oriented design and clean Java architecture  
- Custom game loop and state based update logic  
- Tile map system with layered backgrounds and collision data  
- Player movement, jumping, combat and enemy interactions  
- Sprite animation handling for player, enemies and UI  
- Resource management for images, tilesets and level data  
- Logging and advanced debugging support

## Project Overview

### Core Game Loop (src/se/liu/alfsj019/main)

#### Game.java  
Main game class that sets up the window, initializes systems, and runs the update and render loop.

#### GameCanvas.java  
Canvas responsible for drawing frames, handling buffering, and coordinating rendering.

#### FileHandlerCreator.java  
Utility for setting up file handlers and logging to track behavior during runtime.

### Game States (src/se/liu/alfsj019/game_state)

#### GameState.java and GameStateManager.java  
Abstract base and manager for the different game states such as menus and levels.

#### MenuState.java  
Main menu state that renders the animated background and handles navigation.

#### Level1State.java  
Primary gameplay state that loads the level, updates entities, and processes input.

### Entities and HUD (src/se/liu/alfsj019/entity)

#### Player.java  
Implements player movement, jumping, attacks, state transitions, and interaction with the world.

#### Enemy.java and enemies/Yellow.java  
Base enemy logic and a concrete enemy implementation used in the level.

#### MapCreature.java and MapObject.java  
Shared functionality for movable and static entities that exist on the tile map.

#### Animation.java  
Utility for handling frame based animations from sprite sheets.

#### HUD.java  
Heads up display showing player information using UI elements and sprite based bars.

### Tile Map System (src/se/liu/alfsj019/tile_map)

#### TileMap.java  
Loads tile layers from CSV files, manages scrolling, and performs collision checks.

#### Tile.java and TileType.java  
Represent individual tiles and distinguish between solid and non solid types.

#### Background.java  
Parallax style background rendering to give depth to the level.

### Assets and Levels (resources)

#### resources/images  
Pixel art assets for the environment, characters, UI elements, and animated menu background.

#### resources/level1  
CSV files describing level geometry, collision, enemies, and decorative layers.

#### resources/maps and tiles  
Tileset definitions and supporting map files created with tools like Tiled.
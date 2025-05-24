
import core.*;

import assets.*;
import gui.*;




/**
 * Main class to initialize and run the game.
 * Contains methods to create the player, set up the game engine, handle input, and start the game.
 * @author Brandon Mejia
 * @version 2025/05/07
 */
public class Main
{
    /**
     * Main method to initialize the game and start the game engine.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args)
    {
        // Background shape
        Shape backGroundShape = new Shape(ImagesLoader.loadAnimationFrames("background.gif"), 5);
        // Initialize the GUI with dimensions and background
        SwingGui gui = new SwingGui(1100, 800, backGroundShape);
        // Initialize the game manager with the engine and player
        // Create the game manager with the GUI and gameEngine
        GameManager gameManager = new GameManager(gui);
        // Create the hitbox shape
        gameManager.setHitbox(false);
        // start the game engine
        gameManager.startGame();
    }
}
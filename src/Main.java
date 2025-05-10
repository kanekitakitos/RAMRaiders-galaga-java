
import core.*;

import core.objectsInterface.*;
import geometry.*;
import assets.*;
import gui.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;




/**
 * Main class to initialize and run the game.
 * Contains methods to create the player, set up the game engine, handle input, and start the game.
 * @author Brandon Mejia
 * @version 2025/05/07
 */
public class Main
{

    /**
     * Creates the player game object with its properties, collider, behavior, and shape.
     *
     * @return IGameObject The player game object.
     */
    public static IGameObject createPlayer()
    {
        double scale = 4; // Scale of the player object
        int layer = 1; // Layer of the player object
        double angle = 90; // Initial angle of the player object
        Ponto position = new Ponto(0, -330); // Initial position of the player object
        Ponto[] points = {new Ponto(0, 0), new Ponto(0, 12), new Ponto(12, 6)}; // Points for the collider

        Transform t1 = new Transform(position, layer, angle, scale); // Transform for the player
        Poligono collider = new Poligono(points, t1); // Polygon collider for the player
        // Circulo collider = new Circulo(5, t1); // Alternative circular collider (commented out)
        Shape shape = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 550); // Shape with animation frames
        PlayerBehavior behavior = new PlayerBehavior(); // Behavior of the player
        GameObject player = new GameObject("Player", t1, collider, behavior, shape); // Create the player game object
        player.onInit(); // Initialize the player

        return player;
    }

    public static IInputEvent createInputHandler()
    {
        Map<Integer, String> customKeyMap = new HashMap<>(); // Map for custom key bindings
        customKeyMap.put(KeyEvent.VK_A, "LEFT");
        customKeyMap.put(KeyEvent.VK_LEFT, "LEFT");
        customKeyMap.put(KeyEvent.VK_RIGHT, "RIGHT");
        customKeyMap.put(KeyEvent.VK_D, "RIGHT");
        customKeyMap.put(KeyEvent.VK_C, "ATTACK");
        customKeyMap.put(KeyEvent.VK_X, "EVASIVE");

        Map<Integer, String> customMouseMap = new HashMap<>(); // Map for custom mouse bindings
        customMouseMap.put(MouseEvent.BUTTON1, "ATTACK");
        customMouseMap.put(MouseEvent.BUTTON3, "EVASIVE");

        return new InputEvent(customKeyMap, customMouseMap); // Create and return the input handler
    }

    /**
     * Creates and starts the game engine with the provided GUI.
     *
     * @param GUI The SwingGui instance to be used for the game.
     */
    public static void createGameEngine(SwingGui GUI)
    {
        GameEngine engine = new GameEngine(GUI); // Initialize the game engine with the GUI
        IGameObject player = createPlayer(); // Create the player object

        engine.add(player); // Add the player to the engine
        GameManager gameManager = new GameManager(engine, player); // Initialize the game manager with the engine and player
        
        gameManager.startGame(); // Start the game
    }

    /**
     * Main method to initialize the game and start the game engine.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args)
    {
        Shape backGroundShape = new Shape(AssetLoader.loadAnimationFrames("background.png"), 1000); // Background shape
        SwingGui gui = new SwingGui(1100, 800, backGroundShape); // Initialize the GUI with dimensions and background
        gui.setHitbox(false); // Enable hitbox visualization
        gui.setInput(createInputHandler()); // Set the input handler for the GUI

        createGameEngine(gui); // Create and start the game engine
    }
}
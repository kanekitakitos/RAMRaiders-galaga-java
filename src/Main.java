
import core.*;

import core.objectsInterface.*;
import geometry.*;
import assets.*;
import gui.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import core.behaviorItems.*;




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
        Shape shape = new Shape(AssetLoader.loadAnimationFrames("player.gif"), 150); // Shape with animation frames
        PlayerBehavior behavior = new PlayerBehavior(); // Behavior of the player
        GameObject player = new GameObject("Player", t1, collider, behavior, shape); // Create the player game object
        player.onInit(); // Initialize the player

        return player;
    }

    public static IGameObject createEnemy()
    {
        double scale = 4; // Scale of the player object
        int layer = 2; // Layer of the player object
        double angle = 90; // Initial angle of the player object
        Ponto position = new Ponto(0, 10); // Initial position of the player object
        Ponto[] points = {new Ponto(0, 0), new Ponto(0, 12), new Ponto(12, 6)}; // Points for the collider

        Transform t1 = new Transform(position, layer, angle, scale); // Transform for the player
        Poligono collider = new Poligono(points, t1); // Polygon collider for the player
        // Circulo collider = new Circulo(5, t1); // Alternative circular collider (commented out)
        Shape shape = new Shape(AssetLoader.loadAnimationFrames("inimigo2.gif"), 150); // Shape with animation frames
        EnemyBehavior behavior = new EnemyBehavior(); // Behavior of the player
        GameObject enemy = new GameObject("Enemy", t1, collider, behavior, shape); // Create the enemy game object
        enemy.onInit(); // Initialize the player
        ZigzagMovement zigzagMovement = new ZigzagMovement(); // Create the ZigzagMovement object
        behavior.setMovement(zigzagMovement); // Set the movement strategy for the enemy
        zigzagMovement.setActive(true);
        zigzagMovement.setDirection(true);
        
        //gameManager.startGame(); // Start the game
        return enemy;
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
        customKeyMap.put(KeyEvent.VK_1, "PLAYER1");
        customKeyMap.put(KeyEvent.VK_2, "PLAYER2");
        customKeyMap.put(KeyEvent.VK_NUMPAD1, "PLAYER1");
        customKeyMap.put(KeyEvent.VK_NUMPAD2, "PLAYER2");

        Map<Integer, String> customMouseMap = new HashMap<>(); // Map for custom mouse bindings
        customMouseMap.put(MouseEvent.BUTTON1, "ATTACK");
        customMouseMap.put(MouseEvent.BUTTON3, "EVASIVE");

        return new InputEvent(customKeyMap, customMouseMap); // Create and return the input handler
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

        GameEngine engine = new GameEngine(gui); // Initialize the game engine with the GUI
        GameManager gameManager = new GameManager(engine); // Initialize the game manager with the engine and player
        gameManager.startGame();
    }
}
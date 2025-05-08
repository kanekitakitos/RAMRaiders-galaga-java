package core;

import java.util.ArrayList;
import core.behaviorItems.*;
import core.objectsInterface.*;
import geometry.*;
import assets.*;
import java.util.function.Function;
import java.util.concurrent.*;
import gui.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * The `GameManager` class is responsible for managing the game's enemies,
 * player, and their interactions.
 * It handles the creation, configuration, and management of enemy objects, as
 * well as their behaviors
 * and attack/movement patterns.
 *
 * <p>
 * Responsibilities:
 * </p>
 * - Generate and manage enemy game objects.
 * - Assign movement and attack patterns to enemies.
 * - Enable enemies in the game engine.
 * - Execute group attack strategies.
 *
 * @see GameEngine
 * @see IGameObject
 * @see IEnemyMovement
 * @see IAttackStrategy
 * @see IGroupAttackStrategy
 * @see Transform
 * @see Poligono
 * @see EnemyBehavior
 * @see Shape
 * @see AssetLoader
 *
 * @author Brandon Mejia
 * @version 2025-04-20
 */
public class GameManager
{
    private CopyOnWriteArrayList<IGameObject> enemies = new CopyOnWriteArrayList<>(); // List of enemy game objects
    private GameEngine engine; // The game engine managing game objects
    private IGameObject player; // The player game object
    private IGroupAttackStrategy groupAttackStrategy; // Strategy for group attacks
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();




    /**
     * Validates the invariants for the `GameManager` class.
     * Ensures that the provided `GameEngine` and `IGameObject` instances are not null.
     * If the validation fails, an error message is printed, and the program exits.
     *
     * @param engine The `GameEngine` instance managing game objects. Must not be null.
     * @param player The `IGameObject` instance representing the player. Must not be null.
     */
    private void invariante(GameEngine engine, IGameObject player)
    {
        if(engine != null && player != null)
            return;

        System.out.println("GameManager:iv");
        System.exit(0);
    }

    /**
     * Constructs a `GameManager` instance.
     *
     * @param engine The game engine to manage game objects.
     * @param player The player game object.
     */
    public GameManager(GameEngine engine, IGameObject player)
    {
        invariante(engine,player);

        this.engine = engine;
        this.engine.getGui().setInput(this.getInput());

        this.enemies = new CopyOnWriteArrayList<>();
        this.player = player;


        this.groupAttackStrategy = new EnterGameGroup();
        ((EnterGameGroup)this.groupAttackStrategy).setScheduler(this.scheduler);


        Function<Integer, Integer> spawnIndexFunction = (Integer i) ->
        {
            int groupSize = 8;
            int groupNumber = i / groupSize;
            int patternIndex = groupNumber % 4;

            return patternIndex;
        };

        this.generateEnemies(groupAttackStrategy.getNumberOfEnemies(), spawnIndexFunction);
        this.groupAttackStrategy.onInit(this.enemies, player);
    }


    /**
     * Generates enemy game objects and assigns them to the enemies list.
     *
     * @param count              The number of enemies to generate.
     * @param spawnIndexFunction A function to determine the spawn index for each
     *                           enemy.
     */
    public void generateEnemies(int count, Function<Integer, Integer> spawnIndexFunction)
    {

        if(spawnIndexFunction == null || count <= 0)
            throw new IllegalArgumentException("Invalid parameters for enemy generation");

        // Spawn constants
        double scale = 4;
        int layer = 1;
        double defaultY = this.player.transform().position().y() * 0.5;

        // Spawn positions and angles
        double bottomRight = 100;
        double bottomLeft = -bottomRight;
        double right = 400;
        double left = -right;
        double bottomY = 440;

        double[] spawnAngles = { 0.0, 180.0, 270.0, 270.0 };
        double[] spawnXCoords = { left, right, bottomLeft, bottomRight };
        double[] spawnYCoords = { defaultY, defaultY, bottomY, bottomY };

        Ponto[] pointsTriangulo = { new Ponto(0, 0), new Ponto(0, 12), new Ponto(12, 6) };
        //Ponto[] pointsQuadrado = { new Ponto(-5, 5), new Ponto(5, 5), new Ponto(5, -5), new Ponto(-5, -5)};

        for (int i = 0; i < count; i++)
        {
            int spawnIndex = spawnIndexFunction.apply(i);

            Ponto spawnPoint = new Ponto(spawnXCoords[spawnIndex], spawnYCoords[spawnIndex]);
            double spawnAngle = spawnAngles[spawnIndex];

            Transform t = new Transform(spawnPoint, layer, spawnAngle, scale);
            Poligono collider = new Poligono(pointsTriangulo, t);
            EnemyBehavior behavior = new EnemyBehavior();
            Shape shape = new Shape(AssetLoader.loadAnimationFrames("nave.png"), 550);

            GameObject enemy = new GameObject("Enemy " + i, t, collider, behavior, shape);
            enemy.onInit();
            enemy.behavior().subscribe(this.player);
            enemies.add(enemy);
        }
    }

    /**
     * Retrieves the list of enemy game objects.
     *
     * @return The list of enemies.
     */
    public ArrayList<IGameObject> getEnemys()
    {
        return new ArrayList<>(enemies);
    }

    /**
     * Executes the group attack strategy to relocate enemies.
     */
    private void startRelocateEnemies()
    {
        this.groupAttackStrategy.execute(this.enemies, this.player);
    }

    /**
     * Assigns movement patterns to enemies based on a given pattern matrix.
     *
     * @param pattern          A 2D array representing the movement pattern.
     * @param movementStrategy The movement strategy to assign.
     */
    private void assignMovementPatterns(int[][] pattern, IEnemyMovement movementStrategy) {
        int index = 0;
        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[row].length; col++) {
                if (pattern[row][col] == 1 && index < enemies.size()) {
                    GameObject enemy = (GameObject) enemies.get(index);
                    EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();

                    enemyBehavior.setMovement(movementStrategy);
                    index++;
                }
            }
        }
    }

    /**
     * Assigns attack patterns to enemies based on a given pattern matrix.
     *
     * @param pattern        A 2D array representing the attack pattern.
     * @param attackStrategy The attack strategy to assign.
     */
    private void assignAttackPatterns(int[][] pattern, IAttackStrategy attackStrategy) {
        int index = 0;
        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[row].length; col++) {
                if (pattern[row][col] == 1 && index < enemies.size()) {
                    GameObject enemy = (GameObject) enemies.get(index);
                    EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();

                    enemyBehavior.setAttack(attackStrategy);
                    index++;
                }
            }
        }
    }

    /**
     * Enables all enemies in the game engine.
     */
    public void enableEnemiesToEngine()
    {
        for (IGameObject enemy : enemies)
        {
            engine.addEnable(enemy);
        }
    }

    public void shutdown()
    {
        if (scheduler != null && !scheduler.isShutdown())
        {
            scheduler.shutdown();
        }
    }


    /**
     * Configures and returns the input mapping for the game.
     *
     * @return InputEvent The input event mapping for keys and mouse buttons.
     */
    public InputEvent getInput()
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

        return new InputEvent(customKeyMap, customMouseMap); // Return the input event mapping
    }


    public void startGame()
    {
        this.enableEnemiesToEngine();
        this.startRelocateEnemies();
        engine.run();
    }
}
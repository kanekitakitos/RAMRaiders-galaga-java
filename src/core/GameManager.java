package core;

import java.util.ArrayList;
import core.behaviorItems.*;
import core.objectsInterface.*;
import geometry.*;
import assets.*;
import java.util.function.Function;

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
public class GameManager {
    private ArrayList<IGameObject> enemies = new ArrayList<>(); // List of enemy game objects
    private GameEngine engine; // The game engine managing game objects
    private IGameObject player; // The player game object
    private IGroupAttackStrategy groupAttackStrategy; // Strategy for group attacks

    /**
     * Constructs a `GameManager` instance.
     *
     * @param engine The game engine to manage game objects.
     * @param player The player game object.
     */
    public GameManager(GameEngine engine, IGameObject player) {
        this.engine = engine;
        this.enemies = new ArrayList<>();
        this.player = player;
        this.groupAttackStrategy = new EnterGameGroup();

        Function<Integer, Integer> spawnIndexFunction = (Integer i) -> {
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
    public void generateEnemies(int count, Function<Integer, Integer> spawnIndexFunction) {
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

        Ponto[] points = { new Ponto(-5, 5), new Ponto(5, 5), new Ponto(5, -5), new Ponto(-5, -5) };

        for (int i = 0; i < count; i++) {
            int spawnIndex = spawnIndexFunction.apply(i);

            Ponto spawnPoint = new Ponto(spawnXCoords[spawnIndex], spawnYCoords[spawnIndex]);
            double spawnAngle = spawnAngles[spawnIndex];

            Transform t = new Transform(spawnPoint, layer, spawnAngle, scale);
            Poligono collider = new Poligono(points, t);
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
    public ArrayList<IGameObject> getEnemys() {
        return enemies;
    }

    /**
     * Executes the group attack strategy to relocate enemies.
     */
    public void startRelocateEnemies() {
        this.groupAttackStrategy.execute(this.enemies, this.player);
    }

    /**
     * Assigns movement patterns to enemies based on a given pattern matrix.
     *
     * @param pattern          A 2D array representing the movement pattern.
     * @param movementStrategy The movement strategy to assign.
     */
    public void assignMovementPatterns(int[][] pattern, IEnemyMovement movementStrategy) {
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
    public void assignAttackPatterns(int[][] pattern, IAttackStrategy attackStrategy) {
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
    public void enableEnemiesToEngine() {
        for (IGameObject enemy : enemies) {
            engine.addEnable(enemy);
        }
    }
}
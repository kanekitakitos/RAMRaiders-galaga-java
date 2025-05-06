package core;

import java.util.ArrayList;
import core.behaviorItems.*;
import core.objectsInterface.*;
import geometry.*;
import assets.*;
import java.util.function.Predicate;

public class GameManager
{
    private ArrayList<IGameObject> enemies = new ArrayList<>();
    private GameEngine engine;
    private IGameObject player;
    private IGroupAttackStrategy groupAttackStrategy;

    public GameManager(GameEngine engine, IGameObject player)
    {
        this.engine = engine;
        this.enemies = new ArrayList<>();
        this.player = player;
        this.groupAttackStrategy = new EnterGameGroup();
        this.generateEnemies(groupAttackStrategy.getNumberOfEnemies(), i -> i < groupAttackStrategy.getNumberOfEnemies()/2);
        this.groupAttackStrategy.onInit(this.enemies, player);
    }

    public void generateEnemies(int count, Predicate<Integer> spawnPredicate)
    {
        double angleLeft = 0.0;
        double angleRight = 180.0;
        double scale = 4;
        int layer = 1;
        int spawnRight = 400;
        int spawnLeft = -spawnRight;

        Ponto[] points = {new Ponto(-5, 5), new Ponto(5, 5), new Ponto(5, -5), new Ponto(-5, -5)};
        for (int i = 0; i < count; i++)
        {
            boolean spawnOnRight = spawnPredicate.test(i);
            Transform t = new Transform(
                new Ponto(spawnOnRight ? spawnRight : spawnLeft, this.player.transform().position().y()*0.5),
                layer,
                spawnOnRight ? angleRight : angleLeft,
                scale
            );
            Poligono collider = new Poligono(points, t);
            EnemyBehavior behavior = new EnemyBehavior();

            Shape shape = new Shape(AssetLoader.loadAnimationFrames("nave.png"), 550);
            GameObject enemy = new GameObject("Enemy " + i, t, collider, behavior, shape);
            enemy.onInit();

            enemy.behavior().subscribe(this.player);
            enemies.add(enemy);
        }
    }

    public ArrayList<IGameObject> getEnemys()
    {
        return enemies;
    }

    public void startRelocateEnemies()
    {
        this.groupAttackStrategy.execute(this.enemies, this.player);
    }

    public void assignMovementPatterns(int[][] pattern, IEnemyMovement movementStrategy)
    {
        int index = 0;
        for (int row = 0; row < pattern.length; row++)
        {
            for (int col = 0; col < pattern[row].length; col++)
            {
                if (pattern[row][col] == 1 && index < enemies.size())
                {
                    GameObject enemy = (GameObject) enemies.get(index);
                    EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();

                    enemyBehavior.setMovement(movementStrategy);
                    index++;
                }
            }
        }
    }

    public void assignAttackPatterns(int[][] pattern, IAttackStrategy attackStrategy)
    {
        int index = 0;
        for (int row = 0; row < pattern.length; row++)
        {
            for (int col = 0; col < pattern[row].length; col++)
            {
                if (pattern[row][col] == 1 && index < enemies.size())
                {
                    // Assign a special attack pattern, e.g., LinearShootAttack
                    GameObject enemy = (GameObject) enemies.get(index);
                    EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();

                    enemyBehavior.setAttack(attackStrategy);
                    index++;
                }
            }
        }
    }

    public void enableEnemiesToEngine()
    {
        for (IGameObject enemy : enemies)
        {
            engine.addEnable(enemy);
        }
    }
}
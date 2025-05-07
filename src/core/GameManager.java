package core;

import java.util.ArrayList;
import core.behaviorItems.*;
import core.objectsInterface.*;
import geometry.*;
import assets.*;
import java.util.function.Function;

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



    public void generateEnemies(int count, Function<Integer, Integer> spawnIndexFunction)
    {
        // Constantes de spawn
        double scale = 4;
        int layer = 1;
        double defaultY = this.player.transform().position().y() * 0.5;
        // Índices: 0 = esquerda, 1 = direita, 2 = topo-esquerda, 3 = topo-direita

        double bottomRight = 100;
        double bottomLeft = -bottomRight;
        double right = 400;
        double left = -right;
        double bottomY=390;

        double[] spawnAngles = {0.0, 180.0, 270.0, 270.0};
        double[] spawnXCoords = {left, right, bottomLeft, bottomRight};
        double[] spawnYCoords = {defaultY, defaultY, bottomY, bottomY};

        Ponto[] points = {new Ponto(-5, 5), new Ponto(5, 5), new Ponto(5, -5), new Ponto(-5, -5)};

        for (int i = 0; i < count; i++)
        {
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
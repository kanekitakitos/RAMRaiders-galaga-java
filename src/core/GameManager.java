package core;

import java.util.ArrayList;
import core.behaviorItems.*;
import core.objectsInterface.*;
import geometry.*;
import assets.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameManager
{
    private ArrayList<IGameObject> enemies = new ArrayList<>();
    private GameEngine engine;
    private IGameObject player;
    private int[][] pattern;
    private ArrayList<Ponto> positions;

    private IGroupAttackStrategy groupAttackStrategy;

    public GameManager(GameEngine engine, IGameObject player)
    {
        this.engine = engine;
        this.enemies = new ArrayList<>();
        this.player = player;
        this.pattern = new int[][] {
            {0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        this.groupAttackStrategy = new SimpleGroupAttack();

        this.generateEnemies(new SimpleGroupAttack().getNumberOfEnemies());

        this.positions = calculateEnemyPositions(player.transform().position(), this.pattern, 90);
    }

    public void generateEnemies(int count)
    {
        double angleLeft = 0.0;
        double angleRight = 180.0;
        double scale = 5.5;
        int layer = 1;
        int spawnRight = 400;
        int spawnLeft = -spawnRight;


        Ponto[] points = {new Ponto(-5, 5), new Ponto(5, 5), new Ponto(5, -5), new Ponto(-5, -5)};
        for (int i = 0; i < count; i++)
        {
            Transform t = new Transform(new Ponto(i % 2 == 0? spawnRight : spawnLeft, this.player.transform().position().y()*0.5), layer, i % 2 == 0? angleRight : angleLeft, scale);
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

    private int currentGroup = 0;
    // Puedes definir el tamaño de cada grupo en este array
    private int[] groupSizes = {3, 4, 2, 5}; // Ejemplo: 4 grupos de tamaños diferentes
    private int groupDelayFrames = 60; // X tiempo en frames (ajusta según tu motor)
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void startRelocateEnemies()
    {
        scheduler.scheduleAtFixedRate(() -> {
            relocateEnemies();
        }, 0, groupDelayFrames * 16, TimeUnit.MILLISECONDS); // 16ms ~ 1 frame a 60fps
    }

    public void relocateEnemies()
    {
        if (currentGroup >= groupSizes.length) {
            scheduler.shutdown(); // Para o agendamento quando acabar
            return;
        }

        int startIdx = 0;
        for (int i = 0; i < currentGroup; i++)
            startIdx += groupSizes[i];
        int endIdx = Math.min(startIdx + groupSizes[currentGroup], enemies.size());

        for (int i = startIdx; i < endIdx; i++)
        {
            GameObject enemy = (GameObject) enemies.get(i);

            FlySideMovement movement = new FlySideMovement();
            movement.setFinalTarget(this.positions.get(i));

            boolean isRightToLeft = enemy.transform().angle() == 180;
            movement.setDirection(isRightToLeft);

            EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
            behavior.setMovement(movement);
            movement.setActive(true);
        }

        currentGroup++;
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
                    // Assign a special movement pattern, e.g., FlyMovement
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


    public void enterEnemyFormation()
    {
        // Tamanho do grupo configurável
        int groupSize = 4;
        int totalEnemies = Math.min(enemies.size(), positions.size());
        int numGroups = (int) Math.ceil((double) totalEnemies / groupSize);


        int groupDelay = 30;

        for (int group = 0; group < numGroups; group++)
        {
            for (int i = 0; i < groupSize; i++) {
                int enemyIndex = group * groupSize + i;
                if (enemyIndex >= totalEnemies) break;

                GameObject enemy = (GameObject) enemies.get(enemyIndex);
                Ponto targetPosition = positions.get(enemyIndex);


                double entryX = targetPosition.x();
                double entryY = -100;

                //enemy.transform().setPosition(new Ponto(entryX, entryY));


                EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
                
                //behavior.setMovement(new MoveToPositionMovement(targetPosition, group * groupDelay));

            }
        }
    }

    /**
     * Calcula as posições dos inimigos com base na posição do jogador e no padrão de formação.
     * @param playerPosition Ponto inicial do jogador
     * @param pattern matriz de inteiros representando a formação
     * @param spacing espaçamento entre inimigos (em unidades)
     * @return ArrayList<Ponto> com as posições calculadas dos inimigos
     */
    private ArrayList<Ponto> calculateEnemyPositions(Ponto playerPosition, int[][] pattern, double spacing)
    {
        ArrayList<Ponto> positions = new ArrayList<>();
        int rows = pattern.length;
        int cols = pattern[0].length;

        // Centraliza a formação em relação ao jogador
        double startX = playerPosition.x() - ((cols - 1) * spacing) / 2.0;
        double startY = playerPosition.y() + 500; // Exemplo: formação acima do jogador

        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                if (pattern[row][col] != 0)
                {
                    double x = startX + col * spacing;
                    double y = startY + row * spacing;
                    positions.add(new Ponto(x, y));
                }
            }
        }
        return positions;
    }
}
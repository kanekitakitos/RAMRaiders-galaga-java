package core.behaviorItems;

import java.util.List;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import core.GameObject;
import core.EnemyBehavior;

public class EnterGameGroup implements IGroupAttackStrategy
{
    private final double SPACING = 70;
    private ArrayList<Ponto> positions;
    private int currentGroup = 0;
    private int[] groupSizes = {8, 8, 8, 8, 8};
    private int groupDelayFrames = 60;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private int[][] pattern = {
        {0, 0, 0, 2, 2, 2, 2, 0, 0, 0},
        {0, 3, 3, 2, 1, 1, 2, 3, 3, 0},
        {0, 3, 3, 2, 1, 1, 2, 3, 3, 0},
        {5, 5, 4, 4, 1, 1, 4, 4, 5, 5},
        {5, 5, 4, 4, 1, 1, 4, 4, 5, 5}
    };

    @Override
    public void execute(List<IGameObject> enemies, IGameObject target)
    {
        startRelocateEnemies(enemies);
    }

    @Override
    public void onInit(List<IGameObject> enemies, IGameObject target)
    {
        this.positions = calculateEnemyPositions(target.transform().position(), this.pattern, SPACING);
    }

    public void startRelocateEnemies(List<IGameObject> enemies)
    {
        scheduler.scheduleAtFixedRate(() ->
        {
            relocateEnemies(enemies);
        }, 1000, groupDelayFrames * 120, TimeUnit.MILLISECONDS);
    }

    private void relocateEnemies(List<IGameObject> enemies)
    {
        currentGroup++;
        if (currentGroup > 5)
        {
            scheduler.shutdown();
            return;
        }

        ArrayList<Integer> enemyIndices = new ArrayList<>();
        int currentIndex = 0;

        // Encontra todos os inimigos que pertencem ao grupo atual
        for (int row = 0; row < pattern.length; row++)
        {
            for (int col = 0; col < pattern[row].length; col++)
            {
                if (pattern[row][col] != 0)
                {
                    if (pattern[row][col] == currentGroup && enemyIndices.size() < groupSizes[currentGroup - 1])
                    {
                        enemyIndices.add(currentIndex);
                    }
                    currentIndex++;
                }
            }
        }

        // Agenda a entrada dos inimigos do grupo atual
        for (int i = 0; i < enemyIndices.size(); i++)
        {
            final int enemyIndex = enemyIndices.get(i);
            scheduler.schedule(() -> {
                GameObject enemy = (GameObject) enemies.get(enemyIndex);

                EnterSideMovement movement = new EnterSideMovement();
                movement.setFinalTarget(positions.get(enemyIndex));

                boolean isRightToLeft = enemy.transform().angle() == 180;
                movement.setDirection(isRightToLeft);

                EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
                behavior.setMovement(movement);
                movement.setActive(true);
            }, i * 210, TimeUnit.MILLISECONDS);
        }
    }

    private ArrayList<Ponto> calculateEnemyPositions(Ponto playerPosition, int[][] pattern, double spacing)
    {
        ArrayList<Ponto> positions = new ArrayList<>();
        int rows = pattern.length;
        int cols = pattern[0].length;

        double startX = playerPosition.x() - ((cols - 1) * spacing) / 2.0;
        double startY = playerPosition.y() + 660;

        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                if (pattern[row][col] != 0)
                {
                    double x = startX + col * spacing;
                    double y = startY - row * spacing;
                    positions.add(new Ponto(x, y));
                }
            }
        }
        return positions;
    }

    public int getNumberOfEnemies()
    {
        return 40;
    }
}
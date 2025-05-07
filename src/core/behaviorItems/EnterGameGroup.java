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
        startRelocateEnemies(enemies,target);
    }

    @Override
    public void onInit(List<IGameObject> enemies, IGameObject target)
    {
        this.positions = calculateEnemyPositions(target.transform().position(), this.pattern, SPACING);
    }

    public void startRelocateEnemies(List<IGameObject> enemies,IGameObject target)
    {
        scheduler.scheduleAtFixedRate(() ->
        {
            relocateEnemies(enemies,target);
        }, 1000, groupDelayFrames * 120, TimeUnit.MILLISECONDS);
    }

    private void relocateEnemies(List<IGameObject> enemies,IGameObject target)
    {
        currentGroup++;
        if (currentGroup > 5)
        {
            scheduler.shutdown();
            return;
        }

        // Agenda a entrada dos inimigos do grupo atual
        Ponto playerPosition = target.transform().position();
        for (int i = 0; i < groupSizes[currentGroup - 1]; i++)
        {
            final int enemyIndex = i + ((currentGroup - 1) * 8); // 8 inimigos por grupo
            scheduler.schedule(() ->
            {
                GameObject enemy = (GameObject) enemies.get(enemyIndex);
                Ponto targetPosition = positions.get(enemyIndex);


                IEnemyMovement movement;
                // Se o ângulo é 270, está vindo de cima
                if (enemy.transform().angle() == 270)
                {
                    EnterOverTopMovement bottom = new EnterOverTopMovement();
                    bottom.setFinalTarget(targetPosition);
                    
                    boolean isRightToLeft = enemy.transform().position().x() > playerPosition.x();
                    bottom.setDirection(isRightToLeft);
                    movement = bottom;
                }
                else
                {
                    EnterSideMovement leftOrRight = new EnterSideMovement();
                    leftOrRight.setFinalTarget(targetPosition);
                    // Mantém a lógica original para outros ângulos
                    boolean isRightToLeft = enemy.transform().angle() == 180;
                    leftOrRight.setDirection(isRightToLeft);
                    movement = leftOrRight;
                }

                EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
                behavior.setMovement(movement);
                movement.setActive(true);
            }, i * 210, TimeUnit.MILLISECONDS);
        }
    }


    private ArrayList<Ponto> calculateEnemyPositions(Ponto playerPosition, int[][] pattern, double spacing)
    {
        @SuppressWarnings("unchecked")
        ArrayList<Ponto>[] priorityGroups = new ArrayList[6];
        for (int i = 1; i <= 5; i++)
            priorityGroups[i] = new ArrayList<>();

        double startX = playerPosition.x() - ((pattern[0].length - 1) * spacing) / 2.0;
        double startY = playerPosition.y() + 660;

        for (int row = 0; row < pattern.length; row++)
        {
            for (int col = 0; col < pattern[0].length; col++)
            {
                int priority = pattern[row][col];
                if (priority > 0) {
                    double x = startX + col * spacing;
                    double y = startY - row * spacing;
                    priorityGroups[priority].add(new Ponto(x, y));
                }
            }
        }

        ArrayList<Ponto> positions = new ArrayList<>();
        for (int priority = 1; priority <= 5; priority++)
        {
            positions.addAll(priorityGroups[priority]);
        }

        return positions;
    }

    public int getNumberOfEnemies()
    {
        return 40;
    }
}
package core.EnemyGroupAttack;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import core.objectsInterface.IGameObject;
import core.EnemyBehavior;
import core.behaviorItems.ZigzagMovement;
import core.behaviorItems.IEnemyMovement;
import java.util.concurrent.atomic.AtomicBoolean;


public class ZigzagGroup implements IGroupAttackStrategy
{
    private int[][] pattern = {
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 2, 2, 2, 2, 1, 1, 1, 1, 0 },
        { 0, 2, 2, 2, 2, 1, 1, 1, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    private EnemyGridMapper enemyGridMapper = new EnemyGridMapper(pattern);
    private boolean isGroupAttackComplete = false;
    private ArrayList<IGameObject> enemies;
    private ArrayList<IEnemyMovement> previousMovements = new ArrayList<>();
    private ScheduledExecutorService scheduler;
    private AtomicBoolean processComplete = new AtomicBoolean(false);

    private void applyZigzagMovement()
    {
        if (processComplete.get())
        {
            return;
        }

        int index = 0;
        for (int row = 0; row < pattern.length; row++)
        {
            for (int col = 0; col < pattern[row].length; col++)
            {
                int direction = pattern[row][col];
                if (direction > 0 && index < enemies.size())
                {
                    IGameObject enemy = enemies.get(index);
                    if (enemy != null)
                    {
                        EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();
                        IEnemyMovement currentMovement = enemyBehavior.getMovement();
                        if (currentMovement != null) {
                            currentMovement.setActive(false);
                        }

                        ZigzagMovement zigzagMovement = new ZigzagMovement();
                        // 2 = direita, 1 = esquerda
                        boolean moveRight = (direction == 2);
                        zigzagMovement.setDirection(moveRight);
                        enemyBehavior.setMovement(zigzagMovement);
                        zigzagMovement.setActive(true);
                        
                        scheduler.schedule(() -> {
                            if (!processComplete.get() && currentMovement != null) {
                                enemyBehavior.setMovement(currentMovement);
                                currentMovement.setActive(true);
                            }
                        }, 5000, TimeUnit.MILLISECONDS);
                    }
                    index++;
                }
            }
        }
        processComplete.set(true);
        isGroupAttackComplete = true;
    }

    @Override
    public void onInit(List<IGameObject> enemies, IGameObject target)
    {
        this.enemyGridMapper = new EnemyGridMapper(pattern);
        this.enemies = this.enemyGridMapper.getEnemiesFromPattern(pattern);
        
        // Salva os movimentos anteriores durante a inicialização
        for (IGameObject enemy : this.enemies) {
            if (enemy != null) {
                EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
                previousMovements.add(behavior.getMovement());
            }
        }
    }

    @Override
    public void execute(List<IGameObject> enemies, IGameObject target)
    {

        applyZigzagMovement();
    }


    @Override
    public int getNumberOfEnemies()
    {
        return 40;
    }

    @Override
    public boolean isGroupAttackComplete() {
        return this.isGroupAttackComplete;
    }

    @Override
    public void setScheduler(ScheduledExecutorService scheduler)
    {
        this.scheduler = scheduler;
    }
}

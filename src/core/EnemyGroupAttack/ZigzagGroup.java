package core.EnemyGroupAttack;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import core.objectsInterface.IGameObject;
import core.EnemyBehavior;
import core.behaviorItems.IEnemyMovement;
import core.behaviorItems.ZigzagMovement;
import geometry.Ponto;
import java.util.HashMap;
import java.util.Map;

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

    @Override
    public void onInit(List<IGameObject> enemies, IGameObject target)
    {
        this.enemyGridMapper = new EnemyGridMapper(pattern);
        this.enemies = this.enemyGridMapper.getEnemiesFromPattern(pattern);
        //enemyGridMapper.drawGrid();
    }

    private void applyZigzagMovement()
    {
        int index = 0; // Inicia o índice para percorrer a lista de inimigos
        for (int row = 0; row < pattern.length; row++)
        {
            for (int col = 0; col < pattern[row].length; col++)
            {
                int priority = pattern[row][col];
                if (priority > 0 && index < enemies.size())
                {
                    IGameObject enemy = enemies.get(index);
                    if (enemy != null) // Verifica se o inimigo não é nulo
                    {
                        EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();
                        ZigzagMovement zigzagMovement = new ZigzagMovement();
                        zigzagMovement.setDirection(priority == 2); // Se o número for 2, começa para a direita
                        enemyBehavior.setMovement(zigzagMovement);
                        zigzagMovement.setActive(true);
                    }
                    index++; // Incrementa o índice para o próximo inimigo
                }
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
    public void setScheduler(ScheduledExecutorService scheduler) {
    }
}

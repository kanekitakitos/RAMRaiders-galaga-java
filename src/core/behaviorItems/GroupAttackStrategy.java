package core.behaviorItems;

import java.util.List;
import core.objectsInterface.IGameObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import core.GameObject;
import core.EnemyBehavior;

/**
 * Estratégia para gerenciar ataques coordenados de grupos de inimigos.
 * Controla o timing e padrão de ataque dos inimigos baseado em suas posições
 * e prioridades definidas.
 *
 * @author Brandon Mejia
 * @version 2025-05-07
 */
public class GroupAttackStrategy implements IGroupAttackStrategy {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private int currentGroup = 0;
    private int[] groupSizes = { 8, 8, 8, 8, 8 };
    private int groupDelayFrames = 120;
    private boolean isGroupAttackComplete = false;
    
    private int[][] attackPattern = {
        { 0, 0, 0, 1, 1, 1, 1, 0, 0, 0 },
        { 0, 1, 1, 0, 2, 2, 0, 1, 1, 0 },
        { 0, 2, 2, 0, 1, 1, 0, 2, 2, 0 },
        { 1, 1, 0, 0, 2, 2, 0, 0, 1, 1 },
        { 2, 2, 0, 0, 1, 1, 0, 0, 2, 2 }
    };

    private void invariante(List<IGameObject> enemies, IGameObject target) {
        if(enemies != null && !enemies.isEmpty() && target != null)
            return;

        System.out.println("GroupAttackStrategy:iv");
        System.exit(0);
    }

    @Override
    public void onInit(List<IGameObject> enemies, IGameObject target) {
        invariante(enemies, target);
    }

    @Override
    public void execute(List<IGameObject> enemies, IGameObject target) {
        startGroupAttacks(enemies, target);
    }

    private void startGroupAttacks(List<IGameObject> enemies, IGameObject target) {
        scheduler.scheduleAtFixedRate(() -> {
            executeGroupAttack(enemies, target);
        }, 2000, groupDelayFrames * 60, TimeUnit.MILLISECONDS);
    }

    private void executeGroupAttack(List<IGameObject> enemies, IGameObject target) {
        currentGroup++;
        if (currentGroup > 5) {
            scheduler.shutdown();
            return;
        }

        for (int i = 0; i < groupSizes[currentGroup - 1]; i++) {
            final int enemyIndex = i + ((currentGroup - 1) * 8);
            
            scheduler.schedule(() -> {
                if (enemyIndex < enemies.size()) {
                    GameObject enemy = (GameObject) enemies.get(enemyIndex);
                    EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();

                    // Determina o tipo de ataque baseado no padrão
                    int attackType = getAttackTypeForEnemy(enemyIndex);
                    IAttackStrategy attackStrategy;
                    
                    if (attackType == 1) {
                        attackStrategy = new LinearShootAttack();
                    } else {
                        attackStrategy = new KamikazeAttack();
                    }

                    behavior.setAttack(attackStrategy);
                    behavior.startAttack();
                }
            }, i * 300, TimeUnit.MILLISECONDS);
        }
    }

    private int getAttackTypeForEnemy(int enemyIndex)
    {
        int groupSize = 8;
        int row = (enemyIndex / groupSize) % attackPattern.length;
        int col = enemyIndex % attackPattern[0].length;
        return attackPattern[row][col];
    }

    @Override
    public int getNumberOfEnemies() {
        return 40;
    }

    public boolean isGroupAttackComplete() {
        return isGroupAttackComplete;
    }

    public void setScheduler(ScheduledExecutorService scheduler)
    {
        
    }
}
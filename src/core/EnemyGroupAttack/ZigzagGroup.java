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

/**
 * The `ZigzagGroup` class implements the `IGroupAttackStrategy` interface
 * and defines a group attack strategy where enemies move in a zigzag pattern.
 *
 * <p>
 * Responsibilities:
 * </p>
 * - Initialize the group attack with a specific pattern.
 * - Apply zigzag movement to enemies based on the pattern.
 * - Restore previous movements after the zigzag movement is complete.
 * - Track the completion status of the group attack.
 *
 *
 * @author Brandon Mejia
 * @version 2025-05-16
 */
public class ZigzagGroup implements IGroupAttackStrategy {
    /** The pattern defining the movement directions for the grid. */
    private int[][] pattern = {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 2, 2, 2, 2, 1, 1, 1, 1, 0 },
            { 0, 2, 2, 2, 2, 1, 1, 1, 1, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    /** Manages the grid of enemies. */
    private EnemyGridMapper enemyGridMapper = new EnemyGridMapper(pattern);
    /** Tracks whether the group attack is complete. */
    private boolean isGroupAttackComplete = false;
    /** List of enemies participating in the attack. */
    private ArrayList<IGameObject> enemies;
    /** Stores the previous movements of the enemies. */
    private ArrayList<IEnemyMovement> previousMovements = new ArrayList<>();
    /** Scheduler for managing timed tasks. */
    private ScheduledExecutorService scheduler;
    /** Tracks whether the zigzag movement process is complete. */
    private AtomicBoolean processComplete = new AtomicBoolean(false);

    /**
     * Applies the zigzag movement to the enemies based on the defined pattern.
     * Restores the previous movements after a delay.
     */
    private void applyZigzagMovement() {
        if (processComplete.get()) {
            return;
        }

        int index = 0;
        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[row].length; col++) {
                int direction = pattern[row][col];
                if (direction > 0 && index < enemies.size()) {
                    IGameObject enemy = enemies.get(index);
                    if (enemy != null) {
                        EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();
                        IEnemyMovement currentMovement = enemyBehavior.getMovement();
                        if (currentMovement != null) {
                            currentMovement.setActive(false);
                        }

                        ZigzagMovement zigzagMovement = new ZigzagMovement();
                        // 2 = right, 1 = left
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

    /**
     * Initializes the group attack by associating enemies with the grid
     * and saving their previous movements.
     *
     * @param enemies The list of enemies participating in the attack.
     * @param target  The target of the attack (not used in this implementation).
     */
    @Override
    public void onInit(List<IGameObject> enemies, IGameObject target) {
        this.enemyGridMapper = new EnemyGridMapper(pattern);
        this.enemies = this.enemyGridMapper.getEnemiesFromPattern(pattern);

        // Save previous movements during initialization
        for (IGameObject enemy : this.enemies) {
            if (enemy != null) {
                EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
                previousMovements.add(behavior.getMovement());
            }
        }
    }

    /**
     * Executes the zigzag movement for the group attack.
     *
     * @param enemies The list of enemies participating in the attack.
     * @param target  The target of the attack (not used in this implementation).
     */
    @Override
    public void execute(List<IGameObject> enemies, IGameObject target) {
        applyZigzagMovement();
    }

    /**
     * Returns the number of enemies participating in the group attack.
     *
     * @return The number of enemies (fixed at 40).
     */
    @Override
    public int getNumberOfEnemies() {
        return 40;
    }

    /**
     * Checks if the group attack is complete.
     *
     * @return true if the group attack is complete, false otherwise.
     */
    @Override
    public boolean isGroupAttackComplete() {
        return this.isGroupAttackComplete;
    }

    /**
     * Sets the scheduler for managing timed tasks.
     *
     * @param scheduler The `ScheduledExecutorService` to use for scheduling tasks.
     */
    @Override
    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }
}
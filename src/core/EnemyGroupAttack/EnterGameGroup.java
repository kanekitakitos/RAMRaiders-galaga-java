package core.EnemyGroupAttack;

import java.util.List;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import core.GameObject;
import core.behaviorItems.EnterOverTopMovement;
import core.behaviorItems.EnterSideMovement;
import core.behaviorItems.FlyCircleMovement;
import core.behaviorItems.HomingShootAttack;
import core.behaviorItems.IAttackStrategy;
import core.behaviorItems.IEnemyMovement;
import core.EnemyBehavior;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Strategy for managing the entry of enemy groups into the game.
 * Handles the positioning and movement of enemies based on a defined pattern
 * and timing.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * EnterGameGroup strategy = new EnterGameGroup();
 * strategy.onInit(enemies, target);
 * strategy.execute(enemies, target);
 * </pre>
 *
 * @preConditions:
 *                 - The list of enemies must be initialized and contain valid
 *                 IGameObject instances.
 *                 - The target object must be a valid IGameObject with a
 *                 defined position.
 *                 - The pattern array must define valid priorities for enemy
 *                 positioning.
 *
 * @postConditions:
 *                  - Enemies will be relocated to their designated positions
 *                  based on the defined pattern.
 *                  - Enemy movements will be activated according to their entry
 *                  direction and target positions.
 *                  - The scheduler will manage the timing of enemy group
 *                  entries and shut down after all groups are processed.
 * 
 * @see EnemyGridMapper
 * @see IGroupAttackStrategy
 * @see IEnemyMovement
 * @see IAttackStrategy
 * @see EnemyBehavior
 *
 * @author Brandon Mejia
 * @version 2025-05-07
 */
public class EnterGameGroup implements IGroupAttackStrategy {

    /** List of calculated positions for enemies. */
    private ArrayList<Ponto> positions;
    /** Tracks the current group being processed. */
    private int currentGroup = 0;
    /** Number of enemies in each group. */
    private int[] groupSizes = { 8, 8, 8, 8, 8 };
    /** Delay between group entries in frames. */
    private int groupDelayFrames = 30;
    /** Scheduler for managing timed tasks. */
    private ScheduledExecutorService scheduler;
    /** Pattern defining enemy priorities. */
    private int[][] pattern = {
            { 0, 0, 0, 2, 2, 2, 2, 0, 0, 0 },
            { 0, 3, 3, 2, 1, 1, 2, 3, 3, 0 },
            { 0, 3, 3, 2, 1, 1, 2, 3, 3, 0 },
            { 5, 5, 4, 4, 1, 1, 4, 4, 5, 5 },
            { 5, 5, 4, 4, 1, 1, 4, 4, 5, 5 }
    };
    /** Indicates whether the group attack is complete. */
    private boolean isGroupAttackComplete = false;
    /** Manages the grid of enemies. */
    private EnemyGridMapper enemyGridMapper;

    /** Tracks whether movement patterns have been assigned. */
    private AtomicBoolean patternsAssigned = new AtomicBoolean(false);

    /**
     * Validates the invariants for the `EnterGameGroup` class.
     * Ensures that the provided list of enemies is not null or empty, and that the
     * target object is not null.
     * If validation fails, an error message is printed, and the program exits.
     *
     * @param enemies A list of `IGameObject` instances representing the enemies.
     *                Must not be null or empty.
     * @param target  The target `IGameObject` instance. Must not be null.
     */
    private void invariante(List<IGameObject> enemies, IGameObject target) {
        if (enemies != null && !enemies.isEmpty() && target != null)
            return;

        System.out.println("EnterGameGroup:iv");
        System.exit(0);
    }

    /**
     * Executes the group attack strategy by starting the relocation of enemies.
     *
     * @param enemies List of enemies to be relocated.
     * @param target  The target object to base enemy positions on.
     */
    @Override
    public void execute(List<IGameObject> enemies, IGameObject target) {
        startRelocateEnemies(enemies);
    }

    /**
     * Initializes the strategy by calculating enemy positions based on the target's
     * position.
     *
     * @param enemies List of enemies to be relocated.
     * @param target  The target object to base enemy positions on.
     */
    @Override
    public void onInit(List<IGameObject> enemies, IGameObject target) {
        invariante(enemies, target);
        this.enemyGridMapper = new EnemyGridMapper(this.pattern);
        this.positions = enemyGridMapper.calculateEnemyPositions(target.transform().position(), enemies);
    }

    /**
     * Starts the process of relocating enemies in groups at fixed intervals.
     *
     * @param enemies List of enemies to be relocated.
     */
    public synchronized void startRelocateEnemies(List<IGameObject> enemies) {
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (enemies) {
                relocateEnemies(enemies);
            }
        }, 1500, groupDelayFrames * 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Relocates enemies in the current group to their designated positions.
     * Assigns movement and attack patterns after all groups are relocated.
     *
     * @param enemies List of enemies to be relocated.
     */
    private void relocateEnemies(List<IGameObject> enemies) {
        currentGroup++;
        if (currentGroup > 5) {
            if (patternsAssigned.compareAndSet(false, true)) {
                this.assignMovementPatterns(enemies);
                this.assignAttackPatterns(enemies);
                this.isGroupAttackComplete = true;
            }
            return;
        }
        for (int i = 0; i < groupSizes[currentGroup - 1]; i++) {
            final int enemyIndex = i + ((currentGroup - 1) * 8); // 8 enemies per group
            scheduler.schedule(() -> {
                GameObject enemy = (GameObject) enemies.get(enemyIndex);
                Ponto targetPosition = positions.get(enemyIndex);

                IEnemyMovement movement;
                if (enemy.transform().angle() == 270) { // Coming from above
                    EnterOverTopMovement bottom = new EnterOverTopMovement();
                    bottom.setFinalTarget(targetPosition);
                    boolean isRightToLeft = enemy.transform().position().x() > 0.0; // Check if the enemy is coming from
                                                                                    // the right
                    bottom.setDirection(isRightToLeft);
                    movement = bottom;
                } else {
                    EnterSideMovement leftOrRight = new EnterSideMovement();
                    leftOrRight.setFinalTarget(targetPosition);
                    boolean isRightToLeft = enemy.transform().angle() == 180;
                    leftOrRight.setDirection(isRightToLeft);
                    movement = leftOrRight;
                }

                EnemyBehavior behavior = (EnemyBehavior) enemy.behavior();
                behavior.setMovement(movement);
                movement.setActive(true);
            }, i * 230, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Gets the total number of enemies managed by this strategy.
     *
     * @return The total number of enemies.
     */
    public int getNumberOfEnemies() {
        return 40;
    }

    /**
     * Sets the scheduler for managing timed tasks.
     *
     * @param scheduler The `ScheduledExecutorService` to use for scheduling tasks.
     */
    public void setScheduler(ScheduledExecutorService scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Checks if the group attack is complete.
     *
     * @return true if the group attack is complete, false otherwise.
     */
    public boolean isGroupAttackComplete() {
        return this.isGroupAttackComplete;
    }

    /**
     * Assigns movement patterns to a subset of enemies based on predefined
     * patterns.
     * The method selects one of three movement patterns randomly, retrieves the
     * enemies
     * matching the selected pattern, and applies a circular movement behavior to
     * them.
     *
     * @param enemies A list of `IGameObject` instances representing the enemies.
     */
    private void assignMovementPatterns(List<IGameObject> enemies) {
        // Predefined movement pattern 1
        int[][] movementPattern1 = {
                { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
                { 1, 1, 0, 0, 0, 1, 0, 0, 1, 0 }
        };

        // Predefined movement pattern 2
        int[][] movementPattern2 = {
                { 0, 0, 0, 1, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 1, 0, 1, 0, 0, 0, 1, 0, 0 },
                { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 1, 0, 0, 1, 0, 0 }
        };

        // Predefined movement pattern 3
        int[][] movementPattern3 = {
                { 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
                { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 },
                { 1, 0, 0, 0, 0, 1, 0, 0, 0, 1 }
        };

        // Randomly select one of the three movement patterns
        int[][] movementPattern = switch ((int) (Math.random() * 3)) {
            case 0 -> movementPattern1;
            case 1 -> movementPattern2;
            default -> movementPattern3;
        };

        // Retrieve enemies matching the selected movement pattern
        ArrayList<IGameObject> enemiesToApply = this.enemyGridMapper.getEnemiesFromPattern(movementPattern);

        // Synchronize on the list of enemies to ensure thread safety
        synchronized (enemiesToApply) {
            for (int i = 0; i < enemiesToApply.size(); i++) {
                // Cast the enemy to GameObject and retrieve its behavior
                GameObject enemy = (GameObject) enemiesToApply.get(i);
                EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();

                // Create a new circular movement behavior
                FlyCircleMovement movement = new FlyCircleMovement();

                // Set the movement direction based on the enemy's position
                if (enemy.transform().position().x() > 0)
                    movement.setDirection(true); // Move clockwise
                else
                    movement.setDirection(false); // Move counterclockwise

                // Apply the movement behavior to the enemy and activate it
                enemyBehavior.setMovement(movement);
                movement.setActive(true);
            }
        }
    }

    /**
     * Assigns attack patterns to a subset of enemies based on predefined patterns.
     * The method selects one of two attack patterns randomly, retrieves the enemies
     * matching the selected pattern, and applies a homing shoot attack behavior to
     * them.
     *
     * @param enemies A list of `IGameObject` instances representing the enemies.
     */
    private void assignAttackPatterns(List<IGameObject> enemies) {
        int[][] attackPattern1 = {
                { 0, 0, 0, 1, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
                { 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
        };
        int[][] attackPattern2 = {
                { 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
        };

        int[][] attackPattern = switch ((int) (Math.random() * 2)) {
            case 0 -> attackPattern1;
            default -> attackPattern2;
        };

        ArrayList<IGameObject> enemiesToApply = this.enemyGridMapper.getEnemiesFromPattern(attackPattern);
        synchronized (enemiesToApply) {
            for (int i = 0; i < enemiesToApply.size(); i++) {
                GameObject enemy = (GameObject) enemiesToApply.get(i);
                EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();
                IAttackStrategy attack = new HomingShootAttack();
                enemyBehavior.setAttackStrategy(attack);
                enemyBehavior.startAttack();
            }
        }
    }
}
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
public class EnterGameGroup implements IGroupAttackStrategy
{

    private ArrayList<Ponto> positions; // Calculated positions for enemies
    private int currentGroup = 0; // Tracks the current group being processed
    private int[] groupSizes = { 8, 8, 8, 8, 8 }; // Number of enemies in each group
    private int groupDelayFrames = 50; // Delay between group entries in frames
    private ScheduledExecutorService scheduler; // Scheduler for scheduling group
    private int[][] pattern = { // Pattern defining enemy priorities
            { 0, 0, 0, 2, 2, 2, 2, 0, 0, 0 },
            { 0, 3, 3, 2, 1, 1, 2, 3, 3, 0 },
            { 0, 3, 3, 2, 1, 1, 2, 3, 3, 0 },
            { 5, 5, 4, 4, 1, 1, 4, 4, 5, 5 },
            { 5, 5, 4, 4, 1, 1, 4, 4, 5, 5 }
    };
    private boolean isGroupAttackComplete = false;
    private EnemyGridMapper enemyGridMapper;




    /**
     * Validates the invariants for the `EnterGameGroup` class.
     * Ensures that the provided list of enemies is not null or empty,
     * and that the target object is not null.
     * If the validation fails, an error message is printed, and the program exits.
     *
     * @param enemies A list of `IGameObject` instances representing the enemies. Must not be null or empty.
     * @param target  The target `IGameObject` instance. Must not be null.
     */
    private void invariante(List<IGameObject> enemies, IGameObject target)
    {
        if(enemies != null && !enemies.isEmpty() && target != null)
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
    public void execute(List<IGameObject> enemies, IGameObject target)
    {
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
    public void onInit(List<IGameObject> enemies, IGameObject target)
    {
        invariante(enemies, target);
        this.enemyGridMapper = new EnemyGridMapper(this.pattern);
        this.positions = enemyGridMapper.calculateEnemyPositions(target.transform().position(),enemies);
    }

    /**
     * Starts the process of relocating enemies in groups at fixed intervals.
     *
     * @param enemies List of enemies to be relocated.
     */
    private AtomicBoolean patternsAssigned = new AtomicBoolean(false);

    public synchronized void startRelocateEnemies(List<IGameObject> enemies)
    {
        scheduler.scheduleAtFixedRate(() ->
        {
            synchronized(enemies)
            {
                relocateEnemies(enemies);
            }
        }, 1500, groupDelayFrames * 100, TimeUnit.MILLISECONDS);
    }

    private void relocateEnemies(List<IGameObject> enemies)
    {
        currentGroup++;
        if (currentGroup > 5)
        {
            if (patternsAssigned.compareAndSet(false, true))
            {
                this.assignMovementPatterns(enemies);
                this.assignAttackPatterns(enemies);
                this.isGroupAttackComplete = true;
            }
            return;
        }
        for (int i = 0; i < groupSizes[currentGroup - 1]; i++)
        {
            final int enemyIndex = i + ((currentGroup - 1) * 8); // 8 enemies per group
            scheduler.schedule(() ->
            {
                GameObject enemy = (GameObject) enemies.get(enemyIndex);
                Ponto targetPosition = positions.get(enemyIndex);

                IEnemyMovement movement;
                if (enemy.transform().angle() == 270)
                { // Coming from above
                    EnterOverTopMovement bottom = new EnterOverTopMovement();
                    bottom.setFinalTarget(targetPosition);
                    boolean isRightToLeft = enemy.transform().position().x() > 0.0; // Check if the enemy is coming from the right
                    bottom.setDirection(isRightToLeft);
                    movement = bottom;
                }
                else
                {
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
    public int getNumberOfEnemies()
    {
        return 40;
    }

    public void setScheduler(ScheduledExecutorService scheduler)
    {
        this.scheduler = scheduler;
    }

    public boolean isGroupAttackComplete()
    {
        return this.isGroupAttackComplete;
    }

    /**
     * Assigns movement patterns to enemies based on a given pattern matrix.
     *
     * @param pattern          A 2D array representing the movement pattern.
     */
    private void assignMovementPatterns(List<IGameObject> enemies)
    {
        int[][] movementPattern1 = {
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
            { 1, 1, 0, 0, 0, 1, 0, 0, 1, 0 }
        };
        int[][] movementPattern2 = {
            { 0, 0, 0, 1, 0, 0, 1, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 0, 1, 0, 0, 0, 1, 0, 0 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 0, 1, 0, 0, 1, 0, 0 }
        };
        int[][] movementPattern3 = {
            { 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 1, 0, 0, 0, 0, 1, 0, 0, 0, 1 }
        };

        int[][] movementPattern = switch ((int) (Math.random() * 3))
        {
            case 0 -> movementPattern1;
            case 1 -> movementPattern2;
            default -> movementPattern3;
        };

        ArrayList<IGameObject> enemiesToApply = this.enemyGridMapper.getEnemiesFromPattern(movementPattern); // Obtém os inimigos do pattern
        synchronized(enemiesToApply)
        {
            for (int i = 0; i < enemiesToApply.size(); i++)
            {
                GameObject enemy = (GameObject) enemiesToApply.get(i);
                EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();
                FlyCircleMovement movement = new FlyCircleMovement();

                if(enemy.transform().position().x() > 0)
                    movement.setDirection(true);
                else
                    movement.setDirection(false);

                enemyBehavior.setMovement(movement);
                movement.setActive(true);
            }
        }
    }

    private void assignAttackPatterns(List<IGameObject> enemies)
    {
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

        int[][] attackPattern = switch ((int) (Math.random() * 2))
        {
            case 0 -> attackPattern1;
            default -> attackPattern2;
        };

        ArrayList<IGameObject> enemiesToApply = this.enemyGridMapper.getEnemiesFromPattern(attackPattern);
        synchronized(enemiesToApply)
        {
            for (int i = 0; i < enemiesToApply.size(); i++)
            {
                GameObject enemy = (GameObject) enemiesToApply.get(i);
                EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();
                IAttackStrategy attack = new HomingShootAttack();
                enemyBehavior.setAttackStrategy(attack);
                enemyBehavior.startAttack();
            }
        }
    }
}
package core.behaviorItems;

import java.util.List;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import core.GameObject;
import core.EnemyBehavior;

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
 * @author Brandon Mejia
 * @version 2025-05-07
 */
public class EnterGameGroup implements IGroupAttackStrategy
{

    private final double SPACING = 70; // Spacing between enemy positions
    private ArrayList<Ponto> positions; // Calculated positions for enemies
    private int currentGroup = 0; // Tracks the current group being processed
    private int[] groupSizes = { 8, 8, 8, 8, 8 }; // Number of enemies in each group
    private int groupDelayFrames = 60; // Delay between group entries in frames
    private ScheduledExecutorService scheduler; // Scheduler for scheduling group
    private int[][] pattern = { // Pattern defining enemy priorities
            { 0, 0, 0, 2, 2, 2, 2, 0, 0, 0 },
            { 0, 3, 3, 2, 1, 1, 2, 3, 3, 0 },
            { 0, 3, 3, 2, 1, 1, 2, 3, 3, 0 },
            { 5, 5, 4, 4, 1, 1, 4, 4, 5, 5 },
            { 5, 5, 4, 4, 1, 1, 4, 4, 5, 5 }
    };
    private boolean isGroupAttackComplete = false;




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
    public void onInit(List<IGameObject> enemies, IGameObject target)
    {
        invariante(enemies, target);
        this.positions = calculateEnemyPositions(target.transform().position(), this.pattern, SPACING);
    }

    /**
     * Starts the process of relocating enemies in groups at fixed intervals.
     *
     * @param enemies List of enemies to be relocated.
     */
    public synchronized void startRelocateEnemies(List<IGameObject> enemies)
    {
        scheduler.scheduleAtFixedRate(() ->
        {
            synchronized(enemies)
            {
                relocateEnemies(enemies);
            }
        }, 1000, groupDelayFrames * 100, TimeUnit.MILLISECONDS);
    }

    
    private void relocateEnemies(List<IGameObject> enemies)
    {
        currentGroup++;
        if (currentGroup > 5)
        {
            this.assignAttackPatterns(enemies);
            this.assignMovementPatterns(enemies);

            isGroupAttackComplete = true;

            return;
        }
        for (int i = 0; i < groupSizes[currentGroup - 1]; i++)
        {
            final int enemyIndex = i + ((currentGroup - 1) * 8); // 8 enemies per group
            scheduler.schedule(() -> {
                GameObject enemy = (GameObject) enemies.get(enemyIndex);
                Ponto targetPosition = positions.get(enemyIndex);

                IEnemyMovement movement;
                if (enemy.transform().angle() == 270) { // Coming from above
                    EnterOverTopMovement bottom = new EnterOverTopMovement();
                    bottom.setFinalTarget(targetPosition);
                    boolean isRightToLeft = enemy.transform().position().x() > 0.0; // Check if the enemy is coming from the right
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
            }, i * 210, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Calculates the positions for enemies based on the target's position and the
     * defined pattern.
     *
     * @param playerPosition The position of the target object.
     * @param pattern        The pattern defining enemy priorities.
     * @param spacing        The spacing between enemy positions.
     * @return A list of calculated positions for enemies.
     */
    private ArrayList<Ponto> calculateEnemyPositions(Ponto playerPosition, int[][] pattern, double spacing) {
        @SuppressWarnings("unchecked")
        ArrayList<Ponto>[] priorityGroups = new ArrayList[6];
        for (int i = 1; i <= 5; i++)
            priorityGroups[i] = new ArrayList<>();

        double startX = playerPosition.x() - ((pattern[0].length - 1) * spacing) / 2.0;
        double startY = playerPosition.y() + 660;

        for (int row = 0; row < pattern.length; row++) {
            for (int col = 0; col < pattern[0].length; col++) {
                int priority = pattern[row][col];
                if (priority > 0) {
                    double x = startX + col * spacing;
                    double y = startY - row * spacing;
                    priorityGroups[priority].add(new Ponto(x, y));
                }
            }
        }

        ArrayList<Ponto> positions = new ArrayList<>();
        for (int priority = 1; priority <= 5; priority++) {
            positions.addAll(priorityGroups[priority]);
        }

        return positions;
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
    private void assignMovementPatterns(List<IGameObject> enemies) {
        int[][] movementPattern = {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 0, 0, 1, 0, 0, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 0, 0, 1 }
        };

        // Mapeia as posições do pattern para os índices dos inimigos
        ArrayList<Integer> enemyIndices = new ArrayList<>();
        for (int row = 0; row < movementPattern.length; row++)
        {
            for (int col = 0; col < movementPattern[row].length; col++)
            {
                if (movementPattern[row][col] > 0)
                {
                    int realIndex = findEnemyIndexInOriginalPattern(row, col);
                    if (realIndex >= 0 && realIndex < enemies.size())
                    {
                        enemyIndices.add(realIndex);
                    }
                }
            }
        }

        for (int i = 0; i < enemyIndices.size(); i++)
        {
            final int currentIndex = enemyIndices.get(i);
            scheduler.schedule(() ->
            {
                if (currentIndex < enemies.size())
                {
                    GameObject enemy = (GameObject) enemies.get(currentIndex);
                    EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();
                    FlyCircleMovement movement = new FlyCircleMovement();

                    if(enemy.transform().position().x() > 0)
                        movement.setIsLeft(true);
                    else
                        movement.setIsLeft(false);

                    enemyBehavior.setMovement(movement);

                    

                    movement.setActive(true);

                }
            }, i * 200, TimeUnit.MILLISECONDS);
        }
    }

    private void assignAttackPatterns(List<IGameObject> enemies) {
        int[][] attackPattern = {
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
        };

        // Mapeia as posições do pattern para os índices dos inimigos
        ArrayList<Integer> enemyIndices = new ArrayList<>();
        for (int row = 0; row < attackPattern.length; row++)
        {
            for (int col = 0; col < attackPattern[row].length; col++)
            {
                if (attackPattern[row][col] > 0)
                {
                    // Calcula o índice real do inimigo baseado na sua posição no pattern original
                    int realIndex = findEnemyIndexInOriginalPattern(row, col);
                    if (realIndex >= 0 && realIndex < enemies.size())
                    {
                        enemyIndices.add(realIndex);
                    }
                }
            }
        }

        // Aplica os ataques aos inimigos encontrados
        for (int i = 0; i < enemyIndices.size(); i++)
        {
            final int currentIndex = enemyIndices.get(i);
            scheduler.schedule(() -> {
                if (currentIndex < enemies.size())
                {
                    GameObject enemy = (GameObject) enemies.get(currentIndex);
                    EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();
                    IAttackStrategy attack = new HomingShootAttack();
                    enemyBehavior.setAttack(attack);
                    enemyBehavior.startAttack();
                
                }
            }, i * 200, TimeUnit.MILLISECONDS);
        }
    }

    private int findEnemyIndexInOriginalPattern(int row, int col)
    {
        int count = 0;
        for (int r = 0; r < pattern.length; r++)
        {
            for (int c = 0; c < pattern[r].length; c++)
            {
                if (pattern[r][c] > 0)
                {
                    if (r == row && c == col)
                    {
                        return count;
                    }
                    count++;
                }
            }
        }
        return -1;
    }
}
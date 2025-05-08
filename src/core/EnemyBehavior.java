package core;

import core.behaviorItems.IEnemyMovement;
import core.behaviorItems.ZigzagMovement;
import core.objectsInterface.IGameObject;
import gui.InputEvent;
import core.behaviorItems.IAttackStrategy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The `EnemyBehavior` class defines the behavior of an enemy in the game.
 *
 * <p>
 * This class extends the `Behavior` class and uses the Strategy pattern to
 * dynamically
 * select attack and movement strategies. It also incorporates the Observer
 * pattern
 * (inherited from `Behavior`) to monitor changes in observed objects.
 * </p>
 *
 * @Pre-Conditions:
 *                  - The enemy's game object, transform, and collider must be
 *                  properly initialized.
 *                  - The enemy should be subscribed to a valid observed
 *                  `IGameObject` when required for executing attacks.
 *
 * @Post-Conditions:
 *                   - Attack execution returns a valid game object if an attack
 *                   strategy is set, or null otherwise.
 *                   - Movement is executed based on the active movement
 *                   strategy.
 *
 * @see core.Behavior
 * @see core.behaviorItems.IAttackStrategy
 * @see core.behaviorItems.IGroupAttackStrategy
 * @see core.behaviorItems.IEnemyMovement
 *
 * @Author Brandon Mejia
 * @Version 2025-04-18
 */
public class EnemyBehavior extends Behavior
{

    private IAttackStrategy attackStrategy = null; // The attack strategy used by the enemy
    private IEnemyMovement movement; // The movement strategy used by the enemy
    private static final ScheduledExecutorService localScheduler = Executors.newScheduledThreadPool(1); // Scheduler for
                                                                                                        // timed tasks

    private boolean isAttacking = false; // Indicates whether the enemy is currently attacking
    private long attackDuration = 500; // Attack duration in milliseconds

    /**
     * Default constructor for `EnemyBehavior`.
     * Initializes the movement strategy to a default `ZigzagMovement`.
     */
    public EnemyBehavior()
    {
        super();
        this.movement = new ZigzagMovement();
        // TODO: Implement enemy life logic in the future
    }

    /**
     * Sets the attack strategy for the enemy.
     *
     * @param strategy The attack strategy to use.
     */
    public void setAttack(IAttackStrategy strategy) {
        this.attackStrategy = strategy;
    }

    /**
     * Executes an attack using the currently set attack strategy.
     *
     * @param ie The input event triggering the attack.
     * @return The game object resulting from the attack, or null if no strategy is
     *         set.
     */
    public IGameObject attack(InputEvent ie)
    {
        if (this.attackStrategy != null && !this.isAttacking && this.isEnabled()) {
            // Schedule to reset the attack flag after the specified attack duration
            localScheduler.schedule(this::stopAttack, this.attackDuration, TimeUnit.MILLISECONDS);
            return this.attackStrategy.execute(this.go, this.observedObject);
        } else {
            return null;
        }
    }

    /**
     * Stops the current attack by setting the attacking flag to false.
     */
    public void stopAttack() {
        this.isAttacking = false;
    }

    /**
     * Starts an attack by setting the attacking flag to true.
     */
    public void startAttack() {
        this.isAttacking = true;
    }

    /**
     * Activates or deactivates the movement strategy.
     *
     * @param value `true` to activate movement, `false` to deactivate it.
     */
    public void activateMovement(boolean value)
    {
        if (this.movement != null)
            this.movement.setActive(value);
    }

    /**
     * Checks if the enemy is currently attacking.
     *
     * @return `true` if the enemy is attacking, `false` otherwise.
     */
    public boolean isAttacking() {
        return isAttacking;
    }

    /**
     * Sets the movement strategy for the enemy.
     *
     * @param movement The movement strategy to use.
     */
    public void setMovement(IEnemyMovement movement) {
        if (movement == null)
            return;

        this.movement = movement;
    }

    /**
     * Moves the enemy using the currently set movement strategy.
     * If the movement strategy is active, it updates the enemy's position.
     */
    @Override
    public void move()
    {
        if (movement.isActive())
            movement.move(this.go);

        super.move();
    }

}
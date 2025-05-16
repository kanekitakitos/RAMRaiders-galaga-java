package core;

import core.behaviorItems.IEnemyMovement;
import core.objectsInterface.IGameObject;
import core.objectsInterface.ISoundEffects;
import core.behaviorItems.IAttackStrategy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import gui.IInputEvent;
import assets.ImagesLoader;
import geometry.Ponto;
import java.util.List;
import java.awt.image.BufferedImage;

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
 * @see core.EnemyGroupAttack.IGroupAttackStrategy
 * @see core.behaviorItems.IEnemyMovement
 *
 * @Author Brandon Mejia
 * @Version 2025-04-18
 */
public class EnemyBehavior extends Behavior {

    private IEnemyMovement movement; // The movement strategy used by the enemy
    private static final ScheduledExecutorService localScheduler = Executors.newScheduledThreadPool(1);
    private static List<BufferedImage> explosion = ImagesLoader.loadAnimationFrames("explosion.gif");

    /**
     * Default constructor for `EnemyBehavior`.
     * Initializes the movement strategy to a default `ZigzagMovement`.
     */
    public EnemyBehavior() {
        super();
        this.movement = null;
        // TODO: Implement enemy life logic in the future
    }

    /**
     * Disables the behavior.
     */
    @Override
    public void onDisabled() {
        ISoundEffects soundEffects = this.go.soundEffects();
        if (soundEffects != null)
            soundEffects.playSound("DEATH");

        // Troca o shape para explosão
        try {
            this.go.shape().setFrames(explosion, 100);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        this.go.velocity(new Ponto(0, 0));
        this.go.rotateSpeed(0);
        this.movement = null;
        this.attackStrategy = null;
        // Agenda para desabilitar o objeto após 2 segundos
        localScheduler.schedule(() -> {
            super.onDisabled();
        }, 1, TimeUnit.SECONDS);
    }

    /**
     * Sets the attack strategy for the enemy.
     *
     * @param strategy The attack strategy to use.
     */
    public void setAttackStrategy(IAttackStrategy strategy) {
        this.attackStrategy = strategy;
    }

    /**
     * Executes an attack using the currently set attack strategy.
     *
     * @param ie The input event triggering the attack.
     * @return The game object resulting from the attack, or null if no strategy is
     *         set.
     */
    @Override
    public IGameObject attack(IInputEvent ie) {
        if (this.attackStrategy != null && this.isAttacking && this.isEnabled()) {
            this.stopAttack();
            long minDelay = 1500;
            long maxDelay = 7000;
            long randomDelay = minDelay + (long) (Math.random() * (maxDelay - minDelay));
            // Schedule to reset the attack flag after the specified attack duration
            localScheduler.schedule(() -> {
                this.startAttack();
            }, randomDelay, TimeUnit.MILLISECONDS);

            ISoundEffects soundEffects = this.go.soundEffects();
            if (soundEffects != null)
                soundEffects.playSound("ATTACK");

            return this.attackStrategy.execute(this.go, this.observedObject);
        } else {
            return null;
        }
    }

    /**
     * Updates the behavior logic.
     * Processes input events to handle movement and evasive maneuvers.
     *
     * @param ie The input event to process.
     */
    @Override
    public void onUpdate(IInputEvent ie) {
        if (ie == null)
            return;
        super.onUpdate(ie);

    }

    /**
     * Activates or deactivates the movement strategy.
     *
     * @param value `true` to activate movement, `false` to deactivate it.
     */
    public void activateMovement(boolean value) {
        if (this.movement != null)
            this.movement.setActive(value);

    }

    public IEnemyMovement getMovement() {
        return this.movement;
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
        this.movement = movement;
    }

    /**
     * Moves the enemy using the currently set movement strategy.
     * If the movement strategy is active, it updates the enemy's position.
     */
    @Override
    public void move() {
        if (movement != null && movement.isActive()) {
            movement.move(this.go);
            if (this.movement == null)
                return;

            if (!movement.isActive()) {
                // Generate a random delay between 700 and 2000 milliseconds
                long minDelay = 800;
                long maxDelay = 1500;
                long randomDelay = minDelay + (long) (Math.random() * (maxDelay - minDelay));

                // Schedule to reset the Movement flag after the specified Movement duration
                localScheduler.schedule(() -> {
                    this.movement.setActive(true);
                }, randomDelay, TimeUnit.MILLISECONDS);
            }
        }

        super.move();
    }

}
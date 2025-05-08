package core;

import core.behaviorItems.IAttackStrategy;
import core.behaviorItems.LinearShootAttack;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import gui.IInputEvent;
import gui.InputEvent;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Represents the behavior of a player in the game.
 *
 * <p>
 * This class defines the player's actions, including movement, attacks, and
 * collision handling.
 * It also manages the player's life and invincibility state.
 * </p>
 *
 * @Pre-Conditions:
 *                  - The required dependencies such as the game object,
 *                  transform, and collider must be properly initialized.
 *                  - The attack strategy must be set or a default strategy is
 *                  provided.
 *
 * @Post-Conditions:
 *                   - The player's input events lead to evasive maneuvers,
 *                   attacks, and accurate collision handling.
 *                   - The game object's state is updated accordingly based on
 *                   player actions.
 *
 * @see <a href=
 *      "https://www.youtube.com/watch?v=PJxcxHgmK4w">ScheduledExecutorService</a>
 * @see <a href=
 *      "https://stackoverflow.com/questions/71371997/single-scheduledexecutorservice-instance-vs-multiple-scheduledexecutorservice-in">Single
 *      ScheduledExecutorService instance vs Multiple ScheduledExecutorService
 *      instances</a>
 * @see <a href=
 *      "https://www.blackbox.ai/share/211e5a8d-74e2-4daf-aca4-469f1fa1c7e9">Is
 *      ScheduledExecutorService useful? - BlackBox.ai</a>
 * @see core.Behavior
 *
 * @Author Brandon Mejia
 * @Version 2025-04-18
 */
public class PlayerBehavior extends Behavior
{
    private IAttackStrategy attackStrategy; // The attack strategy used by the player
    private boolean isAttacking = false; // Indicates whether the player is currently attacking

    private int life = 3; // The player's remaining lives
    private boolean isInvincible = false; // Indicates whether the player is invincible
    private final long invincibilityDuration = 2000; // Duration of invincibility in milliseconds

    private final ScheduledExecutorService localScheduler; // Scheduler for handling timed tasks


    /**
     * Constructs a new PlayerBehavior instance.
     * Initializes the default attack strategy and the scheduler.
     */
    public PlayerBehavior()
    {
        super();
        this.attackStrategy = new LinearShootAttack();
        this.localScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Sets the attack strategy for the player.
     *
     * @param attackStrategy The attack strategy to use.
     */
    public void setAttackStrategy(IAttackStrategy attackStrategy)
    {
        if(attackStrategy == null)
            throw new IllegalArgumentException("Attack strategy cannot be null");

        this.attackStrategy = attackStrategy;
    }

    /**
     * Checks if the player is currently invincible.
     *
     * @return True if the player is invincible, false otherwise.
     */
    public boolean isInvincible() {
        return this.isInvincible;
    }

    /**
     * Gets the player's remaining lives.
     *
     * @return The number of lives the player has.
     */
    public int life()
    {
        return this.life;
    }

    /**
     * Updates the behavior logic.
     * Processes input events to handle movement and evasive maneuvers.
     *
     * @param dT The time delta since the last update.
     * @param ie The input event to process.
     */
    @Override
    public void onUpdate(double dT, IInputEvent ie)
    {
        if(ie == null)
            return;

        super.onUpdate(dT, ie);

        this.moveTo(ie);
        this.evasiveManeuver(ie);
    }

    /**
     * Executes an attack using the current attack strategy.
     *
     * @param ie The input event triggering the attack.
     * @return The game object resulting from the attack, or null if no attack is
     *         executed.
     */
    @Override
    public IGameObject attack(IInputEvent ie)
    {
        if (this.attackStrategy == null || ie == null)
            return null;

        if (!this.isAttacking && this.isEnabled() && ie.isActionActive("ATTACK"))
        {
            this.isAttacking = true;

            // Schedule to reset the attack flag after the invincibility duration
            localScheduler.schedule(() -> {
                isAttacking = false;
            }, invincibilityDuration, TimeUnit.MILLISECONDS);

            return this.attackStrategy.execute(this.go, this.observedObject);
        } else {
            return null;
        }
    }

    /**
     * Handles collisions with other game objects.
     * Reduces the player's life and triggers invincibility if applicable.
     *
     * @param collisions A list of game objects that this behavior collided with.
     */
    @Override
    public void onCollision(ArrayList<IGameObject> collisions)
    {
        if(collisions == null || collisions.isEmpty())
            return;

        for (IGameObject go : collisions)
            go.behavior().onDestroy();

        if (this.life > 0 && !this.isInvincible && this.isEnabled()) {
            this.life--;

            this.isInvincible = true;

            // Schedule to reset the invincibility flag after the invincibility duration
            localScheduler.schedule(() ->
            {
                this.isInvincible = false;
            }, invincibilityDuration, TimeUnit.MILLISECONDS);

            System.out.println("Player hit! Remaining life: " + this.life);
        }

        if (this.life <= 0 && this.isEnabled())
            this.onDestroy();
    }


    /**
     * Moves the player based on the input event.
     *
     * <p>
     * This method processes the input event to determine the direction of movement
     * and updates the player's position accordingly. It also ensures that the
     * player's collider position is updated to match the new position.
     * </p>
     *
     * @param ie The input event containing movement actions. If null, no movement is performed.
     */
    public void moveTo(IInputEvent ie)
    {
        if (ie == null)
            return;

        // Initialize movement deltas
        double deltaX = 0;
        double deltaY = 0;

        // Define the step size for movement
        double moveStep = 2.5;

        // Adjust movement deltas based on input actions
        if (ie.isActionActive("RIGHT"))
            deltaX += moveStep;
        if (ie.isActionActive("LEFT"))
            deltaX -= moveStep;

        // Create a movement vector based on the calculated deltas
        final Ponto moveVector = new Ponto(deltaX, deltaY);

        // Update the game object's position and collider
        this.go.transform().move(moveVector, 0);
        this.go.collider().updatePosicao();
    }

    /**
     * Executes an evasive maneuver based on the input event.
     * Moves the player in the specified direction and temporarily enables
     * invincibility.
     *
     * @param ie The input event triggering the evasive maneuver.
     */
    public void evasiveManeuver(IInputEvent ie)
    {
        if (ie == null || !ie.isActionActive("EVASIVE") || this.isInvincible)
            return;

        // Determine direction based on the input event
        double deltaX = 0;
        double deltaY = 0;

        // Define move step
        double moveStep = 2.5*3;

        if (ie.isActionActive("RIGHT"))
            deltaX += moveStep;
        if (ie.isActionActive("LEFT"))
            deltaX -= moveStep;

        final Ponto evasionVector = new Ponto(deltaX, deltaY);

        this.isInvincible = true;
        // Apply an evasive move based on the calculated direction
        this.go.transform().move(evasionVector, 0);
        // Also update the collider position
        this.go.collider().updatePosicao();

        localScheduler.schedule(() -> {
            this.isInvincible = false;
        }, invincibilityDuration, TimeUnit.MILLISECONDS);
    }
}
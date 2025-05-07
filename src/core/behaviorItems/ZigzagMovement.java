package core.behaviorItems;

import core.GameObject;
import geometry.Ponto;

/**
 * The `ZigzagMovement` class implements the `IEnemyMovement` interface.
 * This class defines a zigzag movement pattern for enemy objects, where the
 * enemy alternates its horizontal movement direction over time.
 *
 * <p>The movement is controlled by an active state, and the zigzag pattern
 * is determined by a time-based calculation.</p>
 *
 * @Pre-Conditions:
 * - The enemy object must not be null.
 * - The enemy object must have a valid velocity property.
 *
 * @Post-Conditions:
 * - When active, the enemy's velocity alternates between positive and negative
 *   horizontal directions based on the time variable.
 * - When inactive, the enemy's velocity remains unchanged.
 *
 * @see IEnemyMovement
 * @see GameObject
 * @see Ponto
 *
 * @author Brandon Mejia
 * @version 2025-04-03
 */
public class ZigzagMovement implements IEnemyMovement
{
    private boolean isActive = false; // Indicates whether the movement is active
    private double time = 0; // Tracks the time for calculating the zigzag pattern

    /**
     * Moves the enemy object in a zigzag pattern if the movement is active.
     * The horizontal velocity alternates direction based on the time variable.
     *
     * @param enemy The `GameObject` representing the enemy to be moved.
     */
    @Override
    public void move(GameObject enemy)
    {
        if (!isActive) return;

        time += 45; // Increment time to determine the movement phase
        double amplitude = 0.5; // Amplitude of the zigzag movement
        double dx = (time <= 90 ? amplitude : amplitude * -1); // Alternate direction
        time = time % 180; // Reset time after a full cycle
        enemy.velocity(new Ponto(dx, 0)); // Update the enemy's velocity
    }

    /**
     * Sets the active state of the zigzag movement.
     * When activated, the time variable is reset to start the movement pattern.
     *
     * @param active `true` to activate the movement, `false` to deactivate it.
     */
    @Override
    public void setActive(boolean active)
    {
        this.isActive = active;
        if (active)
            time = 0; // Reset time when movement is activated
    }

    /**
     * Checks if the zigzag movement is currently active.
     *
     * @return `true` if the movement is active, `false` otherwise.
     */
    @Override
    public boolean isActive()
    {
        return isActive;
    }
}
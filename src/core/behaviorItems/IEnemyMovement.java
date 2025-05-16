package core.behaviorItems;

import core.GameObject;

/**
 * Interface representing the movement behavior of an enemy in the game.
 * This interface defines methods to control the movement and activation state
 * of an enemy object.
 *
 * <p>
 * Implementations of this interface should provide specific movement patterns
 * for enemy objects and manage their active state. The active state determines
 * whether the enemy is allowed to move or not.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * IEnemyMovement movement = new ZigZagMovement();
 * movement.setActive(true);
 * movement.move(enemyObject);
 * </pre>
 *
 * @author Brandon Mejia
 * 
 * @version 2025-03-25
 */
public interface IEnemyMovement {
    /**
     * Moves the specified enemy object according to the implemented movement
     * pattern.
     * This method is called to update the position of the enemy based on the logic
     * defined in the implementing class.
     *
     * @param enemy The enemy GameObject to be moved. This object represents the
     *              enemy entity in the game world and contains its current state.
     */
    void move(GameObject enemy);

    /**
     * Sets the active state of the enemy movement behavior.
     * When the movement is active, the enemy will move according to the implemented
     * pattern. When inactive, the enemy remains stationary.
     *
     * @param active true to activate the movement, false to deactivate it.
     */
    void setActive(boolean active);

    /**
     * Checks if the enemy movement behavior is currently active.
     * This method allows querying the current state of the movement behavior.
     *
     * @return true if the movement is active, false otherwise.
     */
    boolean isActive();

    void setDirection(boolean direction);
}
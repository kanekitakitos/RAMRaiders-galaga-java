package core.behaviorItems;

import java.util.List;
import core.objectsInterface.IGameObject;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Interface for defining group attack strategies.
 * This interface allows for the implementation of attack behaviors involving
 * multiple enemies
 * targeting a single target. It follows the Strategy design pattern to enable
 * dynamic assignment
 * of group attack behaviors.
 *
 * <p>
 * Key Features:
 * </p>
 * <ul>
 * <li>Supports initialization of group attack behavior with a list of enemies
 * and a target.</li>
 * <li>Defines the execution logic for coordinated attacks by multiple
 * enemies.</li>
 * <li>Provides a method to retrieve the number of enemies involved in the
 * attack.</li>
 * </ul>
 *
 * @preConditions:
 *                 - The list of enemies must not be null or empty.
 *                 - Each enemy in the list must be a valid `IGameObject`
 *                 instance.
 *                 - The target must be a valid `IGameObject` instance.
 *
 * @postConditions:
 *                  - The group attack strategy will be initialized and
 *                  executed, potentially modifying the state
 *                  of the enemies and/or the target.
 *
 * @see IGameObject
 *
 * @author Brandon Mejia
 * @version 2025-05-07
 */
public interface IGroupAttackStrategy {
    /**
     * Initializes the group attack strategy.
     * This method is called to set up the attack behavior with the specified list
     * of enemies
     * and the target.
     *
     * @param enemies A list of `IGameObject` instances representing the enemies
     *                involved in the attack.
     * @param target  The target `IGameObject` that the enemies will attack.
     * @throws IllegalArgumentException if the list of enemies is null or empty, or
     *                                  if the target is null.
     */
    void onInit(List<IGameObject> enemies, IGameObject target);

    /**
     * Executes the group attack strategy.
     * This method defines the behavior of the attack, coordinating the actions of
     * the enemies
     * to target the specified object.
     *
     * @param enemies A list of `IGameObject` instances representing the enemies
     *                involved in the attack.
     * @param target  The target `IGameObject` that the enemies will attack.
     * @throws IllegalArgumentException if the list of enemies is null or empty, or
     *                                  if the target is null.
     */
    void execute(List<IGameObject> enemies, IGameObject target);

    /**
     * Retrieves the number of enemies involved in the group attack.
     *
     * @return The number of enemies as an integer.
     */
    int getNumberOfEnemies();


    /**
     * Checks if the group attack strategy has completed its execution.
     * This method determines whether all enemies have finished their attack formation
     * and positioning sequence.
     *
     * @return true if the group attack sequence is complete, false otherwise
     * 
     */
    boolean isGroupAttackComplete();


    /**
     * Sets the scheduler service for managing timed operations in the group attack strategy.
     * This method provides the strategy with a scheduler to coordinate the timing of enemy
     * movements and group formations.
     *
     * @param scheduler The ScheduledExecutorService instance to be used for timing operations.
     *                 Must not be null to ensure proper execution of timed enemy movements.
     * 
     * @see java.util.concurrent.ScheduledExecutorService
     */
    void setScheduler(ScheduledExecutorService scheduler);
}
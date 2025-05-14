package core.objectsInterface;

import core.Shape;

/**
 * The IGameObject interface defines the contract for game objects in the game
 * world.
 * A game object represents an entity that can be manipulated in the game,
 * including
 * movement, rotation, and scaling.
 *
 * @author Brandon Mejia
 *
 * @version 2025-03-25
 */
public interface IGameObject {
    /**
     * Gets the name of the game object.
     *
     * @return The name of the game object.
     */
    String name();

    /**
     * Gets the transform of the game object.
     *
     * @return The transform of the game object.
     */
    ITransform transform();

    /**
     * Gets the collider of the game object.
     *
     * @return The collider of the game object.
     */
    ICollider collider();
/**
     * Gets the shape of the game object.
     *
     * @return The `Shape` instance representing the visual appearance of the game object.
     */
    Shape shape();

    /**
     * Gets the behavior of the game object.
     *
     * @return The `IBehavior` instance defining the logic and actions of the game object.
     */
    IBehavior behavior();

    /**
     * Updates the game object.
     * This method is called during the game loop to update the state of the game object.
     */
    void onUpdate();

    /**
     * Sets the sound effects for the game object.
     *
     * @param soundEffects The `ISoundEffects` instance to associate with the game object.
     */
    void setSoundEffects(ISoundEffects soundEffects);

    /**
     * Gets the sound effects associated with the game object.
     *
     * @return The `ISoundEffects` instance managing the sound effects of the game object.
     */
    ISoundEffects soundEffects();

}
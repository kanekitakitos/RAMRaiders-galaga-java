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
 * @author Gabriel Pedroso
 * @author Miguel Correia
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

    Shape shape();

    IBehavior behavior();

    void onUpdate();

}
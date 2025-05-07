package core.objectsInterface;

import java.util.ArrayList;
import gui.IInputEvent;

/**
 * Interface representing the behavior of a game object.
 * Defines the lifecycle methods and interaction mechanisms for game objects.
 */
public interface IBehavior {

    /**
     * Gets the associated game object.
     *
     * @return The game object associated with this behavior.
     */
    IGameObject gameObject();

    /**
     * Sets the associated game object.
     *
     * @param go The game object to associate with this behavior.
     */
    void gameObject(IGameObject go);

    /**
     * Called when the behavior is initialized.
     * This method is used to set up any necessary state or resources.
     */
    void onInit();

    /**
     * Called on each update cycle to process behavior logic.
     *
     * @param dT The time delta since the last update.
     * @param ie The input event to process during this update.
     */
    void onUpdate(double dT, IInputEvent ie);

    /**
     * Called when the behavior is destroyed.
     * This method is used to clean up resources or perform final actions.
     */
    void onDestroy();

    /**
     * Handles collisions with other game objects.
     *
     * @param collisions A list of game objects that this behavior collided with.
     */
    void onCollision(ArrayList<IGameObject> collisions);

    /**
     * Called when the behavior is enabled.
     * This method is used to activate the behavior.
     */
    void onEnabled();

    /**
     * Called when the behavior is disabled.
     * This method is used to deactivate the behavior.
     */
    void onDisabled();

    /**
     * Subscribes an observer to this behavior.
     * Used for implementing the observer pattern.
     *
     * @param observer The game object to subscribe as an observer.
     */
    void subscribe(IGameObject observer);

    /**
     * Unsubscribes the current observer from this behavior.
     * Used for implementing the observer pattern.
     */
    void unsubscribe();

    /**
     * Gets the current observer of this behavior.
     *
     * @return The game object currently observing this behavior.
     */
    IGameObject getObserver();

    /**
     * Executes an attack action based on the provided input event.
     *
     * @param ie The input event triggering the attack.
     * @return The game object resulting from the attack, or null if no attack is
     *         performed.
     */
    IGameObject attack(IInputEvent ie);
}
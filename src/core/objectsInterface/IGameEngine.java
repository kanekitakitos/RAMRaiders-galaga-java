package core.objectsInterface;

/**
 * The IGameEngine interface provides methods to manage game objects,
 * handle their states, and perform game operations such as running
 * the game loop and checking for collisions.
 */
public interface IGameEngine
{
    /**
     * Adds a game object to the list of enabled objects.
     * @param go The game object to be enabled.
     */
    void addEnable(IGameObject go);

    /**
     * Adds a game object to the list of disabled objects.
     * @param go The game object to be disabled.
     */
    void addDisable(IGameObject go);

    /**
     * Enables a game object, allowing it to be updated and checked for collisions.
     * @param go The game object to enable.
     */
    void enable(IGameObject go);

    /**
     * Disables a game object, preventing it from being updated or checked for collisions.
     * @param go The game object to disable.
     */
    void disable(IGameObject go);

    /**
     * Checks if a game object is enabled.
     * @param go The game object to check.
     * @return True if the game object is enabled, false otherwise.
     */
    boolean isEnabled(IGameObject go);

    /**
     * Checks if a game object is disabled.
     * @param go The game object to check.
     * @return True if the game object is disabled, false otherwise.
     */
    boolean isDisabled(IGameObject go);

    /**
     * Destroys a game object, whether it is enabled or disabled.
     * Precondition: go != null
     * Postcondition: go.onDestroy() is called.
     * @param go The game object to destroy.
     */
    void destroy(IGameObject go);

    /**
     * Destroys all game objects.
     * Postcondition: onDestroy() is called for each game object.
     */
    void destroyAll();

    /**
     * Generates a new frame by getting user input, updating enabled game objects,
     * checking for collisions, and updating the UI.
     * Postcondition: UI.input() is called, Behaviour.onUpdate() is called for all enabled objects,
     * Behaviour.onCollision() is checked, and UI.draw() is updated.
     */
    void run();

    /**
     * Checks for collisions among all enabled game objects.
     * Postcondition: Behavior.onCollision(go) is called for all enabled game objects,
     * passing in the list of objects that collided with each game object.
     */
    void checkCollision();
}

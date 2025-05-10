package core;

import java.util.ArrayList;

import core.objectsInterface.IBehavior;
import core.objectsInterface.IGameObject;
import gui.IInputEvent;
import gui.InputEvent;

/**
 * Represents the behavior logic for a GameObject in the game.
 *
 * @Pre-Conditions:
 *                  - The associated GameObject (go) must not be null when
 *                  methods like move(), rotate(), or scale() are called.
 *                  - Observed objects must be valid IGameObject instances when
 *                  subscribing.
 *
 * @Post-Conditions:
 *                   - The behavior is initialized, updated, or destroyed as per
 *                   the game logic.
 *                   - The associated GameObject's transform and collider are
 *                   updated during movement, rotation, or scaling.
 *                   - Observed objects are managed correctly using the observer
 *                   pattern.
 *
 *                   This class encapsulates the logic that controls how a
 *                   GameObject acts, moves, rotates, scales,
 *                   and responds to collisions or events. It is designed to be
 *                   extended for specific behaviors
 *                   (e.g., player, enemy, projectile).
 *
 *                   Additionally, this class implements basic methods for the
 *                   Observer design pattern,
 *                   allowing a Behavior to observe another IGameObject
 *                   (typically the player) and react accordingly.
 *                   The observer mechanism is kept simple: each Behavior can
 *                   observe a single IGameObject.
 *
 * @see IBehavior
 * @see IGameObject
 * @see Transform
 * @see Collider
 * @see InputEvent
 * @see IInputEvent
 *
 * @Author Brandon Mejia
 * @Version 2025-04-16
 */
public class Behavior implements IBehavior
{
    protected GameObject go; // The GameObject associated with this behavior
    protected boolean isEnabled; // Indicates whether the behavior is enabled

    // The GameObject this behavior is observing
    protected IGameObject observedObject;

    /**
     * Default constructor for the Behavior class.
     * Initializes the behavior with no associated GameObject and sets it as
     * disabled.
     */
    public Behavior()
    {
        this.go = null;
        this.isEnabled = false;
        this.observedObject = null;
    }

    /**
     * Gets the GameObject associated with this behavior.
     *
     * @return The associated GameObject.
     */
    @Override
    public IGameObject gameObject() {
        return this.go;
    }

    /**
     * Sets the GameObject associated with this behavior.
     *
     * @param go The GameObject to associate with this behavior.
     */
    @Override
    public void gameObject(IGameObject go) {
        this.go = (GameObject) go;
    }

    /**
     * Initializes the behavior.
     * This method is called when the behavior is first enabled.
     */
    @Override
    public void onInit() {
        this.onEnabled();
    }

    /**
     * Updates the behavior logic.
     *
     * @param dT The time delta since the last update.
     * @param ie The input event to process.
     */
    @Override
    public void onUpdate(double dT, IInputEvent ie) {
        this.update();
        this.go.shape().updateAnimation();
    }

    /**
     * Destroys the behavior.
     * This method is called when the behavior is no longer needed.
     */
    @Override
    public void onDestroy() {
        this.onDisabled();
        // **** no futuro, fazer alguma animação ou outros metodos como explodir */
    }

    /**
     * Handles collisions with other GameObjects.
     *
     * @param collisions A list of GameObjects that this behavior collided with.
     */
    @Override
    public void onCollision(ArrayList<IGameObject> collisions)
    {
        for (IGameObject go : collisions)
                {
                    if(go.name().contains("Player") && go.behavior().isEnabled())
                        continue;
                    else
                        go.behavior().onDisabled(); // Destroy the enemy if it collides with the player or other object
                }

        this.onDestroy();
    }

    /**
     * Enables the behavior.
     */
    @Override
    public void onEnabled() {
        this.isEnabled = true;
    }

    /**
     * Disables the behavior.
     */
    @Override
    public void onDisabled() {
        this.isEnabled = false;
    }

    /**
     * Checks if the behavior is enabled.
     *
     * @return True if the behavior is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled()
    {
        return this.isEnabled;
    }

    // -------------------------------------------------------------------------------------
    // Observer pattern methods

    /**
     * Subscribes this behavior to observe a specific GameObject.
     *
     * @param observer The GameObject to observe.
     */
    @Override
    public void subscribe(IGameObject observer) {
        this.observedObject = observer;
    }

    /**
     * Unsubscribes this behavior from observing any GameObject.
     */
    @Override
    public void unsubscribe() {
        this.observedObject = null;
    }

    /**
     * Gets the GameObject being observed by this behavior.
     *
     * @return The observed GameObject.
     */
    public IGameObject getObserver() {
        return this.observedObject;
    }

    // -------------------------------------------------------------------------------------

    /**
     * Scales the associated GameObject by its scale difference.
     * Updates both the transform and the collider.
     */
    public void scale() {
        if (go.scaleDiff() != 0) {
            go.transform().scale(go.scaleDiff);
            go.collider().updateEscalar();
        }
    }

    /**
     * Rotates the associated GameObject by its rotation speed.
     * Updates both the transform and the collider.
     */
    public void rotate() {
        go.transform().rotate(go.rotateSpeed);
        go.collider().updateRotacao();
    }

    /**
     * Moves the associated GameObject to a new position and layer.
     * Updates both the transform and the collider.
     */
    public void move() {
        go.transform().move(go.velocity, go.velocityLayer);
        go.collider().updatePosicao();
    }

    /**
     * Updates the associated GameObject's state by performing movement, rotation,
     * and scaling.
     * Sequentially calls rotate(), scale(), and move() to update the transform and
     * collider.
     */
    private void update() {
        this.rotate();
        this.scale();
        this.move();

    }

    /**
     * Handles the attack logic for the associated GameObject.
     *
     * @param ie The input event triggering the attack.
     * @return The resulting GameObject from the attack, or null if not applicable.
     */
    public IGameObject attack(IInputEvent ie) {
        return null;
    }

    /**
     * Returns a string representation of the behavior.
     *
     * @return A string containing the behavior's state and associated objects.
     */
    @Override
    public String toString() {
        return "Behavior{" +
                "go=" + go +
                ", isEnabled=" + isEnabled +
                ", observedObject=" + observedObject +
                '}';
    }
}
package core;

import core.objectsInterface.*;
import core.objectsInterface.ICollider;
import core.objectsInterface.IGameObject;
import geometry.Ponto;

/**
 * Represents a game object in the game world.
 *
 * @preConditions:
 *                 - The parameters 'name', 'transform', and 'collider' must not
 *                 be null.
 *                 - The 'transform' must contain a valid position, rotation,
 *                 and scale.
 *                 - The 'collider' must be a valid collider associated with the
 *                 transform.
 *
 * @postConditions:
 *                  - A GameObject is instantiated with its associated name,
 *                  transform, collider, behavior, and shape.
 *                  - Associate the behavior with the GameObject and call
 *                  onInit() to initialize the connection.
 *                  - The provided collider is immediately updated.
 *                  - The GameObject is ready to be initialized and updated.
 *
 *                  Each GameObject encapsulates its own transform (position,
 *                  rotation, scale), collider (for collision detection),
 *                  shape (for rendering), and behavior (logic and actions). It
 *                  also stores movement and transformation parameters
 *                  such as velocity, velocityLayer, rotateSpeed, and scaleDiff.
 *
 *                  GameObjects are managed by the GameEngine and interact with
 *                  other objects through their behaviors and colliders.
 *
 *                  Typical usage:
 *                  - Player, enemies, projectiles, and other entities are all
 *                  GameObjects.
 *                  - Each GameObject can be updated, moved, rotated, scaled,
 *                  and checked for collisions.
 *
 * @see Transform
 * @see Collider
 * @see Shape
 * @see Behavior
 * @see GameEngine
 *
 * @author Brandon Mejia
 * @version 2025-03-25
 */
public class GameObject implements IGameObject {
    final private String name; // The name of the game object
    final private Transform transform; // The transform of the game object
    final private Collider collider; // The collider of the game object
    final private Behavior behaviour; // The behavior of the game object
    final private Shape shape; // The shape of the game object
    private ISoundEffects soundEffects; // The sound effects of the game object

    protected Ponto velocity; // The velocity of the game object
    protected int velocityLayer; // The velocity layer of the game object
    protected double rotateSpeed; // The rotation speed of the game object
    protected double scaleDiff; // The scale difference of the game object

    /**
     * Validates the invariants for the `GameObject` class.
     * Ensures that the provided parameters are not null.
     * If any parameter is null, an error message is printed, and the program exits.
     *
     * @param name      The name of the game object. Must not be null.
     * @param transform The transform of the game object. Must not be null.
     * @param collider  The collider of the game object. Must not be null.
     * @param behaviour The behavior of the game object. Must not be null.
     * @param shape     The shape of the game object. Must not be null.
     */
    private void invariante(String name, ITransform transform, ICollider collider, IBehavior behaviour, IShape shape) {
        if (name == null || transform == null || collider == null || shape == null || behaviour == null) {
            System.out.println("GameObject:iv");
            System.exit(0);
        }
    }

    /**
     * Constructs a GameObject instance with the specified name, transform, and
     * collider.
     * The collider is associated with the game object's transform.
     *
     * @param name      The name of the game object.
     * @param transform The transform of the game object.
     * @param collider  The collider of the game object.
     * @param behaviour The behavior of the game object.
     * @param shape     The shape of the game object.
     * @throws IllegalArgumentException if any of the parameters 'name',
     *                                  'transform', or 'collider' is null.
     */
    public GameObject(String name, Transform transform, Collider collider, Behavior behaviour, Shape shape) {

        invariante(name, transform, collider, behaviour, shape);

        this.name = name;
        this.transform = transform;
        this.collider = collider;
        this.behaviour = behaviour;
        this.shape = shape;

        this.velocity = new Ponto(0.0, 0.0);
        this.velocityLayer = 0;
        this.rotateSpeed = 0;
        this.scaleDiff = 0;
    }

    /**
     * Initializes the game object.
     * This method is called when the game object is created.
     *
     * It sets the game object to be enabled and calls the onInit method of its
     * behavior.
     */
    public void onInit() {
        this.collider.onUpdateCollider();
        this.behaviour.gameObject(this);
        this.shape.setlogicalWidthAndHeight(this.collider.getLogicalWidth(), this.collider.getLogicalHeight());
    }

    /**
     * Sets the sound effects for the game object.
     *
     * @param soundEffects The sound effects to associate with the game object.
     */
    public void setSoundEffects(ISoundEffects soundEffects) {
        this.soundEffects = soundEffects;
    }

    /**
     * Retrieves the sound effects associated with the game object.
     *
     * @return The sound effects of the game object.
     */
    public ISoundEffects soundEffects() {
        return this.soundEffects;
    }

    /**
     * Gets the name of the game object.
     *
     * @return The name of the game object.
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Gets the transform of the game object.
     *
     * @return The transform of the game object.
     */
    @Override
    public Transform transform() {
        return this.transform;
    }

    /**
     * Gets the collider of the game object.
     *
     * @return The collider of the game object.
     */
    @Override
    public ICollider collider() {
        return this.collider;
    }

    /**
     * Gets the shape of the game object.
     *
     * @return The shape of the game object.
     */
    @Override
    public Shape shape() {
        return this.shape;
    }

    /**
     * Gets the behavior of the game object.
     *
     * @return The behavior of the game object.
     */
    @Override
    public IBehavior behavior() {
        return this.behaviour;
    }

    /**
     * Updates the game object.
     * This method calls the update method of the behavior.
     */
    public void onUpdate() {
        if (this.behaviour == null)
            return;

        this.behaviour.onUpdate(null);
    }

    /**
     * Returns a string representation of the game object.
     * The string includes the object's name, transform, and collider information.
     *
     * @return A string representation of the game object.
     */
    @Override
    public String toString() {
        return (this.name + "\n" + this.transform + "\n" + this.collider);
    }

    /**
     * Gets the velocity of the game object.
     *
     * @return The velocity of the game object as a Ponto object.
     */
    public Ponto velocity() {
        return this.velocity;
    }

    /**
     * Gets the velocity layer of the game object.
     *
     * @return The velocity layer of the game object as an integer.
     */
    public int velocityLayer() {
        return this.velocityLayer;
    }

    /**
     * Gets the rotation speed of the game object.
     *
     * @return The rotation speed of the game object as a double.
     */
    public double rotateSpeed() {
        return this.rotateSpeed;
    }

    /**
     * Gets the scale difference of the game object.
     *
     * @return The scale difference of the game object as a double.
     */
    public double scaleDiff() {
        return this.scaleDiff;
    }

    /**
     * Sets the velocity of the game object.
     *
     * @param velocity The new velocity to set, represented as a Ponto object.
     */
    public void velocity(Ponto velocity) {
        this.velocity = velocity;
    }

    /**
     * Sets the velocity layer of the game object.
     *
     * @param velocityLayer The new velocity layer to set, represented as an
     *                      integer.
     */
    public void velocityLayer(int velocityLayer) {
        this.velocityLayer = velocityLayer;
    }

    /**
     * Sets the rotation speed of the game object.
     *
     * @param rotateSpeed The new rotation speed to set, represented as a double.
     */
    public void rotateSpeed(double rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
    }

    /**
     * Sets the scale difference of the game object.
     *
     * @param scaleDiff The new scale difference to set, represented as a double.
     */
    public void scaleDiff(double scaleDiff) {
        this.scaleDiff = scaleDiff;
    }
}
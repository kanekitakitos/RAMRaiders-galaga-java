package core.behaviorItems;

import assets.ImagesLoader;
import core.*;
import core.objectsInterface.IGameObject;
import geometry.Poligono;
import geometry.Ponto;
import java.util.Random;

/**
 * The `LinearShootAttack` class implements the `IAttackStrategy` interface.
 * This class defines a linear shooting attack strategy where a bullet is
 * created
 * and initialized when the `execute` method is called.
 *
 * <p>
 * It follows the Strategy design pattern to define the behavior of a shooting
 * attack.
 * </p>
 *
 * @Pre-Conditions:
 *                  - The attacker must not be null.
 *                  - The attacker object must have a valid transform with a
 *                  defined angle.
 *                  - The attacker's angle determines if the bullet is for the
 *                  player (0 to 180) or for the enemy (>180).
 *
 * @Post-Conditions:
 *                   - A bullet with a rectangular shape is created.
 *                   - The bullet is positioned using a fixed offset based on
 *                   whether it is a player or enemy bullet.
 *                   - The bullet's transform, velocity, and naming follow the
 *                   conditions defined in the method.
 *
 * @see IAttackStrategy
 * @see GameObject
 * @see Transform
 * @see Ponto
 * @see Poligono
 * @see Shape
 *
 * @author Brandon Mejia
 *
 * @version 2025-04-03
 */
public class LinearShootAttack implements IAttackStrategy {
    private static int index = 0; // Counter for unique bullet naming
    private double SPEED = 10.0; // Default speed of the bullet
    private final Shape shape = new Shape(ImagesLoader.loadImage("laser1.gif"), 100); // Default shape of the bullet

    /**
     * Validates the attacker parameter to ensure it is not null.
     *
     * @param attacker The `IGameObject` initiating the attack.
     * @throws IllegalArgumentException if the attacker is null.
     */
    private void invariante(IGameObject attacker)
    {
        if (attacker != null)
            return;


        System.out.println("LinearShootAttack:vi");
        System.exit(0);
    }

    /**
     * Executes the shooting attack by creating and initializing a bullet object.
     *
     * @param attacker The `IGameObject` that initiates the attack.
     * @param target   The `IGameObject` that is the target of the attack (not used
     *                 in this implementation).
     * @return The created bullet object as an `IGameObject`.
     */
    @Override
    public IGameObject execute(IGameObject attacker, IGameObject target)
    {
        invariante(attacker);

        String name = "Linear_Bullet";

        double angle = attacker.transform().angle();
        Transform transform;
        if (angle > 180) {
            transform = this.enemyTransform(attacker);
            SPEED = -1.0 * SPEED;
        } else {
            transform = this.playerTransform(attacker);
        }

        // Define the shape of the bullet as a rectangle
        Ponto[] rPoints = {
                new Ponto(0, 2),
                new Ponto(8, 2),
                new Ponto(8, 0),
                new Ponto(0, 0)
        };
        GameObject bullet = getGameObject(rPoints, transform, name);

        // Increment the index for unique bullet naming
        index++;

        return bullet;
    }

    /**
     * Creates a transform for enemy bullets.
     *
     * @param attacker The `IGameObject` representing the enemy initiating the
     *                 attack.
     * @return A `Transform` object for the enemy bullet.
     */
    private Transform enemyTransform(IGameObject attacker) {
        // Get the position of the attacker
        Ponto p = attacker.transform().position();

        // Add an offset to spawn the bullet slightly in front of the attacker.
        double offsetDistanceY = -35.0;
        Random random = new Random();
        double randomOffsetX = (random.nextDouble() - 0.5) * 25;

        Ponto bulletStart = new Ponto(p.x() + randomOffsetX, p.y() + offsetDistanceY);

        // Create a new transform for the bullet using the offset position
        return new Transform(bulletStart, attacker.transform().layer() - 1, attacker.transform().angle(),
                attacker.transform().scale());
    }

    /**
     * Creates a transform for player bullets.
     *
     * @param attacker The `IGameObject` representing the player initiating the
     *                 attack.
     * @return A `Transform` object for the player bullet.
     */
    private Transform playerTransform(IGameObject attacker) {
        // Get the position of the attacker
        Ponto p = attacker.transform().position();

        // Add an offset to spawn the bullet slightly in front of the attacker.
        double offsetDistanceY = 35.0;
        Random random = new Random();
        double randomOffsetX = (random.nextDouble() - 0.5) * 25;

        Ponto bulletStart = new Ponto(p.x() + randomOffsetX, p.y() + offsetDistanceY);

        // Create a new transform for the bullet using the offset position
        return new Transform(bulletStart, attacker.transform().layer() + 1, attacker.transform().angle(),
                attacker.transform().scale());
    }

    /**
     * Creates a `GameObject` representing the bullet with the specified properties.
     *
     * @param rPoints   The points defining the rectangular shape of the bullet.
     * @param transform The transform of the bullet.
     * @param name      The name of the bullet.
     * @return The created `GameObject` representing the bullet.
     */
    private GameObject getGameObject(Ponto[] rPoints, Transform transform, String name) {
        Poligono rectangle = new Poligono(rPoints, transform);

        // Set the velocity of the bullet
        Ponto velocity = new Ponto(0.0, SPEED);

        // Create the bullet object with its properties
        Shape shape;
        if (this.shape != null)
            shape = this.shape;
        else
            shape = new Shape(this.shape);

        Behavior behavior = new Behavior();
        GameObject bullet = new GameObject(name + " " + index, transform, rectangle, behavior, shape);
        bullet.velocity(velocity);

        // Initialize and update the bullet
        bullet.onInit();
        bullet.onUpdate();
        return bullet;
    }
}
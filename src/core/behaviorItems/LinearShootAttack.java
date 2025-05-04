package core.behaviorItems;
import core.*;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import geometry.Retangulo;

/**
 * The `ShootAttack` class implements the `IAttackStrategy` interface.
 * This class is responsible for creating a bullet object when the `execute` method is called.
 *
 * @Pre-Conditions:
 * The attacker must not be null.
 * The attacker object must have a valid transform with a defined angle.
 * The attacker's angle determines if the bullet is for the player (0 to 180) or for the enemy (>180).
 * @Post-Conditions:
 * A bullet with a rectangular shape is created.
 * The bullet is positioned using a fixed offset based on whether it is a player or enemy bullet.
 * The bullet's transform, velocity, and naming follow the conditions defined in the method.
 *
 * It follows the Strategy design pattern to define the behavior of a shooting attack.
 *
 * @see IAttackStrategy
 *
 * @author Brandon Mejia
 * @author Gabriel Pedroso
 * @author Miguel Correia
 *
 * @version 2025-04-03
 */
public class LinearShootAttack implements IAttackStrategy
{
    private static int index = 0;
    private double SPEED = 1.0;

    private void invariante(IGameObject attacker)
    {

        if(attacker == null)
        {
            System.out.println("LinearShootAttack:vi");
            throw new IllegalArgumentException("attacker is null");
        }
    }

    /**
     * Executes the shooting attack by creating and initializing a bullet object.
     *
     * @param attacker The `IGameObject` that initiates the attack.
     * @param target The `IGameObject` that is the target of the attack (not used in this implementation).
     * @return The created bullet object as an `IGameObject`.
     */
    @Override
    public IGameObject execute(IGameObject attacker, IGameObject target)
    {
        invariante(attacker);

        String name = "LINEAR_BULLET";

        double angle = attacker.transform().angle();
        Transform transform;
        if(angle > 180 )
        {
            transform = this.enemyTransform(attacker);
            SPEED = -1.0;
        }
        else
            transform = this.playerTransform(attacker);

        // Define the shape of the bullet as a rectangle
        Ponto[] rPoints = {
            new Ponto(-0.4, 0.8),
            new Ponto(0.4, 0.8),
            new Ponto(0.4, -0.8),
            new Ponto(-0.4, -0.8)
        };
        GameObject bullet = getGameObject(rPoints, transform, name);

        // Increment the index for unique bullet naming
        index++;

        return bullet;
    }


    /**
     * Creates a transform for enemy bullets.
     * @param attacker The `IGameObject` representing the enemy initiating the attack.
     * @return A `Transform` object for the enemy bullet.
     */
    private Transform enemyTransform(IGameObject attacker)
    {
        // Get the position of the attacker
        Ponto p = attacker.transform().position();

        // Add an offset to spawn the bullet slightly in front of the attacker.
        double offsetDistance = -2.0;
        Ponto bulletStart = new Ponto(p.x(), p.y() + offsetDistance);

        // Create a new transform for the bullet using the offset position
        return new Transform(bulletStart, attacker.transform().layer(), attacker.transform().angle(), attacker.transform().scale());
    }

    /**
     * Creates a transform for player bullets.
     * @param attacker The `IGameObject` representing the player initiating the attack.
     * @return A `Transform` object for the player bullet.
     */
    private Transform playerTransform(IGameObject attacker)
    {
        // Get the position of the attacker
        Ponto p = attacker.transform().position();

        // Add an offset to spawn the bullet slightly in front of the attacker.
        double offsetDistance = 2.0;
        Ponto bulletStart = new Ponto(p.x(), p.y() + offsetDistance);

        // Create a new transform for the bullet using the offset position
        return new Transform(bulletStart, attacker.transform().layer(), attacker.transform().angle(), attacker.transform().scale());
    }


    private GameObject getGameObject(Ponto[] rPoints, Transform transform, String name)
    {
        Retangulo rectangle = new Retangulo(rPoints, transform);

        // Set the velocity of the bullet
        Ponto velocity = new Ponto(0.0, SPEED);

        // Create the bullet object with its properties
        Shape shape = new Shape();
        Behavior behavior = new Behavior();
        GameObject bullet = new GameObject(name + " " + index, transform, rectangle, behavior, shape);
        bullet.velocity(velocity);

        // Initialize and update the bullet
        bullet.onInit();
        bullet.onUpdate();
        return bullet;
    }
}
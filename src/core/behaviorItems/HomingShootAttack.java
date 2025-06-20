package core.behaviorItems;

import assets.ImagesLoader;
import core.Behavior;
import core.GameObject;
import core.Shape;
import core.Transform;
import core.objectsInterface.IGameObject;
import geometry.Ponto;
import geometry.Poligono;
import java.util.Random;

/**
 * The `HomingShootAttack` class implements the `IAttackStrategy` interface.
 * This class defines a homing shooting attack where a bullet is created and
 * directed
 * towards a target. The bullet follows the target's position dynamically.
 *
 * <p>
 * Key Features:
 * </p>
 * <ul>
 * <li>Ensures attacker and target are valid and distinct objects.</li>
 * <li>Creates a homing bullet with a rectangular shape.</li>
 * <li>Calculates the bullet's trajectory and velocity based on the target's
 * position.</li>
 * <li>Follows the Strategy design pattern for attack behavior.</li>
 * </ul>
 *
 * @preConditions:
 *                 - The attacker and target must not be null.
 *                 - The attacker and target must be different objects.
 *                 - Both objects must have valid transforms with defined
 *                 positions.
 *
 * @postConditions:
 *                  - A homing bullet is created and positioned at a fixed
 *                  offset from the attacker.
 *                  - The bullet's trajectory is calculated to move towards the
 *                  target.
 *                  - The bullet's transform, velocity, and naming are set
 *                  according to the defined logic.
 *
 * @see IAttackStrategy
 *
 * @author Brandon Mejia
 *
 * @version 2025-04-21
 */
public class HomingShootAttack implements IAttackStrategy {
    // Static index to ensure unique naming for bullets
    private static int index = 0;
    private final Shape shape = new Shape(ImagesLoader.loadImage("laser2.gif"), 100); // Default shape of the bullet

    /**
     * Validates the invariants for the attack.
     * Ensures that the attacker and target are not null and are not the same
     * object.
     *
     * @param attacker The attacking game object.
     * @param target   The target game object.
     * @throws IllegalArgumentException if the attacker or target is null, or if
     *                                  they are the same object.
     */
    private void invariante(IGameObject attacker, IGameObject target) {
        if (attacker == null || target == null || attacker.equals(target)) {
            System.out.println("HomingShootAttack:vi");
            System.exit(0);
        }
    }

    /**
     * Executes the homing shoot attack by creating a homing bullet directed at the
     * target.
     *
     * @param attacker The attacking game object.
     * @param target   The target game object.
     * @return The created homing bullet as a GameObject.
     */
    @Override
    public IGameObject execute(IGameObject attacker, IGameObject target) {
        invariante(attacker, target);

        String name = "Homing_Bullet";
        // Define the shape of the bullet as a rectangle
        Ponto[] rPoints = {
                new Ponto(0, 2),
                new Ponto(8, 2),
                new Ponto(8, 0),
                new Ponto(0, 0)
        };
        GameObject bullet = getGameObject(rPoints, attacker, name, target);

        // Increment the index for unique bullet naming
        index++;

        return bullet;
    }

    /**
     * Creates a GameObject representing the homing bullet.
     *
     * @param rPoints  The points defining the shape of the bullet.
     * @param attacker The attacking game object.
     * @param name     The name of the bullet.
     * @param target   The target game object.
     * @return The created bullet as a GameObject.
     */
    private GameObject getGameObject(Ponto[] rPoints, IGameObject attacker, String name, IGameObject target) {
        long minSpeed = 7;
        long maxSpeed = 12;
        long randomSpeed = minSpeed + (long) (Math.random() * (maxSpeed - minSpeed));
        double theta = calculateAngleToTarget(attacker, target);

        Random random = new Random();
        double randomOffsetX = (random.nextDouble() - 0.5) * 25;
        Ponto bulletStart = attacker.transform().position();
        bulletStart = calculateBulletStartPosition(new Ponto(bulletStart.x() + randomOffsetX, bulletStart.y()), theta);
        return createBullet(rPoints, attacker, name, theta, bulletStart, randomSpeed, target.transform().layer());
    }

    /**
     * Calculates the angle from the attacker to the target.
     *
     * @param attacker The attacking game object.
     * @param target   The target game object.
     * @return The angle in radians.
     */
    private double calculateAngleToTarget(IGameObject attacker, IGameObject target) {
        Ponto attackerPosition = attacker.transform().position();
        Ponto targetPosition = target.transform().position();
        double dx = targetPosition.x() - attackerPosition.x();
        double dy = targetPosition.y() - attackerPosition.y();
        return Math.toRadians(new Ponto(dx, dy).theta());
    }

    /**
     * Calculates the starting position of the bullet based on the attacker's
     * position and angle.
     *
     * @param attackerPosition The position of the attacker.
     * @param theta            The angle of the bullet's trajectory.
     * @return The starting position of the bullet as a Ponto.
     */
    private Ponto calculateBulletStartPosition(Ponto attackerPosition, double theta) {
        double offsetDistance = 2;
        return new Ponto(
                attackerPosition.x() + offsetDistance * Math.cos(theta),
                attackerPosition.y() + offsetDistance * Math.sin(theta));
    }

    /**
     * Creates the bullet GameObject with the specified parameters.
     *
     * @param rPoints     The points defining the shape of the bullet.
     * @param attacker    The attacking game object.
     * @param name        The name of the bullet.
     * @param theta       The angle of the bullet's trajectory.
     * @param bulletStart The starting position of the bullet.
     * @param speed       The speed of the bullet.
     * @return The created bullet as a GameObject.
     */
    private GameObject createBullet(Ponto[] rPoints, IGameObject attacker, String name, double theta, Ponto bulletStart,
            double speed, int layer) {
        Transform transform = new Transform(
                bulletStart,
                layer,
                (Math.toDegrees(theta) + 360),
                attacker.transform().scale());

        Poligono rectangle = new Poligono(rPoints, transform);
        Ponto velocity = new Ponto(Math.cos(theta) * speed, Math.sin(theta) * speed);

        Shape shape = new Shape(this.shape);

        Behavior behavior = new Behavior();

        GameObject bullet = new GameObject(
                name + " " + index,
                transform,
                rectangle,
                behavior,
                shape);

        bullet.velocity(velocity);

        bullet.onInit();
        return bullet;
    }
}
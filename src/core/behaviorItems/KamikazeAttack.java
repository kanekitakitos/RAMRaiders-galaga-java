package core.behaviorItems;

import core.GameObject;
import core.objectsInterface.IGameObject;
import geometry.Ponto;

/**
 * Implements a kamikaze attack strategy where enemies perform a circular path
 * before
 * directly charging at their target.
 *
 * <p>
 * This attack pattern consists of two phases:
 * </p>
 * <ol>
 * <li>Initial circular movement to disorient/position the attacker</li>
 * <li>Direct charge towards the target</li>
 * </ol>
 *
 * @preConditions:
 *                 - The attacker and target GameObjects must not be null
 *                 - The attacker and target must be different objects
 *                 - The attacker must have a valid Transform component with
 *                 position and angle
 *                 - The attacker must be castable to GameObject to set velocity
 *
 * @postConditions:
 *                  - The attacker's velocity and rotation are updated based on
 *                  the attack phase
 *                  - During circular phase: attacker moves in a circular path
 *                  with radius 1
 *                  - During charge phase: attacker moves directly towards
 *                  target position
 *                  - The attacker's collider remains valid after movement
 *                  updates
 *
 *                  <p>
 *                  Example usage:
 *                  </p>
 * 
 *                  <pre>
 *                  IAttackStrategy kamikaze = new KamikazeAttack();
 *                  kamikaze.execute(enemyObject, playerObject);
 *                  </pre>
 *
 * @author Brandon Mejia
 * @version 2025-03-25
 */
public class KamikazeAttack implements IAttackStrategy {
    private boolean hadPath = false;

    /**
     * Validates that the attacker and target parameters are valid.
     *
     * @param attacker The GameObject performing the attack
     * @param target   The target GameObject
     */
    private void invariante(IGameObject attacker, IGameObject target) {
        if (attacker == null || target == null || attacker.equals(target)) {
            System.out.println("KamikazeAttack:vi");
            System.exit(0);
        }
    }

    /**
     * Executes the kamikaze attack strategy, updating the attacker's movement.
     * Initially performs circular movement, then charges directly at target.
     *
     * @param attacker The GameObject performing the attack
     * @param target   The target GameObject
     * @return The updated attacker GameObject
     * @throws IllegalArgumentException if attacker or target parameters are invalid
     */
    @Override
    public IGameObject execute(IGameObject attacker, IGameObject target) {
        invariante(attacker, target);

        if (hadPath)
            calculateTargetPosition(attacker, target);
        else {
            // Initial circular movement setup
            double radius = 1; // Radius of the circular path

            // Calculate center point for the circle relative to attacker's position
            Ponto currentPos = attacker.transform().position();
            double centerX = currentPos.x() + radius;
            double centerY = currentPos.y();

            // Calculate circular movement
            double angle = attacker.transform().angle() * Math.PI / 180;
            double newX = centerX + radius * Math.cos(angle);
            double newY = centerY + radius * Math.sin(angle);

            // Create velocity vector for circular movement
            Ponto velocity = new Ponto(
                    (newX - currentPos.x()) * 0.5,
                    (newY - currentPos.y()) * 0.5);

            ((GameObject) attacker).velocity(velocity);
            attacker.transform().rotate(15); // Rotate gradually

            // After completing approximately one circle, switch to direct path
            if (attacker.transform().angle() >= 360)
                hadPath = true;
        }

        return attacker;
    }

    /**
     * Calculates the angle between attacker and target positions.
     *
     * @param attacker The GameObject performing the attack
     * @param target   The target GameObject
     * @return The angle in radians between attacker and target
     */
    private double calculateAngleToTarget(IGameObject attacker, IGameObject target) {
        Ponto attackerPosition = attacker.transform().position();
        Ponto targetPosition = target.transform().position();
        double dx = targetPosition.x() - attackerPosition.x();
        double dy = targetPosition.y() - attackerPosition.y();
        return Math.toRadians(new Ponto(dx, dy).theta());
    }

    /**
     * Updates the attacker's velocity and rotation to move towards the target.
     *
     * @param attacker The GameObject performing the attack
     * @param target   The target GameObject
     */
    private void calculateTargetPosition(IGameObject attacker, IGameObject target) {
        double SPEED = 1.0;
        // Calculate angle to target
        double theta = calculateAngleToTarget(attacker, target);

        // Update attacker's velocity to move towards target
        Ponto velocity = new Ponto(
                Math.cos(theta) * SPEED,
                Math.sin(theta) * SPEED);

        // Update attacker's properties
        ((GameObject) attacker).velocity(velocity);

        double nextAngle = (Math.toDegrees(theta) + 360) - attacker.transform().angle();
        double angle = attacker.transform().angle() + nextAngle;
        attacker.transform().rotate(nextAngle == 0.0 ? 0.0 : angle);
    }
}
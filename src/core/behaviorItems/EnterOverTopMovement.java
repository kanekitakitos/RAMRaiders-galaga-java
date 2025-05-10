package core.behaviorItems;

import core.GameObject;
import core.objectsInterface.IGameObject;
import geometry.Ponto;

/**
 * The EnterOverTopMovement class implements a movement pattern for enemies
 * that involves an arc-like path over the top of the screen, followed by a
 * circular motion, and finally a direct approach to a target position.
 * The movement is divided into three phases and uses time-based interpolation.
 *
 * <p>
 * Phases of movement:
 * </p>
 * <ol>
 * <li>Arc phase: Creates an upward arc motion.</li>
 * <li>Circle phase: Performs a circular movement.</li>
 * <li>Final approach phase: Moves directly to the target position.</li>
 * </ol>
 *
 * @preConditions:
 *                 - The final target must be set using `setFinalTarget()`
 *                 before activating the movement.
 *                 - The direction (goRightToLeft) should be set before
 *                 activation.
 *                 - The initial position is automatically captured when
 *                 movement starts.
 *
 * @postConditions:
 *                  - The enemy will execute the three-phase movement pattern.
 *                  - Movement deactivates automatically when the total time
 *                  exceeds t1 + t2 + t3.
 *                  - Upon deactivation:
 *                  - Time (t) resets to 0.
 *                  - Initial position, arc end position, circle center, and
 *                  final approach start position are cleared.
 *                  - Enemy velocity is set to (0, 0).
 *                  - The enemy's final position matches the final target
 *                  position.
 *                  - The movement pattern remains consistent with the chosen
 *                  direction.
 *
 * @author Brandon Mejia
 * @version 2025-04-25
 */
public class EnterOverTopMovement implements IEnemyMovement
{

    private boolean active = false; // Indicates whether the movement is active
    private boolean goRightToLeft = false; // Direction of the movement (right-to-left or left-to-right)

    private double t = 0.0; // Time parameter controlling the movement
    private Ponto initialPosition; // Initial position of the enemy
    private Ponto finalTarget; // Final target position for the movement

    private double arcRadius = 150; // Radius of the arc phase
    private double circleRadius = 170; // Radius of the circular phase
    private double circleStartAngleDeg = 110.0; // Starting angle for the circular phase

    private final double tIncrement = 0.018; // Time increment for each movement step
    private final double t1 = 0.8; // Duration of the arc phase
    private final double t2 = 1.5; // Duration of the circular phase
    private final double t3 = 1.0; // Duration of the final approach phase

    private Ponto arcEnd; // End position of the arc phase
    private Ponto circleCenter; // Center of the circular phase
    private Ponto finalApproachStart; // Starting position of the final approach phase


    private void invariante(IGameObject go)
    {
        if(go != null)
            return;

        System.out.println("EnterOverTopMovement:iv");
        System.exit(0);
    }

    /**
     * Sets the final target position for the movement.
     *
     * @param target The target position as a `Ponto` object.
     */
    public void setFinalTarget(Ponto target)
    {
        if(target != null)
            this.finalTarget = target;
    }

    /**
     * Sets the direction of the movement.
     *
     * @param goRight If true, the movement goes from right-to-left; otherwise,
     *                left-to-right.
     */
    public void setDirection(boolean goRight) {
        this.goRightToLeft = goRight;
    }

    /**
     * Activates or deactivates the movement.
     * When deactivated, resets all movement parameters.
     *
     * @param active True to activate the movement, false to deactivate it.
     * @throws IllegalStateException If the final target is not set when activating.
     */
    @Override
    public void setActive(boolean active)
    {
        this.active = active;
        if (!active) {
            t = 0.0;
            initialPosition = null;
            arcEnd = null;
            circleCenter = null;
            finalApproachStart = null;
        } else if (finalTarget == null) {
            throw new IllegalStateException("finalTarget must be set before activating FlyOverTopMovement.");
        }
    }

    /**
     * Checks if the movement is currently active.
     *
     * @return True if the movement is active, false otherwise.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Updates the enemy's position and velocity based on the current time parameter
     * `t`.
     * Progresses through the movement phases and deactivates the movement when
     * completed.
     *
     * @param enemy The enemy `GameObject` to move.
     */
    @Override
    public void move(GameObject enemy)
    {
        invariante(enemy);

        if (!active)
            return;

        if (initialPosition == null)
            initialPosition = enemy.transform().position();

        if (t >= t1 + t2 && t <= t1 + t2 + t3 && finalApproachStart == null)
            finalApproachStart = enemy.transform().position();

        Ponto current = getPositionAtTime(t);
        Ponto next = getPositionAtTime(t + tIncrement);
        Ponto velocity = new Ponto(next.x() - current.x(), next.y() - current.y());

        enemy.velocity(velocity);
        enemy.rotateSpeed(3);

        t += tIncrement;
        if (t >= t1 + t2 + t3) {
            if (enemy.transform().angle() != 90) {
                rotateToVertical(enemy);
                enemy.velocity(new Ponto(0, 0));
                return;
            }
            setActive(false);
        }
    }

    /**
     * Calculates the position of the enemy at a given time `localT`.
     * Determines the phase of the movement and delegates to the appropriate
     * handler.
     *
     * @param localT The time parameter.
     * @return The position as a `Ponto` object.
     */
    private Ponto getPositionAtTime(double localT) {
        if (localT < t1)
            return handleArc(localT / t1);

        else if (localT < t1 + t2) {
            if (arcEnd == null || circleCenter == null) {
                arcEnd = handleArc(1.0);
                double circleStartAngle = Math.toRadians(adjustAngle(circleStartAngleDeg));
                double cx = arcEnd.x() - circleRadius * Math.cos(circleStartAngle);
                double cy = arcEnd.y() - circleRadius * Math.sin(circleStartAngle);
                circleCenter = new Ponto(cx, cy);
            }
            return handleCircle((localT - t1) / t2);
        } else {
            if (finalApproachStart == null || finalTarget == null) {
                // Fallback: stay at last valid position
                return handleCircle((localT - t1) / t2);
            }
            return handleFinalApproach((localT - t1 - t2) / t3);
        }
    }

    /**
     * Handles the arc phase of the movement.
     *
     * @param normT Normalized time parameter (0.0 to 1.0).
     * @return The position as a `Ponto` object.
     */
    private Ponto handleArc(double normT) {
        double startAngle = Math.toRadians(adjustAngle(160));
        double endAngle = Math.toRadians(adjustAngle(290));
        double angle = startAngle + (endAngle - startAngle) * normT;

        double centerX = initialPosition.x();
        double centerY = initialPosition.y() + arcRadius;

        double x = centerX + arcRadius * Math.cos(angle);
        double y = centerY + arcRadius * Math.sin(angle);

        return new Ponto(x, y);
    }

    /**
     * Handles the circular phase of the movement.
     *
     * @param normT Normalized time parameter (0.0 to 1.0).
     * @return The position as a `Ponto` object.
     */
    private Ponto handleCircle(double normT) {
        double startAngle = Math.toRadians(adjustAngle(circleStartAngleDeg));
        double sweepAngle = Math.toRadians(230);

        double angle;
        if (goRightToLeft) {
            angle = startAngle + sweepAngle * normT;
        } else {
            angle = startAngle - sweepAngle * normT;
        }

        double x = circleCenter.x() + circleRadius * Math.cos(angle);
        double y = circleCenter.y() + circleRadius * Math.sin(angle);

        return new Ponto(x, y);
    }

    /**
     * Handles the final approach phase of the movement.
     *
     * @param normT Normalized time parameter (0.0 to 1.0).
     * @return The position as a `Ponto` object.
     */
    private Ponto handleFinalApproach(double normT) {
        double t = Math.min(1.0, normT);
        double x = finalApproachStart.x() + (finalTarget.x() - finalApproachStart.x()) * t;
        double y = finalApproachStart.y() + (finalTarget.y() - finalApproachStart.y()) * t;

        return new Ponto(x, y);
    }

    /**
     * Adjusts the angle based on the movement direction.
     *
     * @param angleDeg The angle in degrees.
     * @return The adjusted angle in degrees.
     */
    private double adjustAngle(double angleDeg) {
        return goRightToLeft ? 180.0 - angleDeg : angleDeg;
    }

    /**
     * Rotates the enemy to a vertical orientation (90 degrees).
     * Smoothly adjusts the rotation speed to achieve the desired angle.
     *
     * @param enemy The enemy `GameObject` to rotate.
     */
    private void rotateToVertical(GameObject enemy) {
        double currentAngle = enemy.transform().angle();
        double targetAngle = 90.0;

        // Calculate the shortest difference between angles
        double angleDiff = targetAngle - currentAngle;

        // Normalize to the shortest path
        while (angleDiff > 180)
            angleDiff -= 360;
        while (angleDiff < -180)
            angleDiff += 360;

        // Apply a smooth rotation
        double rotationSpeed = angleDiff * 0.15;

        // Limit the maximum rotation speed
        double maxSpeed = 5.0;
        rotationSpeed = Math.max(-maxSpeed, Math.min(maxSpeed, rotationSpeed));

        // If close to the desired angle, stop rotation
        if (Math.abs(angleDiff) < 0.5) {
            enemy.rotateSpeed(0);
        } else {
            enemy.rotateSpeed(rotationSpeed);
        }
    }
}
package core.behaviorItems;

import core.GameObject;
import core.objectsInterface.IGameObject;
import geometry.Ponto;

/**
 * The EnterSideMovement class implements a movement pattern for enemies
 * that combines a lateral entry, a circular lasso motion, and a final approach
 * to a target position. The movement is time-based and scales dynamically.
 *
 * <p>
 * Phases of movement:
 * </p>
 * <ol>
 * <li>Lateral entry with slight ascent.</li>
 * <li>Circular lasso motion (270° to 700°).</li>
 * <li>Final approach to the target position.</li>
 * </ol>
 *
 * @preConditions:
 *                 - A final target position must be set using
 *                 `setFinalTarget()` before movement activation.
 *                 - The `GameObject` passed to `move()` must not be null.
 *                 - The `GameObject` must have a valid `Transform` component.
 *                 - The initial position is captured when movement starts.
 *                 - The movement direction (`fromRight`) should be set before
 *                 activation.
 *
 * @postConditions:
 *                  - The enemy will execute a three-phase movement:
 *                  1. Lateral entry with slight ascent.
 *                  2. Circular lasso motion (270° to 700°).
 *                  3. Final approach to the target position.
 *                  - Movement deactivates automatically when `t > t1 + t2`.
 *                  - When deactivated, time (`t`) resets to 0 and the initial
 *                  position is cleared.
 *                  - The movement scales according to the defined scale factor
 *                  (2.0).
 *                  - The enemy's velocity is updated each frame based on
 *                  position delta.
 *
 * @author Brandon Mejia
 * @version 2025-04-24
 */
public class EnterSideMovement implements IEnemyMovement {

    private boolean active = false; // Indicates whether the movement is active
    private double t = 0.0; // Current time in the movement sequence
    private Ponto initialPosition; // Initial position of the enemy
    private Ponto finalTarget; // Final target position for the movement
    private boolean fromRight = false; // Direction of the movement (true if from right)

    private final double scale = 2; // Global scale factor for the movement
    private final double tIncrement = 0.018; // Time increment for each movement step

    private final double t1 = 2.2; // Duration of the entry and lasso phases
    private final double t2 = 1.3; // Duration of the final approach phase

    // Base distances (in units before applying the scale)
    private final double baseHorizontalDistance = 140.0; // Horizontal distance for entry
    private final double baseCircleRadius = 60; // Radius of the circular lasso


    /**
     * Validates the invariant for the `EnterSideMovement` class.
     * Ensures that the provided `IGameObject` instance is not null.
     * If the validation fails, an error message is printed, and the program exits.
     *
     * @param go The `IGameObject` instance to validate. Must not be null.
     */
    private void invariante(IGameObject go)
    {
        if(go != null)
            return;

        System.out.println("EnterSideMovement:iv");
        System.exit(0);
    }

    /**
     * Sets the direction of the movement.
     *
     * @param fromRight True if the movement starts from the right-to-left, false
     *                  otherwise.
     */
    public void setDirection(boolean fromRight) {
        this.fromRight = fromRight;
    }

    /**
     * Sets the final target position for the movement.
     *
     * @param target The target position as a `Ponto` object.
     */
    public void setFinalTarget(Ponto target) {
        if(target != null)
            this.finalTarget = target;
    }

    /**
     * Activates or deactivates the movement.
     *
     * @param active True to activate the movement, false to deactivate it.
     */
    @Override
    public void setActive(boolean active)
    {
        this.active = active;
        if (!active)
        {
            t = 0.0; // Reset time
            initialPosition = null; // Clear initial position
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
     * Updates the position and velocity of the enemy based on the current time
     * in the movement sequence. Deactivates the movement when it is complete.
     *
     * @param enemy The `GameObject` representing the enemy.
     */
    @Override
    public void move(GameObject enemy)
    {
        invariante(enemy);

        if (!active)
            return;

        if (initialPosition == null)
            initialPosition = enemy.transform().position();

        Ponto current = getPositionAtTime(t);
        Ponto next = getPositionAtTime(t + tIncrement);
        Ponto velocity = new Ponto(next.x() - current.x(), next.y() - current.y());
        enemy.velocity(velocity);
        enemy.rotateSpeed(this.calculateAngle(enemy, next));

        t += tIncrement;

        if (t > t1 + t2) {
            if (enemy.transform().angle() != 90)
            {
                rotateToVertical(enemy);
                enemy.velocity(new Ponto(0, 0));
                return;
            }

            setActive(false);
        }
    }

    /**
     * Calculates the position of the enemy at a given time in the movement
     * sequence.
     *
     * @param localT The time value for which to calculate the position.
     * @return The position as a `Ponto` object.
     */
    private Ponto getPositionAtTime(double localT) {
        if (localT < t1)
            return handleSideAndLasso(localT / t1);
        else
            return handleFinalApproach((localT - t1) / t2);
    }

    /**
     * Handles the lateral entry and circular lasso phases of the movement.
     *
     * @param normT Normalized time (0 to 1) for this phase.
     * @return The position as a `Ponto` object.
     */
    private Ponto handleSideAndLasso(double normT) {
        int dir = fromRight ? -1 : 1;

        double horizontalDistance = baseHorizontalDistance * scale;
        double circleRadius = baseCircleRadius * scale;

        // Final position of the lateral entry (connection point)
        double entryEndX = initialPosition.x() + dir * horizontalDistance;
        double entryEndY = initialPosition.y() - 0.2 * scale;

        if (normT < 0.3) {
            // Phase 1: Lateral entry with slight ascent
            double progress = normT / 0.3;
            double x = initialPosition.x() + dir * horizontalDistance * progress;
            double y = initialPosition.y() + 0.1 * scale * progress;
            return new Ponto(x, y);
        } else {
            // Phase 2: Circular motion starting at 270°
            double progress = (normT - 0.3) / 0.7;

            // Calculate the center based on the position where 270° should be
            double angleStart = Math.toRadians(270);
            double cx = entryEndX - circleRadius * Math.cos(angleStart);
            double cy = entryEndY - circleRadius * Math.sin(angleStart);

            double angle = fromRight
                    ? angleStart - Math.toRadians(430) * progress
                    : angleStart + Math.toRadians(430) * progress;

            double x = cx + circleRadius * Math.cos(angle);
            double y = cy + circleRadius * Math.sin(angle);
            return new Ponto(x, y);
        }
    }

    /**
     * Handles the final approach phase of the movement.
     *
     * @param normT Normalized time (0 to 1) for this phase.
     * @return The position as a `Ponto` object.
     */
    private Ponto handleFinalApproach(double normT) {
        int dir = fromRight ? -1 : 1;

        double horizontalDistance = baseHorizontalDistance * scale;
        double circleRadius = baseCircleRadius * scale;

        // Final position of the lateral entry
        double entryEndX = initialPosition.x() + dir * horizontalDistance;
        double entryEndY = initialPosition.y() - 0.2 * scale;

        double angleStart = Math.toRadians(270);
        double cx = entryEndX - circleRadius * Math.cos(angleStart);
        double cy = entryEndY - circleRadius * Math.sin(angleStart);

        double angle = fromRight
                ? angleStart - Math.toRadians(430)
                : angleStart + Math.toRadians(430);

        double startX = cx + circleRadius * Math.cos(angle);
        double startY = cy + circleRadius * Math.sin(angle);

        double smoothT = 1 - Math.pow(1 - normT, 2);
        double x = (1 - smoothT) * startX + smoothT * finalTarget.x();
        double y = (1 - smoothT) * startY + smoothT * finalTarget.y();

        return new Ponto(x, y);
    }

    /**
     * Calculates the angle for the enemy's rotation based on its current position
     * and the next position.
     *
     * @param enemy     The `GameObject` representing the enemy.
     * @param nextPoint The next position of the enemy.
     * @return The angle for the enemy's rotation.
     */
    private double calculateAngle(GameObject enemy, Ponto nextPoint)
    {
        Ponto shipCenter = enemy.transform().position();
        double dx = nextPoint.x() - shipCenter.x();
        double dy = nextPoint.y() - shipCenter.y();

        double desiredAngle = new Ponto(dx, dy).theta();
        double currentAngle = enemy.transform().angle();

        double nextAngle = (desiredAngle + 360) - currentAngle;

        while (nextAngle > 180)
            nextAngle -= 360;
        while (nextAngle < -180)
            nextAngle += 360;

        return nextAngle;
    }

    /**
     * Rotates the enemy to a vertical orientation (90 degrees).
     * Smoothly adjusts the rotation speed to achieve the desired angle.
     *
     * @param enemy The `GameObject` to rotate.
     */
    private void rotateToVertical(GameObject enemy) {
        double currentAngle = enemy.transform().angle();
        double targetAngle = 90.0;

        double angleDiff = (targetAngle + 360) - currentAngle;

        while (angleDiff > 180)
            angleDiff -= 360;
        while (angleDiff < -180)
            angleDiff += 360;

        double rotationSpeed = angleDiff * 0.1;

        if (Math.abs(rotationSpeed) > 5.0) {
            rotationSpeed = Math.signum(rotationSpeed) * 5.0;
        }

        enemy.rotateSpeed(rotationSpeed);
    }
}
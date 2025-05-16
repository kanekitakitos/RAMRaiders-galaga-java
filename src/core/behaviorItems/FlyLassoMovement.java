package core.behaviorItems;

import core.GameObject;
import geometry.Ponto;

/**
 * @author Brandon Mejia
 * @version 2025-05-16
 */
public class FlyLassoMovement implements IEnemyMovement
{
    private boolean active = false; // Indicates whether the movement is active
    private double t = 0.0; // Time variable for movement progression
    private Ponto initialPosition; // The initial position of the GameObject
    private boolean goRightToLeft = false; // Direction of the circular loop
    private final double scale = 7; // Scaling factor for movement parameters
    private final double tIncrement = 0.018; // Time increment for each movement step
    final private double t1 = 2.5; // Duration of vertical drop
    final private double t2 = 2.0; // Duration of circular loop

    private boolean initialRotationComplete = false; // Flag for initial rotation

    /**
     * Sets the direction of the circular loop.
     * 
     * @param goRightToLeft True if the loop should go from right to left, false
     *                      otherwise.
     */
    public void setDirection(boolean goRightToLeft) {
        this.goRightToLeft = goRightToLeft;
    }

    /**
     * Activates or deactivates the movement.
     * Resets the time and initial position when deactivated.
     * 
     * @param active True to activate the movement, false to deactivate it.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            t = 0.0;
            initialPosition = null;
            initialRotationComplete = false; // Reset rotation flag
        } else {
            // Reset flag when activated to ensure rotation happens if re-enabled
            initialRotationComplete = false;
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
     * Moves the GameObject according to the lasso movement pattern.
     * The movement includes an initial drop, a circular loop, and a final descent.
     * Will first rotate the enemy to 270 degrees smoothly.
     * 
     * @param enemy The GameObject to move.
     */
    @Override
    public void move(GameObject enemy) {
        if (!active)
            return;

        if (initialPosition == null) {
            initialPosition = enemy.transform().position();
        }

        if (!initialRotationComplete) {
            rotateToInitialAngle(enemy);
            enemy.velocity(new Ponto(0, 0)); // Ensure no movement during initial rotation
            // If rotation is not yet complete, skip path movement for this frame
            if (!initialRotationComplete) {
                return;
            }
        }
        
        // Original movement logic
        Ponto current = handlePosition(t, t1, t2);
        Ponto next = handlePosition(t + tIncrement, t1, t2);

        Ponto velocity = new Ponto(next.x() - current.x(), next.y() - current.y());
        enemy.velocity(velocity);
        // No continuous rotation during lasso path, only initial rotation.
        // If continuous dynamic rotation based on path was needed, we'd add:
        // enemy.rotateSpeed(calculateAngle(enemy, next)); 

        t += tIncrement;

        if (t > (t1 + t2)) {
            // Optionally, ensure final angle is exactly 270 if needed, or another target angle
            // For now, just deactivates.
            setActive(false);
        }
    }

    /**
     * Rotates the enemy smoothly to the initial target angle (270 degrees).
     * Sets initialRotationComplete to true when the angle is reached.
     * 
     * @param enemy The GameObject to rotate.
     */
    private void rotateToInitialAngle(GameObject enemy) {
        double currentAngle = enemy.transform().angle();
        double targetAngle = 270.0;

        // Check if already at target angle (within a small tolerance)
        if (Math.abs(currentAngle - targetAngle) < 0.5) {
            enemy.rotateSpeed(0); // Stop rotation
            initialRotationComplete = true;
            return;
        }

        double angleDiff = (targetAngle + 360) - currentAngle;

        // Normalize to the shortest path (-180 to 180)
        while (angleDiff > 180) angleDiff -= 360;
        while (angleDiff < -180) angleDiff += 360;

        // Proportional control for smooth rotation
        double rotationSpeed = angleDiff * 0.1;

        // Limit maximum rotation speed
        double maxRotationSpeed = 5.0; // Degrees per frame
        if (Math.abs(rotationSpeed) > maxRotationSpeed) {
            rotationSpeed = Math.signum(rotationSpeed) * maxRotationSpeed;
        }
        
        enemy.rotateSpeed(rotationSpeed);

        // Check again if target angle reached after applying rotation speed for this frame
        // This helps to snap to the angle if the rotation speed makes it pass the threshold in one step
        if (Math.abs(enemy.transform().angle() - targetAngle) < 0.5) {
             enemy.rotateSpeed(0);
             initialRotationComplete = true;
        }
    }

    /**
     * Determines the position of the GameObject at a given time.
     * 
     * @param localT The current time.
     * @param t1     The duration of the initial drop.
     * @param t2     The duration of the circular loop.
     * @return The position of the GameObject at the given time.
     */
    private Ponto handlePosition(double localT, double t1, double t2) {
        if (localT < t1)
            return handleInitialDrop(localT);

        if (localT < t1 + t2)
            return handleCircularPath(localT, t1, t2);

        return handleFinalDescent(localT, t1, t2);
    }

    /**
     * Handles the initial vertical drop of the movement.
     * 
     * @param t The current time during the drop.
     * @return The position of the GameObject during the drop.
     */
    private Ponto handleInitialDrop(double t) {
        double V1 = 10.0 * scale; // Vertical velocity
        double x = initialPosition.x();
        double y = initialPosition.y() - V1 * t;
        return new Ponto(x, y);
    }

    /**
     * Handles the circular loop of the movement.
     * 
     * @param t  The current time during the loop.
     * @param t1 The duration of the initial drop.
     * @param t2 The duration of the circular loop.
     * @return The position of the GameObject during the loop.
     */
    private Ponto handleCircularPath(double t, double t1, double t2) {
        double R = 8.0 * scale; // Radius of the circular path
        double V1 = 10.0 * scale; // Vertical velocity during the drop

        double angleT = (t - t1) / t2; // Normalized time for the circular path

        double dropX = initialPosition.x();
        double dropY = initialPosition.y() - V1 * t1;

        double centerX = goRightToLeft ? dropX - R : dropX + R;
        double centerY = dropY;

        double thetaStart = goRightToLeft ? 0 : Math.PI;
        double theta = thetaStart + (goRightToLeft ? -1 : 1) * 2 * Math.PI * angleT;

        double x = centerX + R * Math.cos(theta);
        double y = centerY + R * Math.sin(theta);

        return new Ponto(x, y);
    }

    /**
     * Handles the final descent of the movement.
     * 
     * @param t  The current time during the descent.
     * @param t1 The duration of the initial drop.
     * @param t2 The duration of the circular loop.
     * @return The position of the GameObject during the descent.
     */
    private Ponto handleFinalDescent(double t, double t1, double t2) {
        double t3 = t - t1 - t2; // Time during the final descent

        double R = 8.0 * scale; // Radius of the circular path
        double V1 = 35.0 * scale; // Vertical velocity during the drop
        double V2 = 25.0 * scale; // Vertical velocity during the descent
        double offsetX = 5.0 * scale; // Horizontal offset during the descent

        double A3x = 30.0 * scale; // Amplitude of horizontal oscillation
        double B3x = 1.5 * scale; // Frequency of horizontal oscillation
        double A3y = 2.0 * scale; // Amplitude of vertical oscillation
        double B3y = 70.0 * scale; // Frequency of vertical oscillation

        int dir = goRightToLeft ? -1 : 1; // Direction of the descent

        double dropX = initialPosition.x();
        double dropY = initialPosition.y() - V1 * t1;
        double centerX = goRightToLeft ? dropX - R : dropX + R;
        double centerY = dropY;

        double thetaStart = goRightToLeft ? 0 : Math.PI;
        double thetaEnd = thetaStart + (goRightToLeft ? -1 : 1) * 2 * Math.PI;

        double lastX = centerX + R * Math.cos(thetaEnd);
        double lastY = centerY + R * Math.sin(thetaEnd);

        double x = lastX + dir * offsetX * t3 + dir * A3x * Math.sin(B3x * t3);
        double y = lastY - V2 * t3 - A3y * Math.sin(B3y * t3);

        return new Ponto(x, y);
    }
}
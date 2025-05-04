package core.behaviorItems;

import core.GameObject;
import geometry.Ponto;

/**
 * The FlyLassoMovement class implements a movement pattern for enemies
 * that includes an initial vertical drop, a circular loop, and a final descent.
 * This movement is time-based and can be activated or deactivated.
 *
 * @Pre-Conditions:
 * - The GameObject passed to move() must not be null
 * - The GameObject must have a valid Transform component
 * - The direction (goRightToLeft) should be set before activation
 * - The initial position is automatically captured when movement starts
 * - The scale factor must be positive and non-zero
 *
 * @Post-Conditions:
 * - The enemy will execute a three-phase movement:
 *   1. Initial vertical drop with velocity V1
 *   2. Circular loop with radius R
 *   3. Final descent with oscillating motion
 * - Movement deactivates automatically when t > 4.5
 * - When deactivated, time (t) resets to 0 and initial position is cleared
 * - The movement scales according to the defined scale factor (0.2)
 * - The enemy's velocity is updated each frame based on position delta
 * - The movement pattern remains consistent with the chosen direction
 *
 *
 * @author Brandon Mejia
 * @author Gabriel Pedroso
 * @author Miguel Correia
 *
 * @version 2025-04-23
 */
public class FlyLassoMovement implements IEnemyMovement
{
    private boolean active = false; // Indicates whether the movement is active
    private double t = 0.0; // Time variable for movement progression
    private Ponto initialPosition; // The initial position of the GameObject
    private boolean goRightToLeft = false; // Direction of the circular loop
    private final double scale = 0.2; // Scaling factor for movement parameters
    private final double tIncrement = 0.018; // Time increment for each movement step

    /**
     * Sets the direction of the circular loop.
     * @param goRightToLeft True if the loop should go from right to left, false otherwise.
     */
    public void setDirection(boolean goRightToLeft)
    {
        this.goRightToLeft = goRightToLeft;
    }

    /**
     * Activates or deactivates the movement.
     * Resets the time and initial position when deactivated.
     * @param active True to activate the movement, false to deactivate it.
     */
    @Override
    public void setActive(boolean active)
    {
        this.active = active;
        if (!active)
        {
            t = 0.0;
            initialPosition = null;
        }
    }

    /**
     * Checks if the movement is currently active.
     * @return True if the movement is active, false otherwise.
     */
    @Override
    public boolean isActive()
    {
        return active;
    }

    /**
     * Moves the GameObject according to the lasso movement pattern.
     * The movement includes an initial drop, a circular loop, and a final descent.
     * @param enemy The GameObject to move.
     */
    @Override
    public void move(GameObject enemy)
    {
        if (!active) return;

        if (initialPosition == null)
            initialPosition = enemy.transform().position();

        double t1 = 0.6;  // Duration of vertical drop
        double t2 = 1.0;  // Duration of circular loop

        Ponto current = handlePosition(t, t1, t2);
        Ponto next = handlePosition(t + tIncrement, t1, t2);

        Ponto velocity = new Ponto(next.x() - current.x(), next.y() - current.y());
        enemy.velocity(velocity);

        t += tIncrement;

        if (t > 4.5)
            setActive(false);
    }

    /**
     * Determines the position of the GameObject at a given time.
     * @param localT The current time.
     * @param t1 The duration of the initial drop.
     * @param t2 The duration of the circular loop.
     * @return The position of the GameObject at the given time.
     */
    private Ponto handlePosition(double localT, double t1, double t2)
    {
        if (localT < t1)
            return handleInitialDrop(localT);

        if (localT < t1 + t2)
            return handleCircularPath(localT, t1, t2);

        return handleFinalDescent(localT, t1, t2);
    }

    /**
     * Handles the initial vertical drop of the movement.
     * @param t The current time during the drop.
     * @return The position of the GameObject during the drop.
     */
    private Ponto handleInitialDrop(double t)
    {
        double V1 = 35.0 * scale; // Vertical velocity
        double x = initialPosition.x();
        double y = initialPosition.y() - V1 * t;
        return new Ponto(x, y);
    }

    /**
     * Handles the circular loop of the movement.
     * @param t The current time during the loop.
     * @param t1 The duration of the initial drop.
     * @param t2 The duration of the circular loop.
     * @return The position of the GameObject during the loop.
     */
    private Ponto handleCircularPath(double t, double t1, double t2)
    {
        double R = 5.0 * scale; // Radius of the circular path
        double V1 = 35.0 * scale; // Vertical velocity during the drop

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
     * @param t The current time during the descent.
     * @param t1 The duration of the initial drop.
     * @param t2 The duration of the circular loop.
     * @return The position of the GameObject during the descent.
     */
    private Ponto handleFinalDescent(double t, double t1, double t2)
    {
        double t3 = t - t1 - t2; // Time during the final descent

        double R = 5.0 * scale; // Radius of the circular path
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
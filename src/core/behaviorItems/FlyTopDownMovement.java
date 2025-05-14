package core.behaviorItems;

import core.GameObject;
import core.objectsInterface.IGameObject;
import geometry.Ponto;

/**
 * Simulates a top-down enemy movement pattern with a sinusoidal horizontal
 * oscillation and descending vertical motion in two phases. Direction is
 * configurable.
 *
 * <p>
 * Phases of movement:
 * </p>
 * <ol>
 * <li>Phase 1: Sinusoidal horizontal oscillation with a steady vertical
 * descent.</li>
 * <li>Phase 2: Faster vertical descent with continued sinusoidal
 * oscillation.</li>
 * </ol>
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * FlyTopDownMovement movement = new FlyTopDownMovement();
 * movement.setDirection(true); // Set direction to left-to-right
 * movement.setActive(true); // Activate the movement
 *
 * </pre>
 *
 * @preConditions:
 *                 - The `GameObject` must have a valid initial position.
 *                 - The movement must be activated using the `setActive`
 *                 method.
 *                 - The direction of movement must be set using the
 *                 `setDirection` method if needed.
 *
 * @postConditions:
 *                  - The `GameObject` will follow the defined movement pattern.
 *                  - The movement will deactivate automatically after
 *                  completing the defined duration.
 *                  - The velocity of the `GameObject` will be updated at each
 *                  step of the movement.
 *
 * @see <a href=
 *      "https://gamedev.stackexchange.com/questions/82811/implementing-galaga-style-enemy-behavior-in-unity">
 *      Implementing Galaga Style Enemy Behavior </a>
 * @see <a href=
 *      "https://chatgpt.com/share/68095b69-8cb0-8011-8705-2d3eade4aa69"> How to
 *      create a parametric function and what attributes I need to define one -
 *      ChatGPT</a>
 *
 * @author Brandon Mejia
 * @version 2025-04-23
 */
public class FlyTopDownMovement implements IEnemyMovement {
    private boolean active = false; // Indicates whether the movement is active
    private double t = 0.0; // Time variable for movement progression
    private final double scale = 0.2; // Scaling factor for movement parameters
    private boolean goLeftToRight = true; // Direction of horizontal movement
    private Ponto initialPosition; // The initial position of the GameObject

    // Timing and control constants
    private final double tIncrement = 0.0155; // Time increment for each movement step

    // Phase 1 parameters
    private final double A1 = 40.0 * scale; // Amplitude of horizontal movement in phase 1
    private final double B1 = 7.0 * scale; // Amplitude of vertical oscillation in phase 1
    private final double C1 = 2 * Math.PI; // Frequency of vertical oscillation in phase 1
    private final double D1 = 45.0 * scale; // Vertical descent rate in phase 1
    private final double t1 = 2 * Math.PI / C1; // Duration of phase 1


    /**
     * Validates the invariant for the `FlyTopDownMovement` class.
     * Ensures that the provided `IGameObject` instance is not null.
     * If the validation fails, an error message is printed, and the program exits.
     *
     * @param go The `IGameObject` instance to validate. Must not be null.
     */
    private void invariante(IGameObject go)
    {
        if(go != null)
            return;

        System.out.println("FlyTopDownMovement:iv");
        System.exit(0);
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
     * Sets the direction of horizontal movement.
     *
     * @param goLeftToRight True if the movement should go from left to right, false
     *                      otherwise.
     */
    public void setDirection(boolean goLeftToRight) {
        this.goLeftToRight = goLeftToRight;
    }

    /**
     * Moves the GameObject according to the top-down movement pattern.
     * The movement includes a sinusoidal horizontal oscillation and descending
     * vertical motion.
     *
     * @param enemy The GameObject to move.
     */
    @Override
    public void move(GameObject enemy)
    {
        invariante(enemy);

        if (!active)
            return;

        if (initialPosition == null)
            initialPosition = enemy.transform().position();

        Ponto current = computePosition(t);
        Ponto next = computePosition(t + tIncrement);
        Ponto velocity = new Ponto(next.x() - current.x(), next.y() - current.y());

        enemy.velocity(velocity);

        t += tIncrement;

        if (t > 2)
            setActive(false);
    }

    /**
     * Computes the position of the GameObject at a given time.
     *
     * @param localT The current time.
     * @return The position of the GameObject at the given time.
     */
    private Ponto computePosition(double localT) {
        if (localT <= t1)
            return handlePhase1Senoidal(localT);
        else
            return handlePhase2Sinusoidal(localT - t1);
    }

    /**
     * Handles the first phase of the movement, which includes a sinusoidal
     * horizontal oscillation
     * and a descending vertical motion.
     *
     * @param t The current time during phase 1.
     * @return The position of the GameObject during phase 1.
     */
    private Ponto handlePhase1Senoidal(double t) {
        int dir = goLeftToRight ? 1 : -1;

        double x = initialPosition.x() + dir * A1 * t;
        double y = initialPosition.y() - B1 * Math.sin(C1 * t) - D1 * t;

        return new Ponto(x, y);
    }

    /**
     * Handles the second phase of the movement, which includes a sinusoidal
     * horizontal oscillation
     * and a faster descending vertical motion.
     *
     * @param t2 The current time during phase 2.
     * @return The position of the GameObject during phase 2.
     */
    private Ponto handlePhase2Sinusoidal(double t2) {
        int dir = goLeftToRight ? 1 : -1;

        double baseX = initialPosition.x() + dir * A1 * t1;
        double baseY = initialPosition.y() - B1 * Math.sin(C1 * t1) - D1 * t1;

        // Phase 2 parameters
        double a2 = 10.0 * scale; // Horizontal movement rate in phase 2
        double x = baseX + dir * a2 * t2;
        double b2 = 1.5 * scale; // Amplitude of vertical oscillation in phase 2
        double c2 = 2.7 * Math.PI; // Frequency of vertical oscillation in phase 2
        double d2 = 110 * scale; // Vertical descent rate in phase 2
        double y = baseY - b2 * Math.sin(c2 * t2) - d2 * t2;

        return new Ponto(x, y);
    }
}
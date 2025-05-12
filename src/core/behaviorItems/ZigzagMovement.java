package core.behaviorItems;

import core.EnemyBehavior;
import core.GameObject;
import geometry.Ponto;
import java.util.Random;

/**
 * The `ZigzagMovement` class implements the `IEnemyMovement` interface.
 * This class defines a stepped movement pattern for enemy objects. The enemy
 * moves a certain distance in one horizontal direction, pauses, repeats this
 * for a set number of steps, then returns to its starting position using the
 * same pattern of movement and pauses.
 *
 * <p>
 * The movement is controlled by an active state.
 * </p>
 *
 * @Pre-Conditions:
 *                  - The enemy object must not be null.
 *                  - The enemy object must have a valid velocity property.
 *
 * @Post-Conditions:
 *                   - When active, the enemy's velocity changes to perform the
 *                   stepped movement.
 *                   - The movement deactivates automatically upon returning to
 *                   the starting position.
 *                   - When inactive, the enemy's velocity remains unchanged by this behavior.
 *
 * @see IEnemyMovement
 * @see GameObject
 * @see Ponto
 *
 * @author Brandon Mejia
 * @version 2025-04-03 (Modified by Trae AI)
 */
public class ZigzagMovement implements IEnemyMovement
{
    private boolean isActive = false; // Indicates whether the movement is active
    private boolean goLeftToRight = false; // false: initial L->R; true: initial R->L

    // Fields for movement logic state
    private double waitTime = 0;            // Countdown for current pause
    private double moveDistance = 0;        // Distance moved in the current segment/step
    private boolean isReturning = false;    // Flag for outward or return phase
    private int stepsTaken = 0;             // Number of steps/segments completed in the current phase

    // Configurable parameters for the zigzag pattern
    private double targetDistance = 15;     // Distance for each "X" step of the movement
    static private double waitDuration = 70;       // Time (in update calls/frames) to wait between steps
    private int maxSteps = 4;               // Number of steps to take in one direction before returning

    // Internal state for tracking total displacement
    private double accumulatedDisplacement = 0; // Tracks total distance moved away from start before returning

    // Novo campo para guardar a posição inicial
    private Double initialX = null;


    /**
     * Validates the invariant for the `ZigzagMovement` class.
     * Ensures that the provided `GameObject` instance is not null.
     * If the validation fails, an error message is printed, and the program exits.
     *
     * @param enemy The `GameObject` instance to validate. Must not be null.
     */
    private void invariante(GameObject enemy)
    {
        if (enemy != null)
            return;

        System.out.println("ZigzagMovement:iv");
        System.exit(0);
    }


    /**
     * Moves the enemy object according to the defined stepped pattern if active.
     *
     * @param enemy The `GameObject` representing the enemy to be moved.
     */
    @Override
    public void move(GameObject enemy)
    {
        invariante(enemy);

        if (!isActive)
            return;

        // Guarda a posição inicial na primeira ativação
        if (initialX == null)
        {
            initialX = enemy.transform().position().x();
        }

        if (waitTime > 0)
        {
            waitTime--;
            enemy.velocity(new Ponto(0, 0)); // Ensure enemy is stopped during waitTime
            return;
        }

        double speedX = 0.5; // Movement speed per frame/update call
        double dx = 0;          // Change in x-coordinate for this frame

        if (!isReturning)
        {
            // Outward phase: move in steps
            if (stepsTaken < maxSteps)
            {
                if (moveDistance < targetDistance)
                {
                    // Determine direction based on goLeftToRight
                    dx = !goLeftToRight ? speedX : -speedX;
                    moveDistance += Math.abs(speedX);
                    accumulatedDisplacement += Math.abs(speedX); // Accumulate total distance moved outward
                }
                else
                {
                    // Completed one step
                    waitTime = waitDuration;
                    moveDistance = 0; // Reset for the next step
                    stepsTaken++;
                    
                    if (stepsTaken >= maxSteps)
                    {
                        // Finished all outward steps, prepare to return
                        isReturning = true;
                        stepsTaken = 0; // Reset steps counter for return phase
                        // moveDistance is already 0, ready for the first return segment
                    }
                    dx = 0; // Stop for waitTime
                }
            }
            // Fallback if somehow stepsTaken >= maxSteps but not yet returning (should be handled above)
            else
            {
                isReturning = true;
                stepsTaken = 0;
                moveDistance = 0;
                dx = 0;
            }
        }
        else // isReturning is true
        {
            // Return phase: move back in segments until accumulatedDisplacement is covered
            if (accumulatedDisplacement > 0.001) // Using a small epsilon for float comparison to zero
            {
                if (moveDistance < targetDistance)
                {
                    double moveAmountThisFrame = speedX;

                    // Ensure we don't move more than remaining in the current segment
                    if (moveDistance + moveAmountThisFrame > targetDistance)
                    {
                        moveAmountThisFrame = targetDistance - moveDistance;
                    }
                    // Ensure we don't move more than the total remaining accumulatedDisplacement
                    if (moveAmountThisFrame > accumulatedDisplacement)
                    {
                        moveAmountThisFrame = accumulatedDisplacement;
                    }

                    dx = goLeftToRight ? speedX : -speedX;
                    // Ajusta magnitude de dx para moveAmountThisFrame, preservando o sinal
                    if (Math.abs(dx) > 0) dx = (dx / Math.abs(dx)) * moveAmountThisFrame;
                    else dx = 0;

                    moveDistance += moveAmountThisFrame;
                    accumulatedDisplacement -= moveAmountThisFrame;
                }
                else
                {
                    // Completed one return segment
                    waitTime = waitDuration;
                    moveDistance = 0; // Reset for the next return segment
                    stepsTaken++; // Counts return segments
                    dx = 0; // Stop for waitTime
                }
            }
            else
            {
                setActive(false); // Deactivate movement
                dx = 0;
                enemy.velocity(new Ponto(0.0,0.0));
                EnemyBehavior enemyBehavior = (EnemyBehavior) enemy.behavior();
                enemyBehavior.setMovement(null);
            }
        }
        enemy.velocity(new Ponto(dx, 0));
    }

    @Override
    public void setActive(boolean active)
    {
        this.isActive = active;
        // Reset state for a new activation sequence
        moveDistance = 0;
        accumulatedDisplacement = 0;
        waitTime = 0;
        isReturning = false;
        stepsTaken = 0;
        initialX = null; // Reinicia a posição inicial para o próximo movimento

        if (active) 
        {
            Random rand = new Random();
            waitDuration = 30 + rand.nextInt(61); // 61 porque nextInt é exclusivo do limite superior
        }
    }

    /**
     * Checks if the zigzag movement is currently active.
     *
     * @return `true` if the movement is active, `false` otherwise.
     */
    @Override
    public boolean isActive() {
        return isActive;
    }


    /**
     * Sets the initial direction of the movement pattern.
     * 
     * @param goRightToLeft If false, initial movement is Right to Left. 
     *                      If true, initial movement is Left to Right.
     */
    public void setDirection(boolean goLeftToRight)
    {
        this.goLeftToRight = goLeftToRight;
    }

    // Optional setters for configurability
    /**
     * Sets the number of steps to take in one direction before returning.
     * @param maxSteps Number of steps (must be > 0).
     */
    public void setMaxSteps(int maxSteps) {
        if (maxSteps > 0) {
            this.maxSteps = maxSteps;
        }
    }

    /**
     * Sets the distance for each individual step of the movement.
     * @param targetDistance Distance (must be > 0).
     */
    public void setTargetDistance(double targetDistance) {
        if (targetDistance > 0) {
            this.targetDistance = targetDistance;
        }
    }

    /**
     * Sets the duration of the pause between movement steps.
     * @param waitDuration Pause duration in frames/updates (must be >= 0).
     */
    public void setWaitDuration(double waitDuration) {
        if (waitDuration >= 0) {
            this.waitDuration = waitDuration;
        }
    }
}
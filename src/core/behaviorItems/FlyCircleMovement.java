package core.behaviorItems;

import core.GameObject;
import core.objectsInterface.IGameObject;
import geometry.Ponto;

/**
 * FlyCircleMovement class implements the IEnemyMovement interface to define
 * a specific movement pattern for an enemy. The enemy moves in three circular
 * paths (small, large, small) and then returns to its initial position.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * // Create and configure the movement
 * FlyCircleMovement movement = new FlyCircleMovement();
 * movement.setIsLeft(true); // For enemies on the left side
 *
 * // Activate the movement
 * movement.setActive(true);
 *
 * </pre>
 * 
 * @preConditions:
 *                 - The enemy must have a valid position and velocity.
 *                 - The movement must be activated using the setActive method.
 *                 - The isLeft property must be set to determine the movement
 *                 direction.
 *
 * @postConditions:
 *                  - The enemy will follow the defined circular movement
 *                  pattern.
 *                  - The enemy will return to its initial position after
 *                  completing the movement.
 *                  - The movement will deactivate automatically after
 *                  completion.
 *
 * @author Brandon Mejia
 *
 * @version 2025-04-19
 */
public class FlyCircleMovement implements IEnemyMovement {

    private Ponto currentPosition; // Current position of the enemy
    private Ponto circleCenter; // Center of the current circle
    private Ponto initialPosition; // Initial position of the enemy

    private final double radius = 210; // Radius of the large circle
    private final double smallRadius = radius / 3; // Radius of the small circles
    private double currentAngle; // Current angle of movement
    private final double angleIncrement = Math.toRadians(3); // Angle increment per step
    private int currentCircle = 1; // Tracks the current circle (1, 2, or 3)

    private boolean isLeft = false; // Determines if the enemy is on the left side
    private boolean active = false; // Indicates if the movement is active



    /**
     * Validates the invariant for the `FlyCircleMovement` class.
     * Ensures that the provided `IGameObject` instance is not null.
     * If the validation fails, an error message is printed, and the program exits.
     *
     * @param go The `IGameObject` instance to validate. Must not be null.
     */
    private void invariante(IGameObject go)
    {
        if(go != null)
            return;

        System.out.println("FlyCircleMovement:iv");
        System.exit(0);
    }


    /**
     * Moves the enemy based on its current state and position.
     *
     * @param enemy The GameObject representing the enemy.
     */
    @Override
    public void move(GameObject enemy)
    {
        invariante(enemy);

        if (!active)
            return;

        if (isLeft)
            handleCircularMovementLeft(enemy);
        else
            handleCircularMovementRight(enemy);
    }

    /**
     * Activates or deactivates the movement.
     *
     * @param active True to activate, false to deactivate.
     */
    @Override
    public void setActive(boolean active)
    {
        this.active = active;
        if (!active)
        {
            currentPosition = null;
            circleCenter = null;
            initialPosition = null;
        }
    }

    /**
     * Checks if the movement is active.
     *
     * @return True if active, false otherwise.
     */
    @Override
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets whether the enemy is on the left side.
     *
     * @param isLeft True if on the left side, false otherwise.
     */
    public void setIsLeft(boolean isLeft) {
        this.isLeft = isLeft;
    }

    /**
     * Handles the circular movement for enemies on the left side.
     *
     * @param enemy The GameObject representing the enemy.
     */
    private void handleCircularMovementLeft(GameObject enemy) {
        if (currentPosition == null) {
            currentPosition = enemy.transform().position();
            initialPosition = enemy.transform().position();
            circleCenter = new Ponto(currentPosition.x() - smallRadius, currentPosition.y());
            currentAngle = Math.toRadians(0);
            return;
        }

        switch (currentCircle) {
            case 1:
                handleFirstCircleLeft(enemy);
                break;
            case 2:
                handleSecondCircleLeft(enemy);
                break;
            case 3:
                handleThirdCircleLeft(enemy);
                break;
            case 4:
                returnToInitialPosition(enemy);
                break;
            case 5:
                rotateToVertical(enemy);
                break;
        }
    }

    /**
     * Handles the first small circle movement for enemies on the left side.
     *
     * @param enemy The GameObject representing the enemy.
     */
    private void handleFirstCircleLeft(GameObject enemy)
    {
        currentAngle += angleIncrement;
        double newX = circleCenter.x() + smallRadius * Math.cos(currentAngle);
        double newY = circleCenter.y() + smallRadius * Math.sin(currentAngle);
        updateEnemyPositionBigCircle(enemy, newX, newY);

        if (Math.toDegrees(currentAngle) >= 260)
        {
            currentCircle = 2;
            currentAngle = Math.toRadians(90);
            this.currentPosition = enemy.transform().position();
            circleCenter = new Ponto(currentPosition.x(), currentPosition.y() - radius);
        }
    }

    /**
     * Handles the large circle movement for enemies on the left side.
     *
     * @param enemy The GameObject representing the enemy.
     */
    private void handleSecondCircleLeft(GameObject enemy)
    {
        currentAngle -= angleIncrement/1.5;
        double newX = circleCenter.x() + radius * Math.cos(currentAngle);
        double newY = circleCenter.y() + radius * Math.sin(currentAngle);
        updateEnemyPosition(enemy, newX, newY);

        if (Math.toDegrees(currentAngle) < -90)
        {
            currentCircle = 3;
            currentAngle = Math.toRadians(260);
            this.currentPosition = enemy.transform().position();
            circleCenter = new Ponto(currentPosition.x() - Math.toRadians(30), currentPosition.y() + smallRadius);
        }
    }

    /**
     * Handles the third small circle movement for enemies on the left side.
     *
     * @param enemy The GameObject representing the enemy.
     */
    private void handleThirdCircleLeft(GameObject enemy) {
        currentAngle -= angleIncrement;
        double newX = circleCenter.x() + smallRadius * Math.cos(currentAngle);
        double newY = circleCenter.y() + smallRadius * Math.sin(currentAngle);
        updateEnemyPosition(enemy, newX, newY);

        if (Math.toDegrees(currentAngle) < 170) {
            currentCircle = 4;
        }
    }

    /**
     * Handles the circular movement for enemies on the right side.
     *
     * @param enemy The GameObject representing the enemy.
     */
    private void handleCircularMovementRight(GameObject enemy) {
        if (currentPosition == null) {
            currentPosition = enemy.transform().position();
            initialPosition = enemy.transform().position();
            circleCenter = new Ponto(currentPosition.x() + smallRadius, currentPosition.y());
            currentAngle = Math.toRadians(180);

            return;
        }

        switch (currentCircle) {
            case 1:
                handleFirstCircleRight(enemy);
                break;
            case 2:
                handleSecondCircleRight(enemy);
                break;
            case 3:
                handleThirdCircleRight(enemy);
                break;
            case 4:
                returnToInitialPosition(enemy);
                break;
            case 5:
                rotateToVertical(enemy);
                break;
        }
    }

    /**
     * Handles the first small circle movement for enemies on the right side.
     *
     * @param enemy The GameObject representing the enemy.
     */
    private void handleFirstCircleRight(GameObject enemy) {
        currentAngle -= angleIncrement;
        double newX = circleCenter.x() + smallRadius * Math.cos(currentAngle);
        double newY = circleCenter.y() + smallRadius * Math.sin(currentAngle);
        updateEnemyPosition(enemy, newX, newY);

        if (Math.toDegrees(currentAngle) < -80) {
            currentCircle = 2;
            currentAngle = Math.toRadians(90);
            this.currentPosition = enemy.transform().position();
            circleCenter = new Ponto(currentPosition.x(), currentPosition.y() - radius);
        }
    }

    /**
     * Handles the large circle movement for enemies on the right side.
     *
     * @param enemy The GameObject representing the enemy.
     */
    private void handleSecondCircleRight(GameObject enemy)
    {
        currentAngle += angleIncrement/1.5;
        double newX = circleCenter.x() + radius * Math.cos(currentAngle);
        double newY = circleCenter.y() + radius * Math.sin(currentAngle);
        updateEnemyPositionBigCircle(enemy, newX, newY);

        if (Math.toDegrees(currentAngle) >= 280)
        {
            currentCircle = 3;
            currentAngle = Math.toRadians(280);
            this.currentPosition = enemy.transform().position();
            circleCenter = new Ponto(currentPosition.x() + Math.toRadians(30), currentPosition.y() + smallRadius);
        }
    }

    /**
     * Handles the third small circle movement for enemies on the right side.
     *
     * @param enemy The GameObject representing the enemy.
     */
    private void handleThirdCircleRight(GameObject enemy) {
        currentAngle += angleIncrement;
        double newX = circleCenter.x() + smallRadius * Math.cos(currentAngle);
        double newY = circleCenter.y() + smallRadius * Math.sin(currentAngle);
        updateEnemyPosition(enemy, newX, newY);

        if (Math.toDegrees(currentAngle) > 370) {
            currentCircle = 4;
        }
    }

    /**
     * Returns the enemy to its initial position after completing the movement.
     *
     * @param enemy The GameObject representing the enemy.
     */
    /**
     * Verifica se o inimigo está muito próximo da posição inicial e o para completamente.
     * 
     * @param enemy O GameObject representando o inimigo
     * @return true se o inimigo foi parado, false caso contrário
     */
    private boolean handleVeryCloseStop(GameObject enemy, double distance) {
        if (distance < 0.005)
        {

            // Primeiro zera a velocidade
            enemy.velocity(new Ponto(0, 0));
            currentCircle = 5;
            return true;
        }
        return false;
    }

    private void returnToInitialPosition(GameObject enemy)
    {
        Ponto currentPos = enemy.transform().position();
        double dx = initialPosition.x() - currentPos.x();
        double dy = initialPosition.y() - currentPos.y();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Verifica se está muito próximo e deve parar
        if (handleVeryCloseStop(enemy, distance))
        {
            // Garante que o inimigo fique na posição vertical ao parar
            rotateToVertical(enemy);
            return;
        }

        // Sistema de velocidade adaptativa com amortecimento
        double RETURN_SPEED = Math.min(8.0, Math.max(0.1, distance));
        
        // Calcula a velocidade com amortecimento suave
        Ponto velocity = new Ponto(
                dx * RETURN_SPEED / distance,
                dy * RETURN_SPEED / distance);

        // Se a velocidade for muito baixa, usa o método de parada
        if (Math.abs(velocity.x()) < 0.001 && Math.abs(velocity.y()) < 0.001)
        {
            handleVeryCloseStop(enemy, distance);
            return;
        }
        
        enemy.velocity(velocity);
        
        // Calcula e aplica a rotação durante o movimento de retorno
        Ponto nextPoint = new Ponto(
            currentPos.x() + velocity.x(),
            currentPos.y() + velocity.y()
        );
        enemy.rotateSpeed(calculateAngle(enemy, nextPoint));
    }

    /**
     * Updates the enemy's position based on the calculated new position.
     *
     * @param enemy The GameObject representing the enemy.
     * @param newX  The new X-coordinate.
     * @param newY  The new Y-coordinate.
     */
    private void updateEnemyPosition(GameObject enemy, double newX, double newY)
    {
        double SPEED =0.5;
        Ponto currentPos = enemy.transform().position();
        Ponto velocity = new Ponto(
                (newX - currentPos.x()) * SPEED,
                (newY - currentPos.y()) * SPEED);
        enemy.velocity(velocity);
        enemy.rotateSpeed(calculateAngle(enemy, new Ponto(velocity.x()+currentPos.x(), velocity.y()+currentPos.y())));
    }


    /**
     * Updates the enemy's position based on the calculated new position.
     *
     * @param enemy The GameObject representing the enemy.
     * @param newX  The new X-coordinate.
     * @param newY  The new Y-coordinate.
     */
    private void updateEnemyPositionBigCircle(GameObject enemy, double newX, double newY)
    {
        double SPEED = 0.5;
        Ponto currentPos = enemy.transform().position();
        Ponto velocity = new Ponto(
                (newX - currentPos.x()) * SPEED,
                (newY - currentPos.y()) * SPEED);
        enemy.velocity(velocity);
        enemy.rotateSpeed(calculateAngle(enemy, new Ponto(velocity.x()+currentPos.x(), velocity.y()+currentPos.y())));
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
    private void rotateToVertical(GameObject enemy)
    {
        double currentAngle = enemy.transform().angle();
        double targetAngle = 90.0;
        double delta = 0.0001; // Valor delta para considerar o ângulo como 90 graus

        if(Math.abs(currentAngle - targetAngle) < delta)
        {
            currentCircle = 1;
            setActive(false);
            currentPosition = null;
            circleCenter = null;
            initialPosition = null;
            enemy.rotateSpeed(0);
            return;
        }

        double angleDiff = (targetAngle + 360) - currentAngle;

        while (angleDiff > 180)
            angleDiff -= 360;
        while (angleDiff < -180)
            angleDiff += 360;

        double rotationSpeed = angleDiff * 0.1;

        if (Math.abs(rotationSpeed) > 5.0)
        {
            rotationSpeed = Math.signum(rotationSpeed) * 5.0;
        }

        enemy.rotateSpeed(rotationSpeed);
    }

}
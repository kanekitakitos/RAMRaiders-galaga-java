package core.objectsInterface;
import core.Transform;

import geometry.Ponto;
import java.awt.Graphics2D;

/**
 * The ICollider interface defines the contract for collider components in the game.
 * A collider is responsible for detecting and handling collisions between game objects.
 *
 * @author Brandon Mejia
 * @author Gabriel Pedroso
 * @author Miguel Correia
 *
 * @version 2025-03-25
 */
public interface ICollider
{
    /**
     * Gets the centroid (geometric center) of the collider.
     * The centroid is the average position of all points in the collider.
     *
     * @return The centroid of the collider as a Ponto object.
     */
    Ponto centroid();


    Transform transform();
    
   /**
     * Checks for a collision with another collider.
     *
     * @param other The other collider to check for collision with.
     * @return True if a collision is detected, false otherwise.
     */
    boolean colision(ICollider other);

    /**
     * Updates the collider's state. This method is called to ensure
     * the collider is synchronized with the game object's current state.
     */
    void onUpdateCollider();

    /**
     * Updates the position of the collider. This method is responsible
     * for recalculating the collider's position based on the game object's
     * current position.
     */
    void updatePosicao();

    /**
     * Updates the rotation of the collider. This method ensures the collider's
     * rotation matches the game object's current rotation.
     */
    void updateRotacao();

    /**
     * Updates the scale of the collider. This method adjusts the collider's
     * dimensions to match the game object's current scale.
     */
    void updateEscalar();

    /**
     * Draws the collider's visual representation for debugging purposes.
     * This method is used to render the collider's shape and boundaries
     * on the screen, which can be helpful during development and testing.
     *
     * @param g2d The Graphics2D context used for drawing
     * @param centerCartesianPlaneX
     * @param centerCartesianPlaneY
     */
    void draw(Graphics2D g2d, double centerCartesianPlaneX, double centerCartesianPlaneY);

    /**
     * Gets the logical width of the collider.
     * The logical width represents the effective width used for rendering and collision detection,
     * typically scaled by a factor to match the visual representation of the game object.
     *
     * @return The logical width of the collider
     */
    public double getLogicalWidth();
     
    /**
     * Gets the logical height of the collider.
     * The logical height represents the effective height used for rendering and collision detection,
     * typically scaled by a factor to match the visual representation of the game object.
     *
     * @return The logical height of the collider
     */
    public double getLogicalHeight();
}
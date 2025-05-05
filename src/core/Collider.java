package core;

 import core.objectsInterface.ICollider;
 import geometry.Ponto;
 import java.awt.Graphics2D;

 /**
  * The Collider class represents a collider in the game world.
  *
  * @Pre-Conditions:
  * - The provided Transform object must not be null.
  *
  * @Post-Conditions:
  * - A Collider object is instantiated with a valid Transform.
  * - The collider's state is consistent with its Transform.
  *
  * This abstract class implements the ICollider interface and provides the base functionality
  * for collision detection and response by updating rotation, scaling, and position. Subclasses
  * must implement the abstract methods to define specific behavior for scaling, position, and
  * representation.
  *
  * @see core.Transform
  *
  * @author Brandon Mejia
  * @author Gabriel Pedroso
  * @author Miguel Correia
  *
  * @version 2025-03-25
  */
 public abstract class Collider implements ICollider
 {
     protected Transform transform;

     /**
      * Constructs a Collider instance with the specified transform.
      *
      * @param transform The transform associated with this collider.
      * @throws IllegalArgumentException if transform is null.
      */
     public Collider(Transform transform)
     {
         if (transform == null)
             throw new IllegalArgumentException("Collider:iv");
         this.transform = transform;
     }

     /**
      * Gets the current transform of the collider.
      *
      * @return The transform associated with the collider.
      */
     public Transform transform()
     {
         return this.transform;
     }

     /**
      * Gets the centroid of the collider.
      * The centroid is the geometric center of the collider's shape.
      *
      * @return The centroid as a Ponto object.
      */
     @Override
     public Ponto centroid()
     {
         return transform.position();
     }

     /**
      * Updates the collider's state based on its transform.
      * This method sequentially calls:
      * 1. updateRotacao() - to update rotation.
      * 2. updateEscalar() - to update scaling.
      * 3. updatePosicao() - to update position.
      */
     @Override
     public void onUpdateCollider()
     {
         updateRotacao();
         updateEscalar();
         updatePosicao();
     }

     /**
      * Updates the scaling of the collider based on the current transform.
      * Subclasses must implement this method to define scaling update behavior.
      */
     @Override
     public abstract void updateEscalar();

     /**
      * Updates the position of the collider based on the current transform.
      * Subclasses must implement this method to define position update behavior.
      */
     @Override
     public abstract void updatePosicao();

     /**
      * Updates the rotation of the collider based on the current transform.
      * This default implementation is provided; subclasses may override if needed.
      */
     @Override
     public void updateRotacao()
     {
         // TODO: Default implementation is empty.
     }

     /**
      * Returns a string representation of the collider.
      * The string includes information about the collider's type and current state.
      *
      * @return A string representation of the collider.
      */
     @Override
     public abstract String toString();



     /**
     * Draws the collider's visual representation for debugging purposes.
     * This method is used to render the collider's shape and boundaries
     * on the screen, which can be helpful during development and testing.
     *
     * @param g2d The Graphics2D context used for drawing
     * @param panelWidth The width of the panel where the collider is drawn
     * @param panelHeight The height of the panel where the collider is drawn
     */
     public abstract void draw(Graphics2D g2d, double panelWidth, double panelHeight);
 }
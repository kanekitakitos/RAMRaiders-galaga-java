package core;
import core.objectsInterface.ITransform;
import geometry.Ponto;

/**
 * The Transform class represents the transformation properties of a geometric figure,
 * including position, layer, angle, and scale.
 *
 * @preConditions:
 * - The provided Ponto (position) object must not be null.
 * - The angle is expected to be within the range [0, 360), though the constructor and
 *   rotate method will ensure it remains within 0 and 360.
 * - The initial scale and layer should be valid numbers.
 *
 * @postConditions:
 * - A Transform object is created that encapsulates a copy of the provided position.
 * - The transform stores the specific layer, angle, and scale, ensuring that changes
 *   to the original Ponto object do not affect the internal state.
 * - After rotation, the angle is normalized to remain between 0 and 360.
 *
 * This class implements the ITransform interface to provide movement, rotation, and scale
 * modification capabilities.
 *
 * @author Brandon Mejia
 * @author Gabriel Pedroso
 * @author Miguel Correia
 *
 * @version 2025-03-25
 */
public class Transform implements ITransform
{
    private Ponto position; // The position of the transform
    private int layer; // The layer of the transform
    private double angle; // The angle of the transform (0 <= angle < 360)
    private double previousAngle; // The previous angle of the transform
    private double scale; // The scale of the transform

    /**
     * Constructs a Transform object with the specified position, layer, angle, and scale.
     *
     * @param p The position of the transform.
     * @param layer The layer of the transform.
     * @param angle The angle of the transform.
     * @param scale The scale of the transform.
     */
    public Transform(Ponto p, int layer, double angle, double scale)
    {
        this.position = new Ponto(p);
        this.layer = layer;
        this.scale = scale;
        this.angle = angle;

        if(angle < 0)
            this.angle += 360;

        this.angle = angle % 360;
    }

    /**
     * Constructs a Transform object by copying another Transform object.
     *
     * @param t The Transform object to copy.
     */
    public Transform(Transform t)
    {
        this.position = new Ponto(t.position());
        this.layer = t.layer();
        this.angle = t.angle();
        this.scale = t.scale();
    }

    /**
     * Moves the transform by the specified position and layer.
     *
     * @param dPos The position to move by.
     * @param dlayer The layer to move to.
     */
    @Override
    public void move(Ponto dPos, int dlayer)
    {
        this.layer += dlayer;
        this.position = new Ponto(this.position.x() + dPos.x(), this.position.y() + dPos.y());
    }

    /**
     * Rotates the transform by the specified angle.
     *
     * @param dTheta The angle to rotate by.
     */
    @Override
    public void rotate(double dTheta)
    {
        this.previousAngle = this.angle;
        this.angle += dTheta;
        this.previousAngle = this.previousAngle % 360;

        if(this.angle < 0)
            this.angle += 360;

        this.angle = this.angle % 360;
    }

    /**
     * Scales the transform by the specified scale factor.
     *
     * @param dScale The scale factor to scale by.
     */
    @Override
    public void scale(double dScale)
    {
        this.scale += dScale;
    }

    /**
     * Gets the position of the transform.
     *
     * @return The position of the transform.
     */
    @Override
    public Ponto position()
    {
        return new Ponto(this.position);
    }

    /**
     * Gets the layer of the transform.
     *
     * @return The layer of the transform.
     */
    @Override
    public int layer()
    {
        return this.layer;
    }

    /**
     * Gets the angle of the transform.
     *
     * @return The angle of the transform.
     */
    @Override
    public double angle()
    {
        return this.angle;
    }

    /**
     * Gets the previous angle of the transform.
     *
     * @return The previous angle of the transform.
     */
    public double previousAngle()
    {
        return this.previousAngle;
    }

    /**
     * Sets the previous angle of the transform.
     *
     * @param angle The angle to set as the previous angle.
     */
    public void previousAngle(double angle)
    {
         this.previousAngle = angle;
    }

    /**
     * Gets the scale of the transform.
     *
     * @return The scale of the transform.
     */
    @Override
    public double scale()
    {
        return this.scale;
    }

    /**
     * Returns a string representation of the transform.
     *
     * @return A string representation of the transform.
     */
    @Override
    public String toString()
    {
        String pontos = this.position.toString();
        String angle = String.format("%.2f", angle() ).replace(",", ".");
        String scale = String.format("%.2f", scale()).replace(",", ".");

        return String.format("%s %d %s %s", pontos, layer(), angle, scale);
    }
}
package core.objectsInterface;

	import geometry.Ponto;

	/**
	 * The ITransform interface defines the methods for transforming geometric figures,
	 * including moving, rotating, and scaling.
	 * @author Brandon Mejia
	 * @author Gabriel Pedroso
	 * @author Miguel Correia
	 *
	 * @version 2025-03-25
	 */
	public interface ITransform {

	    /**
	     * Moves this ITransform by the specified position and layer differentials.
	     *
	     * @param dPos The 2D differential to move.
	     * @param dlayer The layer differential.
	     */
	    public void move(Ponto dPos, int dlayer);

	    /**
	     * Rotates this ITransform from the current orientation by the specified angle.
	     *
	     * @param dTheta The angle to rotate by.
	     *               Postcondition: 0 <= this.angle() < 360
	     */
	    public void rotate(double dTheta);

	    /**
	     * Increments the ITransform scale by the specified scale factor.
	     *
	     * @param dScale The scale increment.
	     */
	    public void scale(double dScale);

	    /**
	     * Gets the position of the ITransform.
	     *
	     * @return The (x, y) coordinates.
	     */
	    public Ponto position();

	    /**
	     * Gets the layer of the ITransform.
	     *
	     * @return The layer.
	     */
	    public int layer();

	    /**
	     * Gets the angle of the ITransform in degrees.
	     *
	     * @return The angle in degrees.
	     */
	    public double angle();

	    /**
	     * Gets the current scale factor of the ITransform.
	     *
	     * @return The current scale factor.
	     */
	    public double scale();
	}
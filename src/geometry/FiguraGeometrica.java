package geometry;



/**
 * Represents a geometric figure.
 * @author Brandon Mejia
 * @version 2025-03-12
 */
public interface FiguraGeometrica
{

	/**
	 * Calculates the perimeter of the geometric figure.
	 *
	 * @return The perimeter of the geometric figure.
	 */
	 double perimetro();

	/**
	 * Returns a string representation of the geometric figure.
	 *
	 * @return A string representation of the geometric figure.
	 */
	@Override
	String toString();

	/**
	 * Checks if the geometric figure intersects with another geometric figure.
	 *
	 * @param f The other geometric figure to check for intersection.
	 * @return true if the figures intersect, false otherwise.
	 */
	boolean intersecta(FiguraGeometrica f);


	Ponto centro();

}
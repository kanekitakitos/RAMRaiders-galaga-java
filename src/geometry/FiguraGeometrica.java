package geometry;

	 /**
	  * Represents a geometric figure.
	  * This interface defines the basic operations and properties that any geometric figure must implement,
	  * such as calculating the perimeter, checking for intersections, and retrieving the center point.
	  *
	  * @preConditions:
	  * - Implementing classes must provide valid implementations for all methods.
	  * - The `centro` method must return a valid `Ponto` object representing the center of the figure.
	  * - The `intersecta` method must handle null or invalid input gracefully.
	  *
	  * @postConditions:
	  * - The `perimetro` method returns a non-negative value representing the perimeter of the figure.
	  * - The `toString` method provides a meaningful string representation of the figure.
	  * - The `intersecta` method accurately determines if two figures intersect.
	  * - The `centro` method returns the correct center point of the figure.
	  *
	  * @author Brandon Mejia
	  * @version 2025-03-12
	  */
	 public interface FiguraGeometrica {

	     /**
	      * Calculates the perimeter of the geometric figure.
	      *
	      * @return The perimeter of the geometric figure as a double. The value is non-negative.
	      */
	     double perimetro();

	     /**
	      * Returns a string representation of the geometric figure.
	      * The string representation should include relevant details about the figure.
	      *
	      * @return A string representation of the geometric figure.
	      */
	     @Override
	     String toString();

	     /**
	      * Checks if the geometric figure intersects with another geometric figure.
	      *
	      * @param f The other geometric figure to check for intersection. Must not be null.
	      * @return true if the figures intersect, false otherwise.
	      */
	     boolean intersecta(FiguraGeometrica f);

	     /**
	      * Retrieves the center point of the geometric figure.
	      *
	      * @return A `Ponto` object representing the center of the geometric figure.
	      */
	     Ponto centro();
	 }
package geometry;

import core.Transform;

/**
 * Represents a rectangle defined by four vertices and four sides.
 * Ensures the rectangle's invariant properties: sides are perpendicular and
 * opposite sides are equal in length.
 * If the properties are not met, the program prints an error message and exits.
 *
 * @see geometry.Poligono
 *
 * @author Brandon Mejia
 * @version 2025-02-22
 * @inv The sides must be perpendicular and opposite sides must be equal in
 *      length.
 */
public class Retangulo extends Poligono {

	/**
	 * Ensures the rectangle's invariant properties are maintained.
	 * The sides must be perpendicular, and opposite sides must be equal in length.
	 * If the properties are not met, the program prints an error message and exits.
	 *
	 * @param lados An array of segments representing the sides of the rectangle.
	 */
	private void invariante(Segmento[] lados) {
		boolean ladosPerpendiculares = lados[0].isPerpendicular(lados[1]) &&
				lados[1].isPerpendicular(lados[2]) &&
				lados[2].isPerpendicular(lados[3]) &&
				lados[3].isPerpendicular(lados[0]);
		boolean ladosIguais = lados[0].length() == lados[2].length() &&
				lados[1].length() == lados[3].length();

		if (ladosPerpendiculares && ladosIguais)
			return;

		System.out.println("Retangulo:vi");
		System.exit(0);
	}

	/**
	 * Constructs a rectangle from an array of four points.
	 * Ensures the rectangle's invariant properties are validated after
	 * construction.
	 *
	 * @param pontos    An array of four points representing the vertices of the
	 *                  rectangle.
	 * @param transform The transform associated with the rectangle.
	 */
	public Retangulo(Ponto pontos[], Transform transform) {
		super(pontos, transform);
		invariante(this.lados);
	}

	/**
	 * Checks if the rectangle is a square.
	 * A rectangle is considered a square if all four sides have the same length.
	 *
	 * @return True if the rectangle is a square, false otherwise.
	 */
	public boolean isSquare() {
		return lados[0].length() == lados[1].length() &&
				lados[1].length() == lados[2].length() &&
				lados[2].length() == lados[3].length();
	}
}
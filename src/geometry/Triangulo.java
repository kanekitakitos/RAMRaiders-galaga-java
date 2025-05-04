package geometry;

import core.Transform;

/**
 * Represents a triangle in a 2D space.
 * Implements the FiguraGeometrica interface.
 *
 * @see geometry.Poligono
 *
 * @author Brandon Mejia
 * @version 2025-03-08
 * @inv the points are different and not collinear.
 */
public class Triangulo extends Poligono
{
	/**
	 * Ensures the points form a valid triangle.
	 *
	 * @param pts An array of three points.
	 */
	 private void invariante(Ponto[] pts)
	{
		Segmento s = new Segmento(pts[0], pts[1]);
		boolean pontosColineares = !s.isCollinear(pts[2]);
		boolean pontosDiferentes = !pts[0].equals(pts[1]) && !pts[1].equals(pts[2]) && !pts[2].equals(pts[0]);

		if (pontosDiferentes && pontosColineares)
			return;

		System.out.println("Triangulo:vi");
		System.exit(0);

	}

	/**
	 * Constructs a triangle with the given points.
	 *
	 * @param pontos An array of three points.
	 */
	public Triangulo(Ponto pontos[], Transform transform)
	{
		super(pontos, transform);
		invariante(this.vertices);
	}

	/**
	 * Checks if the triangle is a right triangle.
	 *
	 * @return True if the triangle is a right triangle, false otherwise.
	 */
	public boolean isTriangleRetangulo()
	{
		double a = this.lados[0].length();
		double b = this.lados[1].length();
		double c = this.lados[2].length();

		if (c > a && c > b)
			return Math.pow(c, 2) == Math.pow(a, 2) + Math.pow(b, 2);
		else if (a > b && a > c)
			return Math.pow(a, 2) == Math.pow(b, 2) + Math.pow(c, 2);
		else
			return Math.pow(b, 2) == Math.pow(a, 2) + Math.pow(c, 2);
	}



}
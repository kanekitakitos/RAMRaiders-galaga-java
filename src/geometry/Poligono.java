package geometry;
import core.Collider;
import core.objectsInterface.ICollider;
import core.Transform;
import java.awt.Graphics2D;

/**
 * Represents a polygon in a 2D space.
 * Extends the Collider class to provide polygon-based collision detection.
 *
 * @see core.Collider
 * @see geometry.FiguraGeometrica
 * @see geometry.Ponto
 * @see geometry.Segmento
 *
 * @author Brandon Mejia
 * @version 2025-03-08
 */
public class Poligono extends Collider implements FiguraGeometrica
{
	protected Ponto[] vertices;
	protected Segmento[] lados;

	/**
	 * Ensures the points form a valid polygon.
	 *
	 * @param pts An array of points.
	 * @throws IllegalArgumentException if any two points are the same.
	 */
	private void invariante(Ponto[] pts)
	{
		this.lados = new Segmento[pts.length];
		for (int i = 0; i < lados.length; i++)
		{
			this.lados[i] = new Segmento(pts[i], pts[(i + 1) % pts.length]);

			if (this.lados[i].isCollinear(pts[(i + 2) % pts.length])  || (i > 0 && this.lados[i].intersecta(this.lados[i - 1])))
			{
				throw new IllegalArgumentException("Poligono:vi");
			}
		}

		for(Segmento s: this.lados)
		{
			for (Segmento s2: this.lados)
			{
				if(s.equals(s2))
					continue;

				if(s.intersecta(s2))
				{
					throw new IllegalArgumentException("Poligono:vi");
				}
			}
		}
	}

	/**
	 * Constructs a polygon with the given points and transform.
	 *
	 * @param pontos An array of points.
	 * @param transform The transform associated with the polygon.
	 */
	public Poligono(Ponto pontos[], Transform transform)
	{
		super(transform);
		invariante(pontos);
		this.vertices = new Ponto[pontos.length];
		for (int i = 0; i < pontos.length; i++)
			this.vertices[i] = new Ponto(pontos[i]);
	}

	/**
	 * Constructs a polygon with the given points string and transform.
	 *
	 * @param pontos A string of points.
	 * @param transform The transform associated with the polygon.
	 */
	public Poligono(String pontos, Transform transform)
	{
		super(transform);
		String[] pontosArray = pontos.split(" ");
		Ponto[] pontosList = new Ponto[pontosArray.length / 2];
		for (int i = 0; i < pontosArray.length; i += 2)
			pontosList[i / 2] = new Ponto(Double.parseDouble(pontosArray[i]), Double.parseDouble(pontosArray[i + 1]));

		invariante(pontosList);
		this.vertices = new Ponto[pontosList.length];
		for (int i = 0; i < pontosList.length; i++)
			this.vertices[i] = new Ponto(pontosList[i]);
	}

	/**
	 * Returns a copy of the sides of the polygon.
	 *
	 * @return An array of Segmento objects representing the sides of the polygon.
	 */
	public Segmento[] lados()
	{
		Segmento[] ladosCopy = new Segmento[this.lados.length];
		for (int i = 0; i < this.lados.length; i++)
			ladosCopy[i] = new Segmento(this.lados[i]);

		return ladosCopy;
	}

	/**
	 * Returns a copy of the vertices of the polygon.
	 *
	 * @return An array of Ponto objects representing the vertices of the polygon.
	 */
	public Ponto[] vertices()
	{
		Ponto[] verticesCopy = new Ponto[this.vertices.length];
		for (int i = 0; i < this.vertices.length; i++)
			verticesCopy[i] = new Ponto(this.vertices[i]);

		return verticesCopy;
	}



	/**
	 * Returns a string representation of the polygon.
	 *
	 * @return A string representation of the polygon.
	 */
	@Override
	public String toString()
	{
		String s = "";
		for (int i = 0; i < vertices.length; i++)
		{
			if (i == vertices.length - 1)
				s += vertices[i].toString();
			else
				s += vertices[i].toString() + " ";
		}
		return s;
	}

	/**
	 * Calculates the perimeter of the polygon.
	 *
	 * @return The perimeter of the polygon.
	 */
	@Override
	public double perimetro()
	{
		double perimetro = 0;
		for (Segmento s : lados)
			perimetro += s.length();

		return perimetro;
	}

	/**
	 * Translates the geometric figure by the given x and y distances.
	 *
	 * @param dx The distance to translate along the x-axis.
	 * @param dy The distance to translate along the y-axis.
	 * @return A new instance of the geometric figure translated by the given distances.
	 */
	public FiguraGeometrica translacao(int dx, int dy)
	{
		if(dx == 0 && dy == 0)
			return this;

		Ponto[] pontos = new Ponto[this.vertices.length];
		for (int i = 0; i < this.vertices.length; i++)
			pontos[i] = new Ponto(this.vertices[i].x() + dx, this.vertices[i].y() + dy);

		try
		{
			return this.getClass().getConstructor(Ponto[].class).newInstance((Object) pontos);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Translates the polygon by the given distances along the x and y axes.
	 *
	 * @param newCenter The point to translate to.
	 * @return A new instance of the polygon translated by the given distances.
	 */
	public Poligono translacao(Ponto newCenter)
	{
		Ponto centroideAtual = centro();

		// Calculate the translation difference
		double novoCentroX = newCenter.x() - centroideAtual.x();
		double novoCentroY = newCenter.y() - centroideAtual.y();

		// Translate all vertices by the calculated difference
		Ponto[] pontos = new Ponto[this.vertices.length];
		for (int i = 0; i < this.vertices.length; i++)
			pontos[i] = new Ponto(this.vertices[i].x() + novoCentroX, this.vertices[i].y() + novoCentroY);

		return new Poligono(pontos,this.transform);
	}

	/**
	 * Checks if this polygon contains the given point.
	 *
	 * @param ponto The point to check.
	 * @return true if the point is inside this polygon, false otherwise.
	 */
	private boolean isInsidePoint(Ponto ponto)
	{
		int n = this.vertices.length;
		boolean result = false;
		for (int i = 0, j = n - 1; i < n; j = i++) {
			if ((this.vertices[i].y() > ponto.y()) != (this.vertices[j].y() > ponto.y()) &&
					(ponto.x() < (this.vertices[j].x() - this.vertices[i].x()) * (ponto.y() - this.vertices[i].y()) / (this.vertices[j].y() - this.vertices[i].y()) + this.vertices[i].x())) {
				result = !result;
			}
		}
		return result;
	}

	/**
	 * Checks if the given polygon is inside this polygon.
	 *
	 * @param poligono The polygon to check.
	 * @return true if the given polygon is inside this polygon, false otherwise.
	 */
	public boolean isInside(Poligono poligono)
	{
		for (Ponto vertice : poligono.vertices())
			if (!this.isInsidePoint(vertice))
				return false;

		return true;
	}

	/**
	 * Checks if the given circle is inside this polygon.
	 *
	 * @param circulo The circle to check.
	 * @return true if the circle is inside this polygon, false otherwise.
	 */
	public boolean isInside(Circulo circulo)
	{
		Ponto centro = circulo.centro();
		double raio = circulo.r();

		// Check if the center of the circle is inside the polygon
		if (!this.isInsidePoint(centro))
			return false;

		// Check if all vertices of the polygon are within the circle's radius
		for (Ponto vertice : this.vertices)
			if (circulo.distanciaAoCentro(vertice) >= raio)
				return true;

		return false;
	}

	/**
	 * Checks if the given segment intersects with any of the sides of the rectangle.
	 *
	 * @param s The segment to check for intersection.
	 * @return True if the segment intersects with any side of the rectangle, false otherwise.
	 */
	public boolean intersecta(Segmento s)
	{
		for (Segmento l : lados)
			if (l.intersecta(s))
				return true;

		return false;
	}

	/**
	 * Checks if this polygon intersects with another geometric figure.
	 *
	 * @param f The other geometric figure to check for intersection.
	 * @return true if the figures intersect, false otherwise.
	 */
	@Override
	public boolean intersecta(FiguraGeometrica f)
	{
		if (f instanceof Poligono poligono)
		{
			for (Segmento s : poligono.lados)
				if (this.intersecta(s))
					return true;

			return this.isInside(poligono) || poligono.isInside(this);
		}

		if (f instanceof Circulo circulo)
		{
			for (Segmento s : this.lados)
				if (s.intersecta(circulo))
					return true;

			return circulo.isInside(this) || this.isInside(circulo);
		}

		return false;
	}

	/**
	 * Calculates the centroid of the polygon.
	 *
	 * @return A Ponto object representing the centroid of the polygon.
	 */
	public Ponto centro()
	{
		double area = 0;  // Área do polígono
		double xc = 0, yc = 0;  // Coordenadas do centroide
		int n = this.vertices.length;
		double x0, y0, x1, y1, crossProduct;

		// Somamos as áreas e as contribuições para o centroide
		for (int i = 0; i < n; i++) {
			x0 = this.vertices[i].x();
			y0 = this.vertices[i].y();
			x1 = this.vertices[(i + 1) % n].x();
			y1 = this.vertices[(i + 1) % n].y();

			crossProduct = x0 * y1 - x1 * y0; // Produto cruzado entre (x0, y0) e (x1, y1)
			area += crossProduct;  // Soma do produto cruzado (metade da área)

			// Contribuições para o centroide
			xc += (x0 + x1) * crossProduct;
			yc += (y0 + y1) * crossProduct;
		}

		// A área final deve ser dividida por 2 (produto cruzado conta o dobro)
		area /= 2;

		// A coordenada do centroide é dividida pela área total
		xc /= (6 * area);
		yc /= (6 * area);

		if(xc == -0.0)
			xc = 0;
		if(yc == -0.0)
			yc = 0;

		return new Ponto(xc, yc); // Retorna o centroide calculado
	}

	/**
	 * Rotates the polygon by the given angle.
	 *
	 * @param theta The angle in degrees to rotate the polygon.
	 * @return A new instance of the polygon rotated by the given angle.
	 */
	public Poligono rotacao(double theta)
	{
		Ponto[] pontos = new Ponto[this.vertices.length];
		Ponto centro = this.centro();  // Obtém o centroide atual da figura
		double rads = theta *Math.PI/180; // Converte o ângulo para radianos

		// Para cada vértice do polígono
		for (int i = 0; i < this.vertices.length; i++)
		{
			// #1: Mover o ponto para a origem
			double xp = this.vertices[i].x() - centro.x();
			double yp = this.vertices[i].y() - centro.y();

			// #2: Rodar o ponto em torno da origem
			double xn = xp * Math.cos(rads) - yp * Math.sin(rads);
			double yn = xp * Math.sin(rads) + yp * Math.cos(rads);

			// #3: Mover o ponto de volta para a posição original
			pontos[i] = new Ponto(xn + centro.x(), yn + centro.y());
		}

		return new Poligono(pontos,this.transform);
	}

	/**
	 * Scales the polygon by the given factor.
	 *
	 * @param factor The factor to scale the polygon by.
	 * @return A new instance of the polygon scaled by the given factor.
	 */
	public Poligono escalar(double factor)
	{

		Ponto centro = this.centro();
		Ponto[] pontos = new Ponto[this.vertices.length];
		double x, y,newX,newY;

		for (int i = 0; i < this.vertices.length; i++)
		{
			 x = this.vertices[i].x();
			 y = this.vertices[i].y();

			// Calculando as novas coordenadas após aplicar o fator de escala
			 newX = (x - centro.x()) * factor + centro.x();
			 newY = (y - centro.y()) * factor + centro.y();

			pontos[i] = new Ponto(newX, newY);
		}

		return new Poligono(pontos,this.transform);
	}



	
	/**
	* Updates the position of the polygon based on the current transform.
	* Translates the polygon to the new position and updates its vertices and sides.
	*/
	@Override
	public void updatePosicao()
	{
		Poligono poligono = this.translacao(transform.position());
		this.vertices = poligono.vertices();
		this.lados = poligono.lados();
	}

	/**
	* Updates the rotation of the polygon based on the current transform.
	* Rotates the polygon by the difference in angles and updates its vertices and sides.
	*/
	@Override
	public void updateRotacao()
	{
		double angle = transform.angle() - this.transform.previousAngle();
   		if (angle != 0)
   		{
			   Poligono poligono = this.rotacao(angle);
			   this.vertices = poligono.vertices();
			   this.lados = poligono.lados();
			   this.transform.previousAngle(this.transform.angle());
   		}
	}

	/**
	* Updates the scaling of the polygon based on the current transform.
	* Scales the polygon by the transform's scale factor and updates its vertices and sides.
	*/
	@Override
	public void updateEscalar()
	{
	   if (transform.scale() >= 1.0)
	   {
		   Poligono poligono = this.escalar(transform.scale());
		   this.vertices = poligono.vertices();
		   this.lados = poligono.lados();
	   }
	}


	/**
	 * Handles a collision between this collider and another collider.
	 * This method should be implemented by subclasses to define how the collision is handled.
	 */
	@Override
	public boolean colision(ICollider other)
	{
			FiguraGeometrica f = (FiguraGeometrica) other;
			return this.intersecta(f);
	}


	/**
     * Draws the collider's visual representation for debugging purposes.
     * This method is used to render the collider's shape and boundaries
     * on the screen, which can be helpful during development and testing.
     *
     * @param g2d The Graphics2D context used for drawing
     * @param panelWidth The width of the panel where the collider is drawn
     * @param panelHeight The height of the panel where the collider is drawn
     */
	@Override
    public void draw(Graphics2D g2d, double panelWidth, double panelHeight)
    {
        // Salva o estado original
        java.awt.Stroke oldStroke = g2d.getStroke();
        java.awt.Color oldColor = g2d.getColor();

        // Define cor e espessura para depuração
        g2d.setColor(java.awt.Color.RED);
        g2d.setStroke(new java.awt.BasicStroke(2));

        Ponto[] verticesEscalados = this.vertices();
        int n = verticesEscalados.length;
        for (int i = 0; i < n; i++)
        {
            Ponto p1 = verticesEscalados[i];
            Ponto p2 = verticesEscalados[(i + 1) % n];

            // Converte para coordenadas de tela (origem no centro do painel, Y invertido)
            double screenX1 = panelWidth  + p1.x();
            double screenY1 = panelHeight  - p1.y();
            double screenX2 = panelWidth  + p2.x();
            double screenY2 = panelHeight  - p2.y();

            // Desenha a aresta
            g2d.drawLine(
                (int) Math.round(screenX1),
                (int) Math.round(screenY1),
                (int) Math.round(screenX2),
                (int) Math.round(screenY2)
            );
        }

        // Restaura o estado original
        g2d.setStroke(oldStroke);
        g2d.setColor(oldColor);
    }





}
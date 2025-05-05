package geometry;

import java.awt.Graphics2D;

import core.Collider;
import core.objectsInterface.ICollider;
import core.Transform;

/**
 * The Circulo class represents a circle with a center point and a radius.
 * It extends the Collider class to provide circle-based collision detection.
 * @author Brandon Mejia
 * @version 2025-02-10
 *
 * @see core.Collider
 * @see geometry.FiguraGeometrica
 * @see geometry.Ponto
 * @see geometry.Segmento
 *
 * @inv The circle must have a positive radius and its center must be in the first quadrant.
 */
public class Circulo extends Collider implements FiguraGeometrica
{
    private double r;
    private Ponto centro;

    /**
     * Ensures the invariants of the circle are maintained.
     * The radius must be positive and the center must be in the first quadrant.
     *
     * @param r The radius of the circle.
     * @param centro The center point of the circle.
     */
    private void invariantes(double r, Ponto centro) {
        if (r > 0 )
            return;

        System.out.println("Circulo:vi");
        System.exit(0);
    }

    /**
     * Constructs a Circulo object with the specified center coordinates, radius and transform.
     *
     * @param x The x-coordinate of the center.
     * @param y The y-coordinate of the center.
     * @param r The radius of the circle.
     * @param transform The transform associated with the circle.
     */
    public Circulo(double x, double y, double r, Transform transform)
    {
        super(transform);
        this.centro = new Ponto(x, y);
        invariantes(r, centro);
        this.r = r;
    }

    /**
     * Constructs a Circulo object with the specified string representation and transform.
     *
     * @param transform The transform associated with the circle.
     */
    public Circulo(double r, Transform transform)
    {
        super(transform);
        this.centro = transform.position();
        this.r = r;
        invariantes(r, centro);

    }

    /**
     * Constructs a Circulo object with the specified center point, radius and transform.
     *
     * @param c The center point of the circle.
     * @param r The radius of the circle.
     * @param transform The transform associated with the circle.
     */
    public Circulo(Ponto c, double r, Transform transform)
    {
        super(transform);
        this.centro = new Ponto(c);
        invariantes(r, centro);
        this.r = r;
    }

    /**
     * Sets the radius of the circle.
     *
     * @param r The new radius of the circle.
     */
    public void setR(double r) {
        invariantes(r, this.centro);
        this.r = r;
    }

    /**
     * Gets the center point of the circle.
     *
     * @return The center point of the circle.
     */
    public Ponto centro() {
        return new Ponto(this.centro);
    }

    /**
     * Gets the radius of the circle.
     *
     * @return The radius of the circle.
     */
    public double r() {
        return this.r;
    }

    /**
     * Calculates the perimeter of the circle.
     *
     * @return The perimeter of the circle.
     */
    @Override
    public double perimetro()
    {
        return 2 * Math.PI * this.r;
    }

    /**
     * Returns a string representation of the circle.
     *
     * @return A string representation of the circle.
     */
    @Override
    public String toString()
    {
        String r = String.format("%.2f", this.r).replace(",", ".");
        return String.format("%s %s", this.centro.toString(), r);
    }

    /**
     * Translates the circle to a new center point.
     *
     * @param newCenter The new center point to which the circle will be translated.
     * @return A new instance of the circle translated to the new center point.
     */
    public Circulo translacao(Ponto newCenter)
    {
        return new Circulo(new Ponto(newCenter.x(), newCenter.y()), this.r, this.transform);
    }

    /**
     * Calculates the distance from the center of the circle to a given point.
     *
     * @param p The point to which the distance is calculated.
     * @return The distance from the center of the circle to the given point.
     */
    public double distanciaAoCentro(Ponto p)
    {
        return Math.sqrt(Math.pow(this.centro.x() - p.x(), 2) + Math.pow(this.centro.y() - p.y(), 2));
    }

    /**
     * Checks if this circle intersects with another circle.
     *
     * @param c The other circle to check for intersection.
     * @return true if the circles intersect, false otherwise.
     */
    public boolean intersecta(Circulo c)
    {
        double dx = c.centro.x() - this.centro.x();
        double dy = c.centro.y() - this.centro.y();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < this.r + c.r;
    }

    /**
     * Checks if the circle contains the given polygon.
     *
     * @param poligono The polygon to check.
     * @return true if the circle contains the polygon, false otherwise.
     */
    public boolean isInside(Poligono poligono)
    {
        boolean isInside = false;
        double distance = 0;
        for (Ponto vertice : poligono.vertices())
        {
            distance = this.distanciaAoCentro(vertice);
            if ( distance < this.r)
                isInside = true;
        }

        return isInside;
    }

    /**
     * Checks if this geometric figure intersects with another geometric figure.
     *
     * @param f The other geometric figure to check for intersection.
     * @return true if the figures intersect, false otherwise.
     */
    @Override
    public boolean intersecta(FiguraGeometrica f)
    {
        if(f instanceof Poligono poligono)
        {
			for (Segmento s : poligono.lados)
                if ( s.intersecta(this))
                    return true;

            return this.isInside(poligono) || poligono.isInside(this);
        }

        if( f instanceof Circulo  circulo)
        {
            return this.intersecta(circulo);
        }

        return false;
    }


    /**
     * Updates the collider's state based on its transform.
     * This method calls the specific update methods in sequence:
     * 1. updateEscalar() - Updates scaling
     * 2. updatePosicao() - Updates position
     */
    public void onUpdateCollider()
    {
        this.updateEscalar();
        this.updatePosicao();
    }

    @Override
    public void updatePosicao()
    {
        this.centro = transform.position();
    }

    @Override
    public void updateEscalar()
    {
        this.r *= transform.scale();
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
        // Parâmetros de conversão mundo -> tela (deve ser igual ao usado no GamePanel)

        // Centro do círculo em coordenadas do mundo
        double worldX = this.centro.x();
        double worldY = this.centro.y();

        // Converte para coordenadas de tela (origem no centro do painel, Y invertido)
        double screenX = panelWidth / 2.0 + worldX ;
        double screenY = panelHeight / 2.0 - worldY ;

        // Salva o estado original
        java.awt.Stroke oldStroke = g2d.getStroke();
        java.awt.Color oldColor = g2d.getColor();

        // Define cor e espessura para depuração
        g2d.setColor(java.awt.Color.RED);
        g2d.setStroke(new java.awt.BasicStroke(2));

        // Desenha a circunferência centralizada
        g2d.drawOval(
            (int) Math.round(screenX - this.r),
            (int) Math.round(screenY - this.r),
            (int) Math.round(2 * this.r),
            (int) Math.round(2 * this.r)
        );

        // Restaura o estado original
        g2d.setStroke(oldStroke);
        g2d.setColor(oldColor);
    }



}
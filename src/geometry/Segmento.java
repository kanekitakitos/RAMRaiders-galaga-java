package geometry;
/**
 * Represents a line segment defined by two points.
 *
 * @author Brandon Mejia
 * @version 2025-02-22
 *
 * @see geometry.Ponto
 *
 * @inv The points a and b must not be equal.
 */
public class Segmento
{
    private Ponto a;
    private Ponto b;

    /**
     * Ensures the invariants of the segment are maintained.
     * If the points a and b are equal, the program exits.
     *
     * @param a The first point of the segment.
     * @param b The second point of the segment.
     */
    private void invariantes(Ponto a, Ponto b) {
        if (!a.equals(b))
            return;

        System.out.println("Segmento:vi");
        System.exit(0);
    }

    /**
     * Constructs a Segmento object with two points.
     *
     * @param a The first point of the segment.
     * @param b The second point of the segment.
     */
    public Segmento(Ponto a, Ponto b)
    {
        invariantes(a, b);
        this.a = a;
        this.b = b;
    }


    public Segmento(Segmento s)
    {
        this.a = new Ponto(s.a);
        this.b = new Ponto(s.b);
    }

    /**
     * Returns a copy of the first point of the segment.
     *
     * @return A copy of the first point.
     */
    public Ponto a() {
        return new Ponto(a);
    }

    /**
     * Returns a copy of the second point of the segment.
     *
     * @return A copy of the second point.
     */
    public Ponto b() {
        return new Ponto(b);
    }

    /**
     * Calculates the dot product of this segment with another segment.
     * If the result is 0, the segments are perpendicular.
     *
     * @param other The other segment to calculate the dot product with.
     * @return True if the segments are perpendicular, false otherwise.
     * @throws IllegalArgumentException if the other segment is null.
     */
    public boolean isPerpendicular(Segmento other) {
        if (other == null)
            throw new IllegalArgumentException("O segmento não pode ser nulo.");

        // Create vectors representing this segment
        double dx1 = (this.b().x() - this.a().x());
        double dy1 = (this.b().y() - this.a().y());

        // create vectors representing the other segment
        double dx2 = other.b().x() - other.a().x();
        double dy2 = other.b().y() - other.a().y();

        // Calculate the dot product of the two vectors
        double dotProduct = dx1 * dx2 + dy1 * dy2;

        return dotProduct == 0;
    }

    /**
     * Calculates the length of the segment.
     *
     * @return The length of the segment.
     */
    public double length() {
        return Math.sqrt(Math.pow(b.x() - a.x(), 2) + Math.pow(b.y() - a.y(), 2));
    }

    /**
     * Checks if this segment intersects with another segment.
     *
     * @param other The other segment to check for intersection.
     * @return True if the segments intersect, false otherwise.
     */
    public boolean intersecta(Segmento other)
    {
        double denom = (this.b.x() - this.a.x()) * (other.b.y() - other.a().y()) - (this.b.y() - this.a.y()) * (other.b.x() - other.a().x());
        if (denom == 0)
            return false; // The segments are parallel or collinear

        double alpha = ((other.b.x() - other.a().x()) * (this.a.y() - other.a().y()) - (other.b.y() - other.a().y()) * (this.a.x() - other.a().x())) / denom;
        double beta = ((this.b.x() - this.a.x()) * (this.a.y() - other.a().y()) - (this.b.y() - this.a.y()) * (this.a.x() - other.a().x())) / denom;

        // Check if the intersection is within the segments
        // -0.0 are considered not valid value, only 0.0 and +0.0 are valid
        if (alpha >= 0 && alpha <= 1 && beta >= 0 && beta <= 1 && alpha != -0 && beta != -0) {
            // Check if the intersection is not at the end of the segments
            if (isPointOnSegment(other.a()) || isPointOnSegment(other.b()))
                return false;
            else
                return true;

        }


        return false;
    }


    /**
     * Checks if this segment intersects with Circle.
     *
     * @param other The Circle to check for intersection.
     * @return True if the Circle intersect, false otherwise.
     */
    public boolean intersecta(Circulo other)
    {
        double dx = this.b.x() - this.a.x();
        double dy = this.b.y() - this.a.y();
        double dhx = this.a.x() - other.centro().x();
        double dky = this.a.y() - other.centro().y();

        double A = dx * dx + dy * dy;
        double B = 2 * (dx * dhx + dy * dky);
        double C = dhx * dhx + dky * dky - other.r() * other.r();

        double delta = B * B - 4 * A * C;

        // No intersection
        if (delta < 0)
            return false;


        double sqrtDelta = Math.sqrt(delta);
        double t1 = (-B + sqrtDelta) / (2 * A);
        double t2 = (-B - sqrtDelta) / (2 * A);

        boolean intersectionFound = false;

        // verify if t1 is in the range of the segment (0 to 1)
        if (t1 >= 0 && t1 <= 1)
            intersectionFound = true;


        // verify if t2 is in the range of the segment (0 to 1)
        if (t2 >= 0 && t2 <= 1)
            intersectionFound = true;


        return intersectionFound;
    }

    /**
     * Checks if a given point lies on this segment.
     *
     * @param p The point to check.
     * @return True if the point lies on the segment, false otherwise.
     */
    public boolean isPointOnSegment(Ponto p)
    {
        return this.a.distanciaRadial(p) + this.b.distanciaRadial(p) == this.length();
    }

    /**
	 * Checks if three points are colinear.
	 *(use the method isPointOnSegment to check if the point is on the segment)
     *
	 * @param c The third point to check.
	 * @return True if the points are colinear, false otherwise.
	 */
    public boolean isCollinear(Ponto c)
    {
        return (this.a.x() * (this.b.y() - c.y()) + this.b.x() * (c.y() - this.a.y()) + c.x() * (this.a.y() - this.b.y())) == 0;
    }

    /**
     * Returns a string representation of the segment.
     *
     * @return A string in the format "[a,b]" where a and b are the endpoints of the segment.
     */
    public String toString()
    {
        return "[" + this.a + "," + this.b + "]";
    }

    /**
     * Checks if this segment is equal to another segment.
     *
     * @param s The segment to compare with.
     * @return True if both segments have the same endpoints, false otherwise.
     */
    public boolean equals(Segmento s)
    {
        return this.a.equals(s.a) && this.b.equals(s.b);
    }
}
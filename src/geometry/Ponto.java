package geometry;

/**
 * The Ponto class represents a point in a 2D space with polar coordinates (r,
 * theta) or Cartesian coordinates (x, y).
 * It provides methods to access and manipulate the point's coordinates,
 * calculate distances, and compare points.
 *
 * @author Brandon Mejia
 * @version 2025-02-10
 */
public class Ponto
{
    private double x; // The x coordinate of the point
    private double y; // The y coordinate of the point

    /**
     * Constructor that initializes the point with the given x and y coordinates.
     *
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     */
    public Ponto(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor that creates a new point with the same coordinates as the
     * given point.
     *
     * @param p The point to copy.
     */
    public Ponto(Ponto p) {
        this.x = p.x();
        this.y = p.y();
    }

    /**
     * Gets the radial coordinate of the point.
     *
     * @return The radial coordinate (distance from the origin).
     */
    public double r() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Gets the angular coordinate of the point in degrees.
     *
     * @return The angular coordinate (theta) in degrees.
     */
    public double theta() {
        return Math.toDegrees(Math.atan2(y(), x()));
    }

    /**
     * Gets the x coordinate of the point.
     *
     * @return The x coordinate.
     */
    public double x() {
        return this.x;
    }

    /**
     * Gets the y coordinate of the point.
     *
     * @return The y coordinate.
     */
    public double y() {
        return this.y;
    }

    /**
     * Calculates the radial distance between this point and another point.
     * The radial distance is calculated using polar coordinates.
     *
     * @param p The other point to which the distance is calculated.
     * @return The radial distance between this point and the given point.
     */
    public double distanciaRadial(Ponto p) {
        double dx = this.r() * this.r();
        double dxDoPonto = p.r() * p.r();
        return Math.sqrt(dx + dxDoPonto - this.r() * p.r() * 2 * Math.cos(Math.toRadians(p.theta() - this.theta())));
    }

    /**
     * Calculates the Cartesian distance between this point and another point.
     *
     * @param p The other point to which the distance is calculated.
     * @return The Cartesian distance between this point and the given point.
     */
    public double distancia(Ponto p) {
        double dx = this.x() - p.x();
        double dy = this.y() - p.y();

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks if this point is equal to another point.
     * Two points are considered equal if their x and y coordinates are the same.
     *
     * @param p The other point to compare.
     * @return True if the points are equal, false otherwise.
     */
    public boolean equals(Ponto p) {
        return this.x() == p.x() && this.y() == p.y();
    }

    /**
     * Returns a string representation of the point.
     * The string is formatted as "(x, y)" with two decimal places.
     *
     * @return A string representation of the point.
     */
    public String toString() {
        String x = String.format("%.2f", x()).replace(",", ".");
        String y = String.format("%.2f", y()).replace(",", ".");
        return String.format("(%s,%s)", x, y);
    }
}
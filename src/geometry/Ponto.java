package geometry;

/**
 * The Ponto class represents a point in a 2D space with polar coordinates (r, theta) or Cartesian coordinates (x,y).
 * @author Brandon Mejia
 * @version 2025-02-10
 *
 */
public class Ponto
{
    private double x; // The x coordinate of the point
    private double y; // The y coordinate of the point

/*    /**
//     * Checks if the given coordinates are valid (in the first quadrant and less than 10).
//     *
//     * @param r The radial coordinate to check.
//     * @param theta The angular coordinate to check.
//     */
//    private void invariantesPolares(double r, double theta)
//    {
//        if (r >= 0 && theta >= 0 && theta <= 90)
//            return;
//
//        System.out.println("Ponto:vi");
//        System.exit(0);
//    }

//    /**
//     * Checks if the given Cartesian coordinates are valid (both positive).
//     *
//     * @param x The x coordinate to check.
//     * @param y The y coordinate to check.
//     */
//    private void invariantesCartesianas(int x, int y)
//    {
//        if (x >= 0 && y >= 0)
//            return;
//
//        System.out.println("Ponto:vi");
//        System.exit(0);
//    }

//    /**
//     * Constructor that initializes the point with the given r and theta coordinates.
//     *
//     * @param r The radial coordinate of the point.
//     * @param theta The angular coordinate of the point.
//     */
//    public Ponto(int r, int theta)
//    {
////      invariantesPolares(r, theta);
//        this.x = Math.round(r * Math.cos(Math.toRadians(theta)));
//        this.y = Math.round(r * Math.sin(Math.toRadians(theta)));
//    }

    /**
     * Constructor that initializes the point with the given x and y coordinates.
     *
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     */
    public Ponto(double x, double y)
    {
//        invariantesCartesianas(x, y);
        this.x = x;
        this.y = y; // arc tangent of y/x
    }

    /**
     * Copy constructor that creates a new point with the same coordinates as the given point.
     *
     * @param p The point to copy.
     */
    public Ponto(Ponto p)
    {
        this.x = p.x();
        this.y = p.y();
    }

    /**
     * Gets the radial coordinate of the point.
     *
     * @return The radial coordinate.
     */
    public double r() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Gets the angular coordinate of the point.
     *
     * @return The angular coordinate. (toDegrees)
     */
    public double theta()
    {
        return Math.toDegrees(Math.atan2(y(), x()));
    }

    /**
     * Gets the x coordinate of the point.
     *
     * @return The x coordinate.
     */
    public double x()
    {
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
     * Calculates the distance between this point and another point.
     *
     * @param p The other point to which the distance is calculated.
     * @return The distance between this point and the given point.
     */
    public double distancia(Ponto p)
    {
        double dx = this.x() - p.x();
        double dy = this.y() - p.y();

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks if this point is equal to another point.
     *
     * @param p The other point to compare.
     * @return True if the points are equal, false otherwise.
     */
    public boolean equals(Ponto p)
    {
        return this.x() == p.x() && this.y() == p.y();
    }

    /**
     * Returns a string representation of the point.
     *
     * @return A string representation of the point.
     */
    public String toString()
    {
        String x = String.format("%.2f", x()).replace(",", ".");
        String y = String.format("%.2f", y()).replace(",", ".");
        return String.format("(%s,%s)", x,y);
    }
}
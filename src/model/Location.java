package model;

// ============================================================
// CLASS: Location
// ============================================================
// WHAT  : Represents a 2D coordinate point on a map grid.
// WHY   : Every ride has a pickup and drop location.
//         Keeping this in its own class follows the
//         Single Responsibility Principle — it only deals
//         with position data, nothing else.
// BEST CHOICE: A simple value object (no business logic here).
//              Making it immutable means coordinates never
//              accidentally change after construction.
// ============================================================

public class Location {

    // ------------------------------------------------------------
    // FIELDS: x and y represent coordinates on a 2D map grid.
    // 'final' means they cannot be changed after construction.
    // This makes Location an IMMUTABLE VALUE OBJECT.
    // ------------------------------------------------------------
    private final double x; // x-coordinate (horizontal axis)
    private final double y; // y-coordinate (vertical axis)

    // ------------------------------------------------------------
    // CONSTRUCTOR: Sets up a Location with given x and y values.
    // Called whenever we create a new point on the map.
    // ------------------------------------------------------------
    public Location(double x, double y) {
        this.x = x; // assign the horizontal coordinate
        this.y = y; // assign the vertical coordinate
    }

    // ------------------------------------------------------------
    // GETTERS: Read-only access to coordinates.
    // No setters — keeps this class immutable.
    // ------------------------------------------------------------

    // Returns the x (horizontal) coordinate
    public double getX() {
        return x;
    }

    // Returns the y (vertical) coordinate
    public double getY() {
        return y;
    }

    // ------------------------------------------------------------
    // toString(): Provides a neat string for printing.
    // Called automatically when we do System.out.println(location).
    // ------------------------------------------------------------
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")"; // e.g. "(3.0, 4.0)"
    }
}

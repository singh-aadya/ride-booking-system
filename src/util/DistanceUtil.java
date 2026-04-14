package util;

// ============================================================
// CLASS: DistanceUtil
// ============================================================
// WHAT  : A utility class with a single static method to
//         calculate distance between two Location points.
// WHY   : Keeping distance calculation separate from models
//         and services follows the Single Responsibility Principle.
//         If we ever want to switch from Euclidean to Haversine
//         (real GPS distance), we only change this one class.
// BEST CHOICE: Static utility class — no state needed, just math.
//              Making it 'final' and giving it a private constructor
//              prevents anyone from instantiating it (it's a toolbox,
//              not an object).
// ============================================================

import model.Location; // import our Location model

// 'final' prevents subclassing this utility class
public final class DistanceUtil {

    // ------------------------------------------------------------
    // PRIVATE CONSTRUCTOR: Blocks instantiation.
    // "new DistanceUtil()" would be meaningless — this class
    // only has static methods. This is a standard Java pattern
    // for utility/helper classes.
    // ------------------------------------------------------------
    private DistanceUtil() {
        // intentionally empty — no instances allowed
    }

    // ============================================================
    // METHOD: euclideanDistance()
    // ============================================================
    // WHAT  : Calculates the straight-line distance between two
    //         points on a 2D grid using the Pythagorean theorem.
    // FORMULA: distance = √( (x2-x1)² + (y2-y1)² )
    // WHY EUCLIDEAN: Simple and effective for a grid-based map.
    //         In a real app, you'd use Haversine formula for
    //         actual GPS coordinates (accounts for Earth's curve).
    // PARAMETERS:
    //   a — the starting location (e.g. user's position)
    //   b — the ending location   (e.g. driver's position)
    // RETURNS: distance as a double (decimal number)
    // ============================================================
    public static double euclideanDistance(Location a, Location b) {

        // Step 1: Find horizontal difference (Δx)
        double deltaX = b.getX() - a.getX();

        // Step 2: Find vertical difference (Δy)
        double deltaY = b.getY() - a.getY();

        // Step 3: Apply Pythagorean theorem: √(Δx² + Δy²)
        // Math.pow(n, 2) = n²
        // Math.sqrt(n)   = √n
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }
}

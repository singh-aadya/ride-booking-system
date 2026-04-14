package model;

// ============================================================
// CLASS: Driver
// ============================================================
// WHAT  : Represents a ride driver with location and rating.
// WHY   : Drivers have their own attributes (location, rating,
//         availability) distinct from users. Keeping them separate
//         makes it easy to extend each independently.
// BEST CHOICE: Mutable class (driver's location and availability
//              change over time), but with controlled mutation
//              through setters only.
// ============================================================

public class Driver {

    // ------------------------------------------------------------
    // FIELDS: Core data about each driver.
    // driverId and name are final — they never change.
    // location, rating, and available can change over time.
    // ------------------------------------------------------------
    private final String driverId;   // unique driver ID (e.g. "D001")
    private final String name;       // driver's display name
    private Location location;       // current GPS-like position on grid
    private double rating;           // driver rating out of 5.0
    private boolean available;       // true = ready to accept rides

    // ------------------------------------------------------------
    // CONSTRUCTOR: Creates a driver with all required information.
    // Drivers start as available by default when they're added.
    // ------------------------------------------------------------
    public Driver(String driverId, String name, Location location, double rating) {
        this.driverId = driverId;     // set unique driver ID
        this.name = name;             // set driver's name
        this.location = location;     // set initial map position
        this.rating = rating;         // set star rating (e.g. 4.7)
        this.available = true;        // new drivers are available by default
    }

    // ============================================================
    // CONCEPT: equals() and hashCode() Override
    // ============================================================
    // WHAT  : These two methods together define "equality" for Driver.
    // WHY   : Java's default equals() checks memory address (same object
    //         in RAM). We want two Driver objects with the same driverId
    //         to be considered equal — regardless of who created them.
    // WHY IMPORTANT: We store drivers in a HashSet<Driver>.
    //         HashSet uses hashCode() to find the bucket, then equals()
    //         to confirm the match. Without overriding both, a driver
    //         added twice with the same ID would appear as two separate
    //         entries in the Set — defeating the purpose.
    // RULE  : Always override BOTH together. If two objects are equal
    //         (equals() returns true), they MUST have the same hashCode.
    // ============================================================

    // equals(): Two drivers are the same if their IDs match.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                  // same memory reference = definitely equal
        if (o == null || getClass() != o.getClass()) return false; // null or different type = not equal
        Driver driver = (Driver) o;                  // safe cast — we checked the class above
        return driverId.equals(driver.driverId);     // equality is based solely on driverId
    }

    // hashCode(): Must be consistent with equals().
    // Two drivers with the same ID must return the same hash.
    @Override
    public int hashCode() {
        return driverId.hashCode(); // delegate to String's reliable hashCode
    }

    // ------------------------------------------------------------
    // GETTERS: Read-only access to driver data.
    // ------------------------------------------------------------

    // Returns the driver's unique ID
    public String getDriverId() {
        return driverId;
    }

    // Returns the driver's name
    public String getName() {
        return name;
    }

    // Returns the driver's current location
    public Location getLocation() {
        return location;
    }

    // Returns the driver's rating
    public double getRating() {
        return rating;
    }

    // Returns whether the driver is currently available
    public boolean isAvailable() {
        return available;
    }

    // ------------------------------------------------------------
    // SETTERS: Controlled mutation — only these specific fields can change.
    // ------------------------------------------------------------

    // Updates driver's position (called when driver moves)
    public void setLocation(Location location) {
        this.location = location;
    }

    // Updates driver's availability status
    // true = online and ready | false = on a ride or offline
    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Updates driver's rating (after a ride is rated)
    public void setRating(double rating) {
        this.rating = rating;
    }

    // ------------------------------------------------------------
    // toString(): Clean summary of driver info for console output.
    // ------------------------------------------------------------
    @Override
    public String toString() {
        return String.format("Driver[ID=%s, Name=%-15s, Location=%s, Rating=%.1f, %s]",
                driverId, name, location, rating,
                available ? "AVAILABLE" : "BUSY"); // show status clearly
    }
}

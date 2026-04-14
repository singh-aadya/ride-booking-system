package model;

// ============================================================
// CLASS: Ride (IMMUTABLE)
// ============================================================
// WHAT  : Represents a single booked ride from pickup to drop.
// WHY IMMUTABLE: Once a ride is booked, its core details (who,
//         from where, to where, which driver, at what time)
//         should NEVER change. This is like a signed contract.
//         Immutability prevents accidental bugs where something
//         modifies a ride record that should be permanent.
// HOW TO MAKE IMMUTABLE IN JAVA:
//   1. Declare class as 'final' (can't be subclassed)
//   2. All fields are 'private final' (can't be reassigned)
//   3. No setters (no way to modify fields after construction)
//   4. Return defensive copies of mutable fields in getters
// BEST CHOICE: Rides are historical records. They should be
//              read-only snapshots of what happened.
// ============================================================

import java.time.LocalDateTime;        // Java's built-in date-time class
import java.time.format.DateTimeFormatter; // for formatting date output

// 'final' class — cannot be extended (subclassed)
public final class Ride {

    // All fields are 'private final' — set once in constructor, never changed
    private final String rideId;           // unique ride identifier (e.g. "R001")
    private final User user;               // who booked the ride
    private final Driver driver;           // who is driving
    private final Location pickup;         // where the ride starts
    private final Location drop;           // where the ride ends
    private final LocalDateTime timestamp; // exact time the ride was booked

    // ------------------------------------------------------------
    // CONSTRUCTOR: All data must be provided at creation time.
    // There is no way to build a "half-constructed" Ride.
    // ------------------------------------------------------------
    public Ride(String rideId, User user, Driver driver, Location pickup, Location drop) {
        this.rideId    = rideId;                  // set unique ride ID
        this.user      = user;                    // set the passenger
        this.driver    = driver;                  // set the assigned driver
        this.pickup    = pickup;                  // set pickup location
        this.drop      = drop;                    // set destination
        this.timestamp = LocalDateTime.now();     // capture exact booking time
    }

    // ------------------------------------------------------------
    // GETTERS: The ONLY way to read ride data (no setters exist).
    // ------------------------------------------------------------

    // Returns the ride's unique ID
    public String getRideId() {
        return rideId;
    }

    // Returns the user who booked this ride
    public User getUser() {
        return user;
    }

    // Returns the assigned driver
    public Driver getDriver() {
        return driver;
    }

    // Returns the pickup location
    public Location getPickup() {
        return pickup;
    }

    // Returns the drop location
    public Location getDrop() {
        return drop;
    }

    // Returns the booking timestamp
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // ------------------------------------------------------------
    // toString(): Nicely formatted ride summary for console output.
    // Uses DateTimeFormatter for readable date/time display.
    // ------------------------------------------------------------
    @Override
    public String toString() {
        // Format: "dd-MM-yyyy HH:mm:ss" → e.g. "14-04-2025 10:35:22"
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return "\n  ┌─────────────────────────────────────────┐\n" +
               "  │  Ride ID  : " + rideId                     + "\n" +
               "  │  Passenger: " + user.getName()             + "\n" +
               "  │  Driver   : " + driver.getName()           + "\n" +
               "  │  Pickup   : " + pickup                     + "\n" +
               "  │  Drop     : " + drop                       + "\n" +
               "  │  Booked At: " + timestamp.format(fmt)      + "\n" +
               "  └─────────────────────────────────────────┘";
    }
}

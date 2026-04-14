package model;

// ============================================================
// CLASS: User
// ============================================================
// WHAT  : Represents a customer who can book rides.
// WHY   : Separating User from Driver keeps the code clean.
//         Users book rides; drivers fulfill them — different roles.
// BEST CHOICE: A plain POJO (Plain Old Java Object) with fields,
//              constructor, and getters. No logic here — that
//              lives in UserService.
// ============================================================

import java.util.ArrayList; // ArrayList: an ordered, resizable list
import java.util.List;       // List: the interface ArrayList implements

public class User {

    // ------------------------------------------------------------
    // FIELDS: Core data about each user.
    // 'final' for id and name because they never change.
    // ------------------------------------------------------------
    private final String userId;   // unique identifier (e.g. "U001")
    private final String name;     // user's display name

    // ------------------------------------------------------------
    // DATA STRUCTURE CHOICE: List<Ride> for ride history
    // WHY List: Rides are ordered by time (insertion order matters).
    //           We can iterate forward to show history in sequence.
    //           Duplicates are allowed (same route can be booked twice).
    // WHY ArrayList: Fast random access by index, efficient iteration.
    //                Better than LinkedList for read-heavy use cases.
    // ------------------------------------------------------------
    private final List<Ride> rideHistory; // ordered list of past rides

    // ------------------------------------------------------------
    // CONSTRUCTOR: Creates a new user with a unique ID and name.
    // Initializes rideHistory as an empty list.
    // ------------------------------------------------------------
    public User(String userId, String name) {
        this.userId = userId;           // set the unique user ID
        this.name = name;               // set the user's name
        this.rideHistory = new ArrayList<>(); // start with empty history
    }

    // ------------------------------------------------------------
    // METHOD: addRide()
    // Adds a completed ride to this user's ride history.
    // Called by RideService after a ride is successfully booked.
    // ------------------------------------------------------------
    public void addRide(Ride ride) {
        rideHistory.add(ride); // append ride to end of the list
    }

    // ------------------------------------------------------------
    // GETTERS: Read-only access to user data.
    // ------------------------------------------------------------

    // Returns the user's unique ID
    public String getUserId() {
        return userId;
    }

    // Returns the user's name
    public String getName() {
        return name;
    }

    // Returns a copy of the ride history list
    // WHY copy: Prevents external code from directly modifying the list
    public List<Ride> getRideHistory() {
        return new ArrayList<>(rideHistory); // defensive copy
    }

    // ------------------------------------------------------------
    // toString(): Readable representation of a User object.
    // ------------------------------------------------------------
    @Override
    public String toString() {
        return "User[ID=" + userId + ", Name=" + name + "]";
    }
}

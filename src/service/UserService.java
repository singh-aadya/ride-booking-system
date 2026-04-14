package service;

// ============================================================
// CLASS: UserService
// ============================================================
// WHAT  : Manages all user-related operations:
//         adding users, looking them up, retrieving history.
// WHY SERVICE LAYER: Separating business logic from models
//         keeps code clean and testable. The User model just
//         holds data; UserService acts on that data.
// BEST CHOICE: Stateful service class (holds the user registry).
//              In a real app, this would talk to a database.
// ============================================================

import model.User;    // import User model
import model.Ride;    // import Ride model (for history display)

import java.util.HashMap;  // HashMap: key-value store for fast lookup
import java.util.Map;      // Map: the interface HashMap implements
import java.util.List;     // List: for ride history

public class UserService {

    // ============================================================
    // DATA STRUCTURE: Map<String, User>
    // ============================================================
    // WHAT  : A HashMap storing userId → User pairs.
    // WHY MAP: Maps allow O(1) average-time lookup by key.
    //          Given a userId like "U001", we can instantly find
    //          the matching User object without scanning every user.
    // WHY HASHMAP: No ordering needed; just fast lookup by ID.
    //              If we needed sorted users, we'd use TreeMap.
    // ============================================================
    private final Map<String, User> userRegistry; // userId → User

    // Counter to auto-generate unique user IDs (U001, U002, ...)
    private int idCounter = 1;

    // CONSTRUCTOR: Initialize the empty user registry
    public UserService() {
        this.userRegistry = new HashMap<>(); // empty map at startup
    }

    // ============================================================
    // METHOD: registerUser()
    // ============================================================
    // Adds a new user to the system.
    // Auto-generates a unique ID like "U001", "U002", etc.
    // Returns the newly created User object.
    // ============================================================
    public User registerUser(String name) {
        // Generate a unique ID: U001, U002, U003 ...
        // String.format pads the number to 3 digits with leading zeros
        String userId = "U" + String.format("%03d", idCounter++);

        // Create the new User object with generated ID and given name
        User user = new User(userId, name);

        // Store in the registry: key = userId, value = User object
        userRegistry.put(userId, user);

        // Return the created user so the caller can use it
        return user;
    }

    // ============================================================
    // METHOD: getUserById()
    // ============================================================
    // Looks up a user by their ID.
    // Returns null if no user is found with that ID.
    // O(1) lookup thanks to HashMap.
    // ============================================================
    public User getUserById(String userId) {
        return userRegistry.get(userId); // returns null if not found
    }

    // ============================================================
    // METHOD: displayRideHistory()
    // ============================================================
    // Prints all past rides for a given user.
    // Iterates through the user's List<Ride> in insertion order.
    // ============================================================
    public void displayRideHistory(String userId) {
        User user = getUserById(userId); // find the user

        // If user doesn't exist, show error and return
        if (user == null) {
            System.out.println("  [!] No user found with ID: " + userId);
            return;
        }

        // Get the ordered list of rides
        List<Ride> history = user.getRideHistory();

        // Check if history is empty
        if (history.isEmpty()) {
            System.out.println("  [!] No ride history found for " + user.getName() + ".");
            return;
        }

        // Print header
        System.out.println("\n  ═══════════════════════════════════════════");
        System.out.println("   Ride History for: " + user.getName());
        System.out.println("  ═══════════════════════════════════════════");

        // Iterate through each ride and print it
        // Enhanced for-loop: goes through each ride in order
        for (Ride ride : history) {
            System.out.println(ride); // calls Ride's toString()
        }
    }

    // ============================================================
    // METHOD: displayAllUsers()
    // ============================================================
    // Lists every registered user.
    // Iterates over all values in the HashMap.
    // ============================================================
    public void displayAllUsers() {
        if (userRegistry.isEmpty()) {
            System.out.println("  [!] No users registered yet.");
            return;
        }

        System.out.println("\n  Registered Users:");
        System.out.println("  -----------------------------------------");

        // Iterate over all User values in the HashMap
        for (User user : userRegistry.values()) {
            System.out.println("  " + user); // calls User's toString()
        }
    }
}

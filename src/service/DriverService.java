package service;

// ============================================================
// CLASS: DriverService
// ============================================================
// WHAT  : Manages all driver-related operations:
//         adding drivers, toggling availability, listing drivers.
// WHY SERVICE LAYER: Business rules about drivers (e.g. who is
//         available) belong here, not in the Driver model.
// ============================================================

import model.Driver;    // import Driver model
import model.Location;  // import Location model

import java.util.HashMap;  // HashMap: fast lookup by driverId
import java.util.HashSet;  // HashSet: unique set of available drivers
import java.util.Map;      // Map interface
import java.util.Set;      // Set interface

public class DriverService {

    // ============================================================
    // DATA STRUCTURE 1: Map<String, Driver> — driver registry
    // ============================================================
    // WHAT  : Stores all drivers, keyed by their unique driverId.
    // WHY MAP: O(1) lookup when we need to find a specific driver
    //          (e.g. "give me driver D002"). Without a Map, we'd
    //          have to scan every driver in a list — O(n).
    // WHY HASHMAP: No ordering requirement; just fast lookup.
    // ============================================================
    private final Map<String, Driver> driverRegistry; // all drivers

    // ============================================================
    // DATA STRUCTURE 2: Set<Driver> — available drivers
    // ============================================================
    // WHAT  : Tracks only the drivers who are currently free.
    // WHY SET:
    //   1. NO DUPLICATES — a driver can't be "available twice".
    //      HashSet automatically prevents adding the same driver again.
    //   2. FAST REMOVAL — when a driver takes a ride, we remove them
    //      in O(1) time (HashSet uses equals()/hashCode() for this).
    //   3. FAST ADDITION — when a driver finishes, O(1) to add back.
    //   This is why we overrode equals()/hashCode() in Driver class!
    // WHY NOT LIST: Lists allow duplicates and have O(n) removal.
    // ============================================================
    private final Set<Driver> availableDrivers; // currently free drivers

    // Counter for auto-generating driver IDs
    private int idCounter = 1;

    // CONSTRUCTOR: Initialize empty registry and available set
    public DriverService() {
        this.driverRegistry  = new HashMap<>(); // empty driver map
        this.availableDrivers = new HashSet<>(); // empty available set
    }

    // ============================================================
    // METHOD: registerDriver()
    // ============================================================
    // Adds a new driver to the system.
    // Drivers are available by default when registered.
    // Returns the new Driver object.
    // ============================================================
    public Driver registerDriver(String name, Location location, double rating) {
        // Generate unique ID: D001, D002, D003 ...
        String driverId = "D" + String.format("%03d", idCounter++);

        // Create new Driver object
        Driver driver = new Driver(driverId, name, location, rating);

        // Add to registry (all drivers, for lookup)
        driverRegistry.put(driverId, driver);

        // Add to available set (drivers ready to accept rides)
        availableDrivers.add(driver);

        return driver; // return so caller can confirm
    }

    // ============================================================
    // METHOD: setDriverAvailability()
    // ============================================================
    // Marks a driver as available or unavailable.
    // Used to go online/offline or when assigned a ride.
    // ============================================================
    public void setDriverAvailability(String driverId, boolean available) {
        Driver driver = driverRegistry.get(driverId); // O(1) lookup

        if (driver == null) {
            System.out.println("  [!] Driver not found: " + driverId);
            return;
        }

        // Update the driver's availability flag
        driver.setAvailable(available);

        if (available) {
            // Driver is back online → add to available set
            // HashSet.add() won't duplicate because equals()/hashCode() guards it
            availableDrivers.add(driver);
            System.out.println("  ✔ " + driver.getName() + " is now ONLINE.");
        } else {
            // Driver went offline → remove from available set
            // HashSet.remove() uses equals()/hashCode() — O(1) average
            availableDrivers.remove(driver);
            System.out.println("  ✔ " + driver.getName() + " is now OFFLINE.");
        }
    }

    // ============================================================
    // METHOD: getAvailableDrivers()
    // ============================================================
    // Returns the Set of currently available drivers.
    // RideService uses this to find the best driver.
    // ============================================================
    public Set<Driver> getAvailableDrivers() {
        return availableDrivers; // return the live set (not a copy)
    }

    // ============================================================
    // METHOD: displayAvailableDrivers()
    // ============================================================
    // Prints all drivers currently ready to accept rides.
    // ============================================================
    public void displayAvailableDrivers() {
        if (availableDrivers.isEmpty()) {
            System.out.println("  [!] No drivers available right now.");
            return;
        }

        System.out.println("\n  Available Drivers:");
        System.out.println("  -----------------------------------------");

        // Iterate over the Set — order is not guaranteed (HashSet)
        for (Driver driver : availableDrivers) {
            System.out.printf("  %-8s %-15s Rating: %.1f  Location: %s%n",
                    driver.getDriverId(), driver.getName(),
                    driver.getRating(), driver.getLocation());
        }
    }

    // ============================================================
    // METHOD: displayAllDrivers()
    // ============================================================
    // Prints ALL drivers (available and unavailable).
    // ============================================================
    public void displayAllDrivers() {
        if (driverRegistry.isEmpty()) {
            System.out.println("  [!] No drivers registered yet.");
            return;
        }

        System.out.println("\n  All Registered Drivers:");
        System.out.println("  -----------------------------------------");

        for (Driver driver : driverRegistry.values()) {
            System.out.println("  " + driver);
        }
    }
}

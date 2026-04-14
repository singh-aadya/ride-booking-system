package service;

// ============================================================
// CLASS: RideService
// ============================================================
// WHAT  : The core service that handles ride booking.
//         It brings together User, Driver, and Ride to
//         complete the full booking flow.
// WHY SERVICE LAYER: Booking logic doesn't belong in models.
//         RideService orchestrates the entire process:
//         find best driver → assign → record history.
// KEY FEATURE: Uses Java Streams API for driver selection.
// ============================================================

import model.Driver;     // Driver model
import model.Location;   // Location model
import model.Ride;       // Ride model (immutable)
import model.User;       // User model
import util.DistanceUtil; // our Euclidean distance calculator

import java.util.Comparator;  // for sorting logic
import java.util.List;        // list for holding all rides
import java.util.ArrayList;   // implementation of List
import java.util.Optional;    // container that may or may not hold a value
import java.util.Set;         // Set of available drivers
import java.util.stream.Collectors; // Stream terminal operations

public class RideService {

    // ------------------------------------------------------------
    // DEPENDENCY: References to UserService and DriverService.
    // RideService needs both to complete a booking.
    // This is called DEPENDENCY INJECTION (lite version) —
    // we pass the services in rather than creating them here.
    // ------------------------------------------------------------
    private final UserService userService;       // to find users
    private final DriverService driverService;   // to find/assign drivers

    // List of ALL rides booked in the system (complete record)
    private final List<Ride> allRides;

    // Counter for auto-generating unique Ride IDs
    private int rideCounter = 1;

    // CONSTRUCTOR: Accepts the service dependencies
    public RideService(UserService userService, DriverService driverService) {
        this.userService    = userService;   // inject user service
        this.driverService  = driverService; // inject driver service
        this.allRides       = new ArrayList<>(); // empty ride ledger
    }

    // ============================================================
    // METHOD: bookRide()  ← The MAIN method of this whole project
    // ============================================================
    // FLOW:
    //   1. Validate user exists
    //   2. Get all available drivers
    //   3. Use Streams to find the best one (closest + highest rated)
    //   4. Mark driver as unavailable
    //   5. Create immutable Ride object
    //   6. Record in user history and global list
    //   7. Print confirmation
    // ============================================================
    public Ride bookRide(String userId, Location pickup, Location drop) {

        System.out.println("\n  ──────────────────────────────────────────");
        System.out.println("   Searching for best driver...");
        System.out.println("  ──────────────────────────────────────────");

        // STEP 1: Look up the user who wants a ride
        User user = userService.getUserById(userId);
        if (user == null) {
            System.out.println("  [!] User not found: " + userId);
            return null; // can't book without a valid user
        }

        // STEP 2: Get the set of available drivers from DriverService
        Set<Driver> available = driverService.getAvailableDrivers();

        if (available.isEmpty()) {
            System.out.println("  [!] No drivers available at the moment. Please try again later.");
            return null;
        }

        // ============================================================
        // CONCEPT: Java Streams API
        // ============================================================
        // WHAT  : Streams let you process collections in a declarative,
        //         pipeline-style way — similar to SQL queries.
        // WHY   : Instead of writing nested for-loops and if-statements
        //         to find the best driver, Streams let us express the
        //         logic in a readable, step-by-step chain.
        // WHY BEST: Cleaner than loops, easy to modify sorting rules,
        //           and can be parallelized for large datasets.
        //
        // PIPELINE BREAKDOWN:
        //   available.stream()     → convert Set to a Stream
        //   .filter(...)           → keep only truly available drivers
        //   .sorted(...)           → sort by distance, then by rating
        //   .findFirst()           → pick the #1 result
        //
        // Optional<Driver> is used because findFirst() might return
        // nothing if the stream is empty — Optional forces us to
        // handle that case explicitly (no null pointer surprises).
        // ============================================================
        Optional<Driver> bestDriverOpt = available.stream()

            // FILTER: Only consider drivers marked available
            // (Double-check, even though the Set should already guarantee this)
            .filter(Driver::isAvailable)

            // SORT (PRIMARY): By distance — ASCENDING (closer = better)
            // Comparator.comparingDouble compares two doubles
            // d → DistanceUtil.euclideanDistance(...) is a lambda:
            //   "for each driver d, compute distance from driver to pickup"
            .sorted(Comparator.comparingDouble(
                    (Driver d) -> DistanceUtil.euclideanDistance(d.getLocation(), pickup)
                )
                // SORT (SECONDARY): If two drivers are equally close,
                // prefer the one with HIGHER rating.
                // 'reversed()' flips ascending to descending (higher first)
                .thenComparing(
                    Comparator.comparingDouble(Driver::getRating).reversed()
                )
            )

            // TERMINAL OPERATION: Pick the first result (best match)
            // Returns Optional<Driver> — might be empty if no drivers left
            .findFirst();

        // STEP 4: Check if we actually found a driver
        if (bestDriverOpt.isEmpty()) {
            System.out.println("  [!] Could not assign a driver. Please try again.");
            return null;
        }

        // Unwrap the Optional — we know it's present
        Driver assignedDriver = bestDriverOpt.get();

        // STEP 5: Mark driver as unavailable (they're now on a ride)
        // This removes them from the available Set in DriverService
        driverService.setDriverAvailability(assignedDriver.getDriverId(), false);

        // STEP 6: Generate unique Ride ID
        String rideId = "R" + String.format("%03d", rideCounter++);

        // STEP 7: Create the IMMUTABLE Ride object
        // Once created, pickup/drop/driver CANNOT change — it's a record
        Ride ride = new Ride(rideId, user, assignedDriver, pickup, drop);

        // STEP 8: Save ride to the user's personal history
        user.addRide(ride);

        // STEP 9: Save ride to the global ride ledger
        allRides.add(ride);

        // STEP 10: Print a polished confirmation message
        double dist = DistanceUtil.euclideanDistance(assignedDriver.getLocation(), pickup);
        System.out.println("\n  ✔ Ride Successfully Booked!");
        System.out.println("  ─────────────────────────────────────────");
        System.out.printf ("  Driver Assigned : %s (ID: %s)%n",
                assignedDriver.getName(), assignedDriver.getDriverId());
        System.out.printf ("  Driver Rating   : %.1f ⭐%n", assignedDriver.getRating());
        System.out.printf ("  Driver Distance : %.2f units from pickup%n", dist);
        System.out.println("  Pickup Location : " + pickup);
        System.out.println("  Drop Location   : " + drop);
        System.out.println("  Ride ID         : " + rideId);

        return ride; // return the booked ride
    }

    // ============================================================
    // METHOD: completeRide()
    // ============================================================
    // Marks a ride as completed and makes the driver available again.
    // In a real app, this would be called when GPS reaches destination.
    // ============================================================
    public void completeRide(Ride ride) {
        if (ride == null) return;

        // Make the driver available again (they finished the trip)
        String driverId = ride.getDriver().getDriverId();
        driverService.setDriverAvailability(driverId, true);

        System.out.println("\n  ✔ Ride " + ride.getRideId() + " completed.");
        System.out.println("  Driver " + ride.getDriver().getName() + " is now available again.");
    }

    // ============================================================
    // METHOD: displayAllRides()
    // ============================================================
    // Shows every ride ever booked in the system.
    // ============================================================
    public void displayAllRides() {
        if (allRides.isEmpty()) {
            System.out.println("  [!] No rides have been booked yet.");
            return;
        }

        System.out.println("\n  ═══════════════════════════════════════════");
        System.out.println("   All Rides in System (" + allRides.size() + " total)");
        System.out.println("  ═══════════════════════════════════════════");

        for (Ride ride : allRides) {
            System.out.println(ride);
        }
    }
}

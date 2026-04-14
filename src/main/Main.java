package main;

// ============================================================
// CLASS: Main
// ============================================================
// WHAT  : The entry point of the application.
//         Displays the console menu and handles user input.
// WHY SEPARATE: The main runner should only deal with UI flow
//         (reading input, printing menus). All actual logic
//         lives in the service classes. This is the MVC pattern
//         applied to a console app.
// ============================================================

import model.Driver;         // Driver model
import model.Location;       // Location model
import model.Ride;           // Ride model (immutable)
import model.User;           // User model
import service.DriverService; // manages drivers
import service.RideService;   // handles ride booking
import service.UserService;   // manages users

import java.util.Scanner;    // Scanner: reads keyboard input line by line

public class Main {

    // ============================================================
    // SERVICES: Instantiated once and shared across all operations.
    // UserService, DriverService, and RideService are all stateful
    // — they hold data (users, drivers, rides) in memory.
    // ============================================================
    private static final UserService   userService   = new UserService();
    private static final DriverService driverService = new DriverService();

    // RideService depends on both UserService and DriverService
    // We pass them in at construction time (dependency injection)
    private static final RideService   rideService   = new RideService(userService, driverService);

    // Scanner reads text typed by the user in the console
    private static final Scanner scanner = new Scanner(System.in);

    // ============================================================
    // main(): Java's program entry point.
    // Execution starts here.
    // ============================================================
    public static void main(String[] args) {

        // Pre-load the system with sample data so it's ready to use
        loadSampleData();

        // Print welcome banner
        printBanner();

        // Track if the user wants to exit
        boolean running = true;

        // Keep showing the menu until the user chooses to quit
        while (running) {
            printMenu();                      // show the menu options
            String choice = scanner.nextLine().trim(); // read user's choice

            // Use a switch to handle each menu option
            switch (choice) {
                case "1" -> addUser();              // register new user
                case "2" -> addDriver();            // register new driver
                case "3" -> bookRide();             // book a ride
                case "4" -> viewRideHistory();      // view a user's rides
                case "5" -> driverService.displayAvailableDrivers(); // show free drivers
                case "6" -> driverService.displayAllDrivers();       // show all drivers
                case "7" -> userService.displayAllUsers();           // show all users
                case "8" -> rideService.displayAllRides();           // show all rides
                case "9" -> toggleDriverAvailability();              // online/offline toggle
                case "0" -> {
                    // User chose to exit
                    System.out.println("\n  👋 Thank you for using RideBook. Goodbye!\n");
                    running = false; // exit the while loop
                }
                default -> System.out.println("  [!] Invalid option. Please enter 0-9."); // bad input
            }
        }

        scanner.close(); // release the Scanner resource
    }

    // ============================================================
    // loadSampleData(): Pre-populates the system with demo data.
    // This way, users can test the system immediately without
    // manually adding users and drivers first.
    // ============================================================
    private static void loadSampleData() {
        System.out.println("\n  Loading sample data...");

        // Register 3 sample users
        userService.registerUser("Alice Johnson");
        userService.registerUser("Bob Smith");
        userService.registerUser("Carol White");

        // Register 5 sample drivers with different locations and ratings
        // Location(x, y) — think of it as a position on a city grid
        driverService.registerDriver("Raj Kumar",      new Location(2.0, 3.0), 4.8);
        driverService.registerDriver("Priya Mehta",    new Location(5.0, 1.0), 4.5);
        driverService.registerDriver("Suresh Nair",    new Location(1.0, 6.0), 4.9);
        driverService.registerDriver("Deepa Sharma",   new Location(7.0, 4.0), 4.2);
        driverService.registerDriver("Arjun Patel",    new Location(3.0, 3.5), 4.7);

        System.out.println("  ✔ 3 users and 5 drivers loaded.\n");
    }

    // ============================================================
    // printBanner(): Displays the welcome header.
    // ============================================================
    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║        🚗  RIDEBOOK CONSOLE APP  🚗      ║");
        System.out.println("  ║     Uber/Ola-Style Ride Booking System   ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
    }

    // ============================================================
    // printMenu(): Prints all available options.
    // ============================================================
    private static void printMenu() {
        System.out.println("\n  ┌─────────────────────────────────────────┐");
        System.out.println("  │              MAIN MENU                  │");
        System.out.println("  ├─────────────────────────────────────────┤");
        System.out.println("  │  1. Add New User                        │");
        System.out.println("  │  2. Add New Driver                      │");
        System.out.println("  │  3. Book a Ride                         │");
        System.out.println("  │  4. View Ride History (by User ID)      │");
        System.out.println("  │  5. Show Available Drivers              │");
        System.out.println("  │  6. Show All Drivers                    │");
        System.out.println("  │  7. Show All Users                      │");
        System.out.println("  │  8. Show All Rides                      │");
        System.out.println("  │  9. Toggle Driver Availability          │");
        System.out.println("  │  0. Exit                                │");
        System.out.println("  └─────────────────────────────────────────┘");
        System.out.print("  Enter your choice: ");
    }

    // ============================================================
    // addUser(): Prompts for a name and registers a new user.
    // ============================================================
    private static void addUser() {
        System.out.print("\n  Enter user's full name: ");
        String name = scanner.nextLine().trim(); // read the name

        if (name.isEmpty()) {
            System.out.println("  [!] Name cannot be empty.");
            return;
        }

        User user = userService.registerUser(name); // create user
        System.out.println("  ✔ User registered: " + user);
    }

    // ============================================================
    // addDriver(): Prompts for driver details and registers them.
    // ============================================================
    private static void addDriver() {
        System.out.print("\n  Enter driver's full name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("  [!] Name cannot be empty.");
            return;
        }

        // Get location coordinates
        System.out.print("  Enter location (x y) — e.g. '3.5 7.2': ");
        String[] coords = scanner.nextLine().trim().split("\\s+"); // split by spaces

        double x, y, rating;

        try {
            x = Double.parseDouble(coords[0]); // parse x coordinate
            y = Double.parseDouble(coords[1]); // parse y coordinate
        } catch (Exception e) {
            System.out.println("  [!] Invalid coordinates. Use format: x y (e.g. 3.5 7.2)");
            return;
        }

        // Get driver rating
        System.out.print("  Enter driver rating (1.0 - 5.0): ");
        try {
            rating = Double.parseDouble(scanner.nextLine().trim());
            if (rating < 1.0 || rating > 5.0) throw new NumberFormatException(); // validate range
        } catch (Exception e) {
            System.out.println("  [!] Invalid rating. Must be between 1.0 and 5.0.");
            return;
        }

        // Register the driver
        Driver driver = driverService.registerDriver(name, new Location(x, y), rating);
        System.out.println("  ✔ Driver registered: " + driver);
    }

    // ============================================================
    // bookRide(): Walks the user through booking a ride step-by-step.
    // ============================================================
    private static void bookRide() {
        System.out.println("\n  ── Book a Ride ─────────────────────────────");

        // Show current users for convenience
        userService.displayAllUsers();

        // Get user ID
        System.out.print("\n  Enter User ID (e.g. U001): ");
        String userId = scanner.nextLine().trim().toUpperCase(); // normalize to uppercase

        // Get pickup location
        System.out.print("  Enter pickup location (x y) — e.g. '4.0 5.0': ");
        Location pickup = parseLocation();
        if (pickup == null) return; // parsing failed, error already shown

        // Get drop location
        System.out.print("  Enter drop location   (x y) — e.g. '9.0 2.0': ");
        Location drop = parseLocation();
        if (drop == null) return;

        // Book the ride through RideService
        // RideService handles driver selection (via Streams) and history recording
        Ride ride = rideService.bookRide(userId, pickup, drop);

        if (ride != null) {
            // Ask if user wants to complete the ride now
            System.out.print("\n  Simulate ride completion? (y/n): ");
            String yn = scanner.nextLine().trim().toLowerCase();
            if (yn.equals("y")) {
                rideService.completeRide(ride); // driver becomes available again
            }
        }
    }

    // ============================================================
    // viewRideHistory(): Shows all past rides for a specific user.
    // ============================================================
    private static void viewRideHistory() {
        userService.displayAllUsers(); // helpful context

        System.out.print("\n  Enter User ID to view history (e.g. U001): ");
        String userId = scanner.nextLine().trim().toUpperCase();

        userService.displayRideHistory(userId); // delegate to UserService
    }

    // ============================================================
    // toggleDriverAvailability(): Sets a driver online or offline.
    // ============================================================
    private static void toggleDriverAvailability() {
        driverService.displayAllDrivers(); // show current status

        System.out.print("\n  Enter Driver ID (e.g. D001): ");
        String driverId = scanner.nextLine().trim().toUpperCase();

        System.out.print("  Set to (1) Available / (2) Unavailable: ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            driverService.setDriverAvailability(driverId, true);  // online
        } else if (choice.equals("2")) {
            driverService.setDriverAvailability(driverId, false); // offline
        } else {
            System.out.println("  [!] Invalid choice. Enter 1 or 2.");
        }
    }

    // ============================================================
    // parseLocation(): Helper to read and parse "x y" input.
    // Returns null if the input is invalid.
    // ============================================================
    private static Location parseLocation() {
        try {
            String[] parts = scanner.nextLine().trim().split("\\s+"); // split by whitespace
            double x = Double.parseDouble(parts[0]); // parse first number as x
            double y = Double.parseDouble(parts[1]); // parse second number as y
            return new Location(x, y);               // return valid location
        } catch (Exception e) {
            System.out.println("  [!] Invalid location. Use format: x y (e.g. 4.0 5.0)");
            return null; // signal parsing failure
        }
    }
}

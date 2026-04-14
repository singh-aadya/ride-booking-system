# 🚗 RideBook — Java Console Ride Booking System

A clean Java console application simulating an Uber/Ola-style ride booking system.
Built with strong OOP principles, the Java Streams API, and well-chosen data structures.

---

## 📁 Project Structure

```
ride-booking-system/
│
├── README.md                    ← You are here
├── sample-output.txt            ← What the app looks like when running
│
└── src/
    ├── model/
    │   ├── Location.java        ← Immutable 2D coordinate point
    │   ├── User.java            ← Passenger with ride history
    │   ├── Driver.java          ← Driver with location, rating, availability
    │   └── Ride.java            ← Immutable ride record (contract)
    │
    ├── service/
    │   ├── UserService.java     ← Register users, display history
    │   ├── DriverService.java   ← Register drivers, manage availability
    │   └── RideService.java     ← Book rides, select best driver (Streams)
    │
    ├── util/
    │   └── DistanceUtil.java    ← Euclidean distance calculator
    │
    └── main/
        └── Main.java            ← Console UI and menu loop
```

---

## 🏗️ Architecture Overview

The project follows a clean **3-layer architecture**:

| Layer       | Classes                              | Responsibility                               |
|-------------|--------------------------------------|----------------------------------------------|
| **Model**   | User, Driver, Ride, Location         | Hold data only. No business logic.           |
| **Service** | UserService, DriverService, RideService | Business rules and operations.           |
| **Util**    | DistanceUtil                         | Reusable math / helper functions.            |
| **Main**    | Main                                 | Console UI: reads input, calls services.     |

This separation means each class has exactly one reason to change (**Single Responsibility Principle**).

---

## 📦 Data Structure Decisions

### `Map<String, User>` — User Registry
- **Why Map?** O(1) average-time lookup by userId key.
- **Why HashMap?** No ordering needed; just fast access.
- Alternative: `TreeMap` if you needed users sorted alphabetically — but we don't.

### `Map<String, Driver>` — Driver Registry
- Same reasons as User registry above.
- Lets us find any driver by ID in constant time.

### `Set<Driver>` — Available Drivers
- **Why Set?** Prevents duplicate driver entries. A driver can't be "available twice."
- **Why HashSet?** O(1) average add and remove operations.
- When a driver is assigned a ride, they're removed from the Set instantly.
- When a ride completes, they're re-added. This is why `equals()` and `hashCode()` are overridden in `Driver`.

### `List<Ride>` — Ride History
- **Why List?** Rides are ordered by time (insertion order matters for history).
- **Why ArrayList?** Fast iteration and random access. Most operations are reads.

---

## 🧠 How Driver Selection Works (Java Streams)

When a user books a ride, `RideService.bookRide()` runs this pipeline:

```java
Optional<Driver> best = availableDrivers.stream()
    .filter(Driver::isAvailable)
    .sorted(
        Comparator.comparingDouble(d -> DistanceUtil.euclideanDistance(d.getLocation(), pickup))
    )
    .thenComparing(Comparator.comparingDouble(Driver::getRating).reversed())
    .findFirst();
```

**Step by step:**
1. **`.stream()`** — Convert the `HashSet<Driver>` into a processable Stream.
2. **`.filter(Driver::isAvailable)`** — Discard any offline drivers.
3. **`.sorted(...)`** — Sort by distance to pickup (ascending = closer first).
4. **`.thenComparing(...)`** — Tie-break by rating (descending = higher rating first).
5. **`.findFirst()`** — Return the top result wrapped in `Optional<Driver>`.

**Distance Formula (Euclidean):**
```
distance = √( (x2 - x1)² + (y2 - y1)² )
```

---

## 🔒 Immutable Ride Class

The `Ride` class is **immutable** — once booked, it can never change:

- Class is declared `final` (no subclassing)
- All fields are `private final` (set once in constructor)
- No setter methods exist
- Returns defensive copies from getters

This models real-world behavior: a booking confirmation is a permanent record.

---

## ⚖️ `equals()` and `hashCode()` in Driver

These two methods are overridden in `Driver` to ensure correct behavior in `HashSet<Driver>`:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Driver driver = (Driver) o;
    return driverId.equals(driver.driverId);  // same ID = same driver
}

@Override
public int hashCode() {
    return driverId.hashCode();
}
```

**Why this matters:**
Java's `HashSet` uses `hashCode()` to find the bucket, then `equals()` to confirm a match.
Without this override, two `Driver` objects with the same ID would be treated as different objects — causing duplicates in the available-drivers Set.

---

## ▶️ How to Run (Windows 11 — Command Prompt)

### Prerequisites
- Java JDK 17 or higher installed
- Download from: https://adoptium.net

### Step 1 — Verify Java is installed
```cmd
java -version
javac -version
```

### Step 2 — Navigate to project folder
```cmd
cd C:\path\to\ride-booking-system
```

### Step 3 — Create output directory
```cmd
mkdir out
```

### Step 4 — Compile all Java files
```cmd
javac -d out src\model\Location.java src\model\Driver.java src\model\User.java src\model\Ride.java src\util\DistanceUtil.java src\service\UserService.java src\service\DriverService.java src\service\RideService.java src\main\Main.java
```

### Step 5 — Run the application
```cmd
java -cp out main.Main
```

---

## 🐧 How to Run (Linux / macOS — Terminal)

```bash
cd ride-booking-system
mkdir -p out
javac -d out src/model/Location.java src/model/Driver.java src/model/User.java src/model/Ride.java src/util/DistanceUtil.java src/service/UserService.java src/service/DriverService.java src/service/RideService.java src/main/Main.java
java -cp out main.Main
```

---

## 🎯 Key OOP Concepts Demonstrated

| Concept               | Where Used                                      |
|-----------------------|-------------------------------------------------|
| Immutability          | `Ride.java` (final class, final fields)         |
| Encapsulation         | All classes (private fields, public getters)    |
| Single Responsibility | Each class/service has one job                  |
| Dependency Injection  | `RideService` receives services via constructor |
| Streams API           | Driver selection in `RideService.bookRide()`    |
| Optional              | `findFirst()` return type — no null surprises   |
| equals/hashCode       | `Driver.java` — correct HashSet behavior        |
| Polymorphism          | `toString()` overridden in all model classes    |

---

## 📋 Sample Output

See `sample-output.txt` for a full walkthrough of a session.

---

*Built as a learning project to demonstrate Java fundamentals, OOP, and data structures.*

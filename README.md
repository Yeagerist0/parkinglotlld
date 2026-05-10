# Parking Lot System — Low-Level Design

Java implementation of the classic parking-lot LLD interview problem: multi-level, multi-gate parking with nearest-slot allocation and time-based billing.

**How allocation works.** Each level exposes SMALL, MEDIUM, and LARGE slots. When a vehicle enters through a gate, the system picks the nearest available slot of the required type using precomputed gate-to-slot distances, so entering through a different gate can route a vehicle to a different slot.

**Vehicle-to-slot mapping.** Bikes and cars map to SMALL/MEDIUM slots; buses always require a LARGE slot, regardless of which level has capacity.

**Ticketing and billing.** Parking issues a Ticket on entry; on exit, the system computes billable hours (rounded up) and charges a per-vehicle-type hourly rate.

**Design.** A builder-style `ParkingLotFactory.Config` assembles levels, gates, slots, and gate-to-slot distances, then `ParkingLotFactory.create()` wires everything into a running `ParkingLotService`.

## Structure

`models/`, `services/`, `factory/`, `enums/`, `exceptions/`, and `Main.java` (a runnable demo that parks and exits four vehicles under the rules above).

## Run

Compile with `javac` and run `Main`, or open the project in your IDE. It's a self-contained console demo with no external dependencies.

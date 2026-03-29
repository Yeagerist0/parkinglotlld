import enums.SlotType;
import enums.VehicleType;
import factory.ParkingLotFactory;
import models.Ticket;
import models.Vehicle;
import services.ParkingLotService;

/**
 * Demo: 2-level parking lot
 *
 * Level 1 slots:
 *   Slot 1 -> SMALL   Slot 2 -> SMALL
 *   Slot 3 -> MEDIUM  Slot 4 -> MEDIUM
 *   Slot 5 -> LARGE
 *
 * Level 2 slots: same structure (ids 6-10)
 *
 * Gates: Gate 1 (North), Gate 2 (South)
 * Distances from Gate 1 on Level 1: slot1=5, slot2=10, slot3=8, slot4=15, slot5=20
 * Distances from Gate 2 on Level 1: slot1=18, slot2=12, slot3=7, slot4=3, slot5=6
 */
public class Main {

    public static void main(String[] args) {

        ParkingLotFactory.Config config = ParkingLotFactory.Config.builder()
            .numLevels(2)

            // --- Level 1 slots ---
            .addSlot(1, 1, SlotType.SMALL)
            .addSlot(1, 2, SlotType.SMALL)
            .addSlot(1, 3, SlotType.MEDIUM)
            .addSlot(1, 4, SlotType.MEDIUM)
            .addSlot(1, 5, SlotType.LARGE)

            // --- Level 2 slots ---
            .addSlot(2, 6,  SlotType.SMALL)
            .addSlot(2, 7,  SlotType.SMALL)
            .addSlot(2, 8,  SlotType.MEDIUM)
            .addSlot(2, 9,  SlotType.MEDIUM)
            .addSlot(2, 10, SlotType.LARGE)

            // --- Gates ---
            .addGate(1, "North Gate")
            .addGate(2, "South Gate")

            // --- Distances: Level 1, Gate 1 ---
            .addDistance(1, 1, 1,  5)
            .addDistance(1, 1, 2, 10)
            .addDistance(1, 1, 3,  8)
            .addDistance(1, 1, 4, 15)
            .addDistance(1, 1, 5, 20)

            // --- Distances: Level 1, Gate 2 ---
            .addDistance(1, 2, 1, 18)
            .addDistance(1, 2, 2, 12)
            .addDistance(1, 2, 3,  7)
            .addDistance(1, 2, 4,  3)
            .addDistance(1, 2, 5,  6)

            // --- Distances: Level 2, Gate 1 ---
            .addDistance(2, 1, 6,   6)
            .addDistance(2, 1, 7,  11)
            .addDistance(2, 1, 8,   9)
            .addDistance(2, 1, 9,  16)
            .addDistance(2, 1, 10, 21)

            // --- Distances: Level 2, Gate 2 ---
            .addDistance(2, 2, 6,  19)
            .addDistance(2, 2, 7,  13)
            .addDistance(2, 2, 8,   8)
            .addDistance(2, 2, 9,   4)
            .addDistance(2, 2, 10,  7)

            .build();

        ParkingLotService lot = ParkingLotFactory.create(config);

        System.out.println("========== PARKING LOT SYSTEM ==========\n");

        // 1. Park a bike at North Gate -> nearest SMALL slot from gate 1 = slot 1 (dist=5)
        Vehicle bike = Vehicle.create("DL-01-AB-1234", VehicleType.BIKE, "Rahul");
        Ticket t1 = lot.park(bike, "2026-03-29 20:00", SlotType.SMALL, 1);

        // 2. Park a car at South Gate -> nearest MEDIUM from gate 2 = slot 4 (dist=3)
        Vehicle car = Vehicle.create("MH-02-CD-5678", VehicleType.CAR, "Priya");
        Ticket t2 = lot.park(car, "2026-03-29 20:30", SlotType.MEDIUM, 2);

        // 3. Park a bus -> must take LARGE
        Vehicle bus = Vehicle.create("KA-03-EF-9012", VehicleType.BUS, "Transport Co.");
        Ticket t3 = lot.park(bus, "2026-03-29 21:00", SlotType.LARGE, 1);

        // 4. Park another bike; small slot 1 is taken, next nearest from gate 1 = slot 2 (dist=10)
        Vehicle bike2 = Vehicle.create("UP-04-GH-3456", VehicleType.BIKE, "Ankit");
        Ticket t4 = lot.park(bike2, "2026-03-29 21:15", SlotType.SMALL, 1);

        System.out.println("\n\n========== EXITS ==========");

        // Exit: bike parked for 2h -> 2 * ₹20 = ₹40
        int amt1 = lot.exit(t1, "2026-03-29 22:00");
        System.out.println("Rahul pays: ₹" + amt1);

        // Exit: car parked for 3h 45m -> 4 billable hours -> 4 * ₹40 = ₹160
        int amt2 = lot.exit(t2, "2026-03-30 00:15");
        System.out.println("Priya pays: ₹" + amt2);

        // Exit: bus parked for 1h 1m -> 2 billable hours -> 2 * ₹80 = ₹160
        int amt3 = lot.exit(t3, "2026-03-29 22:01");
        System.out.println("Transport Co. pays: ₹" + amt3);
    }
}

package services;

import enums.SlotType;
import enums.VehicleType;
import exceptions.NoSlotAvailableException;
import models.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingLotService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final List<Level>           levels;
    private final Map<Integer, EntryGate> gates;       // gateId -> gate
    private final PricingService        pricingService;
    private final Map<String, Ticket>   activeTickets; // ticketId -> Ticket
    private final AtomicInteger         ticketCounter;

    private ParkingLotService(List<Level> levels, Map<Integer, EntryGate> gates,
                               PricingService pricingService) {
        this.levels         = levels;
        this.gates          = gates;
        this.pricingService = pricingService;
        this.activeTickets  = new HashMap<>();
        this.ticketCounter  = new AtomicInteger(1);
    }

    public static ParkingLotService create(List<Level> levels, Map<Integer, EntryGate> gates,
                                           PricingService pricingService) {
        return new ParkingLotService(levels, gates, pricingService);
    }

    /**
     * Parks a vehicle.
     *
     * @param vehicle     Vehicle to park
     * @param entryTime   Entry time string "yyyy-MM-dd HH:mm"
     * @param slotType    Preferred slot type (vehicle will be upgraded if needed)
     * @param gateId      Entry gate id
     * @return Ticket (invoice) with parking details
     */
    public Ticket park(Vehicle vehicle, String entryTime, SlotType slotType, int gateId) {
        EntryGate gate = gates.get(gateId);
        if (gate == null) {
            throw new IllegalArgumentException("Entry gate not found: " + gateId);
        }

        validateSlotTypeForVehicle(vehicle.getVehicleType(), slotType);

        LocalDateTime entryDateTime = LocalDateTime.parse(entryTime, FMT);

        // try each level in order; assign nearest slot on first level that has space
        for (Level level : levels) {
            try {
                Slot slot = level.parkVehicle(vehicle.getVehicleType(), slotType, gateId);
                String ticketId = "TKT-" + ticketCounter.getAndIncrement();

                Ticket ticket = Ticket.create(ticketId, vehicle, level.getLevelNumber(),
                                              slot, gate, entryDateTime, slot.getSlotType());
                activeTickets.put(ticketId, ticket);

                System.out.println("\n" + ticket);
                return ticket;

            } catch (NoSlotAvailableException ignored) {
                // try next level
            }
        }

        throw new NoSlotAvailableException(
            "No available " + slotType + " (or larger) slot in the entire parking lot.");
    }

    /**
     * Exits the parking lot and computes the charge.
     *
     * @param ticket    Ticket returned by park()
     * @param exitTime  Exit time string "yyyy-MM-dd HH:mm"
     * @return Amount to pay
     */
    public int exit(Ticket ticket, String exitTime) {
        if (!activeTickets.containsKey(ticket.getTicketId())) {
            throw new IllegalArgumentException("Ticket not found or already exited: " + ticket.getTicketId());
        }

        LocalDateTime exitDateTime = LocalDateTime.parse(exitTime, FMT);
        LocalDateTime entryDateTime = ticket.getEntryTime();

        if (exitDateTime.isBefore(entryDateTime)) {
            throw new IllegalArgumentException("Exit time cannot be before entry time.");
        }

        long totalMinutes    = ChronoUnit.MINUTES.between(entryDateTime, exitDateTime);
        long hours           = totalMinutes / 60;
        long remainingMinutes = totalMinutes % 60;

        int amount = pricingService.calculateCharge(ticket.getSlotType(), hours, remainingMinutes);

        // release the slot
        for (Level level : levels) {
            if (level.getLevelNumber() == ticket.getLevelNumber()) {
                level.releaseSlot(ticket.getSlot().getSlotId());
                break;
            }
        }

        activeTickets.remove(ticket.getTicketId());

        System.out.println("\n========== EXIT SUMMARY ==========");
        System.out.println("Ticket ID   : " + ticket.getTicketId());
        System.out.println("Vehicle     : " + ticket.getVehicle());
        System.out.println("Level       : " + ticket.getLevelNumber());
        System.out.println("Slot        : " + ticket.getSlot().getSlotId());
        System.out.println("Slot Type   : " + ticket.getSlotType());
        System.out.println("Entry Time  : " + entryDateTime.format(FMT));
        System.out.println("Exit Time   : " + exitDateTime.format(FMT));
        System.out.println("Duration    : " + hours + "h " + remainingMinutes + "m");
        System.out.println("Rate        : ₹" + pricingService.getHourlyRate(ticket.getSlotType()) + "/hr");
        System.out.println("Amount Due  : ₹" + amount);
        System.out.println("==================================");

        return amount;
    }

    // A small vehicle cannot be assigned a slot smaller than itself
    private void validateSlotTypeForVehicle(VehicleType vehicleType, SlotType slotType) {
        if (vehicleType == VehicleType.CAR && slotType == SlotType.SMALL) {
            throw new IllegalArgumentException("CAR cannot be assigned a SMALL slot.");
        }
        if (vehicleType == VehicleType.BUS &&
            (slotType == SlotType.SMALL || slotType == SlotType.MEDIUM)) {
            throw new IllegalArgumentException("BUS can only be assigned a LARGE slot.");
        }
    }
}

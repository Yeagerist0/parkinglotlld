package models;

import enums.SlotType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String      ticketId;
    private final Vehicle     vehicle;
    private final int         levelNumber;
    private final Slot        slot;
    private final EntryGate   entryGate;
    private final LocalDateTime entryTime;
    private final SlotType    slotType;

    private Ticket(String ticketId, Vehicle vehicle, int levelNumber, Slot slot,
                   EntryGate entryGate, LocalDateTime entryTime, SlotType slotType) {
        this.ticketId    = ticketId;
        this.vehicle     = vehicle;
        this.levelNumber = levelNumber;
        this.slot        = slot;
        this.entryGate   = entryGate;
        this.entryTime   = entryTime;
        this.slotType    = slotType;
    }

    public static Ticket create(String ticketId, Vehicle vehicle, int levelNumber, Slot slot,
                                EntryGate entryGate, LocalDateTime entryTime, SlotType slotType) {
        return new Ticket(ticketId, vehicle, levelNumber, slot, entryGate, entryTime, slotType);
    }

    public String        getTicketId()    { return ticketId; }
    public Vehicle       getVehicle()     { return vehicle; }
    public int           getLevelNumber() { return levelNumber; }
    public Slot          getSlot()        { return slot; }
    public EntryGate     getEntryGate()   { return entryGate; }
    public LocalDateTime getEntryTime()   { return entryTime; }
    public SlotType      getSlotType()    { return slotType; }

    @Override
    public String toString() {
        return "========== PARKING INVOICE ==========\n" +
               "Ticket ID   : " + ticketId + "\n" +
               "Vehicle     : " + vehicle  + "\n" +
               "Entry Gate  : " + entryGate + "\n" +
               "Level       : " + levelNumber + "\n" +
               "Slot        : " + slot + "\n" +
               "Slot Type   : " + slotType + "\n" +
               "Entry Time  : " + entryTime.format(FMT) + "\n" +
               "=====================================";
    }
}

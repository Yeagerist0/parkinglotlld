package models;

import enums.SlotType;
import enums.VehicleType;
import exceptions.NoSlotAvailableException;

import java.util.*;

public class Level {
    private final int levelNumber;
    // slotId -> Slot
    private final Map<Integer, Slot> slots;
    // gateId -> (slotId -> distance)
    private final Map<Integer, Map<Integer, Integer>> gateSlotDistances;

    private Level(int levelNumber, List<Slot> slots, Map<Integer, Map<Integer, Integer>> gateSlotDistances) {
        this.levelNumber       = levelNumber;
        this.slots             = new LinkedHashMap<>();
        this.gateSlotDistances = gateSlotDistances;
        for (Slot s : slots) {
            this.slots.put(s.getSlotId(), s);
        }
    }

    public static Level create(int levelNumber, List<Slot> slots,
                               Map<Integer, Map<Integer, Integer>> gateSlotDistances) {
        return new Level(levelNumber, slots, gateSlotDistances);
    }

    public int getLevelNumber() { return levelNumber; }

    /**
     * Returns compatible slot types for a vehicle type.
     * Small vehicle (bike)  -> SMALL, MEDIUM, LARGE
     * Medium vehicle (car)  -> MEDIUM, LARGE
     * Large vehicle (bus)   -> LARGE
     */
    private static List<SlotType> compatibleSlots(VehicleType vehicleType, SlotType preferredType) {
        // must match at least the preferred type; upgrade allowed if not available
        switch (vehicleType) {
            case BIKE: return Arrays.asList(SlotType.SMALL, SlotType.MEDIUM, SlotType.LARGE);
            case CAR:  return Arrays.asList(SlotType.MEDIUM, SlotType.LARGE);
            case BUS:  return Arrays.asList(SlotType.LARGE);
            default:   return Collections.emptyList();
        }
    }

    /**
     * Finds and occupies the nearest available slot for the given vehicle/gate.
     * Preferred type is tried first; if unavailable, larger types are considered.
     */
    public Slot parkVehicle(VehicleType vehicleType, SlotType preferredType, int gateId) {
        List<SlotType> compatible = compatibleSlots(vehicleType, preferredType);

        // filter to compatible types that are >= preferred size (no downgrade)
        List<SlotType> eligible = new ArrayList<>();
        boolean reached = false;
        for (SlotType st : compatible) {
            if (st == preferredType) reached = true;
            if (reached) eligible.add(st);
        }

        Map<Integer, Integer> distMap = gateSlotDistances.getOrDefault(gateId, Collections.emptyMap());

        Slot nearest = null;
        int minDist  = Integer.MAX_VALUE;

        for (Map.Entry<Integer, Slot> entry : slots.entrySet()) {
            Slot slot = entry.getValue();
            if (slot.isOccupied()) continue;
            if (!eligible.contains(slot.getSlotType())) continue;

            int dist = distMap.getOrDefault(slot.getSlotId(), Integer.MAX_VALUE);
            if (dist < minDist) {
                minDist = dist;
                nearest = slot;
            }
        }

        if (nearest == null) {
            throw new NoSlotAvailableException(
                "No " + preferredType + " (or larger) slot available on level " + levelNumber);
        }

        nearest.occupy();
        return nearest;
    }

    public boolean releaseSlot(int slotId) {
        Slot slot = slots.get(slotId);
        if (slot != null && slot.isOccupied()) {
            slot.release();
            return true;
        }
        return false;
    }

    public boolean hasSlot(int slotId) {
        return slots.containsKey(slotId);
    }

    @Override
    public String toString() {
        return "Level[" + levelNumber + "] slots=" + slots.values();
    }
}

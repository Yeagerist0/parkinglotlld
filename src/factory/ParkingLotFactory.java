package factory;

import models.*;
import services.ParkingLotService;
import services.PricingService;
import enums.SlotType;

import java.util.*;

/**
 * Factory that assembles the entire ParkingLot without the caller
 * ever invoking `new` on any domain class directly.
 *
 * Usage:
 *   ParkingLotFactory.Config config = ParkingLotFactory.Config.builder()
 *       .levels(2)
 *       .levelSlots(levelSlotMap)
 *       .gateSlotDistances(distancesMap)
 *       .gates(gateList)
 *       .build();
 *   ParkingLotService service = ParkingLotFactory.create(config);
 */
public class ParkingLotFactory {

    private ParkingLotFactory() {}

    /**
     * Configuration object built without exposing any model constructors.
     *
     * levelSlots: levelNumber -> list of (slotId, SlotType)
     * gateSlotDistances: levelNumber -> gateId -> slotId -> distance
     * gates: list of (gateId, gateName)
     */
    public static class Config {
        final int                                        numLevels;
        // levelNumber -> list of int[]{slotId, slotTypeOrdinal}
        final Map<Integer, List<int[]>>                  levelSlots;
        // levelNumber -> gateId -> slotId -> distance
        final Map<Integer, Map<Integer, Map<Integer, Integer>>> gateSlotDistances;
        // gateId -> gateName
        final Map<Integer, String>                       gates;

        private Config(Builder b) {
            this.numLevels         = b.numLevels;
            this.levelSlots        = b.levelSlots;
            this.gateSlotDistances = b.gateSlotDistances;
            this.gates             = b.gates;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            int numLevels;
            Map<Integer, List<int[]>>                  levelSlots        = new HashMap<>();
            Map<Integer, Map<Integer, Map<Integer, Integer>>> gateSlotDistances = new HashMap<>();
            Map<Integer, String>                       gates             = new LinkedHashMap<>();

            public Builder numLevels(int n)                         { numLevels = n; return this; }

            /** Add a slot to a level. slotType ordinal: 0=SMALL,1=MEDIUM,2=LARGE */
            public Builder addSlot(int level, int slotId, SlotType slotType) {
                levelSlots.computeIfAbsent(level, k -> new ArrayList<>())
                           .add(new int[]{slotId, slotType.ordinal()});
                return this;
            }

            /** Set distance from gateId to slotId on a given level */
            public Builder addDistance(int level, int gateId, int slotId, int distance) {
                gateSlotDistances
                    .computeIfAbsent(level, k -> new HashMap<>())
                    .computeIfAbsent(gateId, k -> new HashMap<>())
                    .put(slotId, distance);
                return this;
            }

            public Builder addGate(int gateId, String gateName) {
                gates.put(gateId, gateName);
                return this;
            }

            public Config build() { return new Config(this); }
        }
    }

    /** Assembles ParkingLotService from Config — zero `new` calls in caller code. */
    public static ParkingLotService create(Config config) {
        // build gates
        Map<Integer, EntryGate> gateMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> e : config.gates.entrySet()) {
            gateMap.put(e.getKey(), EntryGate.create(e.getKey(), e.getValue()));
        }

        // build levels
        List<Level> levels = new ArrayList<>();
        for (int lvl = 1; lvl <= config.numLevels; lvl++) {
            List<Slot> slots = new ArrayList<>();
            for (int[] slotDef : config.levelSlots.getOrDefault(lvl, Collections.emptyList())) {
                slots.add(Slot.create(slotDef[0], SlotType.values()[slotDef[1]]));
            }

            // gate -> (slotId -> distance) for this level
            Map<Integer, Map<Integer, Integer>> levelDists =
                config.gateSlotDistances.getOrDefault(lvl, Collections.emptyMap());

            levels.add(Level.create(lvl, slots, levelDists));
        }

        PricingService pricing = PricingService.create();
        return ParkingLotService.create(levels, gateMap, pricing);
    }
}

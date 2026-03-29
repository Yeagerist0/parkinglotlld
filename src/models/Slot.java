package models;

import enums.SlotType;

public class Slot {
    private final int slotId;
    private final SlotType slotType;
    private boolean isOccupied;

    private Slot(int slotId, SlotType slotType) {
        this.slotId    = slotId;
        this.slotType  = slotType;
        this.isOccupied = false;
    }

    public static Slot create(int slotId, SlotType slotType) {
        return new Slot(slotId, slotType);
    }

    public int     getSlotId()   { return slotId; }
    public SlotType getSlotType() { return slotType; }
    public boolean isOccupied()  { return isOccupied; }

    public void occupy()  { this.isOccupied = true; }
    public void release() { this.isOccupied = false; }

    @Override
    public String toString() {
        return "Slot[" + slotId + "/" + slotType + "/" + (isOccupied ? "OCCUPIED" : "FREE") + "]";
    }
}

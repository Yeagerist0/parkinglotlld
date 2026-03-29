package models;

public class EntryGate {
    private final int gateId;
    private final String gateName;

    private EntryGate(int gateId, String gateName) {
        this.gateId   = gateId;
        this.gateName = gateName;
    }

    public static EntryGate create(int gateId, String gateName) {
        return new EntryGate(gateId, gateName);
    }

    public int    getGateId()   { return gateId; }
    public String getGateName() { return gateName; }

    @Override
    public String toString() { return "Gate[" + gateId + ":" + gateName + "]"; }
}

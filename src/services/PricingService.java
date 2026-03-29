package services;

import enums.SlotType;

public class PricingService {

    // hourly rates in rupees
    private static final int SMALL_RATE  = 20;
    private static final int MEDIUM_RATE = 40;
    private static final int LARGE_RATE  = 80;

    private PricingService() {}

    public static PricingService create() {
        return new PricingService();
    }

    public int getHourlyRate(SlotType slotType) {
        switch (slotType) {
            case SMALL:  return SMALL_RATE;
            case MEDIUM: return MEDIUM_RATE;
            case LARGE:  return LARGE_RATE;
            default:     throw new IllegalArgumentException("Unknown slot type: " + slotType);
        }
    }

    /**
     * Calculates total charge. Partial hour is rounded up to next hour.
     */
    public int calculateCharge(SlotType slotType, long hours, long remainingMinutes) {
        long billableHours = hours + (remainingMinutes > 0 ? 1 : 0);
        if (billableHours == 0) billableHours = 1; // minimum 1 hour
        return (int) (getHourlyRate(slotType) * billableHours);
    }
}

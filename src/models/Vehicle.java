package models;

import enums.VehicleType;

public class Vehicle {
    private final String registrationNumber;
    private final VehicleType vehicleType;
    private final String ownerName;

    // private constructor — use factory
    private Vehicle(String registrationNumber, VehicleType vehicleType, String ownerName) {
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
        this.ownerName = ownerName;
    }

    public static Vehicle create(String registrationNumber, VehicleType vehicleType, String ownerName) {
        return new Vehicle(registrationNumber, vehicleType, ownerName);
    }

    public String getRegistrationNumber() { return registrationNumber; }
    public VehicleType getVehicleType()   { return vehicleType; }
    public String getOwnerName()          { return ownerName; }

    @Override
    public String toString() {
        return ownerName + " [" + registrationNumber + " / " + vehicleType + "]";
    }
}

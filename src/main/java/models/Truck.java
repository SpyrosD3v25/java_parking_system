package models;

public class Truck extends Vehicle {
    private final double lengthInMeters;
    private final TruckUsageType usageType;

    public Truck(String plate, FuelType fuelType, Driver driver,
                 double lengthInMeters, TruckUsageType usageType) {
        super(plate, 2, fuelType, driver);
        if (lengthInMeters <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        if (usageType == null) {
            throw new IllegalArgumentException("UsageType cannot be null");
        }
        this.lengthInMeters = lengthInMeters;
        this.usageType = usageType;
    }

    public double getLengthInMeters() {
        return lengthInMeters;
    }

    public TruckUsageType getUsageType() {
        return usageType;
    }

    @Override
    public String toString() {
        return super.toString() +
               "[length=" + lengthInMeters +
               "m, usage=" + usageType + "]";
    }
}

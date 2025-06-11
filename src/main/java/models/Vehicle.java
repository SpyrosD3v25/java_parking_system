package models;

public abstract class Vehicle {
    private final String plate;
    private final int size; 
    private final FuelType fuelType;
    private final Driver driver;

    protected Vehicle(String plate, int size, FuelType fuelType, Driver driver) {
        if (plate == null || plate.isEmpty()) {
            throw new IllegalArgumentException("Plate cannot be null or empty");
        }
        if (size < 1 || size > 2) {
            throw new IllegalArgumentException("Size must be 1 or 2");
        }
        if (fuelType == null || driver == null) {
            throw new IllegalArgumentException("FuelType and Driver cannot be null");
        }
        this.plate = plate;
        this.size = size;
        this.fuelType = fuelType;
        this.driver = driver;
    }

    public String getPlate() {
        return plate;
    }

    public int getSize() {
        return size;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public Driver getDriver() {
        return driver;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() +
               " plate=" + plate +
               ", size=" + size +
               ", fuelType=" + fuelType +
               ", driver=" + driver +
               "]";
    }
}

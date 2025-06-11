package models;

public class Motorcycle extends Vehicle {
    public Motorcycle(String plate, FuelType fuelType, Driver driver) {
        super(plate, 1, fuelType, driver);
    }
}

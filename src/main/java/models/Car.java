package models;

public class Car extends Vehicle {
    public Car(String plate, FuelType fuelType, Driver driver) {
        super(plate, 1, fuelType, driver);
    }
}

package tests;

import models.*;
import services.*;
import storage.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

/*
 * This is kind of just conseptual, as there are a lot more tests that should be added.
 * If i was really writting this i would also use Seeders and Factories to create the data
 * and not just HARD-CODE it.
 */
public class RunTests {

    private static boolean testParkingLimits() {
        ParkingLot lot = new ParkingLot(80, 0); // 80 normal, 0 electric
        Driver dummyDriver = new Driver("X", "Y", "0000000000");
        // Park 80 cars
        for (int i = 1; i <= 80; i++) {
            Vehicle car = new Car("CAR" + i, FuelType.DIESEL, dummyDriver);
            List<ParkingSpot> spots = lot.allocateSpots(car.getSize(), false);
            if (spots.isEmpty()) {
                // Should not happen for first 80
                return false;
            }
            ParkingSession session = new ParkingSession(car, dummyDriver, spots, java.time.LocalDateTime.now(), 1);
            lot.markOccupied(spots, session);
        }
        Vehicle car81 = new Car("CAR81", FuelType.DIESEL, dummyDriver);
        List<ParkingSpot> spots81 = lot.allocateSpots(car81.getSize(), false);
        return spots81.isEmpty(); // should be true: no spots left
    }

    private static boolean testTruckConsecutiveSpots() {
        ParkingLot lot = new ParkingLot(5, 0); // only 5 normal spots
        Driver d = new Driver("T", "R", "7000000001");

        // Occupy spots 2 and 4 (breaks consecutiveness)
        Vehicle car1 = new Car("C1", FuelType.DIESEL, d);
        Vehicle car2 = new Car("C2", FuelType.DIESEL, d);

        List<ParkingSpot> s1 = lot.allocateSpots(1, false); // should be spot 1
        List<ParkingSpot> s2 = lot.allocateSpots(1, false); // should be spot 2
        List<ParkingSpot> s3 = lot.allocateSpots(1, false); // should be spot 3
        List<ParkingSpot> s4 = lot.allocateSpots(1, false); // should be spot 4

        // free spot 3 to leave [1=used, 2=used, 3=free, 4=used]
        lot.markOccupied(s1, new ParkingSession(car1, d, s1, java.time.LocalDateTime.now(), 1));
        lot.markOccupied(s2, new ParkingSession(car1, d, s2, java.time.LocalDateTime.now(), 1));
        lot.markOccupied(s4, new ParkingSession(car2, d, s4, java.time.LocalDateTime.now(), 1));

        Vehicle truck = new Truck("T1", FuelType.DIESEL, d, 6.0, TruckUsageType.FOOD_TRANSPORT);
        List<ParkingSpot> truckSpots = lot.allocateSpots(2, false);
        return truckSpots.isEmpty();
    }

    public static void run() {
        Object[][] tests = {
            {"Can Park 80 Cars, Fail 81st", testParkingLimits()},
            {"Truck Blocked by Non-Consecutive Spots", testTruckConsecutiveSpots()}
        };

        for (Object[] test : tests) {
            String name = (String) test[0];
            boolean passed = (Boolean) test[1];
            System.out.println((passed ? "[+] " : "[-] ") + name);
        }
    }

    public static void main(String[] args) {
        run();
    }
}

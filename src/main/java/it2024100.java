import models.*;
import services.*;
import storage.Store;
import java.util.List;
import java.util.Scanner;

public class it2024100 {
    public static void main(String[] args) {
        Store.setToInMemory();
        var repo = Store.get();

        ParkingLot parkingLot = new ParkingLot(80, 20);
        Scanner scanner = new Scanner(System.in);

        DriverService  driverService  = new DriverService(repo, scanner);
        VehicleService vehicleService = new VehicleService(repo, scanner, driverService);

        ParkingService service = new ParkingService(parkingLot, repo, driverService, vehicleService);

        // seedSampleData(parkingLot, repo);

        boolean running = true;
        while (running) {
            System.out.println("\n===== Java_Parking Menu =====");
            System.out.println("1) Park vehicle");
            System.out.println("2) Exit vehicle");
            System.out.println("3) Search by driver phone");
            System.out.println("4) Search by vehicle plate");
            System.out.println("5) Show spots report");
            System.out.println("6) Save parking state to file");
            System.out.println("7) Quit");
            System.out.print("Choose an option [1-7]: ");

            String choice = scanner.nextLine().trim();


            switch (choice) {
                case "1" -> service.parkVehicleInteractive();
                case "2" -> service.exitVehicleInteractive();
                case "3" -> service.searchByDriver();
                case "4" -> service.searchByVehicle();
                case "5" -> service.printSpotReport();
                case "6" -> service.saveStateToFile();
                case "7" -> {
                    System.out.println("Ciao!");
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }

    /**
     * Seeds some sample drivers, vehicles, and parking sessions so that features 2/3/4 can be tested
     */
    private static void seedSampleData(ParkingLot lot, storage.InstanceRepository repo) {
        Driver d1 = new Driver("Alice", "Papadopoulos", "6970010001");
        Driver d2 = new Driver("Basil", "Georgiou",    "6970020002");
        Driver d3 = new Driver("Clara", "Nikolaou",     "6970030003");

        repo.save("driver_" + d1.getPhone(), d1, Driver.class);
        repo.save("driver_" + d2.getPhone(), d2, Driver.class);
        repo.save("driver_" + d3.getPhone(), d3, Driver.class);

        Vehicle v1 = new Car("test1", FuelType.DIESEL, d1);
        Vehicle v2 = new Motorcycle("test2", FuelType.ELECTRIC, d2);
        Vehicle v3 = new Truck("test3", FuelType.DIESEL, d3, 7.5, TruckUsageType.GOODS_TRANSPORT);

        repo.save("vehicle_" + v1.getPlate(), v1, Vehicle.class);
        repo.save("vehicle_" + v2.getPlate(), v2, Vehicle.class);
        repo.save("vehicle_" + v3.getPlate(), v3, Vehicle.class);

        // Testaroyme to search kai to exit
        List<ParkingSpot> spots1 = lot.allocateSpots(v1.getSize(), v1.getFuelType() == FuelType.ELECTRIC);
        if (!spots1.isEmpty()) {
            ParkingSession ps1 = new ParkingSession(v1, d1, spots1, java.time.LocalDateTime.now(), 5);
            lot.markOccupied(spots1, ps1);
            repo.save("session_vehicle_" + v1.getPlate(), ps1, ParkingSession.class);
            repo.save("session_driver_"  + d1.getPhone(),   ps1, ParkingSession.class);
        }

        // Px parkare gia 3 wres
        List<ParkingSpot> spots2 = lot.allocateSpots(v2.getSize(), v2.getFuelType() == FuelType.ELECTRIC);
        if (!spots2.isEmpty()) {
            ParkingSession ps2 = new ParkingSession(v2, d2, spots2, java.time.LocalDateTime.now(), 3);
            lot.markOccupied(spots2, ps2);
            repo.save("session_vehicle_" + v2.getPlate(), ps2, ParkingSession.class);
            repo.save("session_driver_"  + d2.getPhone(),   ps2, ParkingSession.class);
        }

        System.out.println("Sample data seeded: 2 vehicles already parked.");
    }
}

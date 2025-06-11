package services;

import models.*;
import storage.InstanceRepository;

import java.util.List;
import java.util.Scanner;

/**
 * Encapsulates vehicle lookup and creation.  
 *   - If a vehicle with a given plate exists, return it.  
 *   - Otherwise, prompt for all needed fields (type, fuel, length/usage if Truck), create + save, return.
 */
public class VehicleService {
    private final InstanceRepository repo;
    private final Scanner scanner;
    private final DriverService driverService;

    public VehicleService(InstanceRepository repo,
                          Scanner scanner,
                          DriverService driverService) {
        this.repo = repo;
        this.scanner = scanner;
        this.driverService = driverService;
    }

    /**
     * Look up vehicle by plate. If exists, return it.
     * Otherwise:
     * 1. Ask for driver‐phone; let DriverService find/create the Driver.
     * 2. Ask for vehicle‐type (1=Car, 2=Motorcycle, 3=Truck).
     * 3. Ask for fuel‐type (1=UNLEADED_PETROL, 2=DIESEL, 3=ELECTRIC).
     * 4. If Truck: ask for lengthInMeters and usageType(1–3).
     * 5. Create the appropriate subclass, save in repo under "vehicle_<plate>", return.
     */
    public Vehicle findOrCreateVehicle(String plate) {
        List<Vehicle> existingVehicles = repo.getAll("vehicle_" + plate);
        if (!existingVehicles.isEmpty()) {
            Vehicle v = existingVehicles.get(0);
            System.out.println("Found existing vehicle: " + v);
            return v;
        }

        System.out.print("Enter driver phone for this vehicle: ");
        String phone = scanner.nextLine().trim();
        Driver driver = driverService.findOrCreateDriver(phone);

        int vtype;
        while (true) {
            System.out.println("Select vehicle type:");
            System.out.println("  1) Car");
            System.out.println("  2) Motorcycle");
            System.out.println("  3) Truck");
            System.out.print("Your choice [1-3]: ");
            String line = scanner.nextLine().trim();
            try {
                vtype = Integer.parseInt(line);
                if (vtype >= 1 && vtype <= 3) break;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid choice. Enter 1, 2, or 3.");
        }

        int ftype;
        while (true) {
            System.out.println("Select fuel type:");
            System.out.println("  1) DIESEL");
            System.out.println("  2) ELECTRIC");
            System.out.print("Your choice [1-2]: ");
            String line = scanner.nextLine().trim();
            try {
                ftype = Integer.parseInt(line);
                if (ftype >= 1 && ftype <= 3) break;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid choice. Enter 1, or 2");
        }
        FuelType fuel = switch (ftype) {
            case 1 -> FuelType.DIESEL;
            default -> FuelType.ELECTRIC;
        };

        Vehicle vehicle;
        if (vtype == 1) {
            vehicle = new Car(plate, fuel, driver);
        }
        else if (vtype == 2) {
            vehicle = new Motorcycle(plate, fuel, driver);
        }
        else {
            double length;
            while (true) {
                System.out.print("Enter truck length in meters (positive decimal): ");
                String line = scanner.nextLine().trim();
                try {
                    length = Double.parseDouble(line);
                    if (length > 0) break;
                } catch (NumberFormatException ignored) {}
                System.out.println("Invalid length. Must be a positive number.");
            }

            int usageChoice;
            while (true) {
                System.out.println("Select truck usage type:");
                System.out.println("  1) FOOD_TRANSPORT");
                System.out.println("  2) GOODS_TRANSPORT");
                System.out.println("  3) ORDER_DISTRIBUTION");
                System.out.print("Your choice [1-3]: ");
                String line = scanner.nextLine().trim();
                try {
                    usageChoice = Integer.parseInt(line);
                    if (usageChoice >= 1 && usageChoice <= 3) break;
                } catch (NumberFormatException ignored) {}
                System.out.println("Invalid choice. Enter 1, 2, or 3.");
            }
            TruckUsageType usageType = switch (usageChoice) {
                case 1 -> TruckUsageType.FOOD_TRANSPORT;
                case 2 -> TruckUsageType.GOODS_TRANSPORT;
                default -> TruckUsageType.ORDER_DISTRIBUTION;
            };

            vehicle = new Truck(plate, fuel, driver, length, usageType);
        }

        repo.save("vehicle_" + plate, vehicle, Vehicle.class);
        System.out.println("Created & saved new vehicle: " + vehicle);
        return vehicle;
    }
}

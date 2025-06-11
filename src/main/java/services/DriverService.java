package services;

import models.Driver;
import storage.InstanceRepository;

import java.util.List;
import java.util.Scanner;

/**
 * Encapsulates all driver‐lookup and driver‐creation logic.
 *   - If a driver with a given phone exists in repo, return it.
 *   - Otherwise, prompt via Scanner for firstName/lastName/phone, create + save, and return.
 */
public class DriverService {
    private final InstanceRepository repo;
    private final Scanner scanner;

    public DriverService(InstanceRepository repo, Scanner scanner) {
        this.repo = repo;
        this.scanner = scanner;
    }

    /**
     * Look up by phone. If found, return existing Driver.
     * Otherwise, prompt user for firstName/lastName (phone is given),
     * create new Driver, save in repo under key "driver_<phone>", and return it.
     */
    public Driver findOrCreateDriver(String phone) {
        // 1) Try to find existing
        List<Driver> existing = repo.getAll("driver_" + phone);
        if (!existing.isEmpty()) {
            Driver d = existing.get(0);
            System.out.println("Found existing driver: " + d);
            return d;
        }

        // 2) Not found → prompt for name/surname
        System.out.println("Driver not found for phone = " + phone);
        String firstName, lastName;
        while (true) {
            System.out.print("Enter first name: ");
            firstName = scanner.nextLine().trim();
            if (!firstName.isEmpty()) break;
            System.out.println("First name cannot be empty.");
        }
        while (true) {
            System.out.print("Enter last name: ");
            lastName = scanner.nextLine().trim();
            if (!lastName.isEmpty()) break;
            System.out.println("Last name cannot be empty.");
        }

        Driver driver = new Driver(firstName, lastName, phone);
        repo.save("driver_" + phone, driver, Driver.class);
        System.out.println("Created and saved new driver: " + driver);
        return driver;
    }
}

package services;

import models.*;
import storage.InstanceRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ParkingService handles:
 *   • parkVehicleInteractive
 *   • exitVehicleInteractive
 *   • searchByDriver
 *   • searchByVehicle
 *   • printSpotReport
 *   • saveStateToFile
 */
public class ParkingService {
    private final ParkingLot parkingLot;
    private final InstanceRepository repo;
    private final DriverService driverService;
    private final VehicleService vehicleService;
    private final DateTimeFormatter fileDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public ParkingService(ParkingLot parkingLot,
                          InstanceRepository repo   ,
                          DriverService driverService,
                          VehicleService vehicleService) {


        this.parkingLot     = parkingLot;
        this.repo           = repo;
        this.driverService  = driverService;
        this.vehicleService = vehicleService;


    }

    public void parkVehicleInteractive() {
        System.out.println("---- Park Vehicle ----");

        String plate = promptForPlate();
        if (!validatePlate(plate)) { return; }

        Vehicle vehicle = vehicleService.findOrCreateVehicle(plate);
        
        boolean success = allocateAndStartSession(vehicle);
        if (success) {
            System.out.println("Parking completed successfully.");
        } else {
            System.out.println("Could not find a suitable spot for that vehicle.");
        }
    }

    public void exitVehicleInteractive() {
        System.out.println("---- Exit Vehicle ----");

        System.out.print("Enter vehicle plate: ");
        String plate = new Scanner(System.in).nextLine().trim();
        List<ParkingSession> sessions = repo.getAll("session_vehicle_" + plate);

        if (sessions.isEmpty()) {
            System.out.println("No parking session found for plate " + plate);
            return;
        }

        Optional<ParkingSession> opt = sessions.stream()
                .filter(s -> !s.isClosed())
                .findFirst();

        if (opt.isEmpty()) {
            System.out.println("No active parking session for that plate (all are already closed).");
            return;
        }

        ParkingSession sess = opt.get();
        int hours = promptForDuration();
        if (hours == -1) return;

        try {
            sess.closeSession(hours);
            System.out.println("Computed fee: " + sess.getFeeEuros() + "€");
            parkingLot.freeSpots(sess.getSpots());
            System.out.println("Spots freed. Session closed.");
        } catch (Exception e) {
            System.out.println("Failed to close session: " + e.getMessage());
        }
    }

    public void searchByDriver() {
        System.out.println("---- Search by Driver ----");

        System.out.print("Enter driver phone: ");
        String phone = new Scanner(System.in).nextLine().trim();
        List<ParkingSession> sessions = repo.getAll("session_driver_" + phone);

        if (sessions.isEmpty()) {
            System.out.println("No parking history for driver " + phone);
            return;
        }

        System.out.println("History for driver " + phone + ":");
        for (ParkingSession s : sessions) {
            String plate = s.getVehicle().getPlate();
            String start = s.formatStartDateTime();
            if (s.isClosed()) {
                int dur      = s.getDurationHours();
                String fee   = (s.getFeeEuros() == null ? "[not exited]" : s.getFeeEuros() + "€");
                System.out.printf("  Plate=%s, Start=%s, Duration=%dh, Fee=%s%n", plate, start, dur, fee);
            } else {
                System.out.printf("  Plate=%s, Start=%s, [still parked]%n", plate, start);
            }
        }
    }

    public void searchByVehicle() {
        System.out.println("---- Search by Vehicle ----");

        System.out.print("Enter vehicle plate: ");
        String plate = new Scanner(System.in).nextLine().trim();
        List<ParkingSession> sessions = repo.getAll("session_vehicle_" + plate);

        if (sessions.isEmpty()) {
            System.out.println("No parking history for plate " + plate);
            return;
        }

        System.out.println("History for plate " + plate + ":");
        for (ParkingSession s : sessions) {
            String start = s.formatStartDateTime();
            if (s.isClosed()) {
                int dur      = s.getDurationHours();
                String fee   = (s.getFeeEuros() == null ? "[not exited]" : s.getFeeEuros() + "€");
                System.out.printf("  Plate=%s, Start=%s, Duration=%dh, Fee=%s%n", plate, start, dur, fee);
            } else {
                System.out.printf("  Plate=%s, Start=%s, [still parked]%n", plate, start);
            }
        }
    }

    public void printSpotReport() {
        System.out.println(parkingLot.reportSpotStatus());
    }

    public void saveStateToFile() {
        List<ParkingSpot> allSpots = parkingLot.getAllSpotsInOrder();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("parking_state.txt"))) {
            for (ParkingSpot spot : allSpots) {
                StringBuilder line = new StringBuilder();
                line.append(spot.getNumber());
                if (spot.isOccupied()) {
                    ParkingSession s = spot.getCurrentSession().orElseThrow();
                    line.append(" ")
                        .append(s.getVehicle().getPlate())
                        .append(" ")
                        .append(s.getDriver().getPhone())
                        .append(" ")
                        .append(s.getStartDateTime().format(fileDateFormatter));
                }
                writer.write(line.toString());
                writer.newLine();
            }
            System.out.println("Parking state saved to parking_state.txt");
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }


    /*
     * Some helper methods
     */
    private String promptForPlate() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter vehicle plate: ");
        return sc.nextLine().trim();
    }

    private int promptForDuration() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter desired parking duration in hours (1-24): ");
        String line = sc.nextLine().trim();
        try {
            int hours = Integer.parseInt(line);
            if (hours >= 1 && hours <= 24) {
                return hours;
            }
        } catch (NumberFormatException ignored) {}
        System.out.println("Invalid duration.");
        return -1;
    }

    private boolean isVehicleAlreadyParked(String plate) {
        List<ParkingSession> sessions = repo.getAll("session_vehicle_" + plate);
        return sessions.stream()
            .anyMatch(s -> s.isClosed() == false);
    }
 
    /*
     * I put validation for anything with more than one check
     */
    private boolean validatePlate(String plate) {
        if (plate == null || plate.isEmpty()) {
            System.out.println("Plate cannot be empty.");
            return false;
        }
        
        if (isVehicleAlreadyParked(plate)) {
            System.out.println("Vehicle with plate " + plate + " is already parked.");
            return false;
        }
        return true;
    }

    /**
     * Helper: Given a Vehicle (already in repo), attempt to allocate spot‐list.
     * If allocation succeeds, create and “start” a ParkingSession:
     * Returns true if parked successfully; false if no spot could be found.
     */
    private boolean allocateAndStartSession(Vehicle vehicle) {
        boolean isElectric = (vehicle.getFuelType() == FuelType.ELECTRIC);
        List<ParkingSpot> allocated = parkingLot.allocateSpots(vehicle.getSize(), isElectric);
        if (allocated.isEmpty()) {
            return false;
        }

        ParkingSession session = new ParkingSession(
                vehicle,
                vehicle.getDriver(),
                allocated,
                LocalDateTime.now()
        );
        parkingLot.markOccupied(allocated, session);

        repo.save("session_vehicle_" + vehicle.getPlate(), session, ParkingSession.class);
        repo.save("session_driver_"  + vehicle.getDriver().getPhone(), session, ParkingSession.class);
        System.out.printf("Vehicle parked in spot(s): %s%n",
                allocated.stream()
                         .map(s -> Integer.toString(s.getNumber()))
                         .toList());
        return true;
    }
}

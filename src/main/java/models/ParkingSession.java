package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
/*
 * This class represents a parking session: a single event where a vehicle is parked
 * in specific spots by a driver, starting at a certain time and for a certain duration.
 * It stores information about the vehicle, the driver, the parking spots, the start time,
 * whether the session is closed, and the fee to be paid.
 */
public class ParkingSession {
    private final Vehicle vehicle;
    private final Driver driver;
    private final List<ParkingSpot> spots;
    private final LocalDateTime startDateTime;

    private Integer durationHours; // Set only on exit
    private Integer feeEuros;
    private boolean closed = false;

    public ParkingSession(Vehicle vehicle, Driver driver, List<ParkingSpot> spots, LocalDateTime startDateTime) {
        if (vehicle == null || driver == null || spots == null || startDateTime == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.vehicle = vehicle;
        this.driver = driver;
        this.spots = spots;
        this.startDateTime = startDateTime;
    }

    public Vehicle getVehicle() { return vehicle; }
    public Driver getDriver() { return driver; }
    public List<ParkingSpot> getSpots() { return spots; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public Integer getDurationHours() { return durationHours; }
    public Integer getFeeEuros() { return feeEuros; }
    public boolean isClosed() { return closed; }

    public void closeSession(int actualHours) {
        if (closed) throw new IllegalStateException("Session already closed");
        if (actualHours < 1 || actualHours > 24)
            throw new IllegalArgumentException("Invalid duration: must be 1-24");

        this.durationHours = actualHours;
        this.closed = true;

        if (actualHours <= 3) feeEuros = 5;
        else if (actualHours <= 8) feeEuros = 8;
        else if (actualHours <= 23) feeEuros = 12;
        else feeEuros = 15;
    }

    public String formatStartDateTime() {
        return startDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Session[vehicle=");
        sb.append(vehicle.getPlate())
          .append(", driver=").append(driver.getPhone())
          .append(", spots=");
        spots.forEach(s -> sb.append(s.getNumber()).append(" "));
        sb.append(", start=").append(formatStartDateTime());
        if (closed) {
            sb.append(", duration=").append(durationHours).append("h");
            sb.append(", fee=").append(feeEuros).append("€");
        } else {
            sb.append(", [still parked]");
        }
        sb.append("]");
        return sb.toString();
    }
}

package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/*
 * Modelopoiei to lifecycle enos vehicle apo otan eisagete se ena parking spot mexri na kanei exit
 */
public class ParkingSession {
    private final Vehicle vehicle;
    private final Driver driver;
    private final List<ParkingSpot> spots;   // 1 h 2 theseis
    private final LocalDateTime startDateTime;
    private final int durationHours;         // 1–24
    private Integer feeEuros;
    private LocalDateTime exitDateTime;      

    public ParkingSession(Vehicle vehicle, Driver driver,
                          List<ParkingSpot> spots,
                          LocalDateTime startDateTime,
                          int durationHours) {
        if (vehicle == null || driver == null || spots == null || startDateTime == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        if (durationHours < 1 || durationHours > 24) {
            throw new IllegalArgumentException("Duration must be between 1 and 24 hours");
        }
        this.vehicle = vehicle;
        this.driver = driver;
        this.spots = spots;
        this.startDateTime = startDateTime;
        this.durationHours = durationHours;
        this.feeEuros = null;
        this.exitDateTime = null;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Driver getDriver() {
        return driver;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public LocalDateTime getExitDateTime() {
        return exitDateTime;
    }

    public Integer getFeeEuros() {
        return feeEuros;
    }

    /*
     * Called at the end
     */
    public void closeSession() {
        if (exitDateTime != null) {
            throw new IllegalStateException("Session already closed");
        }
        this.exitDateTime = LocalDateTime.now();
        if (durationHours <= 3) {
            feeEuros = 5;
        } else if (durationHours <= 8) {
            feeEuros = 8;
        } else if (durationHours <= 23) {
            feeEuros = 12;
        } else {
            feeEuros = 15;
        }
    }

    public String formatStartDateTime() {
        return startDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    /*
     * php-stiko approach 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Session[vehicle=")
          .append(vehicle.getPlate())
          .append(", driver=")
          .append(driver.getPhone())
          .append(", spots=");
        spots.forEach(s -> sb.append(s.getNumber()).append(" "));
        sb.append(", start=")
          .append(formatStartDateTime())
          .append(", duration=")
          .append(durationHours)
          .append("h");
        if (feeEuros != null) {
            sb.append(", fee=").append(feeEuros).append("€");
        }
        sb.append("]");
        return sb.toString();
    }
}

package models;

import java.util.Optional;
/*
 * This class represents a single parking spot in the parking lot.
 * Each spot has a unique number, a type (NORMAL or ELECTRIC), and can be occupied or free.
 * It keeps track of whether it is occupied and, if so, the current parking session using it.
 */
public class ParkingSpot {
    public enum SpotType { NORMAL, ELECTRIC }

    private final int number;
    private final SpotType type;
    private boolean occupied;
    private ParkingSession currentSession; 

    public ParkingSpot(int number, SpotType type) {
        this.number = number;
        this.type = type;
        this.occupied = false;
        this.currentSession = null;
    }

    public int getNumber() {
        return number;
    }

    public SpotType getType() {
        return type;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void occupy(ParkingSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        this.occupied = true;
        this.currentSession = session;
    }

    public void free() {
        this.occupied = false;
        this.currentSession = null;
    }

    public Optional<ParkingSession> getCurrentSession() {
        return Optional.ofNullable(currentSession);
    }

    @Override
    public String toString() {
        return "Spot#" + number + "(" + type + ", occupied=" + occupied + ")";
    }
}

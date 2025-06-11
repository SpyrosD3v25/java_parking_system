package services;

import models.ParkingSpot;
import models.ParkingSession;
import java.util.*;

public class ParkingLot {
    private final Map<Integer, ParkingSpot> allSpots = new HashMap<>();
    // I used Treeset structure to have O(logn) lookups. I know it uses Red-Black trees.
    private final TreeSet<Integer> freeNormalSpots = new TreeSet<>();
    private final TreeSet<Integer> freeElectricSpots = new TreeSet<>();

    public ParkingLot(int numNormal, int numElectric) {
        if (numNormal < 0 || numElectric < 0) {
            throw new IllegalArgumentException("Number of spots cannot be negative");
        }
        int spotNumber = 1;

        for (int i = 0; i < numNormal; i++, spotNumber++) {
            ParkingSpot spot = new ParkingSpot(spotNumber, ParkingSpot.SpotType.NORMAL);
            allSpots.put(spotNumber, spot);
            freeNormalSpots.add(spotNumber);
        }

        for (int i = 0; i < numElectric; i++, spotNumber++) {
            ParkingSpot spot = new ParkingSpot(spotNumber, ParkingSpot.SpotType.ELECTRIC);
            allSpots.put(spotNumber, spot);
            freeElectricSpots.add(spotNumber);
        }
    }

    public int totalNormalSpots() {
        return (int) allSpots.values().stream()
            .filter(s -> s.getType() == ParkingSpot.SpotType.NORMAL)
            .count();
    }

    public int totalElectricSpots() {
        return (int) allSpots.values().stream()
            .filter(s -> s.getType() == ParkingSpot.SpotType.ELECTRIC)
            .count();
    }

    public int freeNormalCount() {
        return freeNormalSpots.size();
    }

    public int freeElectricCount() {
        return freeElectricSpots.size();
    }

    public int occupiedNormalCount() {
        return totalNormalSpots() - freeNormalSpots.size();
    }

    public int occupiedElectricCount() {
        return totalElectricSpots() - freeElectricSpots.size();
    }

    /**
     * Attempts to allocate `sizeNeeded` consecutive spots for a given vehicle, given its fuel type.
     * Returns a list of `ParkingSpot` if successful; otherwise returns an empty list.
     */
    public List<ParkingSpot> allocateSpots(int sizeNeeded, boolean isElectric) {
        List<ParkingSpot> allocated = new ArrayList<>();
        if (sizeNeeded == 1) {
            if (isElectric && !freeElectricSpots.isEmpty()) {
                // allocate 1 electric spot
                int spotNum = freeElectricSpots.first();

                ParkingSpot spot = allSpots.get(spotNum);
                freeElectricSpots.remove(spotNum);
                allocated.add(spot);

                return allocated;
            } else {
                if (!freeNormalSpots.isEmpty()) {
                    int spotNum = freeNormalSpots.first();
                    ParkingSpot spot = allSpots.get(spotNum);
                    freeNormalSpots.remove(spotNum);
                    allocated.add(spot);

                    return allocated;
                }
            }
        } else if (sizeNeeded == 2) {
            // We look for two consecutive numbers in freeNormalSpots.
            if (freeNormalSpots.size() < 2) {
                return Collections.emptyList();
            }
            Integer prev = null;
            for (Integer cur : freeNormalSpots) {
                if (prev != null && cur == prev + 1) {
                    // found two consecutive!!!
                    ParkingSpot s1 = allSpots.get(prev);
                    ParkingSpot s2 = allSpots.get(cur);
                    freeNormalSpots.remove(prev);
                    freeNormalSpots.remove(cur);
                    allocated.add(s1);
                    allocated.add(s2);

                    return allocated;
                }
                prev = cur;
            }
        }
        return Collections.emptyList();
    }

    public void freeSpots(List<ParkingSpot> spotsToFree) {
        for (ParkingSpot spot : spotsToFree) {
            int num = spot.getNumber();
            if (!allSpots.containsKey(num)) {
                throw new IllegalArgumentException("Spot #" + num + " does not exist");
            }
            // free internally
            spot.free();
            // reinsert into free set by type
            if (spot.getType() == ParkingSpot.SpotType.NORMAL) {
                freeNormalSpots.add(num);
            } else {
                freeElectricSpots.add(num);
            }
        }
    }

    public void markOccupied(List<ParkingSpot> spots, ParkingSession session) {
        for (ParkingSpot spot : spots) {
            spot.occupy(session);
        }
    }

    public String reportSpotStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Parking Spots Status ===\n");
        sb.append("Normal Spots: total=").append(totalNormalSpots())
          .append(", free=").append(freeNormalCount())
          .append(", occupied=").append(occupiedNormalCount()).append("\n");
        sb.append("Electric Spots: total=").append(totalElectricSpots())
          .append(", free=").append(freeElectricCount())
          .append(", occupied=").append(occupiedElectricCount()).append("\n");
        sb.append("============================");
        return sb.toString();
    }

    public List<ParkingSpot> getAllSpotsInOrder() {
        List<ParkingSpot> list = new ArrayList<>(allSpots.values());
        list.sort(Comparator.comparingInt(ParkingSpot::getNumber));
        return list;
    }
}

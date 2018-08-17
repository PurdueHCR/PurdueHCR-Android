package Models;

import android.support.annotation.NonNull;

public class House implements Comparable<House> {
    private int numResidents;
    private int totalPoints;
    private float pointsPerResident;
    private String name;

    public House(String n, int residents, int points) {
        name = n;
        numResidents = residents;
        totalPoints = points;
        pointsPerResident = (float) points / residents;
    }

    public String getName() {
        return name;
    }

    public int getNumResidents() {
        return numResidents;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public float getPointsPerResident() {
        return pointsPerResident;
    }

    @Override
    public int compareTo(@NonNull House house) {
        return house.totalPoints - totalPoints;
    }
}

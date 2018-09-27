package Models;

import androidx.annotation.NonNull;

public class Reward implements Comparable<Reward>{
    private String name;
    private int requiredPoints;
    private int imageResource;

    public Reward(String n, int points, int resource){
        name = n;
        requiredPoints = points;
        imageResource = resource;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    @Override
    public int compareTo(@NonNull Reward reward) {
        return requiredPoints - reward.requiredPoints;
    }
}

package ua.nau.edu.NAU_Guide.Debug;

public class MapsDistanceDataModel {
    int minId;
    double distance;

    public MapsDistanceDataModel(int minId, double distance) {
        this.minId = minId;
        this.distance = distance;
    }

    public int getMinId() {
        return minId;
    }

    public double getDistance() {
        return distance;
    }
}

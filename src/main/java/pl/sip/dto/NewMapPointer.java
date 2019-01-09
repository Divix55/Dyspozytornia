package pl.sip.dto;

public class NewMapPointer {
    private int pointId;
    private String pointName;
    private double pointLongitude;
    private double pointLatitude;

    public void setPointLongitude(double pointLongitude) {
        this.pointLongitude = pointLongitude;
    }

    public void setPointLatitude(double pointLatitude) {
        this.pointLatitude = pointLatitude;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public double getPointLongitude() {
        return pointLongitude;
    }

    public double getPointLatitude() {
        return pointLatitude;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }
}

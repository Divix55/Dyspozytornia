package pl.sip.dto;

public class NewMapPointer {
    private int pointId;
    private String pointName;
    private String pointCity;
    private String pointAddress;
    private String pointAddressBlockNumber;
    private double pointLongitude;
    private double pointLatitude;
    private String pointType;

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

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getPointCity() { return pointCity; }

    public void setPointCity(String pointCity) { this.pointCity = pointCity; }

    public String getPointAddress() { return pointAddress; }

    public void setPointAddress(String pointAddress) { this.pointAddress = pointAddress; }

    public String getPointAddressBlockNumber() { return pointAddressBlockNumber; }

    public void setPointAddressBlockNumber(String pointAddressBlockNumber) { this.pointAddressBlockNumber = pointAddressBlockNumber; }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }
}

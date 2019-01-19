package pl.sip.dto;

public class SupplyTicket {
    private int ticketId;
    private int storeId;
    private int shopId;
    private int driverId;
    private String deliveryDate;
    private boolean isCompleted;
    private String shopName;
    private float shopLon;
    private float shopLat;
    private String shopDay;
    private String shopMonth;
    private String shopYear;
    private String shopHour;
    private String shopMinute;
    private double distance;
    private int duration;

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getShopName() {
        return shopName;
    }

    public float getShopLon() { return shopLon; }

    public float getShopLat() {
        return shopLat;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopYear(String shopYear) {
        this.shopYear = shopYear;
    }

    public void setShopMonth(String shopMonth) {
        this.shopMonth = shopMonth;
    }

    public void setShopDay(String shopDay) {
        this.shopDay = shopDay;
    }

    public String getShopDay() {
        return shopDay;
    }

    public String getShopMonth() {
        return shopMonth;
    }

    public String getShopYear() {
        return shopYear;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getShopHour() {
        return shopHour;
    }

    public void setShopHour(String shopHour) {
        this.shopHour = shopHour;
    }

    public String getShopMinute() {
        return shopMinute;
    }

    public void setShopMinute(String shopMinute) {
        this.shopMinute = shopMinute;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

package pl.sip.dao;

import pl.sip.dto.SupplyTicket;

import java.util.ArrayList;

public interface SupplyTicketDAO {
    ArrayList<SupplyTicket> createTicketTable();
    void createTicket(SupplyTicket ticket);
    String getShopsName(int shopsId);
    float getShopsLat(int shopsId);
    float getShopsLon(int shopsId);
    float getStoreLat(int storeId);
    float getStoreLon(int storeId);
    int[] getDriversByStoreId(int storeId);
    ArrayList<SupplyTicket> getTicketsByDrivers(int[] drivers);
}

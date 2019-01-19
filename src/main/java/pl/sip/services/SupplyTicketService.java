package pl.sip.services;

import pl.sip.dto.SupplyTicket;

import java.util.ArrayList;

public interface SupplyTicketService {
    ArrayList<SupplyTicket> showTickets();
    void createTicket(SupplyTicket ticket);
    String getShopsName(int shopId);
    float getShopsLon(int shopId);
    float getShopsLat(int shopId);
    int[] getDriversByStoreId(int storeId);
    ArrayList<SupplyTicket> getTicketsByDrivers(int[] drivers);
}

package pl.sip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.sip.dao.SupplyTicketDAO;
import pl.sip.dto.SupplyTicket;

import java.util.ArrayList;

@Service
public class SupplyTicketServiceImpl implements SupplyTicketService {

    private SupplyTicketDAO supplyTicketDAO;

    @Autowired
    public void setSupplyTicketDAO(SupplyTicketDAO supplyTicketDAO) { this.supplyTicketDAO = supplyTicketDAO; }

    public ArrayList<SupplyTicket> showTickets() {
        return supplyTicketDAO.createTicketTable();
    }

    public void createTicket(SupplyTicket ticket) {
        this.supplyTicketDAO.createTicket(ticket);
    }

    public String getShopsName(int shopId) { return supplyTicketDAO.getShopsName(shopId); }

    public float getShopsLon(int shopId) {
        return supplyTicketDAO.getShopsLon(shopId);
    }

    public float getShopsLat(int shopId) {
        return supplyTicketDAO.getShopsLat(shopId);
    }


    public float getStoreLat(int storeId){
        return supplyTicketDAO.getStoreLat(storeId);

    }

    public float getStoreLon(int storeId){
        return supplyTicketDAO.getStoreLon(storeId);
    }

    @Override
    public int[] getDriversByStoreId(int storeId) {
        return supplyTicketDAO.getDriversByStoreId(storeId);
    }

    @Override
    public ArrayList<SupplyTicket> getTicketsByDrivers(int[] drivers) {
        return supplyTicketDAO.getTicketsByDrivers(drivers);
    }
}

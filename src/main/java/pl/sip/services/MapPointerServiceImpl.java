package pl.sip.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.sip.dao.MapPointerDAO;
import pl.sip.dto.NewMapPointer;

import java.util.ArrayList;

@Service
public class MapPointerServiceImpl implements MapPointerService {

    private MapPointerDAO mapPointerDAO;

    @Autowired
    public void setMapPointerDAO(MapPointerDAO mapPointerDAO) { this.mapPointerDAO = mapPointerDAO; }

    public ArrayList<NewMapPointer> showStoreTable() {return mapPointerDAO.createStoreTable(); }
    public ArrayList<NewMapPointer> showShopTable() {return mapPointerDAO.createShopTable(); }

    public void createMapPointer(NewMapPointer mapPointer, String typeOfPoint) {
        this.mapPointerDAO.createMapPointer(mapPointer, typeOfPoint);
    }
}

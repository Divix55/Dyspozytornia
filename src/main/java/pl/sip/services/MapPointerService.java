package pl.sip.services;

import pl.sip.dto.NewMapPointer;

import java.util.ArrayList;

public interface MapPointerService {
    ArrayList<NewMapPointer> showStoreTable();
    ArrayList<NewMapPointer> showShopTable();
    void createMapPointer(NewMapPointer mapPointer, String typeOfPoint);

}

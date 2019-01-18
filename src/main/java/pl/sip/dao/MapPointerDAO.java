package pl.sip.dao;

import pl.sip.dto.NewMapPointer;

import java.util.ArrayList;

public interface MapPointerDAO {
    ArrayList<NewMapPointer> createShopTable();
    ArrayList<NewMapPointer> createStoreTable();
    void createMapPointer(NewMapPointer mapPointer, String typeOfPoint);

    NewMapPointer getPointerByName(String shopName);
}

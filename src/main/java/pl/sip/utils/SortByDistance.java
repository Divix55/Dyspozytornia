package pl.sip.utils;

import pl.sip.dto.Warehouse;
import java.util.Comparator;

public class SortByDistance implements Comparator<Warehouse> {

    public int compare(Warehouse o1, Warehouse o2) {
        return (int) (o1.getDistance() - o2.getDistance());
    }
}
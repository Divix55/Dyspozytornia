package pl.sip.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.sip.dto.NewMapPointer;
import pl.sip.services.MapPointerService;

import java.util.ArrayList;

@Controller
public class MapPointerController {

    private final MapPointerService pointerService;

    @Autowired
    public MapPointerController(MapPointerService pointerService) {
        this.pointerService = pointerService;
    }

    @GetMapping("/stores")
    public String showStores(Model model) {
        ArrayList<NewMapPointer> mapPointer = pointerService.showStoreTable();
        tableFillerFunction(model, mapPointer);
        return "stores";
    }

    @GetMapping("/shops")
    public String showShops(Model model) {
        ArrayList<NewMapPointer> mapPointer = pointerService.showShopTable();
        tableFillerFunction(model, mapPointer);
        return "shops";
    }

    private void tableFillerFunction(Model model, ArrayList<NewMapPointer> mapPointer) {
        String tableFill = "";
        for(NewMapPointer point: mapPointer) {
            String htmlTag = "<tr><td>" + point.getPointName() + "</td><td>" + point.getPointLatitude() +
                    "</td><td>" + point.getPointLongitude() + "</td><td></tr>";
            tableFill += htmlTag;
        }
        model.addAttribute("mapPointerFill", tableFill);
    }
}

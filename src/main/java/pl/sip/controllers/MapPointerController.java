package pl.sip.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pl.sip.dto.NewMapPointer;
import pl.sip.services.MapPointerService;

import javax.validation.Valid;
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
        model.addAttribute("latitude", 48.0);
        model.addAttribute("longitude", 48.0);
        return "shops";
    }

    private void tableFillerFunction(Model model, ArrayList<NewMapPointer> mapPointer) {
        StringBuilder tableFill = new StringBuilder();
        for(NewMapPointer point: mapPointer) {
            String htmlTag = "<tr class=\"wiersz\"><td class=\"name\">" + point.getPointName() + "</td><td class=\"lat\">" + point.getPointLatitude() +
                    "</td><td class=\"lon\">" + point.getPointLongitude() + "</td></tr>";
            tableFill.append(htmlTag);
        }
        model.addAttribute("mapPointerFill", tableFill.toString());
    }

    @RequestMapping(value = "/mapPointerRegister", method = RequestMethod.GET)
    public String mapPointerRegister(Model model){
        model.addAttribute("mapPointerRegisterForm", new NewMapPointer());
        return "mapPointerRegister";
    }

    @RequestMapping(value = "/mapPointerRegister", method = RequestMethod.POST)
    public String checkmapPointerRegister(@ModelAttribute("mapPointerRegisterForm") @Valid NewMapPointer form,
                                          BindingResult result,
                                          Model model){
        if(result.hasErrors()){
            model.addAttribute("error_msg", "Wrong credentials!");
            return "home";
        }
        else{
            if(form.getPointType().equals("Shop")){
                pointerService.createMapPointer(form, "Shops");
                return "redirect:/shops";
            } else if(form.getPointType().equals("Store")) {
                pointerService.createMapPointer(form, "Stores");
                return "redirect:/stores";
            } else{
                return "home";
            }
        }
    }
}

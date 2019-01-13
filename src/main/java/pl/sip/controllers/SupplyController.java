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
import pl.sip.dto.SupplyTicket;
import pl.sip.services.MapPointerService;
import pl.sip.services.SupplyTicketService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.logging.Logger;

@Controller
public class SupplyController {

    private final SupplyTicketService ticketService;
    private final MapPointerService pointerService;
    protected final Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    public SupplyController(SupplyTicketService ticketService, MapPointerService pointerService) {
        this.ticketService = ticketService;
        this.pointerService = pointerService;
    }

    @GetMapping("/supply")
    public String showSupply(Model model){
        ArrayList<SupplyTicket> supplyTickets = ticketService.showTickets();
        String tableFill = "Obecnie nie mamy zadnych zamowien";

        if( !supplyTickets.isEmpty() ) {
            tableFill = "<table><tr><th>Numer zamowienia</th><th>Nazwa sklepu</th><th>Oczekiwana data dostawy</th></tr>\n";
            for (SupplyTicket ticket : supplyTickets) {
                if (!ticket.isCompleted()) {
                    String shopName = getShopName(ticket.getShopId());
                    String htmlTag = "<tr><td>" + ticket.getTicketId() + "</td><td>" + shopName +
                            "</td><td>" + ticket.getDeliveryDate() + "</td></tr>\n";
                    tableFill += htmlTag;
                }
            }
            tableFill += "</table>";
        }

        model.addAttribute("deliveryTicketFill", tableFill);

        return "supply";
    }

    @RequestMapping(value = "/supplyDeliveryRequest", method = RequestMethod.GET)
    public String mapPointerRegister(Model model){
        ArrayList<NewMapPointer> shopList = pointerService.showShopTable();
        String shopOptions = "";
        String shopDay = "";
        String shopMonth = "";
        String shopYear = "";

        for(NewMapPointer shop: shopList){
            String htmlTag = "<option>" + shop.getPointName() + "</option>";
            shopOptions += htmlTag;
        }

        for (int i=1; i<=50; i++){
            if (i<= 12){
                shopMonth += "<option>" + i + "</option>";
            }
            if(i<=31){
                shopDay += "<option>" + i + "</option>";
            }
            shopYear += "<option>" + (2018 + i) + "</option>";
        }


        model.addAttribute("supplyDeliveryRequestForm", new SupplyTicket());
        model.addAttribute("shopIdOptions", shopOptions);
        model.addAttribute("shopDay", shopDay);
        model.addAttribute("shopMonth", shopMonth);
        model.addAttribute("shopYear", shopYear);

        return "supplyDeliveryRequest";
    }

    @RequestMapping(value = "/supplyDeliveryRequest", method = RequestMethod.POST)
    public String checkMapPointerRegister(@ModelAttribute("supplyDeliveryRequestForm") @Valid SupplyTicket form,
                                          BindingResult result,
                                          Model model){

        String date = form.getShopDay() + "-" + form.getShopMonth() + "-" + form.getShopYear();
        form.setDeliveryDate(date);
        if(result.hasErrors()){
            model.addAttribute("error_msg", "Wrong credentials!");
            return "home";
        }
        else{
            ticketService.createTicket(form);
            return "redirect:/supply";
        }
    }

    private String getShopName(int shopId){
        return ticketService.getShopsName(shopId);
    }
}

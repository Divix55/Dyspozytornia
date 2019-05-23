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
import pl.sip.dto.Warehouse;
import pl.sip.services.MapPointerService;
import pl.sip.services.SupplyTicketService;
import pl.sip.utils.SipFunctions;
import pl.sip.utils.SortByDistance;

import javax.validation.Valid;
import java.util.*;

@Controller
public class SupplyController {

    private final SupplyTicketService ticketService;
    private final MapPointerService pointerService;

    @Autowired
    public SupplyController(SupplyTicketService ticketService, MapPointerService pointerService) {
        this.ticketService = ticketService;
        this.pointerService = pointerService;
    }

    @GetMapping("/supply")
    public String showSupply(Model model){
        ArrayList<SupplyTicket> supplyTickets = ticketService.showTickets();
        StringBuilder tableFill = new StringBuilder("Obecnie nie mamy zadnych zamowien");

        if( !supplyTickets.isEmpty() ) {
            tableFill = new StringBuilder("<table id='tabela_dostawy'><tr><th>Numer zamowienia</th><th>Nazwa sklepu</th><th style='display: none'>Lon</th><th style='display: none'>Lat</th><th>Store Id</th><th>Driver Id</th><th>Czas trwania</th><th>Oczekiwana data dostawy</th></tr>\n");
            for (SupplyTicket ticket : supplyTickets) {
                if (!ticket.isCompleted()) {
                    String shopName = ticketService.getShopsName(ticket.getShopId());
                    float shopLon = ticketService.getShopsLon(ticket.getShopId());
                    float shopLat = ticketService.getShopsLat(ticket.getShopId());
                    float storeLon = ticketService.getStoreLon(ticket.getStoreId());
                    float storeLat = ticketService.getStoreLat(ticket.getStoreId());
                    String htmlTag = "<tr><td>" + ticket.getTicketId() + "</td><td>" + shopName +
                            "</td><td style='display: none'>" + shopLon + "</td><td style='display: none'>" + shopLat +
                            "</td><td style='display: none'>" + storeLon + "</td><td style='display: none'>" + storeLat +
                            "</td><td>" + ticket.getStoreId() + "</td><td>"+ ticket.getDriverId() +"</td><td>"+ ticket.getDuration() +"</td><td>" + ticket.getDeliveryDate() + "</td></tr>\n";
                    tableFill.append(htmlTag);
                }
            }
            tableFill.append("</table>");
        }

        model.addAttribute("deliveryTicketFill", tableFill.toString());

        return "supply";
    }

    @RequestMapping(value = "/supplyDeliveryRequest", method = RequestMethod.GET)
    public String mapPointerRegister(Model model){
        ArrayList<NewMapPointer> shopList = pointerService.showShopTable();
        StringBuilder shopOptions = new StringBuilder();
        StringBuilder shopDay = new StringBuilder();
        StringBuilder shopMonth = new StringBuilder();
        StringBuilder shopYear = new StringBuilder();
        StringBuilder shopHour = new StringBuilder();
        StringBuilder shopMinute = new StringBuilder();

        for(NewMapPointer shop: shopList){
            String htmlTag = "<option>" + shop.getPointName() + "</option>";
            shopOptions.append(htmlTag);
        }

        for (int i=1; i<=60; i++){
            if (i<= 12){
                shopMonth.append("<option>").append(i).append("</option>");
            }
            if(i<=31){
                shopDay.append("<option>").append(i).append("</option>");
            }
            if(i >= 8 && i< 16){
                shopHour.append("<option>").append(i).append("</option>");
            }
            shopMinute.append("<option>").append(i - 1).append("</option>");
            shopYear.append("<option>").append(2018 + i).append("</option>");
        }


        model.addAttribute("supplyDeliveryRequestForm", new SupplyTicket());
        model.addAttribute("shopIdOptions", shopOptions.toString());
        model.addAttribute("shopDay", shopDay.toString());
        model.addAttribute("shopMonth", shopMonth.toString());
        model.addAttribute("shopYear", shopYear.toString());
        model.addAttribute("shopHour", shopHour.toString());
        model.addAttribute("shopMinute", shopMinute.toString());

        return "supplyDeliveryRequest";
    }

    @RequestMapping(value = "/supplyDeliveryRequest", method = RequestMethod.POST)
    public String checkMapPointerRegister(@ModelAttribute("supplyDeliveryRequestForm") @Valid SupplyTicket form,
                                          BindingResult result,
                                          Model model){

        String date = form.getShopYear() + "-" + form.getShopMonth() + "-" + form.getShopDay();
        String hour = form.getShopHour() + ":" + form.getShopMinute();
        if(result.hasErrors()){
            model.addAttribute("error_msg", "Wrong credentials!");
            return "home";
        }
        else{
            ArrayList<NewMapPointer> warehouses = pointerService.showStoreTable();
            NewMapPointer whereToDeliver = pointerService.getPointerByName(form.getShopName());

            boolean isDriverAlreadyPicked = false;

            while(!isDriverAlreadyPicked) {
                List<Warehouse>calculatedWarehouses = calculateWarehousesByTime(warehouses, date, hour, whereToDeliver);
                if(!calculatedWarehouses.isEmpty()) {
                    Collections.sort(calculatedWarehouses, new SortByDistance());
                    for (Warehouse store : calculatedWarehouses) {
                        if (store.getAvailableDrivers() > 0) {
                            form.setDriverId(store.getDriverId());
                            form.setDistance(store.getDistance());
                            form.setStoreId(store.getStoreId());
                            form.setDuration(SipFunctions.calculateDuration(store.getDistance()));
                            form.setDeliveryDate(date + " " + hour);
                            isDriverAlreadyPicked = true;
                            break;
                        }
                    }
                }
                String newDate = SipFunctions.tryNextHour(date, hour);
                date = newDate.split(" ")[0];
                hour = newDate.split(" ")[1];
            }

            ticketService.createTicket(form);
            return "redirect:/supply";
        }
    }

    private ArrayList<Warehouse> calculateWarehousesByTime(ArrayList<NewMapPointer> warehouses,
                                                           String date,
                                                           String hour,
                                                           NewMapPointer whereToDeliver){
        ArrayList<Warehouse>calculatedWarehouses = new ArrayList<>();
        for(NewMapPointer store: warehouses){
            //function picks first suitable
            double distance = SipFunctions.calculateDistanceInStraightLine(whereToDeliver, store);
            int availableDrivers = checkAvailableDrivers(store.getPointId(), distance, date, hour);
            if (availableDrivers != 0) {

                Warehouse calculatedStore = new Warehouse();
                calculatedStore.setAvailableDrivers(availableDrivers);
                calculatedStore.setStoreId(store.getPointId());
                calculatedStore.setDistance(distance);
                calculatedStore.setDriverId(availableDrivers);

                calculatedWarehouses.add(calculatedStore);
            }
        }
        return calculatedWarehouses;
    }

    private int checkAvailableDrivers(int storeId, double distance, String deliveryDate, String deliveryHour){
        int[] drivers = ticketService.getDriversByStoreId(storeId);
        int deliveryDuration = SipFunctions.calculateDuration(distance);
        ArrayList<SupplyTicket> driversTickets = ticketService.getTicketsByDrivers(drivers);

        for(int driver: drivers){
            boolean ifAvailableForDelivery = checkAvailability(driver, driversTickets, deliveryDate, deliveryHour, deliveryDuration);
            if(ifAvailableForDelivery){
                return driver;
            }
        }

        return 0;
    }

    private boolean checkAvailability(int driverId,
                                             ArrayList<SupplyTicket> driversTickets,
                                             String deliveryDate,
                                             String deliveryHour,
                                             int deliveryDuration) {
        for(SupplyTicket ticket: driversTickets){
            if (ticket.getDriverId() == driverId){
                String ticketFullDate = ticket.getDeliveryDate().split(" ")[0];
                String ticketFullTime = ticket.getDeliveryDate().split(" ")[1];
                if (ticketFullDate.equals(deliveryDate)){
                    String[] deliveryFullTime = deliveryHour.split((":"));
                    String[] ticketTime = ticketFullTime.split((":"));
                    int deliveryHourInt = Integer.parseInt(deliveryFullTime[0]);
                    int deliveryMinuteInt = Integer.parseInt(deliveryFullTime[1]);
                    int ticketHourInt = Integer.parseInt(ticketTime[0]);
                    int ticketMinuteInt = Integer.parseInt(ticketTime[1]);

                    //in minutes
                    int difference = 60 * (Math.abs(deliveryHourInt - ticketHourInt)) +
                            (Math.abs(deliveryMinuteInt - ticketMinuteInt));
                    if((difference - deliveryDuration) - ticket.getDuration() < 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
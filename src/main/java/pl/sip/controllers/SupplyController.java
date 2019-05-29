package pl.sip.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
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
import java.util.logging.Logger;

@Controller
public class SupplyController {

    private final SupplyTicketService ticketService;
    private final MapPointerService pointerService;
    private Logger log = Logger.getLogger("SupplyController");

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
            tableFill = new StringBuilder("<table id='tabela_dostawy'><tr><th>Numer zamowienia</th><th>Nazwa sklepu" +
                    "</th><th style='display: none'>Lon</th><th style='display: none'>Lat</th><th>Store Id</th><th>" +
                    "Driver Id</th><th>Czas trwania</th><th>Status</th><th>Oczekiwana data dostawy</th></tr>\n");
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
                            "</td><td>" + ticket.getStoreId() + "</td><td>"+ ticket.getDriverId() +"</td><td>"+
                            ticket.getDuration() + "</td><td>" + ticket.getTicketStatus() + "</td><td>"+
                            ticket.getDeliveryDate() + "</td></tr>\n";
                    tableFill.append(htmlTag);
                }
            }
            tableFill.append("</table>");
        }

        model.addAttribute("deliveryTicketFill", tableFill.toString());

        return "supply";
    }

    @RequestMapping(value = "/acceptDelivery", method = RequestMethod.GET)
    public String acceptDelivery(Model model){
        ArrayList<SupplyTicket> supplyTickets = ticketService.showTickets();

        for (SupplyTicket ticket: supplyTickets){
            if(ticket.getTicketStatus().equals("oczekujace")){
                ticket.setTicketStatus("w realizacji");

                log.info("Przed wyznaczeniem trasy");
                //TODO: use new distance algorithm instead of this
                createTicketDataNaiveAlgorithm(ticket);
                log.info("Po wyznaczeniu trasy");
            }
        }
        // TODO: distance algorithm goes here
        //change their status
        //send them back to db
        return "supply";
    }

    private void createTicketDataNaiveAlgorithm(SupplyTicket ticket){
        String date = ticket.getDeliveryDate().split(" ")[0];
        String hour = ticket.getDeliveryDate().split(" ")[1];

        ArrayList<NewMapPointer> warehouses = pointerService.showStoreTable();

        ticket.setShopName(ticketService.getShopsName(ticket.getShopId())); // TODO: [Hubert] na Boga, zmień to ._. // [Hubert] zmienię jak będzie czas
        log.info(ticket.toString());
        NewMapPointer whereToDeliver = pointerService.getPointerByName(ticket.getShopName());
        boolean isDriverAlreadyPicked = false;

        while(!isDriverAlreadyPicked) {
            List<Warehouse>calculatedWarehouses = calculateWarehousesByTime(warehouses, date, hour, whereToDeliver);
            if(!calculatedWarehouses.isEmpty()) {
                Collections.sort(calculatedWarehouses, new SortByDistance());
                for (Warehouse store : calculatedWarehouses) {
                    if (store.getAvailableDrivers() > 0) {
                        ticket.setDriverId(store.getDriverId());
                        ticket.setDistance(store.getDistance());
                        ticket.setStoreId(store.getStoreId());
                        ticket.setDuration(SipFunctions.calculateDuration(store.getDistance()));
                        ticket.setDeliveryDate(date + " " + hour);
                        ticket.setTicketStatus("w realizacji");
                        isDriverAlreadyPicked = true;
                        break;
                    }
                }
            }
            String newDate = SipFunctions.tryNextHour(date, hour);
            date = newDate.split(" ")[0];
            hour = newDate.split(" ")[1];
        }

        ticketService.createTicketNaive(ticket);
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
            if(i<=10) {
                shopMinute.append("<option>").append("0").append(i - 1).append("</option>");
            }
            else
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
    public String checkMapPointerRegister(@ModelAttribute("supplyDeliveryRequestForm") @Valid SupplyTicket ticket,
                                          BindingResult result,
                                          Model model){

        if(result.hasErrors()){
            model.addAttribute("error_msg", "Wrong credentials!");
            return "home";
        }
        else{
            ticketService.createTicketEntry(ticket);
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
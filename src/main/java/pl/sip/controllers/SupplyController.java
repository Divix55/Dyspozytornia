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
import pl.sip.dto.Werehouse;
import pl.sip.services.MapPointerService;
import pl.sip.services.SupplyTicketService;

import javax.validation.Valid;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class SupplyController {

    private static final double SPEED_FACTOR = 1.2;
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
            tableFill = "<table id='tabela_dostawy'><tr><th>Numer zamowienia</th><th>Nazwa sklepu</th><th style='display: none'>Lon</th><th style='display: none'>Lat</th><th>Store Id</th><th>Driver Id</th><th>Czas trwania</th><th>Oczekiwana data dostawy</th></tr>\n";
            for (SupplyTicket ticket : supplyTickets) {
                if (!ticket.isCompleted()) {
                    String shopName = getShopName(ticket.getShopId());
                    float shopLon = getShopLon(ticket.getShopId());
                    float shopLat = getShopLat(ticket.getShopId());
                    float storeLon = ticketService.getStoreLon(ticket.getStoreId());
                    float storeLat = ticketService.getStoreLat(ticket.getStoreId());
                    String htmlTag = "<tr><td>" + ticket.getTicketId() + "</td><td>" + shopName +
                            "</td><td style='display: none'>" + shopLon + "</td><td style='display: none'>" + shopLat +
                            "</td><td style='display: none'>" + storeLon + "</td><td style='display: none'>" + storeLat +
                            "</td><td>" + ticket.getStoreId() + "</td><td>"+ ticket.getDriverId() +"</td><td>"+ ticket.getDuration() +"</td><td>" + ticket.getDeliveryDate() + "</td></tr>\n";
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
        String shopHour = "";
        String shopMinute = "";

        for(NewMapPointer shop: shopList){
            String htmlTag = "<option>" + shop.getPointName() + "</option>";
            shopOptions += htmlTag;
        }

        for (int i=1; i<=60; i++){
            if (i<= 12){
                shopMonth += "<option>" + i + "</option>";
            }
            if(i<=31){
                shopDay += "<option>" + i + "</option>";
            }
            if(i >= 8 && i< 16){
                shopHour += "<option>" + i + "</option>";
            }
            shopMinute += "<option>" + (i - 1) + "</option>";
            shopYear += "<option>" + (2018 + i) + "</option>";
        }


        model.addAttribute("supplyDeliveryRequestForm", new SupplyTicket());
        model.addAttribute("shopIdOptions", shopOptions);
        model.addAttribute("shopDay", shopDay);
        model.addAttribute("shopMonth", shopMonth);
        model.addAttribute("shopYear", shopYear);
        model.addAttribute("shopHour", shopHour);
        model.addAttribute("shopMinute", shopMinute);

        return "supplyDeliveryRequest";
    }

    private ArrayList<Werehouse> calculateWerehousesByTime(ArrayList<NewMapPointer> werehouses,
                                                           String date,
                                                           String hour,
                                                           NewMapPointer whereToDeliver){
        ArrayList<Werehouse>calculatedWerehouses = new ArrayList<Werehouse>();
        for(NewMapPointer store: werehouses){
            //function picks first suitable
            double distance = calculateDistanceInStraightLine(whereToDeliver, store);
            int availableDrivers = checkAvailableDrivers(store.getPointId(), distance, date, hour);
            if (availableDrivers != 0) {

                Werehouse calculatedStore = new Werehouse();
                calculatedStore.setAvailableDrivers(availableDrivers);
                calculatedStore.setStoreId(store.getPointId());
                calculatedStore.setDistance(distance);
                calculatedStore.setDriverId(availableDrivers);

                calculatedWerehouses.add(calculatedStore);
            }
        }
        return calculatedWerehouses;
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
            ArrayList<NewMapPointer> werehouses = addWerehouses();
            NewMapPointer whereToDeliver = pointerService.getPointerByName(form.getShopName());

            boolean isDriverAlreadyPicked = false;

            while(!isDriverAlreadyPicked) {
                List<Werehouse>calculatedWerehouses = calculateWerehousesByTime(werehouses, date, hour, whereToDeliver);
                if(!calculatedWerehouses.isEmpty()) {
                    Collections.sort(calculatedWerehouses, new SortByDistance());
                    for (Werehouse store : calculatedWerehouses) {
                        if (store.getAvailableDrivers() > 0) {
                            form.setDriverId(store.getDriverId());
                            form.setDistance(store.getDistance());
                            form.setStoreId(store.getStoreId());
                            form.setDuration(calculateDuration(store.getDistance()));
                            form.setDeliveryDate(date + " " + hour);
                            isDriverAlreadyPicked = true;
                            break;
                        }
                    }
                }
                String newDate = tryNextHour(date, hour);
                date = newDate.split(" ")[0];
                hour = newDate.split(" ")[1];
            }

            ticketService.createTicket(form);
            return "redirect:/supply";
        }
    }

    private int calculateDuration(double distance) {
        int duration = (int) Math.round(distance*SPEED_FACTOR);
        return  duration;
    }

    private int checkAvailableDrivers(int storeId, double distance, String deliveryDate, String deliveryHour){
        int[] drivers = ticketService.getDriversByStoreId(storeId);
        int deliveryDuration = calculateDuration(distance);
        ArrayList<SupplyTicket> driversTickets = ticketService.getTicketsByDrivers(drivers);

        for(int driver: drivers){
            boolean ifAvailableForDelivery = checkAvailibility(driver, driversTickets, deliveryDate, deliveryHour, deliveryDuration);
            if(ifAvailableForDelivery){
                return driver;
            }
        }

        return 0;
    }

    private boolean checkAvailibility(int driverId,
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

    private String tryNextHour(String date, String h) {
        String[] hour = h.split(":");
        int hourInt = Integer.parseInt(hour[0]);
        int minInt = Integer.parseInt(hour[1]);
        if (hourInt >= 8 && hourInt < 16){
            if (minInt >= 0 && minInt < 60){
                if(hourInt == 15 && minInt == 59){
                    h = addDigitToTime(hourInt, minInt, 'H');
                }
                h = addDigitToTime(hourInt, minInt, 'M');
            }
            else{
                h = addDigitToTime(hourInt, minInt, 'H');
            }
        }
        else{
            hourInt = 8;
            minInt = 0;
            date = setDateToNextDay(date);
            h = hourInt + ":" + minInt;
        }

        return date + " " + h;
    }

    private String addDigitToTime(int timeHour, int timeMin, char type){
        //minutes
        if (type == 'M'){
            if (timeMin == 59){
                timeHour += 1;
                timeMin = 0;
            }
            else{
                timeMin += 1;
            }
        }
        //hours
        else if(type == 'H'){
            timeHour += 1;
            timeMin = 0;
        }

        return timeHour + ":" + timeMin;
    }

    private static String setDateToNextDay(String d){
        String[] date = d.split("-");
        int yearInt = Integer.parseInt(date[0]);
        int monthInt = Integer.parseInt(date[1]);
        int dayInt = Integer.parseInt(date[2]);

        if (dayInt < 31){
            dayInt += 1;
        }
        else if(monthInt < 12){
            dayInt = 1;
            monthInt += 1;
        }
        else{
            dayInt = 1;
            monthInt = 1;
            yearInt += 1;
        }
        return yearInt + "-" + monthInt + "-" + dayInt;
    }

    private ArrayList<NewMapPointer> addWerehouses() {
        return pointerService.showStoreTable();
    }

    private static double calculateDistanceInStraightLine( NewMapPointer point1, NewMapPointer point2) {
        if (point1.equals(point2)) {
            return 0;
        }
        else {
            double lon1 = point1.getPointLongitude();
            double lon2 = point2.getPointLongitude();
            double lat1 = point1.getPointLatitude();
            double lat2 = point2.getPointLatitude();
            double theta = lon1 - lon2;

            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }
    }

    private String getShopName(int shopId){
        return ticketService.getShopsName(shopId);
    }

    private float getShopLon(int shopId){
        return ticketService.getShopsLon(shopId);
    }

    private float getShopLat(int shopId){
        return ticketService.getShopsLat(shopId);
    }
}

class SortByDistance implements Comparator<Werehouse> {

    public int compare(Werehouse o1, Werehouse o2) {
        return (int) (o1.getDistance() - o2.getDistance());
    }
}
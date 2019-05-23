package pl.sip.utils;

import pl.sip.dto.NewMapPointer;

public class SipFunctions {
    private static final double SPEED_FACTOR = 1.2;

    public static int calculateDuration(double distance) {
        return (int) Math.round(distance*SPEED_FACTOR);
    }

    public static String tryNextHour(String date, String h) {
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

    public static double calculateDistanceInStraightLine(NewMapPointer point1, NewMapPointer point2) {
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

    private static String addDigitToTime(int timeHour, int timeMin, char type){
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
}

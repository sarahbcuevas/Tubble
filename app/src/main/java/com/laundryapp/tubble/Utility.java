package com.laundryapp.tubble;

public class Utility {

    public static String getTimeDifference(long startDate, long endDate) {
        String differenceStr = "";
        long difference = endDate - startDate;

        if (difference <= 0) {
            return "0 hrs 0 mins";
        }

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        if (elapsedDays > 0) {
            differenceStr += elapsedDays + " days ";
        }
        differenceStr += elapsedHours + " hrs " + elapsedMinutes + " mins";

        return differenceStr;
    }
}

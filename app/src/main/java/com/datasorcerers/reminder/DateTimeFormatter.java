package com.datasorcerers.reminder;

import org.joda.time.DateTime;

public class DateTimeFormatter {

    public static String formatTime(DateTime dt) {
        String minutes = dt.minuteOfHour().getAsText();
        if (minutes.length() == 1) {
            minutes += "0";
        }
        return dt.getHourOfDay()+":"+minutes;
    }

    public static String formatDate(DateTime dt) {
        return dt.getDayOfMonth()+" "+dt.monthOfYear().getAsShortText();
    }
}

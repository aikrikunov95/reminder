package com.datasorcerers.reminder.UI;

import org.joda.time.DateTime;

public class DateTimeFormatter {

    public static String formatTime(DateTime dt) {
        String minutes = dt.minuteOfHour().getAsText();
        if (minutes.length() == 1) {
            minutes += "0";
        }
        return dt.getHourOfDay()+":"+minutes;
    }

    public static String formatDateWithWeekday(DateTime dt) {
        if (isToday(dt)) {
            return "Сегодня";
        } else if (isTomorrow(dt)) {
            return "Завтра";
        } else if (isThisWeek(dt)) {
            String s = dt.dayOfWeek().getAsText();
            return s.substring(0,1).toUpperCase()+s.substring(1);
        } else {
            String s = dt.dayOfWeek().getAsShortText();
            s = s.substring(0,1).toUpperCase()+s.substring(1);
            return dt.getDayOfMonth() + " " + dt.monthOfYear().getAsShortText()+", "+s;
        }
    }

    public static String formatDate(DateTime dt) {
        if (isToday(dt)) {
            return "Сегодня";
        } else if (isTomorrow(dt)) {
            return "Завтра";
        } else if (isThisWeek(dt)) {
            String s = dt.dayOfWeek().getAsText();
            return s.substring(0,1).toUpperCase()+s.substring(1);
        } else {
            String s = dt.dayOfWeek().getAsShortText();
            s = s.substring(0,1).toUpperCase()+s.substring(1);
            return dt.getDayOfMonth() + " " + dt.monthOfYear().getAsShortText();
        }
    }

    private static boolean isToday(DateTime dt) {
        return dt.toLocalDate().equals(new DateTime().toLocalDate());
    }

    private static boolean isTomorrow(DateTime dt) {
        return dt.toLocalDate().minusDays(1).equals(new DateTime().toLocalDate());
    }

    private static boolean isThisWeek(DateTime dt) {
        return dt.getWeekOfWeekyear() == new DateTime().getWeekOfWeekyear();
    }
}

package com.datasorcerers.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmManagerHelper {

    public static void set(Context context, long time, String note) {
        PendingIntent pendingIntent =  getPendingIntent(context, note);
        getAlarmManager(context).set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public static void cancel(Context context, String note) {
        PendingIntent pendingIntent =  getPendingIntent(context, note);
        getAlarmManager(context).cancel(pendingIntent);
    }

    private static AlarmManager getAlarmManager(Context context) {
        return ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
    }

    private static PendingIntent getPendingIntent(Context context, String note) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("note", note);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

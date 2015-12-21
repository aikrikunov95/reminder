package com.datasorcerers.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmManagerHelper {
    private Context context;
    private AlarmManager am;

    public AlarmManagerHelper(Context context) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void set(Alarm alarm) {
        PendingIntent pendingIntent = getPendingIntent(alarm);
        am.set(AlarmManager.RTC_WAKEUP, alarm.getDatetime(), pendingIntent);
    }

    public void set(long time, Alarm alarm) {
        PendingIntent pendingIntent = getPendingIntent(alarm);
        am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public void cancel(Alarm alarm) {
        PendingIntent pendingIntent = getPendingIntent(alarm);
        am.cancel(pendingIntent);
    }

    private PendingIntent getPendingIntent(Alarm alarm) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Alarm.TAG, alarm);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

package com.datasorcerers.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

public class AlarmManagerHelper {
    private Context context;
    private AlarmManager am;
    private static HashMap<Alarm, Integer> ids = new HashMap<>(); // TODO REMAKE

    public AlarmManagerHelper(Context context) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void set(Alarm alarm, int action) {
        PendingIntent pendingIntent = getPendingIntent(alarm, action);
        am.set(AlarmManager.RTC_WAKEUP, alarm.getDatetime(), pendingIntent);
    }

    public void set(long time, Alarm alarm, int action) {
        PendingIntent pendingIntent = getPendingIntent(alarm, action);
        am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public void cancel(Alarm alarm) {
        PendingIntent pendingIntent = getPendingIntent(alarm, 0);
        am.cancel(pendingIntent);
        ids.remove(alarm);
    }

    private PendingIntent getPendingIntent(Alarm alarm, int action) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Alarm.ALARM_EXTRA_NAME, alarm);
        intent.putExtra(AlarmReceiver.ACTION_EXTRA_NAME, action);
        final int id = (int) System.currentTimeMillis();
        if (action == AlarmReceiver.ACTION_ADD) {
            ids.put(alarm, id);
        }
        return PendingIntent.getBroadcast(context, ids.get(alarm), intent, PendingIntent.FLAG_ONE_SHOT);
    }
}

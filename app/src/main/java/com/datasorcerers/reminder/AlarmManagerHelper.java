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

    public void set(long time, String note) {
        PendingIntent pendingIntent = getPendingIntent(note);
        am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public void cancel(String note) {
        PendingIntent pendingIntent = getPendingIntent(note);
        am.cancel(pendingIntent);
    }

    private PendingIntent getPendingIntent(String note) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("note", note);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

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

    public void set(long time, Alarm alarm) {
        am.set(AlarmManager.RTC_WAKEUP, time, getAlarmPendingIntent(alarm));
    }

    public void cancel(Alarm alarm) {
        am.cancel(getAlarmPendingIntent(alarm));
    }

    private PendingIntent getAlarmPendingIntent(Alarm alarm) {
        Intent i = new Intent(context, NotificationService.class);
        i.setAction(NotificationService.ACTION_START);
        i.putExtra(Alarm.ALARM_EXTRA_NAME, alarm);
        return PendingIntent.getService(context, alarm.getId(), i, PendingIntent.FLAG_ONE_SHOT);
    }
}

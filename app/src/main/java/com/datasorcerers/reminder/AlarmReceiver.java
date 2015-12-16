package com.datasorcerers.reminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int ALARM_DISMISS_ACTION = 0;
    public static final int ALARM_START_ACTION = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("action", 2);
        switch (action) {
            case 0:
                AlarmKlaxon.stop();
                AlarmWakeLock.release();
                break;
            case 1:
                AlarmKlaxon.start(context);
                AlarmWakeLock.acquire(context);
                showNotification(context, intent);
                break;
        }
    }

    public static void setup(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cal.getTimeInMillis() + 5000); // TODO hardcode

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("action", ALARM_START_ACTION); // TODO hardcode
        intent.putExtra("note", "note");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    private void showNotification(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent deleteIntent = new Intent(context, AlarmReceiver.class);
        deleteIntent.putExtra("action", ALARM_DISMISS_ACTION);
        PendingIntent delete = PendingIntent.getBroadcast(context, 0, deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder nb = new Notification.Builder(context);
        nb
                .setSmallIcon(R.mipmap.ic_launcher) // TODO hardcode db
                .setContentTitle("Reminder")
                .setContentText(intent.getStringExtra("note"))
                .setDeleteIntent(delete)
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(new long[0]);

        nm.notify(1, nb.build()); // TODO hardcode
    }
}

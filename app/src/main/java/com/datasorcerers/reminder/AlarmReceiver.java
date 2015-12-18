package com.datasorcerers.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.joda.time.DateTime;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int ACTION_SETUP = 0;
    public static final int ACTION_SNOOZE = 1;
    public static final int ACTION_DISMISS = 2;

    private static boolean notificationIsActive = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("action", 3);
        switch (action) {
            case 0:
                AlarmKlaxon.stop();
                AlarmWakeLock.release();
                NotificationService.cancelNotification(context);
                setNotificationIsActive(false);
                break;
            case 1:
                AlarmKlaxon.start(context);
                AlarmWakeLock.acquire(context);
                NotificationService.issueNotification(context, intent);
                setNotificationIsActive(true);

                Intent notificationServiceIntent = new Intent(context, NotificationService.class);
                notificationServiceIntent.putExtra("note", intent.getStringExtra("note"));
                context.startService(notificationServiceIntent);
                break;
            case 2:
                AlarmKlaxon.stop();
                AlarmWakeLock.release();
                NotificationService.cancelNotification(context);

                start(context, intent.getStringExtra("note"), new DateTime().getMillis() + 10000);
                break;

        }
    }

    public static void start(Context context, String note, long time) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("action", AlarmReceiver.ACTION_SETUP);
        intent.putExtra("note", note);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public static void snooze(Context context, Intent dataIntent) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("action", AlarmReceiver.ACTION_SNOOZE);
        intent.putExtra("note", dataIntent.getStringExtra("note"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, new DateTime().getMillis() + 10000, pendingIntent);
    }

    public static void setNotificationIsActive(boolean b) {
        notificationIsActive = b;
    }

    public static boolean notificationIsActive() {
        return notificationIsActive;
    }
}

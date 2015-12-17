package com.datasorcerers.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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

    private void showNotification(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent deleteIntent = new Intent(context, AlarmReceiver.class);
        deleteIntent.putExtra("action", ALARM_DISMISS_ACTION);
        PendingIntent delete = PendingIntent.getBroadcast(context, 0, deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder nb = new Notification.Builder(context);
        nb
                .setSmallIcon(R.mipmap.ic_launcher) // TODO icon
                .setContentTitle("Reminder")
                .setContentText(intent.getStringExtra("note"))
                .setDeleteIntent(delete)
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(new long[0]);

        nm.notify(1, nb.build()); // TODO hardcode
    }
}

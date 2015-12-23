package com.datasorcerers.reminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Looper;

import org.joda.time.DateTime;

public class NotificationService extends IntentService {
    private final static long TIMEOUT_TIME = 3 * 1000;
    private final static long SNOOZE_TIME = 5 * 1000;

    private final static String START_TAG = "start";

    private static boolean notificationCanceled;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final AlarmManagerHelper amHelper = new AlarmManagerHelper(this);
        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        final Context context = getApplicationContext();
        final Alarm alarm = intent.getParcelableExtra(Alarm.TAG);

        if (intent.getBooleanExtra(START_TAG, true)) {
            AlarmKlaxon.start(context);
            AlarmWakeLock.acquire(context);
            issueNotification(context, intent);
            notificationCanceled = false;

            new CountDownTimer(TIMEOUT_TIME, TIMEOUT_TIME) {
                @Override
                public void onTick(long millisUntilFinished) {}

                @Override
                public void onFinish() {
                    Looper.myLooper().quit();
                    AlarmKlaxon.stop();
                    AlarmWakeLock.release();
                    if (!notificationCanceled) {
                        amHelper.set(new DateTime().getMillis() + SNOOZE_TIME, alarm);
                    }
                }
            }.start();
            Looper.loop();
        } else {
            AlarmKlaxon.stop();
            AlarmWakeLock.release();
            notificationCanceled = true;
            amHelper.cancel(alarm);
            dbHelper.delete(alarm);

            Intent updateUIIntent = new Intent("updateUI");
            updateUIIntent.putExtra(Alarm.TAG, alarm);
            sendBroadcast(updateUIIntent);
        }
    }

    private void issueNotification(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Alarm alarm = intent.getParcelableExtra(Alarm.TAG);

        Intent deleteIntent = new Intent(context, NotificationService.class);
        deleteIntent.putExtra(START_TAG, false);
        deleteIntent.putExtra(Alarm.TAG, alarm);
        PendingIntent delete = PendingIntent.getService(context, 0, deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder nb = new Notification.Builder(context);
        nb
                .setSmallIcon(R.mipmap.ic_launcher) // TODO icon
                .setContentTitle("Reminder")
                .setContentText(alarm.getNote())
                .setDeleteIntent(delete)
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(new long[0]);

        nm.notify(1, nb.build()); // TODO hardcode
    }
}

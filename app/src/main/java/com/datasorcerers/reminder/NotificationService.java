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

    private static boolean notificationCanceled;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final Context context = getApplicationContext();
        final String note = intent.getStringExtra("note");
        if (intent.getBooleanExtra("start", true)) {
            AlarmKlaxon.start(context);
            AlarmWakeLock.acquire(context);
            issueNotification(context, intent);
            notificationCanceled = false;

            new CountDownTimer(TIMEOUT_TIME, TIMEOUT_TIME) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Looper.myLooper().quit();
                    AlarmKlaxon.stop();
                    AlarmWakeLock.release();
                    if (!notificationCanceled) {
                        AlarmManagerHelper.set(context, new DateTime().getMillis() + SNOOZE_TIME, note);
                    }
                }
            }.start();
            Looper.loop();
        } else {
            AlarmKlaxon.stop();
            AlarmWakeLock.release();
            notificationCanceled = true;
            AlarmManagerHelper.cancel(context, note);
        }
    }

    private void issueNotification(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent deleteIntent = new Intent(context, NotificationService.class);
        deleteIntent.putExtra("start", false);
        PendingIntent delete = PendingIntent.getService(context, 0, deleteIntent,
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

package com.datasorcerers.reminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Looper;

public class NotificationService extends IntentService {
    public static final String ACTION_EXTRA_NAME = "com.datasorcerers.reminder.NotificationService.ACTION_EXTRA_NAME";
    private final static long TIMEOUT_TIME_SECONDS = 3;
    private final static long SNOOZE_TIME_SECONDS = 5;

    public final static int ACTION_ISSUE = 1;
    public final static int ACTION_CANCEL = 2;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        final Context context = getApplicationContext();
        final Alarm alarm = intent.getParcelableExtra(Alarm.ALARM_EXTRA_NAME);

        int action = intent.getIntExtra(ACTION_EXTRA_NAME, 0);
        switch (action) {
            case ACTION_ISSUE:
                AlarmKlaxon.start(context);
                AlarmWakeLock.acquire(context);
                issueNotification(context, intent);

                new CountDownTimer(TIMEOUT_TIME_SECONDS * 1000, TIMEOUT_TIME_SECONDS * 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        AlarmKlaxon.stop();
                        AlarmWakeLock.release();
                    }
                }.start();
                new CountDownTimer(TIMEOUT_TIME_SECONDS * 1000 + SNOOZE_TIME_SECONDS * 1000,
                        TIMEOUT_TIME_SECONDS * 1000 + SNOOZE_TIME_SECONDS * 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        Intent notificationServiceIntent = new Intent(context, NotificationService.class);
                        notificationServiceIntent.putExtra(ACTION_EXTRA_NAME, ACTION_ISSUE);
                        notificationServiceIntent.putExtra(Alarm.ALARM_EXTRA_NAME, alarm);
                        context.startService(notificationServiceIntent);
                    }
                }.start();
                Looper.loop();
                break;
            case ACTION_CANCEL:
                AlarmKlaxon.stop();
                AlarmWakeLock.release();
                dbHelper.delete(alarm);

                Intent updateUIIntent = new Intent(AlarmList.UPDATE_UI_ACTION);
                updateUIIntent.putExtra(Alarm.ALARM_EXTRA_NAME, alarm);
                sendBroadcast(updateUIIntent);

                Intent pollQueueIntent = new Intent(context, AlarmReceiver.class);
                pollQueueIntent.putExtra(AlarmReceiver.ACTION_EXTRA_NAME, AlarmReceiver.ACTION_POLL);
                sendBroadcast(pollQueueIntent);
                break;
        }
    }

    private void issueNotification(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Alarm alarm = intent.getParcelableExtra(Alarm.ALARM_EXTRA_NAME);

        Intent deleteIntent = new Intent(context, NotificationService.class);
        deleteIntent.putExtra(ACTION_EXTRA_NAME, ACTION_CANCEL);
        deleteIntent.putExtra(Alarm.ALARM_EXTRA_NAME, alarm);
        final int id = (int) System.currentTimeMillis();
        PendingIntent delete = PendingIntent.
                getService(context, id, deleteIntent, PendingIntent.FLAG_ONE_SHOT);

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

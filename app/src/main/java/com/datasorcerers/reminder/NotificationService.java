package com.datasorcerers.reminder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.SystemClock;

import com.datasorcerers.reminder.ui.AlertActivity;

import org.joda.time.DateTime;

public class NotificationService extends IntentService {

    public final static long TIMEOUT_TIME = 5000;
    public final static long SNOOZE_TIME = 5000;

    public final static String ACTION_START = "com.datasorcerers.reminder.NotificationService.action_start";
    public final static String ACTION_DISMISS = "com.datasorcerers.reminder.NotificationService.action_dismiss";
    public final static String ACTION_SNOOZE = "com.datasorcerers.reminder.NotificationService.action_snooze";

    private Context context;
    private Alarm alarm;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
        DatabaseHelper db = new DatabaseHelper(context);

        // get alarm
        alarm = intent.getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
        if (alarm == null) {
            alarm = db.getLatestNotified();
        }
        // check
        if (alarm == null) {
            return;
        }

        // act
        String action = intent.getAction();
        switch (action) {
            case ACTION_START:
                if (!alarm.isNotified()) {
                    alarm.makeNotified();
                    db.update(alarm);
                    Intent i = new Intent("update_list");
                    sendBroadcast(i);
                }
                if (Prefs.isNotifying(context)) {
                    hideAlert();
                }
                issueNotification();
                showAlert();
                startNotifying();
                break;
            case ACTION_DISMISS:
                Klaxon.stop();
                WakeLock.release();
                cancelNotification();
                hideAlert();
                Prefs.setNotifying(context, false);
                db.delete(alarm);
                next();
                break;
            case ACTION_SNOOZE:
                snooze();
                Klaxon.stop();
                WakeLock.release();
                cancelNotification();
                hideAlert();
                Prefs.setNotifying(context, false);
                break;
        }
        Intent i = new Intent("update_list");
        sendBroadcast(i);
    }

    private void next() {
        Intent i = new Intent(getApplicationContext(), NotificationService.class);
        i.setAction(ACTION_START);
        SystemClock.sleep(100);
        startService(i);
    }

    private void startNotifying() {
        // start sound and vibration
        Prefs.setNotifying(context, true);
        WakeLock.acquire(context);
        Klaxon.start(context);
        new CountDownTimer(TIMEOUT_TIME, TIMEOUT_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                Klaxon.stop();
                WakeLock.release();
                hideAlert();
                snooze();
                Looper.myLooper().quit();
            }
        }.start();
        Looper.loop();
    }

    private void snooze() {
        Intent i = new Intent(context, NotificationService.class);
        i.setAction(ACTION_START);
        PendingIntent p = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, new DateTime().getMillis() + SNOOZE_TIME, p);
    }

    private void issueNotification() {
        Notification.Builder nb = new Notification.Builder(context);

        Intent doneIntent = new Intent(getApplicationContext(), NotificationService.class);
        doneIntent.setAction(ACTION_DISMISS);
        PendingIntent donePendingIntent = PendingIntent.getService(getApplicationContext(), 0, doneIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeIntent = new Intent(getApplicationContext(), NotificationService.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        PendingIntent snoozePendingIntent = PendingIntent.getService(getApplicationContext(), 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        nb.setSmallIcon(R.mipmap.ic_launcher) // TODO icon
                .setContentTitle("Reminder")
                .setContentText(alarm.getNote())
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.ic_done_white_48dp, "DONE", donePendingIntent)
                .addAction(R.drawable.ic_update_white_48dp, "SNOOZE", snoozePendingIntent)
                .setVibrate(new long[0]);

        // issue
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT < 16) {
            nm.notify(1, nb.getNotification());
        } else {
            nm.notify(1, nb.build());
        }
    }

    private void cancelNotification() {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1);
    }

    private void showAlert() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            if (AlertActivity.instance == null) {
                Intent i = new Intent(context, AlertActivity.class);
                i.putExtra(Alarm.ALARM_EXTRA_NAME, alarm);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }
    }

    private void hideAlert() {
        if (AlertActivity.instance != null) {
            AlertActivity.instance.finish();
        }
    }

    private void updateAlert() {
        if (AlertActivity.instance != null) {
            Intent i = new Intent(getApplicationContext(), AlertActivity.class);
            i.putExtra(Alarm.ALARM_EXTRA_NAME, alarm);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
}

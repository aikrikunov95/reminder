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

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends IntentService {
    public final static long TIMEOUT_TIME = 2000;
    public final static long SNOOZE_TIME = 2000;

    public final static String ACTION_START = "action_start";
    public final static String ACTION_STOP = "action_stop";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        Alarm alarm = intent.getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
        AlarmManagerHelper am = new AlarmManagerHelper(context);
        DatabaseHelper db = new DatabaseHelper(context);

        if (!alarm.isNotified()) {
            alarm.setNotified(true);
            db.update(alarm);
        }
        ArrayList<Alarm> notified = (ArrayList<Alarm>) db.getAllNotified();
        for (int i=0; i < notified.size(); i++) {
            am.cancel(notified.get(i));
        }
        Klaxon.stop();
        WakeLock.release();

        String action = intent.getAction();
        switch (action) {
            case ACTION_START:
                issue(context, notified); // TODO all notified alarms
                am.set(new DateTime().getMillis() + TIMEOUT_TIME + SNOOZE_TIME, alarm);
                WakeLock.acquire(context);
                Klaxon.start(context);
                new CountDownTimer(TIMEOUT_TIME, TIMEOUT_TIME) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        Klaxon.stop();
                        WakeLock.release();
                        Looper.myLooper().quit();
                    }
                }.start();
                Looper.loop();
                break;
            case ACTION_STOP:
                Klaxon.stop();
                WakeLock.release();
                db.deleteAllNotified();
                // update list
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void issue(Context context, List<Alarm> notified) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        DatabaseHelper db = new DatabaseHelper(context);

        // make delete intent
        Intent deleteIntent = new Intent(context, NotificationService.class);
        deleteIntent.setAction(ACTION_STOP);
        deleteIntent.putExtra(Alarm.ALARM_EXTRA_NAME, notified.get(notified.size()-1));
        PendingIntent delete = PendingIntent.
                getService(context, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // builder
        Notification.Builder nb = new Notification.Builder(context);

        // if one alarm notified just show its note
        if (notified.size() == 1) {
            nb.setContentText(notified.get(0).getNote());
        } else {
            // show all notified alarms contents
            Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
            for (int i=0; i < notified.size(); i++) {
                Alarm a = notified.get(i);
                inboxStyle.addLine(a.getNote() + " " + a.getDatetime());
            }
            nb.setStyle(inboxStyle);
            nb.setContentText(notified.size() + " alarms went off");
        }

        // set remained stuff
        nb.setSmallIcon(R.mipmap.ic_launcher) // TODO icon
                .setContentTitle("Reminder")
                .setDeleteIntent(delete)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        //issue
        nm.notify(1, nb.build()); // TODO hardcode
    }
}

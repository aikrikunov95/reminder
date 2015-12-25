package com.datasorcerers.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.LinkedList;
import java.util.Queue;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_EXTRA_NAME = "com.datasorcerers.reminder.AlarmReceiver.ACTION_EXTRA_NAME";
    public static final int ACTION_ADD = 1;
    public static final int ACTION_POLL = 2;

    private static Queue<Intent> intentQueue = new LinkedList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra(ACTION_EXTRA_NAME, 0);

        switch (action) {
            case ACTION_ADD:
                if (intentQueue.isEmpty()) {
                    startNotificationService(context, intent);
                }
                intentQueue.add(intent);
                break;
            case ACTION_POLL:
                if (!intentQueue.isEmpty()) {
                    intentQueue.poll();
                }
                if (!intentQueue.isEmpty()) {
                    startNotificationService(context, intentQueue.peek());
                }
                break;
        }
    }

    private void startNotificationService(Context context, Intent intent) {
        Alarm alarm = intent.getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
        Intent notificationServiceIntent = new Intent(context, NotificationService.class);
        notificationServiceIntent.putExtra(NotificationService.ACTION_EXTRA_NAME, NotificationService.ACTION_ISSUE);
        notificationServiceIntent.putExtra(Alarm.ALARM_EXTRA_NAME, alarm);
        context.startService(notificationServiceIntent);
    }
}

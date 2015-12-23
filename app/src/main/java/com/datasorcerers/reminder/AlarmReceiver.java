package com.datasorcerers.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = intent.getParcelableExtra(Alarm.TAG);
        Log.d("AlarmReceiver", "received " + alarm.getNote());
        Intent notificationServiceIntent = new Intent(context, NotificationService.class);
        notificationServiceIntent.putExtra(Alarm.TAG, alarm);
        context.startService(notificationServiceIntent);
    }
}

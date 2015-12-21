package com.datasorcerers.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationServiceIntent = new Intent(context, NotificationService.class);
        notificationServiceIntent.putExtra("note", intent.getStringExtra("note"));
        context.startService(notificationServiceIntent);
    }
}

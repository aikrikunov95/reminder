package com.datasorcerers.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        AlarmManagerHelper amHelper = new AlarmManagerHelper(context);

        ArrayList<Alarm> alarms = (ArrayList<Alarm>) dbHelper.getAll();
        for (Alarm a : alarms) {
            DateTime dt = new DateTime(a.getDatetime());
            if (dt.isBeforeNow()) {
                amHelper.set(new DateTime().getMillis()+500, a, AlarmReceiver.ACTION_ADD);
                SystemClock.sleep(500);
            } else {
                amHelper.set(a, AlarmReceiver.ACTION_ADD);
            }
        }
    }
}

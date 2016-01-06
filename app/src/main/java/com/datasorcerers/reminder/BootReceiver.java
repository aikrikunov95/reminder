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
        DatabaseHelper db = new DatabaseHelper(context);
        AlarmManagerHelper am = new AlarmManagerHelper(context);

        ArrayList<Alarm> alarms = (ArrayList<Alarm>) db.getAll();
        for (Alarm a : alarms) {
            DateTime dt = new DateTime(a.getDatetime());
            if (dt.isBeforeNow()) {
                am.set(new DateTime().getMillis() + 500, a);
                SystemClock.sleep(500);
            } else {
                am.set(a.getDatetime(), a);
            }
        }
    }
}

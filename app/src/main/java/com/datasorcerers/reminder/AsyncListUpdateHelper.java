package com.datasorcerers.reminder;

import android.content.Context;
import android.content.Intent;

public class AsyncListUpdateHelper {
    private Context mContext;

    public AsyncListUpdateHelper(Context context) {
        this.mContext = context;
    }

    public void update(Alarm alarm, String action) {
        Intent i = new Intent(action);
        i.putExtra(Alarm.ALARM_EXTRA_NAME, alarm);
        mContext.sendBroadcast(i);
    }
}

package com.datasorcerers.reminder;

import android.content.Context;

public class AlarmCrudHelper {
    AlarmManagerHelper am;
    DatabaseHelper db;

    public AlarmCrudHelper(Context context) {
        this.am = new AlarmManagerHelper(context);
        this.db = new DatabaseHelper(context);
    }

    public Alarm create(Alarm alarm) {
        Alarm a = db.add(alarm.getNote(), alarm.getDatetime());
        am.set(alarm.getDatetime(), a);
        return a;
    }

    public void update(Alarm alarm) {
        db.update(alarm);
        am.cancel(alarm);
        am.set(alarm.getDatetime(), alarm);
    }

    public void delete(Alarm alarm) {
        db.delete(alarm);
        am.cancel(alarm);
    }
}

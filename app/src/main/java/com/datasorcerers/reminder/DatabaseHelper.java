package com.datasorcerers.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "reminder";

    // Alarms table name
    private static final String TABLE_ALARMS = "alarms";

    // Alarms Table Columns names
    private static final String KEY_NOTE= "note";
    private static final String KEY_DATETIME = "datetime";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARMS + "("
                + KEY_NOTE + " TEXT," + KEY_DATETIME + " INT,"
                + "UNIQUE(" + KEY_NOTE + ", " + KEY_DATETIME + ") ON CONFLICT IGNORE)";
        db.execSQL(CREATE_ALARMS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);

        // Create tables again
        onCreate(db);
    }

    public void add(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, alarm.getNote());
        values.put(KEY_DATETIME, alarm.getDatetime());

        db.insert(TABLE_ALARMS, null, values);
        db.close();
    }

    public List<Alarm> getAll() {
        List<Alarm> alarms = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS + " ORDER BY " + KEY_DATETIME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String note = cursor.getString(0);
                long date = cursor.getLong(1);
                Alarm alarm = new Alarm(note, date);
                alarms.add(alarm);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return alarms;
    }

    public int update(Alarm oldAlarm, Alarm newAlarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, newAlarm.getNote());
        values.put(KEY_DATETIME, newAlarm.getDatetime());

        return db.update(TABLE_ALARMS, values, KEY_NOTE + " = ? AND " + KEY_DATETIME +  " = ?",
                new String[] { String.valueOf(oldAlarm.getNote()), String.valueOf(oldAlarm.getDatetime()) });
    }

    public void delete(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, KEY_NOTE + " = ? AND " + KEY_DATETIME + " = ?",
                new String[]{String.valueOf(alarm.getNote()), String.valueOf(alarm.getDatetime())});
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, null, null);
        db.close();
    }
}

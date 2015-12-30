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
    private static final String KEY_ID = "_id";
    private static final String KEY_NOTE = "note";
    private static final String KEY_DATETIME = "datetime";
    private static final String KEY_NOTIFIED = "notified";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARMS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NOTE + " TEXT, " +
                KEY_DATETIME + " INT, " +
                KEY_NOTIFIED + " INT, " +
                "UNIQUE (" + KEY_NOTE + ", " + KEY_DATETIME + ") ON CONFLICT IGNORE)";
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

    public Alarm add(String note, long datetime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, note);
        values.put(KEY_DATETIME, datetime);
        values.put(KEY_NOTIFIED, 0);

        db.insert(TABLE_ALARMS, null, values);
        db.close();

        return get(note, datetime);
    }

    public Alarm get(String note, long datetime) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = new String[]{KEY_ID, KEY_NOTE, KEY_DATETIME, KEY_NOTIFIED};
        String[] values = new String[]{String.valueOf(note), String.valueOf(datetime)};
        Cursor cursor = db.query(TABLE_ALARMS, columns,
                KEY_NOTE + " = ? AND " + KEY_DATETIME + " = ?",
                values, null, null, null, null);
        if (cursor.moveToFirst()) {
            return new Alarm(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getLong(2),
                cursor.getInt(3) != 0
            );
        }
        cursor.close();
        db.close();

        return null;
    }

    public List<Alarm> getAll() {
        List<Alarm> alarms = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ALARMS +
                " ORDER BY " + KEY_DATETIME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String note = cursor.getString(1);
                long date = cursor.getLong(2);
                boolean notified = cursor.getInt(0) != 0;
                Alarm alarm = new Alarm(id, note, date, notified);
                alarms.add(alarm);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return alarms;
    }

    public List<Alarm> getAllNotified() {
        List<Alarm> alarms = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ALARMS +
                " WHERE " + KEY_NOTIFIED + " = 1" +
                " ORDER BY " + KEY_DATETIME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String note = cursor.getString(1);
                long date = cursor.getLong(2);
                boolean notified = cursor.getInt(0) != 0;
                Alarm alarm = new Alarm(id, note, date, notified);
                alarms.add(alarm);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return alarms;
    }

    public int update(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, alarm.getNote());
        values.put(KEY_DATETIME, alarm.getDatetime());
        values.put(KEY_NOTIFIED, alarm.isNotified() ? 1 : 0);

        return db.update(TABLE_ALARMS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(alarm.getId()) });
    }

    public void delete(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, KEY_ID + " = ?",
                new String[]{String.valueOf(alarm.getId())});
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, null, null);
        db.close();
    }

    public void deleteAllNotified() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, KEY_NOTIFIED + " = 1", null);
        db.close();
    }

}

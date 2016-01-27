package com.datasorcerers.reminder;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {

    public static final String ALARM_EXTRA_NAME = "com.datasorcerers.reminder.Alarm.ALARM_EXTRA_NAME";

    private int id;
    private String note;
    private long datetime;
    private boolean notified;

    public Alarm(int id, String note, long datetime, boolean notified) {
        this.id = id;
        this.note = note;
        this.datetime = datetime;
        this.notified = notified;
    }

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public boolean isNotified() {
        return notified;
    }

    public void makeNotified() {
        this.notified = true;
    }

    @Override
    public String toString() {
        return note + " " + String.valueOf(datetime);
    }

    // Parcelable stuff

    private Alarm(Parcel in) {
        this.id = in.readInt();
        this.note = in.readString();
        this.datetime = in.readLong();
        this.notified = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(note);
        dest.writeLong(datetime);
        dest.writeByte((byte) (notified ? 1 : 0));
    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel source) {
            return new Alarm(source);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };
}

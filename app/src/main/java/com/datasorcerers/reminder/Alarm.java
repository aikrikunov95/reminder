package com.datasorcerers.reminder;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm implements Parcelable {

    public static final String TAG = "alarm";

    private String note;
    private long datetime;

    public Alarm() {
        this.note = "";
        this.datetime = 0;
    }

    public Alarm(String note, long datetime) {
        this.note = note;
        this.datetime = datetime;
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

    @Override
    public String toString() {
        return note + " " + String.valueOf(datetime);
    }

    // Parcelable stuff

    private Alarm(Parcel in) {
        this.note = in.readString();
        this.datetime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(note);
        dest.writeLong(datetime);
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

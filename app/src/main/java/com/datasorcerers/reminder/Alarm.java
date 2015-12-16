package com.datasorcerers.reminder;

public class Alarm {

    private String note;
    private long date;

    public Alarm() {
        this.note = "";
        this.date = 0;
    }

    public Alarm(String note, long date) {
        this.note = note;
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return note + " " + String.valueOf(date);
    }
}

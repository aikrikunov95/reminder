package com.datasorcerers.reminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.joda.time.DateTime;

public class EditActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText note;
    private EditText date;
    private EditText time;
    private Toolbar toolbar;
    private InputMethodManager imm;

    private Alarm alarmToUpdate;
    private String newAlarmNote;
    private DateTime newAlarmDatetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Data

        alarmToUpdate = getIntent().getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
        newAlarmDatetime = new DateTime();
        if (alarmToUpdate != null) {
            newAlarmNote = alarmToUpdate.getNote();
            newAlarmDatetime = new DateTime(alarmToUpdate.getDatetime());
        }

        // UI

        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        note = (EditText) findViewById(R.id.note);
        note.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        note.setText(newAlarmNote);
        note.requestFocus();

        date = (EditText) findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(note.getWindowToken(), 0);

                // Prepare default data for picker
                Bundle bundle = new Bundle();
                DateTime bundleDatetime = new DateTime();
                if (alarmToUpdate != null) {
                    bundleDatetime = new DateTime(alarmToUpdate.getDatetime());
                }
                bundle.putInt("year", bundleDatetime.getYear());
                bundle.putInt("month", bundleDatetime.getMonthOfYear() - 1);
                bundle.putInt("day", bundleDatetime.getDayOfMonth());

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        String d = newAlarmDatetime.getYear() + "." + newAlarmDatetime.getMonthOfYear() + "." + newAlarmDatetime.getDayOfMonth();
        date.setText(d);

        time = (EditText) findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(note.getWindowToken(), 0);

                // Prepare data for picker
                Bundle bundle = new Bundle();
                DateTime bundleDatetime = new DateTime();
                if (alarmToUpdate != null) {
                    bundleDatetime = new DateTime(alarmToUpdate.getDatetime());
                }
                bundle.putInt("hour", bundleDatetime.getHourOfDay());
                bundle.putInt("minute", bundleDatetime.getMinuteOfHour());

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        String t = newAlarmDatetime.getHourOfDay()+":"+newAlarmDatetime.getMinuteOfHour();
        time.setText(t);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        newAlarmDatetime = newAlarmDatetime
                .withYear(year)
                .withMonthOfYear(monthOfYear+1)
                .withDayOfMonth(dayOfMonth);
        String s = dayOfMonth+"."+(monthOfYear+1)+"."+year;
        date.setText(s);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        newAlarmDatetime = newAlarmDatetime
                .withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute);
        String s = hourOfDay+":"+minute;
        time.setText(s);
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle bundle = this.getArguments();

            int year = bundle.getInt("year");
            int month = bundle.getInt("month");
            int day = bundle.getInt("day");

            return new DatePickerDialog(getActivity(), (EditActivity)getActivity(), year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle bundle = this.getArguments();

            int hour = bundle.getInt("hour");
            int minute = bundle.getInt("minute");

            return new TimePickerDialog(getActivity(), (EditActivity)getActivity(), hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
    }
}

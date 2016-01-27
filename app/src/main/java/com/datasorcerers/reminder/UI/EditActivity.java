package com.datasorcerers.reminder.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.datasorcerers.reminder.Alarm;
import com.datasorcerers.reminder.AlarmManagerHelper;
import com.datasorcerers.reminder.DatabaseHelper;
import com.datasorcerers.reminder.R;

import org.joda.time.DateTime;

public class EditActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText noteText;
    private TextView dateText;
    private TextView timeText;
    private Toolbar toolbar;
    private Button readyButton;
    private Button closeButton;
    private InputMethodManager imm;

    private Alarm alarm;
    private String note;
    private DateTime datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // data

        alarm = getIntent().getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
        if (alarm != null) {
            note = alarm.getNote();
            datetime = new DateTime(alarm.getDatetime());
        } else {
            note = "";
            datetime = new DateTime();
        }

        // UI

        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        noteText = (EditText) findViewById(R.id.note_text);
        noteText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        noteText.setText(note);
        noteText.requestFocus();

        dateText = (TextView) findViewById(R.id.date_text);
        dateText.setPaintFlags(dateText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prepare default data for picker
                Bundle bundle = new Bundle();
                DateTime bundleDatetime = new DateTime();
                if (alarm != null) {
                    bundleDatetime = new DateTime(alarm.getDatetime());
                }
                bundle.putInt("year", bundleDatetime.getYear());
                bundle.putInt("month", bundleDatetime.getMonthOfYear() - 1);
                bundle.putInt("day", bundleDatetime.getDayOfMonth());

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        dateText.setText(DateTimeFormatter.formatDate(datetime));

        timeText = (TextView) findViewById(R.id.time_text);
        timeText.setPaintFlags(timeText.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prepare data for picker
                Bundle bundle = new Bundle();
                DateTime bundleDatetime = new DateTime();
                if (alarm != null) {
                    bundleDatetime = new DateTime(alarm.getDatetime());
                }
                bundle.putInt("hour", bundleDatetime.getHourOfDay());
                bundle.putInt("minute", bundleDatetime.getMinuteOfHour());

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        timeText.setText(DateTimeFormatter.formatTime(datetime));

        readyButton = (Button) findViewById(R.id.ready_button);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                AlarmManagerHelper am = new AlarmManagerHelper(getApplicationContext());
                note = noteText.getText().toString();
                if (alarm == null) {
                    alarm = db.create(note, datetime.getMillis());
                    am.set(alarm);
                } else {
                    db.update(alarm);
                    am.update(alarm);
                }
                if (ListActivity.instance != null) {
                    ((ListActivity) ListActivity.instance).updateList();
                }
                hideKeyboard();
                finish();
            }
        });

        closeButton = (Button) findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        datetime = datetime
                .withYear(year)
                .withMonthOfYear(monthOfYear+1)
                .withDayOfMonth(dayOfMonth);
        dateText.setText(DateTimeFormatter.formatDate(datetime));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        datetime = datetime
                .withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        timeText.setText(DateTimeFormatter.formatTime(datetime));
    }

    private void hideKeyboard() {
        imm.hideSoftInputFromWindow(noteText.getWindowToken(), 0);
        SystemClock.sleep(400);
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

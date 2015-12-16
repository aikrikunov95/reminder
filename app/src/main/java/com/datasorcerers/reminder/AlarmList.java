package com.datasorcerers.reminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.util.Calendar;

public class AlarmList extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Alarm newAlarm;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_list);

        recyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        adapter = new AlarmListAdapter(databaseHelper.getAll());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                databaseHelper.delete(((AlarmListAdapter)adapter).get(position));
                ((AlarmListAdapter)adapter).removeItemAt(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public void addAlarm(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Description");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                newAlarm = new Alarm();
                newAlarm.setNote(input.getText().toString());
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        DateTime dt = new DateTime();
        dt = dt.withDate(year, monthOfYear, dayOfMonth);
        newAlarm.setDate(dt.getMillis());
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        DateTime dt = new DateTime(newAlarm.getDate());
        dt = dt.withHourOfDay(hourOfDay);
        dt = dt.withMinuteOfHour(minute);
        newAlarm.setDate(dt.getMillis());

        databaseHelper.add(newAlarm);
        ((AlarmListAdapter) adapter).add(newAlarm);
        newAlarm = null;
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), (AlarmList)getActivity(), year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), (AlarmList)getActivity(), hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
    }
}


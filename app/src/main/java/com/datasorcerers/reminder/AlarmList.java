package com.datasorcerers.reminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

    public static final String UPDATE_UI_ACTION = "com.datasorcerers.reminder.AlarmList.UPDATE_UI";
    private static final String PICKERS_DATETIME_BUNDLE_NAME = "com.datasorcerers.reminder.AlarmList.PICKERS_DATETIME_BUNDLE_NAME";
    private static final String DATEPICKER_TAG = "com.datasorcerers.reminder.AlarmList.DATEPICKER_TAG";
    private static final String TIMEPICKER_TAG = "com.datasorcerers.reminder.AlarmList.DATEPICKER_TAG";

    // UI
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;

    // service
    private BroadcastReceiver receiver;
    private DatabaseHelper dbHelper;
    private AlarmManagerHelper amHelper;

    // add and edit workflow
    private Alarm newAlarm;
    private Alarm oldAlarm;
    private Alarm deleteAlarm;
    private boolean editAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_list);

        // recyclerview setup
        recyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // db and alarmmanager helpers setup
        dbHelper = new DatabaseHelper(getApplicationContext());
        amHelper = new AlarmManagerHelper(this);

        // list adapter setup
        adapter = new AlarmListAdapter(dbHelper.getAll(), new AlarmListAdapter.ViewHolder.IViewHolderClick() {
            @Override
            public void showEditDialog(View caller, int position) {
                editAlarm = true;
                oldAlarm = ((AlarmListAdapter) adapter).get(position);
                createAlarm(caller);
            }
        });
        recyclerView.setAdapter(adapter);

        // create alarm on button click
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAlarm = false;
                createAlarm(v);
            }
        });

        // delete on list item swipe
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
                deleteAlarm = ((AlarmListAdapter) adapter).get(position);
                dbHelper.delete(deleteAlarm);
                amHelper.cancel(deleteAlarm);
                ((AlarmListAdapter) adapter).removeItemAt(position); // TODO cancel action
                deleteAlarm = null;
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // delete on notification dismiss
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                deleteAlarm = intent.getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
                ((AlarmListAdapter) adapter).remove(deleteAlarm);
                deleteAlarm = null;
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_UI_ACTION);
        registerReceiver(receiver, filter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /*
    * starts add and edit workflow
    */
    public void createAlarm(View v) {
        // create note input dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Note");
        final EditText input = new EditText(this);
        if (oldAlarm != null) { // if edit, set oldalarm note as default
            input.append(oldAlarm.getNote());
        }
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newAlarm = new Alarm();
                newAlarm.setNote(input.getText().toString());
                // Create a bundle to pass the date
                Bundle bundle = new Bundle();
                if (oldAlarm != null) {
                    bundle.putLong(PICKERS_DATETIME_BUNDLE_NAME, oldAlarm.getDatetime());
                }
                DialogFragment newFragment = new DatePickerFragment();
                // Pass bundle to picker
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), DATEPICKER_TAG);
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
        dt = dt.withDate(year, monthOfYear+1, dayOfMonth);
        newAlarm.setDatetime(dt.getMillis());
        // Create a bundle to pass the date
        Bundle bundle = new Bundle();
        if (oldAlarm != null) {
            bundle.putLong(PICKERS_DATETIME_BUNDLE_NAME, oldAlarm.getDatetime());
        }
        // Pass bundle to picker
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), TIMEPICKER_TAG);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        DateTime dt = new DateTime(newAlarm.getDatetime());
        dt = dt.withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        //dt = new DateTime().plus(2000); // TODO REMOVE!!!
        newAlarm.setDatetime(dt.getMillis());

        if (dt.isAfterNow()) {
            if (editAlarm) {
                // update db
                dbHelper.update(oldAlarm, newAlarm);
                // remove old alarm
                amHelper.cancel(oldAlarm);
                ((AlarmListAdapter) adapter).remove(oldAlarm);
                // add new alarm
                amHelper.set(newAlarm, AlarmReceiver.ACTION_ADD);
                ((AlarmListAdapter) adapter).add(newAlarm);
            } else {
                // create new alarm
                dbHelper.add(newAlarm);
                amHelper.set(newAlarm, AlarmReceiver.ACTION_ADD);
                ((AlarmListAdapter) adapter).add(newAlarm);
            }
        }
        // clear variables
        oldAlarm = null;
        newAlarm = null;
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle bundle = this.getArguments();
            Long datetime = bundle.getLong(PICKERS_DATETIME_BUNDLE_NAME);

            int year, month, day;
            if (datetime != 0) { // if edit, set oldAlarm data as default
                DateTime dt = new DateTime(datetime);
                year = dt.getYear();
                month = dt.getMonthOfYear()-1;
                day = dt.getDayOfMonth();
            } else { // otherwise now
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }

            return new DatePickerDialog(getActivity(), (AlarmList)getActivity(), year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle bundle = this.getArguments();
            Long datetime = bundle.getLong(PICKERS_DATETIME_BUNDLE_NAME);

            int hour, minute;
            if (datetime != 0) { // if edit, set oldAlarm data as default
                DateTime dt = new DateTime(datetime);
                hour = dt.getHourOfDay();
                minute = dt.getMinuteOfHour();
            } else { // otherwise now
                final Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }

            return new TimePickerDialog(getActivity(), (AlarmList)getActivity(), hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
    }
}


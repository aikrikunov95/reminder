package com.datasorcerers.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    public static final String UPDATE_LIST_ACTION_DELETE_NOTIFIED = "com.datasorcerers.reminder.ListActivity.DELETE_NOTIFIED";
    public static final String UPDATE_LIST_ACTION_UPDATE_ALARM = "com.datasorcerers.reminder.ListActivity.UPDATE_ALARM";
    public static final String UPDATE_LIST_ACTION_CREATE_ALARM = "com.datasorcerers.reminder.ListActivity.CREATE_ALARM";
    public static final String UPDATE_LIST_ACTION_ALARM_MISSED = "com.datasorcerers.reminder.ListActivity.ALARM_MISSED";

    // UI
    private RecyclerView recyclerView;
    private SectionedListAdapter adapter;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    // service
    private BroadcastReceiver receiver;
    private DatabaseHelper db;
    private AlarmCrudHelper crud;

    private Alarm deleteAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db = new DatabaseHelper(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        // recycler view setup
        recyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // list adapter
        adapter = new SectionedListAdapter(this,R.layout.list_section,R.id.section_text, db.getAll(),
                new ListAdapter.ViewHolder.AlarmClickListener() {
            @Override
            public void onAlarmClick(View caller, int position) {
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                    i.putExtra(Alarm.ALARM_EXTRA_NAME, adapter.get(adapter.sectionedPositionToPosition(position)));
                startActivity(i);
            }
        });

        // delete on list item swipe
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof SectionedListAdapter.SectionViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = adapter.sectionedPositionToPosition(
                        viewHolder.getAdapterPosition());
                Alarm alarm = adapter.get(position);
                crud.delete(alarm);
                adapter.remove(alarm);
                // TODO show cancel action toast
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);

        crud = new AlarmCrudHelper(this, adapter);

        // create alarm on button click
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(i);
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Alarm alarm = intent.getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
                switch (action) {
                    case UPDATE_LIST_ACTION_DELETE_NOTIFIED:
                        ArrayList<Alarm> notified = (ArrayList<Alarm>) db.getAllNotified();
                        for (int i = 0; i < notified.size(); i++) {
                            Alarm a = notified.get(i);
                            crud.delete(a);
                            adapter.remove(a);
                        }
                        break;
                    case UPDATE_LIST_ACTION_UPDATE_ALARM:
                        crud.update(alarm);
                        adapter.updateItem(alarm);
                        adapter.notifyAlarmsDataSetChanged();
                        break;
                    case UPDATE_LIST_ACTION_CREATE_ALARM:
                        alarm = crud.create(alarm);
                        adapter.add(alarm);
                        break;
                    case UPDATE_LIST_ACTION_ALARM_MISSED:
                        adapter.updateItem(alarm);
                        adapter.notifyAlarmsDataSetChanged();
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_LIST_ACTION_DELETE_NOTIFIED);
        filter.addAction(UPDATE_LIST_ACTION_UPDATE_ALARM);
        filter.addAction(UPDATE_LIST_ACTION_CREATE_ALARM);
        filter.addAction(UPDATE_LIST_ACTION_ALARM_MISSED);
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
}


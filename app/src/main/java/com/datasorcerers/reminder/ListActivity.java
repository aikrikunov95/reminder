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

    // UI
    private RecyclerView recyclerView;
    private SectionedListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    // service
    private BroadcastReceiver receiver;
    private DatabaseHelper db;
    private AlarmManagerHelper am;

    private Alarm deleteAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        // recyclerview setup
        recyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // db and alarmmanager helpers setup
        db = new DatabaseHelper(getApplicationContext());
        am = new AlarmManagerHelper(this);

        // list adapter
        adapter = new SectionedListAdapter(this,R.layout.list_section,R.id.section_text, db.getAll(),
                new ListAdapter.ViewHolder.AlarmClickListener() {
            @Override
            public void onAlarmClick(View caller, int position) {
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                if (adapter != null) {
                    i.putExtra(Alarm.ALARM_EXTRA_NAME, adapter.get(adapter.sectionedPositionToPosition(position)));
                }
                startActivity(i);
            }
        });
        adapter.setSections();
        recyclerView.setAdapter(adapter);

        // create alarm on button click
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
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
                deleteAlarm = adapter.get(position);
                db.delete(deleteAlarm);
                am.cancel(deleteAlarm);
                adapter.removeItemAt(position);
                deleteAlarm = null;
                // TODO show cancel action toast
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // delete on notification dismiss
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Alarm alarm = intent.getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
                switch (action) {
                    case UPDATE_LIST_ACTION_DELETE_NOTIFIED:
                        ArrayList<Alarm> notified = (ArrayList<Alarm>) db.getAllNotified();
                        for (int i = 0; i < notified.size(); i++) {
                            deleteAlarm = notified.get(i);
                            db.delete(deleteAlarm);
                            adapter.remove(deleteAlarm);
                        }
                        break;
                    case UPDATE_LIST_ACTION_UPDATE_ALARM:
                        db.update(alarm);
                        am.cancel(alarm);
                        am.set(alarm.getDatetime(), alarm);
                        adapter.updateItem(alarm);
                        adapter.notifyAlarmsDataSetChanged();
                        break;
                    case UPDATE_LIST_ACTION_CREATE_ALARM:
                        am.set(alarm.getDatetime(), alarm);
                        adapter.add(alarm);
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_LIST_ACTION_DELETE_NOTIFIED);
        filter.addAction(UPDATE_LIST_ACTION_UPDATE_ALARM);
        filter.addAction(UPDATE_LIST_ACTION_CREATE_ALARM);
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


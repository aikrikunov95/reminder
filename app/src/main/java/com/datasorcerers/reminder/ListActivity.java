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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ListActivity extends AppCompatActivity {

    public static final String UPDATE_UI_ACTION = "com.datasorcerers.reminder.ListActivity.UPDATE_UI";

    // UI
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fab;

    // service
    private BroadcastReceiver receiver;
    private DatabaseHelper dbHelper;
    private AlarmManagerHelper amHelper;

    private Alarm deleteAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // recyclerview setup
        recyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // db and alarmmanager helpers setup
        dbHelper = new DatabaseHelper(getApplicationContext());
        amHelper = new AlarmManagerHelper(this);

        // list adapter, edit click listener setup
        adapter = new ListAdapter(dbHelper.getAll(), new ListAdapter.ViewHolder.IViewHolderClick() {
            @Override
            public void showEditDialog(View caller, int position) {
                // TODO start alarm edit activity
            }
        });
        recyclerView.setAdapter(adapter);

        // create alarm on button click
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO start alarm edit activity
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
                deleteAlarm = ((ListAdapter) adapter).get(position);
                dbHelper.delete(deleteAlarm);
                amHelper.cancel(deleteAlarm);
                ((ListAdapter) adapter).removeItemAt(position);
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
                // TODO delete dismissed alarms
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
}


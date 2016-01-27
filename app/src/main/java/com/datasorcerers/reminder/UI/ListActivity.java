package com.datasorcerers.reminder.ui;

import android.content.Intent;
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

import com.datasorcerers.reminder.Alarm;
import com.datasorcerers.reminder.AlarmManagerHelper;
import com.datasorcerers.reminder.DatabaseHelper;
import com.datasorcerers.reminder.R;

public class ListActivity extends AppCompatActivity {

    public static AppCompatActivity instance;
    
    // ui
    private RecyclerView recyclerView;
    private SectionedListAdapter adapter;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        instance = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        // recycler view setup
        recyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final DatabaseHelper db = new DatabaseHelper(getApplicationContext());

        // list adapter
        adapter = new com.datasorcerers.reminder.ui.SectionedListAdapter(this,R.layout.list_section,R.id.section_text, db.getAll(),
                new com.datasorcerers.reminder.ui.ListAdapter.ViewHolder.AlarmClickListener() {
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
                if (viewHolder instanceof com.datasorcerers.reminder.ui.SectionedListAdapter.SectionViewHolder) return 0;
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
                adapter.remove(alarm);
                db.delete(alarm);
                new AlarmManagerHelper(getApplicationContext()).cancel(alarm);
                // TODO show cancel action toast
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
    }

    public void updateList() {
        adapter.refresh();
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
        instance = null;
    }
}


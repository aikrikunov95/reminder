package com.datasorcerers.reminder;

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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    // UI
    private RecyclerView recyclerView;
    private SectionedListAdapter adapter;
    private FloatingActionButton fab;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);

        // recycler view setup
        recyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Alarm> data = new ArrayList<>();
        data.add(new Alarm(0, "Позвонить маме", new DateTime().getMillis()));
        data.add(new Alarm(1, "Выбросить мусор", new DateTime().getMillis()));
        data.add(new Alarm(2, "Позвонить папке", new DateTime().getMillis()));
        data.add(new Alarm(3, "Сходить в магазин", new DateTime().plusDays(1).getMillis()));
        data.add(new Alarm(4, "Постирать одежду", new DateTime().plusDays(1).getMillis()));
        data.add(new Alarm(5, "Постирать одежду", new DateTime().plusDays(1).getMillis()));

        // list adapter
        adapter = new SectionedListAdapter(this,R.layout.list_section,R.id.section_text, data,
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
                adapter.remove(alarm);
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
}


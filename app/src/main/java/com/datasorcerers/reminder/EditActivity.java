package com.datasorcerers.reminder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.joda.time.DateTime;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        EditText noteEdit = (EditText) findViewById(R.id.note);
        EditText dateEdit = (EditText) findViewById(R.id.date);
        EditText timeEdit = (EditText) findViewById(R.id.time);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManagerHelper am = new AlarmManagerHelper(getApplicationContext());
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());

                db.deleteAll();

                Alarm a = db.add("A", new DateTime().getMillis() + 1000);
                am.set(a.getDatetime(), a);

                Alarm b = db.add("B", new DateTime().getMillis() + 10000);
                am.set(b.getDatetime(), b);


            }
        });
    }

}

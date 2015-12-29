package com.datasorcerers.reminder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.joda.time.DateTime;

public class AlarmActivity extends AppCompatActivity {

    private String note;
    private DateTime datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        initData();

        EditText noteEdit = (EditText) findViewById(R.id.note);
        EditText dateEdit = (EditText) findViewById(R.id.date);
        EditText timeEdit = (EditText) findViewById(R.id.time);


    }

}

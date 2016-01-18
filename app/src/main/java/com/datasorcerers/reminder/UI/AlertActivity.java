package com.datasorcerers.reminder.UI;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.datasorcerers.reminder.Alarm;
import com.datasorcerers.reminder.R;

public class AlertActivity extends AppCompatActivity {

    private TextView note;
    private FloatingActionButton dismiss;
    private FloatingActionButton snooze;

    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_alert);

        note = (TextView) findViewById(R.id.alert_note);
        dismiss = (FloatingActionButton) findViewById(R.id.alert_dismiss);
        snooze = (FloatingActionButton) findViewById(R.id.alert_snooze);

        alarm = getIntent().getParcelableExtra(Alarm.ALARM_EXTRA_NAME);

        note.setText(alarm.getNote());
    }

    public void dismiss(View v) {
        finish();
    }

    public void snooze(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
    }
}

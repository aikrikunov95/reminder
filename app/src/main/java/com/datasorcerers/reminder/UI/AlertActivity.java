package com.datasorcerers.reminder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.datasorcerers.reminder.Alarm;
import com.datasorcerers.reminder.NotificationService;
import com.datasorcerers.reminder.R;

    public class AlertActivity extends AppCompatActivity {

        public static AppCompatActivity instance;

        private TextView note;

        private Alarm alarm;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_alert);

        note = (TextView) findViewById(R.id.alert_note);
        alarm = getIntent().getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
        note.setText(alarm.getNote());
    }

    public void dismiss(View v) {
        Intent i = new Intent(getApplicationContext(), NotificationService.class);
        i.setAction(NotificationService.ACTION_DISMISS);
        startService(i);
        finish();
    }

    public void snooze(View v) {
        Intent i = new Intent(getApplicationContext(), NotificationService.class);
        i.setAction(NotificationService.ACTION_SNOOZE);
        startService(i);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Alarm alarm = intent.getParcelableExtra(Alarm.ALARM_EXTRA_NAME);
        note.setText(alarm.getNote());
    }

    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}

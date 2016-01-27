package com.datasorcerers.reminder;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

public class Klaxon {

    private static Ringtone ringtone;
    private static Vibrator vibrator;

    private static final long[] sVibratePattern = {500, 500};

    private static Ringtone getRingtone(Context context) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        return RingtoneManager.getRingtone(context.getApplicationContext(), uri);
    }

    private static Vibrator getVibrator(Context context) {
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static void start(Context context) {
        if (ringtone != null && vibrator != null) {
            return;
        }

        ringtone = getRingtone(context);
        vibrator = getVibrator(context);

        vibrator.vibrate(sVibratePattern, 0);
        ringtone.play();
    }

    public static void stop() {
        if (ringtone != null  && vibrator != null) {
            ringtone.stop();
            vibrator.cancel();
            ringtone = null;
            vibrator = null;
        }
    }
}

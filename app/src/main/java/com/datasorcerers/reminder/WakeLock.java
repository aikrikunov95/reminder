package com.datasorcerers.reminder;

import android.content.Context;
import android.os.PowerManager;

public class WakeLock {

    private static PowerManager.WakeLock wakeLock;

    private static PowerManager.WakeLock createWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(
                (PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.FULL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP), "WakeLock");
    }

    public static void acquire(Context context) {
        if (wakeLock != null) {
            return;
        }

        wakeLock = createWakeLock(context);
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}

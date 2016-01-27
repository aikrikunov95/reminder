package com.datasorcerers.reminder;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private static final String PREFS_NAME = "com.datasorcerers.reminder.prefs.APP_PREFERENCES_NAME";
    private static final String PREF_NOTIFYING = "com.datasorcerers.reminder.prefs.PREF_NOTIFYING";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isNotifying(Context context) {
        SharedPreferences prefs = getPrefs(context);
        return prefs.getBoolean(PREF_NOTIFYING, false);
    }

    public static void setNotifying(Context context, boolean b) {
        SharedPreferences prefs = getPrefs(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_NOTIFYING, b); // TODO hardcode
        editor.commit();
    }
}

package com.raftls.running.app.utils;

import android.content.Context;

import com.raftls.running.R;

public class DateUtils {
    public static String getDurationToString(Context context, float duration) {
        return getDurationToString(context, duration, R.plurals.hour, R.plurals.minute, R.plurals.second);
    }

    public static String getDurationToFullString(Context context, float duration) {
        return getDurationToString(context, duration, R.plurals.hour_full, R.plurals.minute_full, R.plurals.second_full);
    }

    private static String getDurationToString(Context context, float duration, int hourStringId, int minuteStringId, int secondStringId) {
        int hours = floor(duration / 3600);
        int minutes = floor((duration % 3600) / 60);
        int seconds = floor(duration % 60);
        String stringHours = getQuantityString(context, hourStringId, hours);
        String stringMinutes = getQuantityString(context, minuteStringId, minutes);
        String stringSeconds = getQuantityString(context, secondStringId, seconds);
        return stringHours + stringMinutes + stringSeconds;
    }

    private static String getQuantityString(Context context, int resourceId, int quantity) {
        if (quantity == 0) return "";
        return context.getResources().getQuantityString(resourceId, quantity, quantity);
    }

    private static int floor(float a) {
        return (int) Math.floor(a);
    }
}

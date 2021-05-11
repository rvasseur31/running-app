package com.raftls.running.app.utils;

import android.content.Context;

import com.raftls.running.R;

public class DateUtils {
    public static String getDurationToString(Context context, float duration) {
        int hours = floor(duration / 3600);
        int minutes = floor((duration % 3600) / 60);
        int seconds = floor(duration % 60);
        String stringHours  = hours == 0 ? "" : hours + context.getString(R.string.hour) + " ";
        String stringMinutes = minutes == 0 ? "" : minutes + context.getString(R.string.minut) + " ";
        return stringHours + stringMinutes + seconds + context.getString(R.string.second);
    }

    private static int floor(float a) {
        return (int) Math.floor(a);
    }
}

package com.raftls.running.app.utils;

import android.content.Context;

import com.raftls.running.R;

public class DateUtils {
    public static String getDurationToString(Context context, long duration) {
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;
        return hours + context.getString(R.string.hour) + " " +
                minutes + context.getString(R.string.minut) + " " +
                seconds + context.getString(R.string.second);
    }
}

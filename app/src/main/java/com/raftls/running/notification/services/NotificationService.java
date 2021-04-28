package com.raftls.running.notification.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.raftls.running.R;
import com.raftls.running.app.activities.MainActivity;
import com.raftls.running.notification.models.ENotificationType;
import com.raftls.running.notification.models.NotificationContent;

import java.util.HashMap;

public class NotificationService {
    public static final HashMap<ENotificationType, NotificationContent> notifications = new HashMap<>();
    private static NotificationService instance;

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    private NotificationService() {
        notifications.put(ENotificationType.SAVING_RUN, new NotificationContent(1,
                R.string.saving_run_notification_title, R.string.saving_run_notification_description,
                R.string.saving_run_channel_id, R.string.saving_run_channel_title,
                R.string.saving_run_channel_description));
    }

    public void createNotification(Context context, ENotificationType notificationType) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationContent notificationContent = notifications.get(notificationType);
        createNotificationChannel(context, context.getString(notificationContent.getChannelId()), context.getString(notificationContent.getChannelTitle()), context.getString(notificationContent.getChannelDescription()));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(notificationContent.getChannelId()))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(context.getString(notificationContent.getNotificationTitle()))
                .setContentText(context.getString(notificationContent.getNotificationDescription()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        fireNotification(context, notificationContent.getNotificationId(), builder);
    }

    private void fireNotification(Context context, int notificationId, NotificationCompat.Builder builder) {
        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }

    private void createNotificationChannel(Context context, String channelId, CharSequence channelTitle, String channelDescription) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelTitle, importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
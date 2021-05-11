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

import org.jetbrains.annotations.NotNull;

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

    public NotificationCompat.Builder createNotificationBuilder(Context context, @NotNull NotificationContent notificationContent) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        createNotificationChannel(context, context.getString(notificationContent.getChannelId()), context.getString(notificationContent.getChannelTitle()), context.getString(notificationContent.getChannelDescription()));
        return new NotificationCompat.Builder(context, context.getString(notificationContent.getChannelId()))
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(context.getString(notificationContent.getNotificationTitle()))
                .setContentText(context.getString(notificationContent.getNotificationDescription()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    public void createNotification(Context context, ENotificationType notificationType) {
        NotificationContent notificationContent = notifications.get(notificationType);
        if (notificationContent != null) {
            NotificationCompat.Builder builder = createNotificationBuilder(context, notificationContent);
            fireNotification(context, notificationContent.getNotificationId(), builder);
        }
    }

    public void createNotificationWithCustomBody(Context context, ENotificationType notificationType, String customText) {
        NotificationContent notificationContent = notifications.get(notificationType);
        if (notificationContent != null) {
            NotificationCompat.Builder builder = createNotificationBuilder(context, notificationContent);
            builder.setContentText("");
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(context.getString(notificationContent.getNotificationDescription()) + "\n" +
                            customText)
            );
            fireNotification(context, notificationContent.getNotificationId(), builder);
        }
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

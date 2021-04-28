package com.raftls.running.notification.models;

public class NotificationContent {
    private final int notificationId;
    private final int notificationTitle;
    private final int notificationDescription;
    private final int channelId;
    private final int channelTitle;
    private final int channelDescription;

    public NotificationContent(int notificationId, int notificationTitle, int notificationDescription, int channelId, int channelTitle, int channelDescription) {
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.notificationDescription = notificationDescription;
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.channelDescription = channelDescription;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public int getNotificationTitle() {
        return notificationTitle;
    }

    public int getNotificationDescription() {
        return notificationDescription;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getChannelTitle() {
        return channelTitle;
    }

    public int getChannelDescription() {
        return channelDescription;
    }
}

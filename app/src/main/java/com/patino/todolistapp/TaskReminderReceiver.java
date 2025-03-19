package com.patino.todolistapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;

public class TaskReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "task_notification_channel";
    private static final int NOTIFICATION_ID = 1;

    /**
     * Called when the BroadcastReceiver receives an Intent.
     *
     * Retrieves the task details from the Intent and creates a notification.
     *
     * Also, vibrates the device if it has a vibrator.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get task details from the intent
        String taskTitle = intent.getStringExtra("taskTitle");
        long taskTime = intent.getLongExtra("taskTime", 0);

        // Create the notification
        createNotification(context, taskTitle, taskTime);

        // Vibrate
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500); // Vibrate for 500ms
        }
    }
    /**
     * Creates a notification for a task reminder.
     *
     * Creates a notification channel for Android 8.0 and above, and then builds the notification using the NotificationCompat.Builder.
     *
     * Sets the notification title, text, small icon, and priority, and then shows the notification using the NotificationManager.
     *
     * @param context The Context in which the notification will be shown.
     * @param taskTitle The title of the task being reminded.
     * @param taskTime The time at which the task is scheduled.
     */
    private void createNotification(Context context, String taskTitle, long taskTime) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Recordatorio de tarea")
                .setContentText("Tu tarea '" + taskTitle + "' está a punto de comenzar.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
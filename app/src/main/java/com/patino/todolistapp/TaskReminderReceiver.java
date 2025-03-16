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

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtener la tarea de la intención
        String taskTitle = intent.getStringExtra("taskTitle");
        long taskTime = intent.getLongExtra("taskTime", 0);

        // Crear la notificación
        createNotification(context, taskTitle, taskTime);

        // Activar la vibración
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500); // Vibrar por 500 milisegundos
        }
    }

    private void createNotification(Context context, String taskTitle, long taskTime) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear un canal de notificación (solo necesario para Android 8.0 y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Task Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Crear la notificación
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Recordatorio de tarea")
                .setContentText("Tu tarea '" + taskTitle + "' está a punto de comenzar.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // Mostrar la notificación
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}

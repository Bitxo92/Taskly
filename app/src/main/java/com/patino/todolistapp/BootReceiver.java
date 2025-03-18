package com.patino.todolistapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    /**
     * Called when the device boots up and the ACTION_BOOT_COMPLETED intent is received.
     * Reschedules alarms for tasks that are stored in the database and have a timestamp in the future.
     * This method iterates through all tasks in the database, checks if each task's timestamp is in the future,
     * and schedules a reminder for that task using the MainActivity.scheduleTaskReminder method.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Device booted, rescheduling alarms");
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            List<Task> tasks = databaseHelper.getAllTasks();

            for (Task task : tasks) {
                long taskTime = task.getTimestamp();
                String taskTitle = task.getTitle();
                if (taskTime > System.currentTimeMillis()) { // Only schedule alarms for future tasks
                    MainActivity.scheduleTaskReminder(context, taskTime, taskTitle);
                }
            }
        }
    }
}
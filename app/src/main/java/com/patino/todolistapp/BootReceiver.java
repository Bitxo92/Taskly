package com.patino.todolistapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
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
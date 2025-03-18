package com.patino.todolistapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.Manifest;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskLongClickListener {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private DatabaseHelper databaseHelper;
    private static final int ADD_TASK_REQUEST = 1;
    private static final int TASK_REMINDER_REQUEST_CODE = 1001;
    private LinearLayout noTasksContainer; // Updated to handle the entire LinearLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        noTasksContainer = findViewById(R.id.noTasksContainer); // Initialize the LinearLayout
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Load tasks and reschedule alarms
        loadTasks();
        rescheduleAlarmsForAllTasks();

        // Set up FloatingActionButton to add a new task
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(view -> startActivityForResult(new Intent(this, AddTaskActivity.class), ADD_TASK_REQUEST));

        // Enable swipe-to-delete feature
        enableSwipeToDelete();

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Check for exact alarm permission for Android 12 and above
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            loadTasks(); // Refresh the task list

            // Check if the intent contains a task time (this assumes you're sending the task time from AddTaskActivity)
            if (data != null) {
                long taskTime = data.getLongExtra("taskTime", 0);
                String taskTitle = data.getStringExtra("taskTitle");

                // Call the scheduleTaskReminder method to schedule the task reminder
                if (taskTime > 0 && taskTitle != null) {
                    scheduleTaskReminder(this, taskTime, taskTitle);
                }
            }
        }
    }

    private void loadTasks() {
        List<Task> tasks = databaseHelper.getAllTasks();
        Collections.sort(tasks, (t1, t2) -> Long.compare(t1.getTimestamp(), t2.getTimestamp()));

        // Initialize the adapter with the task list and this activity as the long-click listener
        adapter = new TaskAdapter(tasks, databaseHelper, this);
        recyclerView.setAdapter(adapter);

        // Check if there are no tasks and show/hide the noTasksContainer
        if (adapter.getItemCount() == 0) {
            noTasksContainer.setVisibility(View.VISIBLE); // Show the entire LinearLayout
        } else {
            noTasksContainer.setVisibility(View.GONE); // Hide the entire LinearLayout
        }
    }

    private void rescheduleAlarmsForAllTasks() {
        List<Task> tasks = databaseHelper.getAllTasks();
        for (Task task : tasks) {
            long taskTime = task.getTimestamp();
            String taskTitle = task.getTitle();
            if (taskTime > System.currentTimeMillis()) { // Only schedule alarms for future tasks
                scheduleTaskReminder(this, taskTime, taskTitle);
            }
        }
    }

    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task task = adapter.getTaskAt(position);

                // Cancel the alarm for the deleted task
                cancelTaskReminder(task.getTimestamp());

                // Delete the task from the database
                databaseHelper.deleteTask(task.getId());
                adapter.removeTask(position);

                // After removal, check if the list is empty and show/hide the noTasksContainer
                if (adapter.getItemCount() == 0) {
                    noTasksContainer.setVisibility(View.VISIBLE); // Show the entire LinearLayout
                }
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }

    // Method to cancel the task reminder alarm
    public void cancelTaskReminder(long taskTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Create an intent for the broadcast receiver
        Intent intent = new Intent(this, TaskReminderReceiver.class);

        // Create a PendingIntent with the same request code used to schedule the alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) taskTime, // Use taskTime as the request code to ensure uniqueness
                intent,
                PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for security
        );

        // Cancel the alarm
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("TaskReminder", "Alarm canceled for task time: " + taskTime);
        }
    }

    // In MainActivity.java
    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleTaskReminder(Context context, long taskTime, String taskTitle) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long reminderTime = taskTime - 10 * 60 * 1000; // 10 minutes before (in milliseconds)

        Log.d("TaskReminder", "Task time: " + taskTime + ", Reminder time: " + reminderTime);

        if (reminderTime <= System.currentTimeMillis()) {
            Log.d("TaskReminder", "Reminder time is in the past. Cannot schedule.");
            return;
        }

        // Create an intent for the broadcast receiver
        Intent intent = new Intent(context, TaskReminderReceiver.class);
        intent.putExtra("taskTitle", taskTitle);
        intent.putExtra("taskTime", taskTime);

        // Create a PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) taskTime, // Use taskTime as the request code to ensure uniqueness
                intent,
                PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for security
        );

        // Schedule the alarm
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            }
            Log.d("TaskReminder", "Alarm set for: " + reminderTime);
        }
    }

    // Implement the long-click listener
    @Override
    public void onTaskLongClick(Task task) {
        // Open EditTaskActivity and pass the task data
        Intent intent = new Intent(this, EditTaskActivity.class);
        intent.putExtra("taskId", task.getId()); // Pass the task ID
        intent.putExtra("taskTitle", task.getTitle()); // Pass the task title
        intent.putExtra("taskDescription", task.getDescription()); // Pass the task description
        intent.putExtra("taskTimestamp", task.getTimestamp()); // Pass the task timestamp
        startActivityForResult(intent, ADD_TASK_REQUEST); // Use the same request code as adding a task
    }
}
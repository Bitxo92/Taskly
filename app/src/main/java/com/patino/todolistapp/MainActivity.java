package com.patino.todolistapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private DatabaseHelper databaseHelper;
    private static final int ADD_TASK_REQUEST = 1;
    private static final int TASK_REMINDER_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable edge-to-edge UI
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Apply padding to respect system bars
        View rootView = findViewById(android.R.id.content);
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseHelper = new DatabaseHelper(this);

        loadTasks();

        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(view -> startActivityForResult(new Intent(this, AddTaskActivity.class), ADD_TASK_REQUEST));

        enableSwipeToDelete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            loadTasks();

            // Check if the intent contains a task time (this assumes you're sending the task time from AddTaskActivity)
            if (data != null) {
                long taskTime = data.getLongExtra("taskTime", 0);
                String taskTitle = data.getStringExtra("taskTitle");

                // Call the scheduleTaskReminder method to schedule the task reminder
                if (taskTime > 0 && taskTitle != null) {
                    scheduleTaskReminder(taskTime, taskTitle);
                }
            }
        }
    }


    private void loadTasks() {
        List<Task> tasks = databaseHelper.getAllTasks();
        Collections.sort(tasks, (t1, t2) -> Long.compare(t1.getTimestamp(), t2.getTimestamp()));
        adapter = new TaskAdapter(tasks, databaseHelper);
        recyclerView.setAdapter(adapter);
    }

    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task task = adapter.getTaskAt(position);
                databaseHelper.deleteTask(task.getId());
                adapter.removeTask(position);
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }

    // Method to schedule the task reminder alarm
    @SuppressLint("ScheduleExactAlarm")
    public void scheduleTaskReminder(long taskTime, String taskTitle) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        long reminderTime = taskTime - 10 * 60 * 1000; // 10 minutes before (in milliseconds)


        Log.d("TaskReminder", "Task time: " + taskTime + ", Reminder time: " + reminderTime);


        if (reminderTime <= System.currentTimeMillis()) {
            Log.d("TaskReminder", "Reminder time is in the past. Cannot schedule.");
            return;
        }

        // Create an intent for the broadcast receiver
        Intent intent = new Intent(this, TaskReminderReceiver.class);
        intent.putExtra("taskTitle", taskTitle);
        intent.putExtra("taskTime", taskTime);

       //for BroadcastReceiver to be triggered
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                TASK_REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_IMMUTABLE // Ensure we use FLAG_IMMUTABLE for security
        );

        // Schedule the alarm
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            Log.d("TaskReminder", "Alarm set for: " + reminderTime);
        }
    }
}

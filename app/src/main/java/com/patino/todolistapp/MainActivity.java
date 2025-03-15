package com.patino.todolistapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

    // Método para programar la alarma
    public void scheduleTaskReminder(long taskTime, String taskTitle) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Establecer el tiempo para la alarma (10 minutos antes de la hora de la tarea)
        long reminderTime = taskTime - 10 * 60 * 1000; // 10 minutos antes (en milisegundos)

        // Crear la intención para el broadcast receiver
        Intent intent = new Intent(this, TaskReminderReceiver.class);
        intent.putExtra("taskTitle", taskTitle);
        intent.putExtra("taskTime", taskTime);

        // Crear un PendingIntent para que el BroadcastReceiver sea activado
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                TASK_REMINDER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Programar la alarma
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        }
    }

}

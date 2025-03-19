package com.patino.todolistapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextDescription;
    private TextView textViewDateTime;
    private Button buttonSave, buttonPickDateTime;
    private DatabaseHelper databaseHelper;
    private Calendar selectedDateTime = Calendar.getInstance();
    private int taskId;

    /**
     * Called when the activity is created.
     *
     * Initializes the views, retrieves task data from the intent, and pre-fills the task data.
     * Sets up the date/time picker and save button.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task); // Reuse the same layout as AddTaskActivity

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewDateTime = findViewById(R.id.textViewDateTime);
        buttonPickDateTime = findViewById(R.id.buttonPickDateTime);
        buttonSave = findViewById(R.id.buttonSave);
        databaseHelper = new DatabaseHelper(this);

        // Get task data from the intent
        Intent intent = getIntent();
        taskId = intent.getIntExtra("taskId", -1);
        String taskTitle = intent.getStringExtra("taskTitle");
        String taskDescription = intent.getStringExtra("taskDescription");
        long taskTimestamp = intent.getLongExtra("taskTimestamp", 0);

        // Pre-fill the task data
        editTextTitle.setText(taskTitle);
        editTextDescription.setText(taskDescription);
        selectedDateTime.setTimeInMillis(taskTimestamp);
        textViewDateTime.setText(selectedDateTime.getTime().toString());

        // Set up date/time picker
        buttonPickDateTime.setOnClickListener(v -> pickDateTime());

        // Set up save button
        buttonSave.setOnClickListener(v -> updateTask());
    }

    /**
     * Displays a date and time picker dialog to select a date and time.
     *
     * Uses a DatePickerDialog to select the date, and then a TimePickerDialog to select the time.
     * Updates the selected date and time in the textViewDateTime.
     */
    private void pickDateTime() {
        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(year, month, dayOfMonth);
            TimePickerDialog timePicker = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
                textViewDateTime.setText(selectedDateTime.getTime().toString());
            }, selectedDateTime.get(Calendar.HOUR_OF_DAY), selectedDateTime.get(Calendar.MINUTE), true);
            timePicker.show();
        }, selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH), selectedDateTime.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    /**
     * Updates a task with the provided title, description, and timestamp.
     *
     * Retrieves the title and description from the editTextTitle and editTextDescription views,
     * and the timestamp from the selectedDateTime object.
     *
     * Validates that the title and description are not empty before updating the task.
     *
     * Updates the task in the database using the databaseHelper object.
     *
     * Displays a toast message to indicate that the task has been updated, and returns a result code to the calling activity.
     */
    private void updateTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        long timestamp = selectedDateTime.getTimeInMillis();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the task in the database
        databaseHelper.updateTask(taskId, title, description, timestamp);
        Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
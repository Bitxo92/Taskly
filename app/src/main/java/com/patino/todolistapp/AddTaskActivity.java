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

public class AddTaskActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextDescription;
    private TextView textViewDateTime;
    private Button buttonSave, buttonPickDateTime;
    private DatabaseHelper databaseHelper;
    private Calendar selectedDateTime = Calendar.getInstance();

    /**
     * Called when the activity is created.
     *
     * Initializes the activity's UI components, sets up event listeners, and establishes a connection to the database.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, or null if the activity is being created for the first time.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewDateTime = findViewById(R.id.textViewDateTime);
        buttonPickDateTime = findViewById(R.id.buttonPickDateTime);
        buttonSave = findViewById(R.id.buttonSave);
        databaseHelper = new DatabaseHelper(this);

        buttonPickDateTime.setOnClickListener(v -> pickDateTime());
        buttonSave.setOnClickListener(v -> saveTask());
    }
    /**
     * Displays a date and time picker dialog to allow the user to select a date and time.
     *
     * When a date is selected, a time picker dialog is displayed to allow the user to select a time.
     * The selected date and time are then displayed in the `textViewDateTime` field.
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
     * Saves a new task to the database.
     *
     * Retrieves the title and description from the `editTextTitle` and `editTextDescription` fields, respectively.
     * If either field is empty, displays an error message and returns without saving the task.
     * Otherwise, adds the task to the database with the selected date and time, and displays a success message.
     *
     * After saving the task, sets the activity result to `RESULT_OK` and finishes the activity.
     */
    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        long timestamp = selectedDateTime.getTimeInMillis();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please enter both title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseHelper.addTask(title, description, timestamp);
        Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}

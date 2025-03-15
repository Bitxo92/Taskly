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

    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        long timestamp = selectedDateTime.getTimeInMillis();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseHelper.addTask(title, description, timestamp);
        Toast.makeText(this, "Tarea guardada", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}

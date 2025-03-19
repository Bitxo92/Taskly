package com.patino.todolistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_TIMESTAMP = "timestamp";

   // Class Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * Creates the tasks table with the necessary columns.
     *
     * @param db The SQLiteDatabase instance to create the table in.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_TIMESTAMP + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    /**
     * Called when the database needs to be upgraded to a new version.
     *
     * Drops the existing tasks table and recreates it using the onCreate method.
     *
     * @param db The SQLiteDatabase instance to upgrade.
     * @param oldVersion The old version of the database.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    /**
     * Adds a new task to the database.
     *
     * Inserts a new row into the tasks table with the provided title, description, and timestamp.
     *
     * @param title The title of the task to add.
     * @param description The description of the task to add.
     * @param timestamp The timestamp of the task to add.
     */
    public void addTask(String title, String description, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_TIMESTAMP, timestamp);
        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    /**
     * Retrieves all tasks from the database.
     *
     * Queries the tasks table and returns a list of Task objects, sorted by timestamp in ascending order.
     *
     * @return A list of all tasks in the database.
     */
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_TIMESTAMP},
                null, null, null, null, COLUMN_TIMESTAMP + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getLong(3)
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }
    /**
     * Deletes a task from the database by its ID.
     *
     * Removes the task with the specified ID from the tasks table.
     *
     * @param id The ID of the task to delete.
     */
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Updates a task in the database.
     *
     * Updates the task with the specified ID with the provided title, description, and timestamp.
     *
     * @param id The ID of the task to update.
     * @param title The new title of the task.
     * @param description The new description of the task.
     * @param timestamp The new timestamp of the task.
     */
    public void updateTask(int id, String title, String description, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_TIMESTAMP, timestamp);
        db.update(TABLE_TASKS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}



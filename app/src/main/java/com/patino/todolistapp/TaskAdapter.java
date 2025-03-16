package com.patino.todolistapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private DatabaseHelper databaseHelper;
    private OnTaskLongClickListener longClickListener; // Interface for long-click listener

    // Interface for long-click listener
    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }

    // Constructor with long-click listener
    public TaskAdapter(List<Task> taskList, DatabaseHelper databaseHelper, OnTaskLongClickListener longClickListener) {
        this.taskList = taskList;
        this.databaseHelper = databaseHelper;
        this.longClickListener = longClickListener; // Initialize the listener
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the task item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.dateTime.setText(dateFormat.format(task.getTimestamp()));

        // Set long-click listener
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onTaskLongClick(task); // Trigger the long-click listener
            }
            return true; // Return true to indicate the event is consumed
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // Method to get the task at a specific position
    public Task getTaskAt(int position) {
        return taskList.get(position);
    }

    // Method to remove a task from the list
    public void removeTask(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    // ViewHolder class
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, dateTime;

        public TaskViewHolder(View itemView) {
            super(itemView);
            // Initialize views
            title = itemView.findViewById(R.id.task_title);
            description = itemView.findViewById(R.id.task_description);
            dateTime = itemView.findViewById(R.id.task_datetime);
        }
    }

    // Date formatter for displaying the task timestamp
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy - HH:mm", Locale.getDefault());
}
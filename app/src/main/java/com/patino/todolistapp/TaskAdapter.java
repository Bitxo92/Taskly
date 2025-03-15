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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy - HH:mm", Locale.getDefault());

    public TaskAdapter(List<Task> taskList, DatabaseHelper databaseHelper) {
        this.taskList = taskList;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.dateTime.setText(dateFormat.format(task.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public Task getTaskAt(int position) {
        return taskList.get(position);
    }

    public void removeTask(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, dateTime;

        public TaskViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.task_title);
            description = itemView.findViewById(R.id.task_description);
            dateTime = itemView.findViewById(R.id.task_datetime);
        }
    }
}




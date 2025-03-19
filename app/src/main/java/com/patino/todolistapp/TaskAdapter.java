package com.patino.todolistapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

    // Class Constructor
    public TaskAdapter(List<Task> taskList, DatabaseHelper databaseHelper, OnTaskLongClickListener longClickListener) {
        this.taskList = taskList;
        this.databaseHelper = databaseHelper;
        this.longClickListener = longClickListener; // Initialize the listener
    }

    /**
     * Creates a new TaskViewHolder instance.
     *
     * Inflates the task item layout and returns a new TaskViewHolder instance.
     *
     * @param parent The ViewGroup into which the new view will be added after it is bound to an adapter position.
     * @param viewType The view type of the new view.
     * @return A new TaskViewHolder instance.
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the task item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    /**
     * Binds the TaskViewHolder instance to the task data at the specified position.
     *
     * Retrieves the task data from the taskList and updates the views in the TaskViewHolder instance.
     *
     * Also, updates the visibility and styles of the views based on the task's timestamp and the current time.
     *
     * Sets a long-click listener on the itemView to handle long clicks on the task item.
     *
     * @param holder The TaskViewHolder instance to bind the data to.
     * @param position The position of the task data in the taskList.
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.dateTime.setText(dateFormat.format(task.getTimestamp()));

        long currentTime = System.currentTimeMillis();
        long taskTime = task.getTimestamp();
        long timeDifference = taskTime - currentTime;

        // Reset visibility and styles
        holder.iconWarning.setVisibility(View.GONE);
        holder.iconClose.setVisibility(View.GONE);
        holder.iconOntime.setVisibility(View.GONE);
        holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)); // Remove strikethrough
        holder.title.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black)); // Reset text color

        if (timeDifference <= 0) {
            // Task time has passed
            holder.iconClose.setVisibility(View.VISIBLE);
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Add strikethrough
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark)); // Red text
        } else if (timeDifference <= 10 * 60 * 1000) {
            // 10 minutes or less remaining
            holder.iconWarning.setVisibility(View.VISIBLE);
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_orange_dark)); // Yellow text
        }else{
            holder.iconOntime.setVisibility(View.VISIBLE);
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark)); // Green text
        }


        // Set long-click listener
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Task task = taskList.get(position);
                    longClickListener.onTaskLongClick(task);
                }
                return true;
            }
        });
    }

    /**
     * Returns the number of items in the taskList.
     *
     * @return The number of items in the taskList.
     */
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * Retrieves the Task at the specified position in the task list.
     *
     * @param position The position of the Task to retrieve.
     * @return The Task at the specified position.
     */
    public Task getTaskAt(int position) {
        return taskList.get(position);
    }

    /**
     * Removes the Task at the specified position from the task list.
     *
     * Notifies the adapter that an item has been removed, triggering a layout update.
     *
     * @param position The position of the Task to remove.
     */
    public void removeTask(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }
    /**
     * A ViewHolder class that represents a single task item in the RecyclerView.
     *
     * Holds the views that will be used to display the task data.
     */
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, dateTime;
        ImageView iconWarning, iconClose,iconOntime;

        /**
         * Constructs a new TaskViewHolder instance.
         *
         * Initializes the views that will be used to display the task data.
         *
         * @param itemView The view that will be used to display the task data.
         */
        public TaskViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.task_title);
            description = itemView.findViewById(R.id.task_description);
            dateTime = itemView.findViewById(R.id.task_datetime);
            iconWarning = itemView.findViewById(R.id.icon_warning);
            iconClose = itemView.findViewById(R.id.icon_close);
            iconOntime = itemView.findViewById(R.id.icon_ontime);

        }
    }

    // Date formatter for displaying the task timestamp
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy - HH:mm", Locale.getDefault());
}
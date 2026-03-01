package com.qprovep.exlog.ui.workout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.WorkoutTemplate;

public class WorkoutAdapter extends ListAdapter<WorkoutTemplate, WorkoutAdapter.ViewHolder> {

    public interface OnWorkoutClickListener {
        void onWorkoutClick(WorkoutTemplate workout);

        void onWorkoutLongClick(WorkoutTemplate workout);
    }

    private final OnWorkoutClickListener listener;

    public WorkoutAdapter(OnWorkoutClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<WorkoutTemplate> DIFF_CALLBACK = new DiffUtil.ItemCallback<WorkoutTemplate>() {
        @Override
        public boolean areItemsTheSame(@NonNull WorkoutTemplate oldItem, @NonNull WorkoutTemplate newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull WorkoutTemplate oldItem, @NonNull WorkoutTemplate newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView createdAtText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.workout_name);
            createdAtText = itemView.findViewById(R.id.workout_created_at);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onWorkoutClick(getItem(pos));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onWorkoutLongClick(getItem(pos));
                }
                return true;
            });
        }

        void bind(WorkoutTemplate workout) {
            nameText.setText(workout.getName());
            if (workout.getCreatedAt() > 0) {
                String date = java.text.DateFormat.getDateInstance().format(
                        new java.util.Date(workout.getCreatedAt()));
                createdAtText.setText(date);
            } else {
                createdAtText.setText(R.string.unknown_date);
            }
        }
    }
}

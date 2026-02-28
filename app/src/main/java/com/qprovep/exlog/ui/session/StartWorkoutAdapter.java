package com.qprovep.exlog.ui.session;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.WorkoutTemplate;

public class StartWorkoutAdapter extends ListAdapter<WorkoutTemplate, StartWorkoutAdapter.ViewHolder> {

    public interface OnStartClickListener {
        void onStartClick(WorkoutTemplate workout);
    }

    private final OnStartClickListener listener;

    public StartWorkoutAdapter(OnStartClickListener listener) {
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
                .inflate(R.layout.item_start_workout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView infoText;
        private final MaterialButton startBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.workout_name);
            infoText = itemView.findViewById(R.id.workout_info);
            startBtn = itemView.findViewById(R.id.btn_start);

            startBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onStartClick(getItem(pos));
                }
            });
        }

        void bind(WorkoutTemplate workout) {
            nameText.setText(workout.getName());
            infoText.setText("Tap Start to begin session");
        }
    }
}

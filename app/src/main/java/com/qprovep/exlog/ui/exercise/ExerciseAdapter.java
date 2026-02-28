package com.qprovep.exlog.ui.exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.ExerciseTemplate;

public class ExerciseAdapter extends ListAdapter<ExerciseTemplate, ExerciseAdapter.ViewHolder> {

    public interface OnExerciseClickListener {
        void onExerciseClick(ExerciseTemplate exercise);

        void onExerciseLongClick(ExerciseTemplate exercise);
    }

    private final OnExerciseClickListener listener;

    public ExerciseAdapter(OnExerciseClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ExerciseTemplate> DIFF_CALLBACK = new DiffUtil.ItemCallback<ExerciseTemplate>() {
        @Override
        public boolean areItemsTheSame(@NonNull ExerciseTemplate oldItem, @NonNull ExerciseTemplate newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ExerciseTemplate oldItem, @NonNull ExerciseTemplate newItem) {
            return oldItem.getName().equals(newItem.getName())
                    && String.valueOf(oldItem.getNote()).equals(String.valueOf(newItem.getNote()));
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseTemplate exercise = getItem(position);
        holder.bind(exercise);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView noteText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            noteText = itemView.findViewById(R.id.exercise_note);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onExerciseClick(getItem(pos));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onExerciseLongClick(getItem(pos));
                }
                return true;
            });
        }

        void bind(ExerciseTemplate exercise) {
            nameText.setText(exercise.getName());
            if (exercise.getNote() != null && !exercise.getNote().isEmpty()) {
                noteText.setText(exercise.getNote());
                noteText.setVisibility(View.VISIBLE);
            } else {
                noteText.setVisibility(View.GONE);
            }
        }
    }
}

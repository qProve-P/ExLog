package com.qprovep.exlog.ui.workout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.ExerciseTemplate;

import java.util.ArrayList;
import java.util.List;

public class WorkoutExerciseEditAdapter extends RecyclerView.Adapter<WorkoutExerciseEditAdapter.ViewHolder> {

    public static class ExerciseEntry {
        public ExerciseTemplate exercise;
        public double referenceWeight;
        public int targetSets;
        public int targetReps;

        public ExerciseEntry(ExerciseTemplate exercise, double referenceWeight, int targetSets, int targetReps) {
            this.exercise = exercise;
            this.referenceWeight = referenceWeight;
            this.targetSets = targetSets;
            this.targetReps = targetReps;
        }
    }

    private final List<ExerciseEntry> entries = new ArrayList<>();

    public void setEntries(List<ExerciseEntry> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        notifyDataSetChanged();
    }

    public void addEntry(ExerciseEntry entry) {
        entries.add(entry);
        notifyItemInserted(entries.size() - 1);
    }

    public List<ExerciseEntry> getEntries() {
        return entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(entries.get(position));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final EditText refWeightEdit;
        private final EditText targetSetsEdit;
        private final EditText targetRepsEdit;
        private final ImageButton removeBtn;
        private ExerciseEntry currentEntry;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            refWeightEdit = itemView.findViewById(R.id.edit_ref_weight);
            targetSetsEdit = itemView.findViewById(R.id.edit_target_sets);
            targetRepsEdit = itemView.findViewById(R.id.edit_target_reps);
            removeBtn = itemView.findViewById(R.id.btn_remove);

            removeBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    entries.remove(pos);
                    notifyItemRemoved(pos);
                }
            });

            refWeightEdit.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (currentEntry != null) {
                        try {
                            currentEntry.referenceWeight = Double.parseDouble(s.toString());
                        } catch (NumberFormatException e) {
                            currentEntry.referenceWeight = 0;
                        }
                    }
                }
            });

            targetSetsEdit.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (currentEntry != null) {
                        try {
                            currentEntry.targetSets = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            currentEntry.targetSets = 0;
                        }
                    }
                }
            });

            targetRepsEdit.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (currentEntry != null) {
                        try {
                            currentEntry.targetReps = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            currentEntry.targetReps = 0;
                        }
                    }
                }
            });
        }

        void bind(ExerciseEntry entry) {
            currentEntry = entry;
            nameText.setText(entry.exercise.getName());
            refWeightEdit.setText(entry.referenceWeight > 0 ? String.valueOf(entry.referenceWeight) : "");
            targetSetsEdit.setText(entry.targetSets > 0 ? String.valueOf(entry.targetSets) : "");
            targetRepsEdit.setText(entry.targetReps > 0 ? String.valueOf(entry.targetReps) : "");
        }
    }

    private static abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }
}

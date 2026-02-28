package com.qprovep.exlog.ui.session;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.qprovep.exlog.R;

import java.util.ArrayList;
import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<SessionViewModel.SessionExerciseEntry> exercises = new ArrayList<>();
    private final SessionViewModel viewModel;

    public SessionAdapter(SessionViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setExercises(List<SessionViewModel.SessionExerciseEntry> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(exercises.get(position), position);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final LinearLayout setsContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            setsContainer = itemView.findViewById(R.id.sets_container);
        }

        void bind(SessionViewModel.SessionExerciseEntry entry, final int exerciseIndex) {
            String name = entry.exercise.getName();
            if (entry.exercise.getCategory() != null && !entry.exercise.getCategory().isEmpty()) {
                name += " (" + entry.exercise.getCategory() + ")";
            }
            nameText.setText(name);

            setsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            for (int i = 0; i < entry.sets.size(); i++) {
                final int setIndex = i;
                SessionViewModel.SetEntry set = entry.sets.get(i);

                View setRow = inflater.inflate(R.layout.item_set_input, setsContainer, false);
                TextView setNum = setRow.findViewById(R.id.set_number);
                EditText weightEdit = setRow.findViewById(R.id.edit_weight);
                EditText repsEdit = setRow.findViewById(R.id.edit_reps);
                MaterialCheckBox checkbox = setRow.findViewById(R.id.checkbox_completed);

                setNum.setText(String.valueOf(set.setNumber));

                if (set.weight > 0) {
                    weightEdit.setText(String.valueOf(set.weight));
                }
                if (set.reps > 0) {
                    repsEdit.setText(String.valueOf(set.reps));
                }

                checkbox.setChecked(set.isCompleted);

                TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        float w = set.weight;
                        try {
                            w = Float.parseFloat(weightEdit.getText().toString());
                        } catch (NumberFormatException ignored) {
                        }

                        int r = set.reps;
                        try {
                            r = Integer.parseInt(repsEdit.getText().toString());
                        } catch (NumberFormatException ignored) {
                        }

                        viewModel.updateSet(exerciseIndex, setIndex, w, r, checkbox.isChecked());
                    }
                };

                weightEdit.addTextChangedListener(watcher);
                repsEdit.addTextChangedListener(watcher);

                checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    float w = set.weight;
                    try {
                        w = Float.parseFloat(weightEdit.getText().toString());
                    } catch (Exception ignored) {
                    }

                    int r = set.reps;
                    try {
                        r = Integer.parseInt(repsEdit.getText().toString());
                    } catch (Exception ignored) {
                    }

                    viewModel.updateSet(exerciseIndex, setIndex, w, r, isChecked);
                });

                setsContainer.addView(setRow);
            }
        }
    }
}

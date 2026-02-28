package com.qprovep.exlog.ui.session;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.qprovep.exlog.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<SessionViewModel.SessionExerciseEntry> exercises = new ArrayList<>();
    private final SessionViewModel viewModel;
    private final Set<Integer> expandedPositions = new HashSet<>();
    private boolean firstBindDone = false;

    private static final float WEIGHT_STEP = 0.5f;
    private static final float WEIGHT_MAX = 200f;
    private static final int WEIGHT_PICKER_MAX = (int) (WEIGHT_MAX / WEIGHT_STEP);
    private static final String[] WEIGHT_VALUES;
    private static final int REPS_MAX = 100;

    static {
        WEIGHT_VALUES = new String[WEIGHT_PICKER_MAX + 1];
        for (int i = 0; i <= WEIGHT_PICKER_MAX; i++) {
            float val = i * WEIGHT_STEP;
            if (val == (int) val) {
                WEIGHT_VALUES[i] = String.valueOf((int) val);
            } else {
                WEIGHT_VALUES[i] = String.valueOf(val);
            }
        }
    }

    public SessionAdapter(SessionViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setExercises(List<SessionViewModel.SessionExerciseEntry> exercises) {
        this.exercises = exercises;
        if (!firstBindDone && !exercises.isEmpty()) {
            expandedPositions.add(0);
            firstBindDone = true;
        }
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

    private int weightToPickerIndex(float weight) {
        int index = Math.round(weight / WEIGHT_STEP);
        return Math.max(0, Math.min(index, WEIGHT_PICKER_MAX));
    }

    private float pickerIndexToWeight(int index) {
        return index * WEIGHT_STEP;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final ImageView expandIcon;
        private final TextView doneIndicator;
        private final LinearLayout header;
        private final LinearLayout content;
        private final LinearLayout setsContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            expandIcon = itemView.findViewById(R.id.expand_icon);
            doneIndicator = itemView.findViewById(R.id.done_indicator);
            header = itemView.findViewById(R.id.exercise_header);
            content = itemView.findViewById(R.id.exercise_content);
            setsContainer = itemView.findViewById(R.id.sets_container);
        }

        void bind(SessionViewModel.SessionExerciseEntry entry, final int exerciseIndex) {
            nameText.setText(entry.exercise.getName());

            boolean isExpanded = expandedPositions.contains(exerciseIndex);
            content.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            expandIcon.setRotation(isExpanded ? 180f : 0f);

            updateDoneIndicator(entry);

            header.setOnClickListener(v -> {
                if (expandedPositions.contains(exerciseIndex)) {
                    expandedPositions.remove(exerciseIndex);
                    content.setVisibility(View.GONE);
                    expandIcon.setRotation(0f);
                } else {
                    expandedPositions.add(exerciseIndex);
                    content.setVisibility(View.VISIBLE);
                    expandIcon.setRotation(180f);
                }
            });

            setsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            for (int i = 0; i < entry.sets.size(); i++) {
                final int setIndex = i;
                SessionViewModel.SetEntry set = entry.sets.get(i);

                View setRow = inflater.inflate(R.layout.item_set_input, setsContainer, false);
                TextView setNum = setRow.findViewById(R.id.set_number);
                NumberPicker weightPicker = setRow.findViewById(R.id.picker_weight);
                NumberPicker repsPicker = setRow.findViewById(R.id.picker_reps);
                MaterialCheckBox checkbox = setRow.findViewById(R.id.checkbox_completed);

                setNum.setText(String.valueOf(set.setNumber));

                weightPicker.setMinValue(0);
                weightPicker.setMaxValue(WEIGHT_PICKER_MAX);
                weightPicker.setDisplayedValues(WEIGHT_VALUES);
                weightPicker.setWrapSelectorWheel(false);
                weightPicker.setValue(weightToPickerIndex(set.weight));

                repsPicker.setMinValue(0);
                repsPicker.setMaxValue(REPS_MAX);
                repsPicker.setWrapSelectorWheel(false);
                repsPicker.setValue(set.reps);

                checkbox.setChecked(set.isCompleted);

                weightPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                    float w = pickerIndexToWeight(newVal);
                    viewModel.updateSet(exerciseIndex, setIndex, w, repsPicker.getValue(), checkbox.isChecked());
                });

                repsPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                    float w = pickerIndexToWeight(weightPicker.getValue());
                    viewModel.updateSet(exerciseIndex, setIndex, w, newVal, checkbox.isChecked());
                });

                checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    float w = pickerIndexToWeight(weightPicker.getValue());
                    viewModel.updateSet(exerciseIndex, setIndex, w, repsPicker.getValue(), isChecked);
                    updateDoneIndicator(entry);
                });

                setsContainer.addView(setRow);
            }
        }

        private void updateDoneIndicator(SessionViewModel.SessionExerciseEntry entry) {
            boolean allDone = !entry.sets.isEmpty();
            for (SessionViewModel.SetEntry set : entry.sets) {
                if (!set.isCompleted) {
                    allDone = false;
                    break;
                }
            }
            doneIndicator.setVisibility(allDone ? View.VISIBLE : View.GONE);
        }
    }
}

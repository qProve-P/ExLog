package com.qprovep.exlog.ui.templates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.qprovep.exlog.R;
import com.qprovep.exlog.ui.exercise.ExerciseListFragment;
import com.qprovep.exlog.ui.workout.WorkoutListFragment;

public class TemplatesFragment extends Fragment {

    private Fragment exerciseFragment;
    private Fragment workoutFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_templates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggle_group);

        exerciseFragment = new ExerciseListFragment();
        workoutFragment = new WorkoutListFragment();

        // Show exercises by default
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.templates_container, exerciseFragment)
                    .commit();
        }

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked)
                return;

            Fragment target;
            if (checkedId == R.id.btn_exercises) {
                target = exerciseFragment;
            } else {
                target = workoutFragment;
            }

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.templates_container, target)
                    .commit();
        });
    }
}

package com.qprovep.exlog.ui.workout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.qprovep.exlog.R;
import com.qprovep.exlog.data.AppDatabase;
import com.qprovep.exlog.data.entity.ExerciseTemplate;
import com.qprovep.exlog.data.entity.WorkoutExercise;
import com.qprovep.exlog.data.entity.WorkoutTemplate;
import com.qprovep.exlog.data.relation.WorkoutExerciseWithTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkoutDetailFragment extends Fragment {

    private WorkoutViewModel viewModel;
    private WorkoutExerciseEditAdapter exerciseAdapter;
    private EditText nameEdit;
    private int workoutId = -1;
    private WorkoutTemplate existingWorkout;
    private List<ExerciseTemplate> allExercisesCached;
    private List<String> categoriesCached = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);

        nameEdit = view.findViewById(R.id.edit_workout_name);
        RecyclerView recyclerView = view.findViewById(R.id.workout_exercises_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        exerciseAdapter = new WorkoutExerciseEditAdapter();
        recyclerView.setAdapter(exerciseAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    @NonNull RecyclerView.ViewHolder target) {
                return exerciseAdapter.onItemMove(viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        exerciseAdapter.setDragStartListener(itemTouchHelper::startDrag);
        MaterialButton btnAddExercise = view.findViewById(R.id.btn_add_exercise);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_workout);

        viewModel.getAllExercises().observe(getViewLifecycleOwner(), exercises -> {
            allExercisesCached = exercises;
            buildCategoryList();
        });

        if (getArguments() != null) {
            workoutId = getArguments().getInt("workoutId", -1);
        }

        if (workoutId != -1) {
            viewModel.getWorkoutWithExercises(workoutId).observe(getViewLifecycleOwner(), data -> {
                if (data == null)
                    return;

                existingWorkout = data.workoutTemplate;
                nameEdit.setText(existingWorkout.getName());

                if (exerciseAdapter.getEntries().isEmpty() && data.exercises != null) {
                    List<WorkoutExerciseEditAdapter.ExerciseEntry> entries = new ArrayList<>();
                    List<WorkoutExerciseWithTemplate> sortedExercises = new ArrayList<>(data.exercises);
                    Collections.sort(sortedExercises, Comparator.comparingInt(
                            e -> e.workoutExercise.getOrderIndex()));
                    for (WorkoutExerciseWithTemplate weWithTemplate : sortedExercises) {
                        entries.add(new WorkoutExerciseEditAdapter.ExerciseEntry(
                                weWithTemplate.exerciseTemplate,
                                weWithTemplate.workoutExercise.getReferenceWeight(),
                                weWithTemplate.workoutExercise.getTargetSets(),
                                weWithTemplate.workoutExercise.getTargetReps()));
                    }
                    exerciseAdapter.setEntries(entries);
                }
            });
        }

        btnAddExercise.setOnClickListener(v -> showExercisePicker());
        btnSave.setOnClickListener(v -> saveWorkout());
    }

    private void buildCategoryList() {
        if (allExercisesCached == null)
            return;
        Set<String> cats = new LinkedHashSet<>();
        for (ExerciseTemplate ex : allExercisesCached) {
            if (ex.getCategory() != null && !ex.getCategory().isEmpty()) {
                cats.add(ex.getCategory());
            }
        }
        categoriesCached.clear();
        categoriesCached.addAll(cats);
    }

    private void showExercisePicker() {
        if (allExercisesCached == null || allExercisesCached.isEmpty()) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.pick_exercises)
                    .setMessage(R.string.no_exercises)
                    .setPositiveButton(R.string.cancel, null)
                    .show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_exercise_picker, null);

        EditText searchEdit = dialogView.findViewById(R.id.search_exercises);
        AutoCompleteTextView categoryFilter = dialogView.findViewById(R.id.filter_category);
        RecyclerView pickerRecycler = dialogView.findViewById(R.id.exercise_picker_recycler);
        pickerRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        ExercisePickerAdapter pickerAdapter = new ExercisePickerAdapter();

        Set<Integer> preSelected = new HashSet<>();
        for (WorkoutExerciseEditAdapter.ExerciseEntry entry : exerciseAdapter.getEntries()) {
            preSelected.add(entry.exercise.getId());
        }

        pickerAdapter.setExercises(allExercisesCached, preSelected);
        pickerRecycler.setAdapter(pickerAdapter);

        List<String> filterOptions = new ArrayList<>();
        filterOptions.add(getString(R.string.all_categories));
        filterOptions.add("\u2014");
        filterOptions.addAll(categoriesCached);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, filterOptions);
        categoryFilter.setAdapter(catAdapter);
        categoryFilter.setText(getString(R.string.all_categories), false);

        categoryFilter.setOnItemClickListener((parent, view, position, id) -> {
            String selected = filterOptions.get(position);
            if (selected.equals(getString(R.string.all_categories))) {
                pickerAdapter.filterByCategory("");
            } else {
                pickerAdapter.filterByCategory(selected);
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pickerAdapter.filterByName(s.toString());
            }
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.pick_exercises)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    Set<Integer> selectedIds = pickerAdapter.getSelectedIds();
                    applySelectedExercises(selectedIds);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void applySelectedExercises(Set<Integer> selectedIds) {
        List<WorkoutExerciseEditAdapter.ExerciseEntry> existingEntries = new ArrayList<>();
        for (WorkoutExerciseEditAdapter.ExerciseEntry entry : exerciseAdapter.getEntries()) {
            if (selectedIds.contains(entry.exercise.getId())) {
                existingEntries.add(entry);
            }
        }

        Set<Integer> existingIds = new HashSet<>();
        for (WorkoutExerciseEditAdapter.ExerciseEntry entry : existingEntries) {
            existingIds.add(entry.exercise.getId());
        }

        for (ExerciseTemplate ex : allExercisesCached) {
            if (selectedIds.contains(ex.getId()) && !existingIds.contains(ex.getId())) {
                existingEntries.add(new WorkoutExerciseEditAdapter.ExerciseEntry(ex, 0, 3, 10));
            }
        }

        exerciseAdapter.setEntries(existingEntries);
    }

    private void saveWorkout() {
        String name = nameEdit.getText().toString().trim();
        if (name.isEmpty()) {
            nameEdit.setError("Name required");
            return;
        }

        List<WorkoutExerciseEditAdapter.ExerciseEntry> entries = exerciseAdapter.getEntries();
        List<WorkoutExercise> workoutExercises = new ArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            WorkoutExerciseEditAdapter.ExerciseEntry e = entries.get(i);
            workoutExercises.add(new WorkoutExercise(
                    0,
                    e.exercise.getId(),
                    i,
                    e.referenceWeight,
                    e.targetSets,
                    e.targetReps));
        }

        if (existingWorkout != null) {
            existingWorkout.setName(name);
            viewModel.updateWorkout(existingWorkout, workoutExercises);
        } else {
            WorkoutTemplate newWorkout = new WorkoutTemplate(name);
            viewModel.insertWorkout(newWorkout, workoutExercises, null);
        }

        Navigation.findNavController(requireView()).popBackStack();
    }
}

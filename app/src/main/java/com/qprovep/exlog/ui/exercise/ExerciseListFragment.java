package com.qprovep.exlog.ui.exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.ExerciseTemplate;

public class ExerciseListFragment extends Fragment implements ExerciseAdapter.OnExerciseClickListener {

    private ExerciseViewModel viewModel;
    private ExerciseAdapter adapter;
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);

        emptyText = view.findViewById(R.id.empty_text);
        RecyclerView recyclerView = view.findViewById(R.id.exercise_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ExerciseAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_exercise);
        fab.setOnClickListener(v -> showEditDialog(null));

        viewModel.getAllExercises().observe(getViewLifecycleOwner(), exercises -> {
            adapter.submitList(exercises);
            emptyText.setVisibility(exercises == null || exercises.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onExerciseClick(ExerciseTemplate exercise) {
        showEditDialog(exercise);
    }

    @Override
    public void onExerciseLongClick(ExerciseTemplate exercise) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setMessage(exercise.getName())
                .setPositiveButton(R.string.delete, (d, w) -> viewModel.delete(exercise))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showEditDialog(@Nullable ExerciseTemplate exercise) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_exercise_edit, null);

        EditText nameEdit = dialogView.findViewById(R.id.edit_name);
        EditText noteEdit = dialogView.findViewById(R.id.edit_note);
        EditText linkEdit = dialogView.findViewById(R.id.edit_example_link);

        if (exercise != null) {
            nameEdit.setText(exercise.getName());
            noteEdit.setText(exercise.getNote());
            linkEdit.setText(exercise.getExampleLink());
        }

        String title = exercise == null ? getString(R.string.add_exercise) : getString(R.string.edit_exercise);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (d, w) -> {
                    String name = nameEdit.getText().toString().trim();
                    if (name.isEmpty())
                        return;

                    String note = noteEdit.getText().toString().trim();
                    String link = linkEdit.getText().toString().trim();

                    if (exercise == null) {
                        viewModel.insert(new ExerciseTemplate(name, note, link));
                    } else {
                        exercise.setName(name);
                        exercise.setNote(note);
                        exercise.setExampleLink(link);
                        viewModel.update(exercise);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}

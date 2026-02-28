package com.qprovep.exlog.ui.exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.ExerciseTemplate;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListFragment extends Fragment implements ExerciseAdapter.OnExerciseClickListener {

    private ExerciseViewModel viewModel;
    private ExerciseAdapter adapter;
    private TextView emptyText;
    private List<String> categoriesList = new ArrayList<>();

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

        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            categoriesList.clear();
            if (categories != null)
                categoriesList.addAll(categories);
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
        MaterialButton categoryBtn = dialogView.findViewById(R.id.btn_select_category);
        EditText noteEdit = dialogView.findViewById(R.id.edit_note);
        EditText linkEdit = dialogView.findViewById(R.id.edit_example_link);
        MaterialButton toggleBtn = dialogView.findViewById(R.id.btn_toggle_details);
        LinearLayout detailsSection = dialogView.findViewById(R.id.details_section);

        final String[] selectedCategory = { "" };

        toggleBtn.setOnClickListener(v -> {
            if (detailsSection.getVisibility() == View.GONE) {
                detailsSection.setVisibility(View.VISIBLE);
                toggleBtn.setText(R.string.less_details);
            } else {
                detailsSection.setVisibility(View.GONE);
                toggleBtn.setText(R.string.more_details);
            }
        });

        categoryBtn.setOnClickListener(v -> showCategoryPicker(categoryBtn, selectedCategory));

        if (exercise != null) {
            nameEdit.setText(exercise.getName());
            if (exercise.getCategory() != null && !exercise.getCategory().isEmpty()) {
                selectedCategory[0] = exercise.getCategory();
                categoryBtn.setText(exercise.getCategory());
            }
            noteEdit.setText(exercise.getNote());
            linkEdit.setText(exercise.getExampleLink());

            boolean hasDetails = (exercise.getNote() != null && !exercise.getNote().isEmpty())
                    || (exercise.getExampleLink() != null && !exercise.getExampleLink().isEmpty());
            if (hasDetails) {
                detailsSection.setVisibility(View.VISIBLE);
                toggleBtn.setText(R.string.less_details);
            }
        }

        String title = exercise == null ? getString(R.string.add_exercise) : getString(R.string.edit_exercise);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (d, w) -> {
                    String name = nameEdit.getText().toString().trim();
                    if (name.isEmpty())
                        return;

                    String category = selectedCategory[0];
                    String note = noteEdit.getText().toString().trim();
                    String link = linkEdit.getText().toString().trim();

                    if (exercise == null) {
                        viewModel.insert(new ExerciseTemplate(name, category, note, link));
                    } else {
                        exercise.setName(name);
                        exercise.setCategory(category);
                        exercise.setNote(note);
                        exercise.setExampleLink(link);
                        viewModel.update(exercise);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showCategoryPicker(MaterialButton categoryBtn, String[] selectedCategory) {
        View pickerView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_category_picker, null);

        EditText newCatEdit = pickerView.findViewById(R.id.edit_new_category);
        MaterialButton addBtn = pickerView.findViewById(R.id.btn_add_category);
        RecyclerView catRecycler = pickerView.findViewById(R.id.category_list);
        catRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        AlertDialog pickerDialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.select_category)
                .setView(pickerView)
                .setNeutralButton(R.string.no_category, (d, w) -> {
                    selectedCategory[0] = "";
                    categoryBtn.setText(R.string.no_category);
                })
                .setNegativeButton(R.string.cancel, null)
                .create();

        CategoryAdapter catAdapter = new CategoryAdapter(new CategoryAdapter.OnCategoryListener() {
            @Override
            public void onCategorySelected(String category) {
                selectedCategory[0] = category;
                categoryBtn.setText(category);
                pickerDialog.dismiss();
            }

            @Override
            public void onCategoryDeleted(String category) {
                viewModel.deleteCategory(category);
            }
        });

        catAdapter.setCategories(new ArrayList<>(categoriesList));
        catAdapter.setSelectedCategory(selectedCategory[0]);
        catRecycler.setAdapter(catAdapter);

        addBtn.setOnClickListener(v -> {
            String newCat = newCatEdit.getText().toString().trim();
            if (!newCat.isEmpty()) {
                viewModel.addCategory(newCat);
                catAdapter.addCategory(newCat);
                newCatEdit.setText("");
            }
        });

        pickerDialog.show();
    }
}

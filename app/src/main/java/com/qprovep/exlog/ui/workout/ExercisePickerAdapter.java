package com.qprovep.exlog.ui.workout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.ExerciseTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExercisePickerAdapter extends RecyclerView.Adapter<ExercisePickerAdapter.ViewHolder> {

    private List<ExerciseTemplate> allExercises = new ArrayList<>();
    private List<ExerciseTemplate> filteredExercises = new ArrayList<>();
    private final Set<Integer> selectedIds = new HashSet<>();
    private String nameQuery = "";
    private String categoryFilter = "";

    public void setExercises(List<ExerciseTemplate> exercises, Set<Integer> preSelectedIds) {
        allExercises = new ArrayList<>(exercises);
        selectedIds.clear();
        selectedIds.addAll(preSelectedIds);
        applyFilters();
    }

    public void filterByName(String query) {
        nameQuery = query == null ? "" : query.trim().toLowerCase();
        applyFilters();
    }

    public void filterByCategory(String category) {
        categoryFilter = category == null ? "" : category;
        applyFilters();
    }

    private void applyFilters() {
        filteredExercises.clear();
        for (ExerciseTemplate ex : allExercises) {
            boolean matchesName = nameQuery.isEmpty()
                    || ex.getName().toLowerCase().contains(nameQuery);

            boolean matchesCategory;
            if (categoryFilter.isEmpty()) {
                matchesCategory = true;
            } else if (categoryFilter.equals("\u2014")) {
                matchesCategory = ex.getCategory() == null || ex.getCategory().isEmpty();
            } else {
                matchesCategory = categoryFilter.equals(ex.getCategory());
            }

            if (matchesName && matchesCategory) {
                filteredExercises.add(ex);
            }
        }
        notifyDataSetChanged();
    }

    public Set<Integer> getSelectedIds() {
        return selectedIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_picker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(filteredExercises.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredExercises.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCheckBox checkbox;
        private final TextView nameText;
        private final TextView categoryText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            nameText = itemView.findViewById(R.id.exercise_name);
            categoryText = itemView.findViewById(R.id.exercise_category);

            View.OnClickListener toggle = v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION)
                    return;
                ExerciseTemplate ex = filteredExercises.get(pos);
                if (selectedIds.contains(ex.getId())) {
                    selectedIds.remove(ex.getId());
                    checkbox.setChecked(false);
                } else {
                    selectedIds.add(ex.getId());
                    checkbox.setChecked(true);
                }
            };

            checkbox.setOnClickListener(toggle);
            itemView.setOnClickListener(toggle);
        }

        void bind(ExerciseTemplate exercise) {
            nameText.setText(exercise.getName());
            checkbox.setChecked(selectedIds.contains(exercise.getId()));

            if (exercise.getCategory() != null && !exercise.getCategory().isEmpty()) {
                categoryText.setText(exercise.getCategory());
                categoryText.setVisibility(View.VISIBLE);
            } else {
                categoryText.setVisibility(View.GONE);
            }
        }
    }
}

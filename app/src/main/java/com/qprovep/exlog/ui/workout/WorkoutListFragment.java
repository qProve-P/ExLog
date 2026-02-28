package com.qprovep.exlog.ui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.WorkoutTemplate;

public class WorkoutListFragment extends Fragment implements WorkoutAdapter.OnWorkoutClickListener {

    private WorkoutViewModel viewModel;
    private WorkoutAdapter adapter;
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);

        emptyText = view.findViewById(R.id.empty_text);
        RecyclerView recyclerView = view.findViewById(R.id.workout_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new WorkoutAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_workout);
        fab.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("workoutId", -1);
            Navigation.findNavController(v).navigate(R.id.workoutDetailFragment, args);
        });

        viewModel.getAllWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            adapter.submitList(workouts);
            emptyText.setVisibility(workouts == null || workouts.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onWorkoutClick(WorkoutTemplate workout) {
        Bundle args = new Bundle();
        args.putInt("workoutId", workout.getId());
        Navigation.findNavController(requireView()).navigate(R.id.workoutDetailFragment, args);
    }

    @Override
    public void onWorkoutDeleteClick(WorkoutTemplate workout) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setMessage(workout.getName())
                .setPositiveButton(R.string.delete, (d, w) -> viewModel.deleteWorkout(workout))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}

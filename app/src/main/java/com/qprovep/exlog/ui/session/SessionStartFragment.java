package com.qprovep.exlog.ui.session;

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

import com.google.android.material.textfield.TextInputEditText;
import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.WorkoutTemplate;
import com.qprovep.exlog.ui.workout.WorkoutViewModel;

import java.util.ArrayList;
import java.util.List;

public class SessionStartFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_start, container, false);
    }

    private List<WorkoutTemplate> allWorkouts = new ArrayList<>();
    private StartWorkoutAdapter adapter;
    private TextView emptyText;
    private String currentQuery = "";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkoutViewModel workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);

        emptyText = view.findViewById(R.id.empty_text);
        RecyclerView recyclerView = view.findViewById(R.id.start_workout_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        TextInputEditText searchInput = view.findViewById(R.id.search_input);

        adapter = new StartWorkoutAdapter(workout -> {
            Bundle args = new Bundle();
            args.putInt("startWorkoutId", workout.getId());
            Navigation.findNavController(view).navigate(R.id.sessionFragment, args);
        });
        recyclerView.setAdapter(adapter);

        workoutViewModel.getAllWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            allWorkouts = workouts != null ? workouts : new ArrayList<>();
            filterWorkouts();
        });

        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                currentQuery = s.toString().trim().toLowerCase();
                filterWorkouts();
            }
        });
    }

    private void filterWorkouts() {
        if (allWorkouts.isEmpty()) {
            adapter.submitList(new ArrayList<>());
            emptyText.setVisibility(View.VISIBLE);
            return;
        }

        if (currentQuery.isEmpty()) {
            adapter.submitList(new ArrayList<>(allWorkouts));
            emptyText.setVisibility(View.GONE);
            return;
        }

        List<WorkoutTemplate> filtered = new ArrayList<>();
        for (WorkoutTemplate workout : allWorkouts) {
            if (workout.getName().toLowerCase().contains(currentQuery)) {
                filtered.add(workout);
            }
        }
        
        adapter.submitList(filtered);
        emptyText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }
}

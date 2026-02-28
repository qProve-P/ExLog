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

import com.qprovep.exlog.R;
import com.qprovep.exlog.ui.workout.WorkoutViewModel;

public class SessionStartFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkoutViewModel workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);

        TextView emptyText = view.findViewById(R.id.empty_text);
        RecyclerView recyclerView = view.findViewById(R.id.start_workout_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        StartWorkoutAdapter adapter = new StartWorkoutAdapter(workout -> {
            Bundle args = new Bundle();
            args.putInt("startWorkoutId", workout.getId());
            Navigation.findNavController(view).navigate(R.id.sessionFragment, args);
        });
        recyclerView.setAdapter(adapter);

        workoutViewModel.getAllWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            adapter.submitList(workouts);
            emptyText.setVisibility(workouts == null || workouts.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}

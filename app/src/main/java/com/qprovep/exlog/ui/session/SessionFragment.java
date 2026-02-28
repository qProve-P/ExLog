package com.qprovep.exlog.ui.session;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.WorkoutTemplate;

import java.util.Locale;

public class SessionFragment extends Fragment {

    private SessionViewModel viewModel;
    private SessionAdapter adapter;
    private TextView timerText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);

        MaterialToolbar toolbar = view.findViewById(R.id.session_toolbar);
        timerText = view.findViewById(R.id.session_timer);
        RecyclerView recyclerView = view.findViewById(R.id.session_exercises_recycler);
        MaterialButton finishBtn = view.findViewById(R.id.btn_finish_session);
        MaterialButton pauseResumeBtn = view.findViewById(R.id.btn_pause_resume);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SessionAdapter(viewModel);
        recyclerView.setAdapter(adapter);

        viewModel.getCurrentWorkout().observe(getViewLifecycleOwner(), template -> {
            if (template != null) {
                toolbar.setTitle(template.getName());
            } else {
                toolbar.setTitle(R.string.app_name);
            }
        });

        viewModel.getSessionExercises().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null) {
                adapter.setExercises(entries);
            }
        });

        viewModel.getElapsedTime().observe(getViewLifecycleOwner(), millis -> {
            long seconds = millis / 1000;
            long mins = seconds / 60;
            long secs = seconds % 60;
            timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
        });

        viewModel.isTimerPaused().observe(getViewLifecycleOwner(), paused -> {
            if (paused != null && paused) {
                pauseResumeBtn.setText("Resume");
                pauseResumeBtn.setIconResource(android.R.drawable.ic_media_play);
            } else {
                pauseResumeBtn.setText("Pause");
                pauseResumeBtn.setIconResource(android.R.drawable.ic_media_pause);
            }
        });

        pauseResumeBtn.setOnClickListener(v -> viewModel.togglePause());

        finishBtn.setOnClickListener(v -> {
            WorkoutTemplate current = viewModel.getCurrentWorkout().getValue();
            if (current == null) {
                Toast.makeText(requireContext(), "No active session", Toast.LENGTH_SHORT).show();
                return;
            }

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Finish Workout?")
                    .setMessage("Are you sure you want to finish this session? Incomplete sets will be discarded.")
                    .setPositiveButton("Finish", (dialog, which) -> {
                        viewModel.finishSession("");
                        Toast.makeText(requireContext(), "Session saved!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).popBackStack(R.id.templatesFragment, false);
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        if (getArguments() != null) {
            int startWorkoutId = getArguments().getInt("startWorkoutId", -1);
            if (startWorkoutId != -1) {
                viewModel.startSession(startWorkoutId);
                getArguments().remove("startWorkoutId");
            }
        }
    }
}

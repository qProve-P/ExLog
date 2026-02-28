package com.qprovep.exlog.ui.history;

import android.os.Bundle;
import android.text.format.DateFormat;
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

import com.google.android.material.appbar.MaterialToolbar;
import com.qprovep.exlog.R;

import java.util.Date;

public class SessionDetailFragment extends Fragment {

    private SessionDetailViewModel viewModel;
    private SessionDetailAdapter adapter;
    private MaterialToolbar toolbar;
    private TextView subtitleText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(SessionDetailViewModel.class);

        toolbar = view.findViewById(R.id.detail_toolbar);
        subtitleText = view.findViewById(R.id.detail_subtitle);
        RecyclerView recyclerView = view.findViewById(R.id.detail_recycler);

        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        adapter = new SessionDetailAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            int sessionId = getArguments().getInt("sessionId", -1);
            if (sessionId != -1) {
                viewModel.setSessionId(sessionId);
            }
        }

        viewModel.getWorkoutTemplate().observe(getViewLifecycleOwner(), wt -> {
            if (wt != null) {
                toolbar.setTitle(wt.getName());
            }
        });

        viewModel.getSessionData().observe(getViewLifecycleOwner(), sessionWithLogs -> {
            if (sessionWithLogs != null && sessionWithLogs.session != null) {
                long dateMs = sessionWithLogs.session.getDate();
                long durationMs = sessionWithLogs.session.getDurationMs();

                String dateStr = DateFormat.format("dd.MM yyyy - HH:mm", new Date(dateMs)).toString();
                String durStr = formatDuration(durationMs);

                subtitleText.setText(dateStr + " • " + durStr);
            }
        });

        viewModel.getExercisesData().observe(getViewLifecycleOwner(), exercises -> {
            if (exercises != null) {
                adapter.setExercises(exercises);
            }
        });
    }

    private String formatDuration(long durationMs) {
        long seconds = durationMs / 1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;

        if (h > 0) {
            return String.format("%dh %dm", h, m);
        } else {
            return String.format("%dm %ds", m, s);
        }
    }
}

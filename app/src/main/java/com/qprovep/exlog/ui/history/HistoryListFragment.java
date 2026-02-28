package com.qprovep.exlog.ui.history;

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
import com.qprovep.exlog.R;

public class HistoryListFragment extends Fragment {

    private HistoryViewModel viewModel;
    private HistoryAdapter adapter;
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireParentFragment()).get(HistoryViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.history_recycler);
        emptyText = view.findViewById(R.id.empty_history_text);

        com.google.android.material.button.MaterialButton btnPickDate = view.findViewById(R.id.btn_pick_date);
        com.google.android.material.button.MaterialButton btnClearDate = view.findViewById(R.id.btn_clear_date);

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd.MM yyyy",
                java.util.Locale.getDefault());

        btnPickDate.setOnClickListener(v -> {
            com.google.android.material.datepicker.MaterialDatePicker<Long> picker = com.google.android.material.datepicker.MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Select date")
                    .setSelection(com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                viewModel.setSelectedDate(selection);
                btnPickDate.setText(dateFormat.format(new java.util.Date(selection)));
                btnClearDate.setVisibility(View.VISIBLE);
            });

            picker.show(getChildFragmentManager(), "DATE_PICKER");
        });

        btnClearDate.setOnClickListener(v -> {
            viewModel.setSelectedDate(null);
            btnPickDate.setText(getString(R.string.filter_by_date));
            btnClearDate.setVisibility(View.GONE);
        });

        adapter = new HistoryAdapter(
                item -> {
                    Bundle args = new Bundle();
                    args.putInt("sessionId", item.sessionId);
                    Navigation.findNavController(requireParentFragment().requireView())
                            .navigate(R.id.action_history_to_sessionDetail, args);
                },
                item -> {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Delete Session")
                            .setMessage("Are you sure you want to delete this session? This action cannot be undone.")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Delete", (dialog, which) -> {
                                viewModel.deleteSession(item.sessionId);
                            })
                            .show();
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel.getSessionHistory().observe(getViewLifecycleOwner(), sessions -> {
            adapter.setSessions(sessions);
            if (sessions == null || sessions.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
}

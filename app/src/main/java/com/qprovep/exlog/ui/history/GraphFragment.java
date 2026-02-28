package com.qprovep.exlog.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.google.android.material.chip.ChipGroup;
import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.ExerciseTemplate;

import java.util.ArrayList;
import java.util.List;

public class GraphFragment extends Fragment {

    private GraphViewModel viewModel;
    private AutoCompleteTextView exerciseDropdown;
    private ChipGroup timePeriodGroup;
    private LineChart lineChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(GraphViewModel.class);

        exerciseDropdown = view.findViewById(R.id.graph_exercise_dropdown);
        timePeriodGroup = view.findViewById(R.id.graph_time_period_group);
        lineChart = view.findViewById(R.id.graph_line_chart);

        setupChart();

        final boolean[] userInteracting = { false };

        timePeriodGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty() && userInteracting[0]) {
                int id = checkedIds.get(0);
                if (id == R.id.chip_1w)
                    viewModel.setTimePeriodFilter(GraphViewModel.TimePeriod.ONE_WEEK);
                else if (id == R.id.chip_1m)
                    viewModel.setTimePeriodFilter(GraphViewModel.TimePeriod.ONE_MONTH);
                else if (id == R.id.chip_1y)
                    viewModel.setTimePeriodFilter(GraphViewModel.TimePeriod.ONE_YEAR);
                else if (id == R.id.chip_all)
                    viewModel.setTimePeriodFilter(GraphViewModel.TimePeriod.ALL_TIME);
                else if (id == R.id.chip_custom) {
                    com.google.android.material.datepicker.MaterialDatePicker<androidx.core.util.Pair<Long, Long>> rangePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder
                            .dateRangePicker()
                            .setTitleText("Select date range")
                            .build();

                    rangePicker.addOnPositiveButtonClickListener(selection -> {
                        viewModel.setCustomDateRange(selection.first, selection.second);
                    });

                    rangePicker.show(getChildFragmentManager(), "DATE_RANGE_PICKER");
                }
            }
        });

        view.post(() -> userInteracting[0] = true);

        com.google.android.material.chip.Chip weightToggle = view.findViewById(R.id.chip_toggle_weight);
        com.google.android.material.chip.Chip repsToggle = view.findViewById(R.id.chip_toggle_reps);

        weightToggle.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.setShowWeight(isChecked));
        repsToggle.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.setShowReps(isChecked));

        viewModel.getAllExercises().observe(getViewLifecycleOwner(), exercises -> {
            if (exercises != null && !exercises.isEmpty()) {
                List<String> names = new ArrayList<>();
                for (ExerciseTemplate ex : exercises) {
                    names.add(ex.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, names);
                exerciseDropdown.setAdapter(adapter);

                if (viewModel.getSelectedExerciseId().getValue() == null) {
                    ExerciseTemplate first = exercises.get(0);
                    exerciseDropdown.setText(first.getName(), false);
                    viewModel.setSelectedExerciseId(first.getId());
                }

                exerciseDropdown.setOnItemClickListener((parent, view1, position, id) -> {
                    String selectedName = (String) parent.getItemAtPosition(position);
                    for (ExerciseTemplate ex : exercises) {
                        if (ex.getName().equals(selectedName)) {
                            viewModel.setSelectedExerciseId(ex.getId());
                            break;
                        }
                    }
                });

                exerciseDropdown.addTextChangedListener(new android.text.TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(android.text.Editable s) {
                        String typed = s.toString();
                        for (ExerciseTemplate ex : exercises) {
                            if (ex.getName().equalsIgnoreCase(typed)) {
                                viewModel.setSelectedExerciseId(ex.getId());
                                break;
                            }
                        }
                    }
                });
            }
        });

        viewModel.getChartData().observe(getViewLifecycleOwner(), bundle -> {
            boolean hasWeight = bundle.weightEntries != null && !bundle.weightEntries.isEmpty();
            boolean hasReps = bundle.repsEntries != null && !bundle.repsEntries.isEmpty();

            if (hasWeight || hasReps) {
                com.github.mikephil.charting.data.LineData lineData = new com.github.mikephil.charting.data.LineData();

                int primaryRed = ContextCompat.getColor(requireContext(), R.color.primary);
                int primaryBlue = ContextCompat.getColor(requireContext(), R.color.secondary);

                if (hasWeight) {
                    com.github.mikephil.charting.data.LineDataSet weightSet = new com.github.mikephil.charting.data.LineDataSet(
                            bundle.weightEntries, "Weight (kg)");
                    weightSet.setColor(primaryRed);
                    weightSet.setValueTextColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
                    weightSet.setCircleColor(primaryRed);
                    weightSet.setLineWidth(2f);
                    weightSet.setCircleRadius(4f);
                    weightSet.setDrawValues(false);
                    weightSet.setAxisDependency(com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT);
                    lineData.addDataSet(weightSet);
                }

                if (hasReps) {
                    com.github.mikephil.charting.data.LineDataSet repsSet = new com.github.mikephil.charting.data.LineDataSet(
                            bundle.repsEntries, "Reps");
                    repsSet.setColor(primaryBlue);
                    repsSet.setValueTextColor(ContextCompat.getColor(requireContext(), android.R.color.transparent));
                    repsSet.setCircleColor(primaryBlue);
                    repsSet.setLineWidth(2f);
                    repsSet.setCircleRadius(4f);
                    repsSet.setDrawValues(false);
                    repsSet.setAxisDependency(com.github.mikephil.charting.components.YAxis.AxisDependency.RIGHT);
                    lineData.addDataSet(repsSet);
                }

                lineChart.setData(lineData);
                lineChart.invalidate();

                lineChart.getAxisRight().setEnabled(hasReps);
            } else {
                lineChart.clear();
                lineChart.setNoDataText("No logged data for this period.");
                lineChart.invalidate();
                lineChart.getAxisRight().setEnabled(false);
            }
        });

    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);

        int textColor = ContextCompat.getColor(requireContext(), android.R.color.tab_indicator_text);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            private final java.text.SimpleDateFormat mFormat = new java.text.SimpleDateFormat("MMM dd",
                    java.util.Locale.getDefault());

            @Override
            public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
                return mFormat.format(new java.util.Date((long) value));
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(textColor);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setTextColor(textColor);
    }
}

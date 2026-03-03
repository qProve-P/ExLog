package com.qprovep.exlog.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.github.mikephil.charting.data.Entry;
import com.qprovep.exlog.data.AppDatabase;
import com.qprovep.exlog.data.dao.ExerciseTemplateDao;
import com.qprovep.exlog.data.dao.SetLogDao;
import com.qprovep.exlog.data.entity.ExerciseTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GraphViewModel extends AndroidViewModel {

    public enum TimePeriod {
        ONE_WEEK, ONE_MONTH, THREE_MONTHS, SIX_MONTHS, ONE_YEAR, ALL_TIME, CUSTOM
    }

    private final ExerciseTemplateDao exerciseDao;
    private final SetLogDao setLogDao;

    private final LiveData<List<ExerciseTemplate>> allExercises;
    private final MutableLiveData<Integer> selectedExerciseId = new MutableLiveData<>();
    private final MutableLiveData<TimePeriod> timePeriodFilter = new MutableLiveData<>(TimePeriod.ALL_TIME);

    private final MutableLiveData<Boolean> showWeight = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> showReps = new MutableLiveData<>(false);

    private long customStartTime = 0;
    private long customEndTime = Long.MAX_VALUE;

    private final MutableLiveData<Trigger> computeTrigger = new MutableLiveData<>();

    public static class ChartDataBundle {
        public List<Entry> weightEntries = new ArrayList<>();
        public List<Entry> repsEntries = new ArrayList<>();
    }

    private final MutableLiveData<ChartDataBundle> chartData = new MutableLiveData<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public GraphViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        exerciseDao = db.exerciseTemplateDao();
        setLogDao = db.setLogDao();

        allExercises = exerciseDao.getAllExercises();

        selectedExerciseId.observeForever(id -> updateTrigger());
        timePeriodFilter.observeForever(period -> updateTrigger());
        showWeight.observeForever(show -> updateTrigger());
        showReps.observeForever(show -> updateTrigger());

        computeTrigger.observeForever(trigger -> {
            if (trigger != null && trigger.exerciseId != null && trigger.period != null) {
                computeChartData(trigger.exerciseId, trigger.period, trigger.showWeight, trigger.showReps);
            }
        });
    }

    private void updateTrigger() {
        computeTrigger.setValue(new Trigger(
                selectedExerciseId.getValue(),
                timePeriodFilter.getValue(),
                Boolean.TRUE.equals(showWeight.getValue()),
                Boolean.TRUE.equals(showReps.getValue())));
    }

    public LiveData<List<ExerciseTemplate>> getAllExercises() {
        return allExercises;
    }

    public MutableLiveData<Integer> getSelectedExerciseId() {
        return selectedExerciseId;
    }

    public void setSelectedExerciseId(int id) {
        if (selectedExerciseId.getValue() == null || selectedExerciseId.getValue() != id) {
            selectedExerciseId.setValue(id);
        }
    }

    public MutableLiveData<TimePeriod> getTimePeriodFilter() {
        return timePeriodFilter;
    }

    public void setTimePeriodFilter(TimePeriod period) {
        if (timePeriodFilter.getValue() != period) {
            timePeriodFilter.setValue(period);
        }
    }

    public void setCustomDateRange(long startMillis, long endMillis) {
        this.customStartTime = startMillis;
        this.customEndTime = endMillis;
        setTimePeriodFilter(TimePeriod.CUSTOM);
        if (timePeriodFilter.getValue() == TimePeriod.CUSTOM) {
            updateTrigger();
        }
    }

    public MutableLiveData<Boolean> getShowWeight() {
        return showWeight;
    }

    public void setShowWeight(boolean show) {
        if (!Boolean.valueOf(show).equals(showWeight.getValue())) {
            showWeight.setValue(show);
        }
    }

    public MutableLiveData<Boolean> getShowReps() {
        return showReps;
    }

    public void setShowReps(boolean show) {
        if (!Boolean.valueOf(show).equals(showReps.getValue())) {
            showReps.setValue(show);
        }
    }

    public LiveData<ChartDataBundle> getChartData() {
        return chartData;
    }

    private void computeChartData(int exerciseId, TimePeriod period, boolean showW, boolean showR) {
        executor.execute(() -> {
            long startTime;
            long endTimeMs = Long.MAX_VALUE;
            if (period == TimePeriod.CUSTOM) {
                startTime = customStartTime;
                endTimeMs = customEndTime;
            } else {
                startTime = getStartTimeForPeriod(period);
            }

            List<SetLogDao.SetLogWithDate> logs = setLogDao.getSetLogsWithDateForExerciseSync(exerciseId, startTime);

            ChartDataBundle bundle = new ChartDataBundle();

            for (SetLogDao.SetLogWithDate log : logs) {
                if (period == TimePeriod.CUSTOM && log.date > endTimeMs)
                    continue;
                if (log.setLog.getWeight() >= 0) {
                    if (showW) {
                        bundle.weightEntries.add(new Entry((float) log.date, (float) log.setLog.getWeight()));
                    }
                    if (showR) {
                        bundle.repsEntries.add(new Entry((float) log.date, (float) log.setLog.getReps()));
                    }
                }
            }

            chartData.postValue(bundle);
        });
    }

    private long getStartTimeForPeriod(TimePeriod period) {
        if (period == TimePeriod.ALL_TIME) {
            return 0;
        }

        Calendar cal = Calendar.getInstance();
        switch (period) {
            case ONE_WEEK:
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case ONE_MONTH:
                cal.add(Calendar.MONTH, -1);
                break;
            case THREE_MONTHS:
                cal.add(Calendar.MONTH, -3);
                break;
            case SIX_MONTHS:
                cal.add(Calendar.MONTH, -6);
                break;
            case ONE_YEAR:
                cal.add(Calendar.YEAR, -1);
                break;
        }
        return cal.getTimeInMillis();
    }

    private static class Trigger {
        Integer exerciseId;
        TimePeriod period;
        boolean showWeight;
        boolean showReps;

        Trigger(Integer exerciseId, TimePeriod period, boolean showWeight, boolean showReps) {
            this.exerciseId = exerciseId;
            this.period = period;
            this.showWeight = showWeight;
            this.showReps = showReps;
        }
    }
}

package com.qprovep.exlog.ui.session;

import android.app.Application;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.qprovep.exlog.data.AppDatabase;
import com.qprovep.exlog.data.dao.SessionDao;
import com.qprovep.exlog.data.dao.SetLogDao;
import com.qprovep.exlog.data.dao.WorkoutTemplateDao;
import com.qprovep.exlog.data.entity.ExerciseTemplate;
import com.qprovep.exlog.data.entity.Session;
import com.qprovep.exlog.data.entity.SetLog;
import com.qprovep.exlog.data.entity.WorkoutTemplate;
import com.qprovep.exlog.data.relation.WorkoutWithExercises;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionViewModel extends AndroidViewModel {

    private final SessionDao sessionDao;
    private final SetLogDao setLogDao;
    private final WorkoutTemplateDao workoutTemplateDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<WorkoutTemplate> currentWorkout = new MutableLiveData<>();
    private final MutableLiveData<List<SessionExerciseEntry>> sessionExercises = new MutableLiveData<>(
            new ArrayList<>());

    private long startTimeMillis = 0;
    private final MutableLiveData<Long> elapsedTime = new MutableLiveData<>(0L);
    private boolean isTimerRunning = false;
    private Thread timerThread;

    public SessionViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        sessionDao = db.sessionDao();
        setLogDao = db.setLogDao();
        workoutTemplateDao = db.workoutTemplateDao();
    }

    public LiveData<WorkoutTemplate> getCurrentWorkout() {
        return currentWorkout;
    }

    public LiveData<List<SessionExerciseEntry>> getSessionExercises() {
        return sessionExercises;
    }

    public LiveData<Long> getElapsedTime() {
        return elapsedTime;
    }

    public void startSession(int workoutId) {
        if (isTimerRunning)
            return;

        executor.execute(() -> {
            WorkoutWithExercises data = workoutTemplateDao.getWorkoutWithExercisesSync(workoutId);
            if (data == null)
                return;

            currentWorkout.postValue(data.workoutTemplate);

            List<SessionExerciseEntry> entries = new ArrayList<>();
            for (var we : data.exercises) {
                List<SetEntry> sets = new ArrayList<>();
                for (int i = 0; i < we.workoutExercise.getTargetSets(); i++) {
                    sets.add(new SetEntry(i + 1, (float) we.workoutExercise.getReferenceWeight(),
                            we.workoutExercise.getTargetReps()));
                }
                entries.add(new SessionExerciseEntry(we.exerciseTemplate, sets));
            }
            sessionExercises.postValue(entries);

            startTimeMillis = SystemClock.elapsedRealtime();
            isTimerRunning = true;
            startTimerThread();
        });
    }

    private void startTimerThread() {
        timerThread = new Thread(() -> {
            while (isTimerRunning) {
                long elapsed = SystemClock.elapsedRealtime() - startTimeMillis;
                elapsedTime.postValue(elapsed);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        timerThread.start();
    }

    public void updateSet(int exerciseIndex, int setIndex, float weight, int reps, boolean isCompleted) {
        List<SessionExerciseEntry> current = sessionExercises.getValue();
        if (current == null)
            return;

        SetEntry set = current.get(exerciseIndex).sets.get(setIndex);
        set.weight = weight;
        set.reps = reps;
        set.isCompleted = isCompleted;

        sessionExercises.postValue(current);
    }

    public void finishSession(String notes) {
        if (!isTimerRunning)
            return;

        isTimerRunning = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }

        long durationMs = SystemClock.elapsedRealtime() - startTimeMillis;
        WorkoutTemplate template = currentWorkout.getValue();
        List<SessionExerciseEntry> entries = sessionExercises.getValue();

        if (template == null || entries == null)
            return;

        executor.execute(() -> {
            Session session = new Session(template.getId(), new Date().getTime(), durationMs, notes);
            long sessionId = sessionDao.insert(session);

            List<SetLog> logsToSave = new ArrayList<>();
            for (SessionExerciseEntry entry : entries) {
                for (SetEntry set : entry.sets) {
                    if (set.isCompleted) {
                        logsToSave.add(new SetLog((int) sessionId, entry.exercise.getId(), set.setNumber, set.reps,
                                set.weight));
                    }
                }
            }

            if (!logsToSave.isEmpty()) {
                setLogDao.insertAll(logsToSave);
            }

            currentWorkout.postValue(null);
            sessionExercises.postValue(new ArrayList<>());
            elapsedTime.postValue(0L);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        isTimerRunning = false;
        if (timerThread != null)
            timerThread.interrupt();
    }

    public static class SessionExerciseEntry {
        public ExerciseTemplate exercise;
        public List<SetEntry> sets;

        public SessionExerciseEntry(ExerciseTemplate exercise, List<SetEntry> sets) {
            this.exercise = exercise;
            this.sets = sets;
        }
    }

    public static class SetEntry {
        public int setNumber;
        public float weight;
        public int reps;
        public boolean isCompleted;

        public SetEntry(int setNumber, float weight, int reps) {
            this.setNumber = setNumber;
            this.weight = weight;
            this.reps = reps;
            this.isCompleted = false;
        }
    }
}

package com.qprovep.exlog.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.qprovep.exlog.data.AppDatabase;
import com.qprovep.exlog.data.dao.ExerciseTemplateDao;
import com.qprovep.exlog.data.dao.SessionDao;
import com.qprovep.exlog.data.dao.WorkoutTemplateDao;
import com.qprovep.exlog.data.entity.ExerciseTemplate;
import com.qprovep.exlog.data.entity.SetLog;
import com.qprovep.exlog.data.entity.WorkoutTemplate;
import com.qprovep.exlog.data.relation.SessionWithSetLogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionDetailViewModel extends AndroidViewModel {

    private final SessionDao sessionDao;
    private final WorkoutTemplateDao workoutDao;
    private final ExerciseTemplateDao exerciseDao;
    private final MutableLiveData<Integer> sessionIdInput = new MutableLiveData<>();
    private final LiveData<SessionWithSetLogs> sessionData;

    private final MutableLiveData<WorkoutTemplate> workoutTemplate = new MutableLiveData<>();
    private final MutableLiveData<List<DetailExerciseEntry>> exercisesData = new MutableLiveData<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SessionDetailViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        sessionDao = db.sessionDao();
        workoutDao = db.workoutTemplateDao();
        exerciseDao = db.exerciseTemplateDao();

        sessionData = Transformations.switchMap(sessionIdInput, id -> {
            return sessionDao.getSessionWithSetLogs(id);
        });

        sessionData.observeForever(sessionWithLogs -> {
            if (sessionWithLogs != null) {
                executor.execute(() -> {
                    com.qprovep.exlog.data.relation.WorkoutWithExercises workoutData = workoutDao
                            .getWorkoutWithExercisesSync(sessionWithLogs.session.getWorkoutTemplateId());

                    if (workoutData != null) {
                        workoutTemplate.postValue(workoutData.workoutTemplate);

                        Map<Integer, DetailExerciseEntry> map = new HashMap<>();
                        List<DetailExerciseEntry> orderedList = new ArrayList<>();

                        for (com.qprovep.exlog.data.relation.WorkoutExerciseWithTemplate exWithTemplate : workoutData.exercises) {
                            ExerciseTemplate ex = exWithTemplate.exerciseTemplate;
                            if (ex != null && !map.containsKey(ex.getId())) {
                                DetailExerciseEntry entry = new DetailExerciseEntry(ex,
                                        exWithTemplate.workoutExercise.getTargetSets());
                                map.put(ex.getId(), entry);
                                orderedList.add(entry);
                            }
                        }

                        for (SetLog set : sessionWithLogs.setLogs) {
                            int exId = set.getExerciseTemplateId();
                            DetailExerciseEntry entry = map.get(exId);
                            if (entry == null) {
                                ExerciseTemplate ex = exerciseDao.getById(exId);
                                if (ex != null) {
                                    entry = new DetailExerciseEntry(ex, 0);
                                    map.put(exId, entry);
                                    orderedList.add(entry);
                                }
                            }
                            if (entry != null) {
                                entry.sets.add(set);
                            }
                        }

                        for (DetailExerciseEntry entry : orderedList) {
                            entry.sets.sort((s1, s2) -> Integer.compare(s1.getSetNumber(), s2.getSetNumber()));

                            if (entry.targetSets > entry.sets.size()) {
                                int existingCount = entry.sets.size();
                                for (int i = existingCount + 1; i <= entry.targetSets; i++) {
                                    SetLog skippedSet = new SetLog(0, 0, i, -1, -1.0);
                                    entry.sets.add(skippedSet);
                                }
                            }
                        }

                        exercisesData.postValue(orderedList);
                    }
                });
            }
        });
    }

    public void setSessionId(int id) {
        if (sessionIdInput.getValue() == null || sessionIdInput.getValue() != id) {
            sessionIdInput.setValue(id);
        }
    }

    public LiveData<SessionWithSetLogs> getSessionData() {
        return sessionData;
    }

    public LiveData<WorkoutTemplate> getWorkoutTemplate() {
        return workoutTemplate;
    }

    public LiveData<List<DetailExerciseEntry>> getExercisesData() {
        return exercisesData;
    }

    public static class DetailExerciseEntry {
        public final ExerciseTemplate exercise;
        public final int targetSets;
        public final List<SetLog> sets = new ArrayList<>();

        public DetailExerciseEntry(ExerciseTemplate exercise, int targetSets) {
            this.exercise = exercise;
            this.targetSets = targetSets;
        }
    }
}

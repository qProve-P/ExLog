package com.qprovep.exlog.ui.exercise;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.qprovep.exlog.data.AppDatabase;
import com.qprovep.exlog.data.dao.ExerciseTemplateDao;
import com.qprovep.exlog.data.entity.ExerciseTemplate;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExerciseViewModel extends AndroidViewModel {

    private final ExerciseTemplateDao dao;
    private final LiveData<List<ExerciseTemplate>> allExercises;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ExerciseViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.exerciseTemplateDao();
        allExercises = dao.getAllExercises();
    }

    public LiveData<List<ExerciseTemplate>> getAllExercises() {
        return allExercises;
    }

    public void insert(ExerciseTemplate exercise) {
        executor.execute(() -> dao.insert(exercise));
    }

    public void update(ExerciseTemplate exercise) {
        executor.execute(() -> dao.update(exercise));
    }

    public void delete(ExerciseTemplate exercise) {
        executor.execute(() -> dao.delete(exercise));
    }
}

package com.qprovep.exlog.ui.exercise;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.qprovep.exlog.data.AppDatabase;
import com.qprovep.exlog.data.dao.CategoryDao;
import com.qprovep.exlog.data.dao.ExerciseTemplateDao;
import com.qprovep.exlog.data.entity.Category;
import com.qprovep.exlog.data.entity.ExerciseTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExerciseViewModel extends AndroidViewModel {

    private final ExerciseTemplateDao dao;
    private final CategoryDao categoryDao;
    private final LiveData<List<ExerciseTemplate>> allExercises;
    private final LiveData<List<String>> allCategoryNames;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ExerciseViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.exerciseTemplateDao();
        categoryDao = db.categoryDao();
        allExercises = dao.getAllExercises();
        allCategoryNames = Transformations.map(categoryDao.getAllCategories(), categories -> {
            List<String> names = new ArrayList<>();
            for (Category c : categories) {
                names.add(c.getName());
            }
            return names;
        });
    }

    public LiveData<List<ExerciseTemplate>> getAllExercises() {
        return allExercises;
    }

    public LiveData<List<String>> getAllCategories() {
        return allCategoryNames;
    }

    public void addCategory(String name) {
        executor.execute(() -> categoryDao.insert(new Category(name)));
    }

    public void deleteCategory(String name) {
        executor.execute(() -> categoryDao.deleteByName(name));
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

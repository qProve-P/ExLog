package com.qprovep.exlog.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.qprovep.exlog.data.entity.ExerciseTemplate;

import java.util.List;

@Dao
public interface ExerciseTemplateDao {

    @Insert
    long insert(ExerciseTemplate exercise);

    @Update
    void update(ExerciseTemplate exercise);

    @Delete
    void delete(ExerciseTemplate exercise);

    @Query("SELECT * FROM exercise_templates ORDER BY name ASC")
    LiveData<List<ExerciseTemplate>> getAllExercises();

    @Query("SELECT * FROM exercise_templates WHERE id = :id")
    ExerciseTemplate getById(int id);

    @Query("SELECT * FROM exercise_templates WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<ExerciseTemplate>> search(String query);
}

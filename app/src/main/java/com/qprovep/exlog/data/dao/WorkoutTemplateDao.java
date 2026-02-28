package com.qprovep.exlog.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.qprovep.exlog.data.entity.WorkoutTemplate;
import com.qprovep.exlog.data.relation.WorkoutWithExercises;

import java.util.List;

@Dao
public interface WorkoutTemplateDao {

    @Insert
    long insert(WorkoutTemplate workout);

    @Update
    void update(WorkoutTemplate workout);

    @Delete
    void delete(WorkoutTemplate workout);

    @Query("SELECT * FROM workout_templates ORDER BY name ASC")
    LiveData<List<WorkoutTemplate>> getAllWorkouts();

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    WorkoutTemplate getById(int id);

    @Transaction
    @Query("SELECT * FROM workout_templates WHERE id = :id")
    LiveData<WorkoutWithExercises> getWorkoutWithExercises(int id);

    @Transaction
    @Query("SELECT * FROM workout_templates ORDER BY name ASC")
    LiveData<List<WorkoutWithExercises>> getAllWorkoutsWithExercises();
}

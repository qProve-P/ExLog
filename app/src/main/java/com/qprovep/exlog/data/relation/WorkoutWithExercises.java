package com.qprovep.exlog.data.relation;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.qprovep.exlog.data.entity.ExerciseTemplate;
import com.qprovep.exlog.data.entity.WorkoutExercise;
import com.qprovep.exlog.data.entity.WorkoutTemplate;

import java.util.List;

public class WorkoutWithExercises {

    @Embedded
    public WorkoutTemplate workoutTemplate;

    @Relation(entity = WorkoutExercise.class, parentColumn = "id", entityColumn = "workoutTemplateId")
    public List<WorkoutExerciseWithTemplate> exercises;
}

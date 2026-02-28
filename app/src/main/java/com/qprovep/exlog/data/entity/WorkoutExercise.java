package com.qprovep.exlog.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_exercises", foreignKeys = {
        @ForeignKey(entity = WorkoutTemplate.class, parentColumns = "id", childColumns = "workoutTemplateId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = ExerciseTemplate.class, parentColumns = "id", childColumns = "exerciseTemplateId", onDelete = ForeignKey.CASCADE)
}, indices = {
        @Index("workoutTemplateId"),
        @Index("exerciseTemplateId")
})
public class WorkoutExercise {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int workoutTemplateId;
    private int exerciseTemplateId;
    private int orderIndex;
    private double referenceWeight;
    private int targetSets;
    private int targetReps;

    public WorkoutExercise(int workoutTemplateId, int exerciseTemplateId,
            int orderIndex, double referenceWeight,
            int targetSets, int targetReps) {
        this.workoutTemplateId = workoutTemplateId;
        this.exerciseTemplateId = exerciseTemplateId;
        this.orderIndex = orderIndex;
        this.referenceWeight = referenceWeight;
        this.targetSets = targetSets;
        this.targetReps = targetReps;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkoutTemplateId() {
        return workoutTemplateId;
    }

    public void setWorkoutTemplateId(int workoutTemplateId) {
        this.workoutTemplateId = workoutTemplateId;
    }

    public int getExerciseTemplateId() {
        return exerciseTemplateId;
    }

    public void setExerciseTemplateId(int exerciseTemplateId) {
        this.exerciseTemplateId = exerciseTemplateId;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public double getReferenceWeight() {
        return referenceWeight;
    }

    public void setReferenceWeight(double referenceWeight) {
        this.referenceWeight = referenceWeight;
    }

    public int getTargetSets() {
        return targetSets;
    }

    public void setTargetSets(int targetSets) {
        this.targetSets = targetSets;
    }

    public int getTargetReps() {
        return targetReps;
    }

    public void setTargetReps(int targetReps) {
        this.targetReps = targetReps;
    }
}

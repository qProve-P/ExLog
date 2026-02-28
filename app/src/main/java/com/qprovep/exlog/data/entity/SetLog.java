package com.qprovep.exlog.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "set_logs", foreignKeys = {
        @ForeignKey(entity = Session.class, parentColumns = "id", childColumns = "sessionId", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = ExerciseTemplate.class, parentColumns = "id", childColumns = "exerciseTemplateId", onDelete = ForeignKey.CASCADE)
}, indices = {
        @Index("sessionId"),
        @Index("exerciseTemplateId")
})
public class SetLog {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int sessionId;
    private int exerciseTemplateId;
    private int setNumber;
    private int reps;
    private double weight;

    public SetLog(int sessionId, int exerciseTemplateId, int setNumber, int reps, double weight) {
        this.sessionId = sessionId;
        this.exerciseTemplateId = exerciseTemplateId;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getExerciseTemplateId() {
        return exerciseTemplateId;
    }

    public void setExerciseTemplateId(int exerciseTemplateId) {
        this.exerciseTemplateId = exerciseTemplateId;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}

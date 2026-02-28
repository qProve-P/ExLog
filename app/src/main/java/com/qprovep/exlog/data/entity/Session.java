package com.qprovep.exlog.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions", foreignKeys = {
        @ForeignKey(entity = WorkoutTemplate.class, parentColumns = "id", childColumns = "workoutTemplateId", onDelete = ForeignKey.SET_NULL)
}, indices = {
        @Index("workoutTemplateId")
})
public class Session {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private Integer workoutTemplateId;
    private long date; // timestamp in millis
    private long durationMs;
    private String notes;

    public Session(Integer workoutTemplateId, long date, long durationMs, String notes) {
        this.workoutTemplateId = workoutTemplateId;
        this.date = date;
        this.durationMs = durationMs;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getWorkoutTemplateId() {
        return workoutTemplateId;
    }

    public void setWorkoutTemplateId(Integer workoutTemplateId) {
        this.workoutTemplateId = workoutTemplateId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

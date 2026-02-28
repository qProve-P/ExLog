package com.qprovep.exlog.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_templates")
public class WorkoutTemplate {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    public WorkoutTemplate(String name) {
        this.name = name;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

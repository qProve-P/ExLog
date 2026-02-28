package com.qprovep.exlog.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercise_templates")
public class ExerciseTemplate {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String note;
    private String exampleLink;

    public ExerciseTemplate(String name, String note, String exampleLink) {
        this.name = name;
        this.note = note;
        this.exampleLink = exampleLink;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getExampleLink() {
        return exampleLink;
    }

    public void setExampleLink(String exampleLink) {
        this.exampleLink = exampleLink;
    }
}

package com.example.exercise.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "check_in_records")
public class CheckInRecord {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String date;
    private String exerciseType;
    private int durationMinutes;
    private int calories;
    private String note;
    private long timestamp;

    public CheckInRecord() {}

    @androidx.room.Ignore
    public CheckInRecord(String date, String exerciseType, int durationMinutes, int calories, String note) {
        this.date = date;
        this.exerciseType = exerciseType;
        this.durationMinutes = durationMinutes;
        this.calories = calories;
        this.note = note;
        this.timestamp = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
package com.example.exercise.data.model;

public class FitnessCourse {

    private String name;
    private String description;
    private String difficulty;
    private int durationMinutes;
    private int caloriesBurn;
    private int imageResId;
    private String category;

    public FitnessCourse(String name, String description, String difficulty,
                         int durationMinutes, int caloriesBurn, int imageResId, String category) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.durationMinutes = durationMinutes;
        this.caloriesBurn = caloriesBurn;
        this.imageResId = imageResId;
        this.category = category;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public int getCaloriesBurn() { return caloriesBurn; }
    public void setCaloriesBurn(int caloriesBurn) { this.caloriesBurn = caloriesBurn; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
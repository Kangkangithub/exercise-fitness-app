package com.example.exercise.data.model;

public class UserProfile {

    private String nickname;
    private String avatar;
    private float weight;
    private float height;
    private int dailyGoalMinutes;
    private int dailyGoalCalories;
    private int totalCheckIns;
    private int totalMinutes;
    private int streakDays;

    public UserProfile() {
        this.nickname = "运动达人";
        this.avatar = "";
        this.weight = 65.0f;
        this.height = 170.0f;
        this.dailyGoalMinutes = 30;
        this.dailyGoalCalories = 300;
        this.totalCheckIns = 0;
        this.totalMinutes = 0;
        this.streakDays = 0;
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    public int getDailyGoalMinutes() { return dailyGoalMinutes; }
    public void setDailyGoalMinutes(int dailyGoalMinutes) { this.dailyGoalMinutes = dailyGoalMinutes; }
    public int getDailyGoalCalories() { return dailyGoalCalories; }
    public void setDailyGoalCalories(int dailyGoalCalories) { this.dailyGoalCalories = dailyGoalCalories; }
    public int getTotalCheckIns() { return totalCheckIns; }
    public void setTotalCheckIns(int totalCheckIns) { this.totalCheckIns = totalCheckIns; }
    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }
    public int getStreakDays() { return streakDays; }
    public void setStreakDays(int streakDays) { this.streakDays = streakDays; }
}
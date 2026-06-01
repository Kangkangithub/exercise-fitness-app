package com.example.exercise.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final String PREF_NAME = "exercise_prefs";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_DAILY_GOAL_MINUTES = "daily_goal_minutes";
    private static final String KEY_DAILY_GOAL_CALORIES = "daily_goal_calories";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_TODAY_STEPS = "today_steps";
    private static final String KEY_STEP_GOAL = "step_goal";
    private static final String KEY_STREAK_DAYS = "streak_days";
    private static final String KEY_LAST_CHECKIN_DATE = "last_checkin_date";

    private final SharedPreferences prefs;
    private static PreferencesHelper instance;

    private PreferencesHelper(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesHelper(context);
        }
        return instance;
    }

    public String getNickname() { return prefs.getString(KEY_NICKNAME, "运动达人"); }
    public void setNickname(String nickname) { prefs.edit().putString(KEY_NICKNAME, nickname).apply(); }
    public float getWeight() { return prefs.getFloat(KEY_WEIGHT, 65.0f); }
    public void setWeight(float weight) { prefs.edit().putFloat(KEY_WEIGHT, weight).apply(); }
    public float getHeight() { return prefs.getFloat(KEY_HEIGHT, 170.0f); }
    public void setHeight(float height) { prefs.edit().putFloat(KEY_HEIGHT, height).apply(); }
    public int getDailyGoalMinutes() { return prefs.getInt(KEY_DAILY_GOAL_MINUTES, 30); }
    public void setDailyGoalMinutes(int minutes) { prefs.edit().putInt(KEY_DAILY_GOAL_MINUTES, minutes).apply(); }
    public int getDailyGoalCalories() { return prefs.getInt(KEY_DAILY_GOAL_CALORIES, 300); }
    public void setDailyGoalCalories(int calories) { prefs.edit().putInt(KEY_DAILY_GOAL_CALORIES, calories).apply(); }
    public boolean isFirstLaunch() { return prefs.getBoolean(KEY_FIRST_LAUNCH, true); }
    public void setFirstLaunch(boolean firstLaunch) { prefs.edit().putBoolean(KEY_FIRST_LAUNCH, firstLaunch).apply(); }
    public int getTodaySteps() { return prefs.getInt(KEY_TODAY_STEPS, 0); }
    public void setTodaySteps(int steps) { prefs.edit().putInt(KEY_TODAY_STEPS, steps).apply(); }
    public int getStepGoal() { return prefs.getInt(KEY_STEP_GOAL, 8000); }
    public void setStepGoal(int goal) { prefs.edit().putInt(KEY_STEP_GOAL, goal).apply(); }
    public int getStreakDays() { return prefs.getInt(KEY_STREAK_DAYS, 0); }
    public void setStreakDays(int days) { prefs.edit().putInt(KEY_STREAK_DAYS, days).apply(); }
    public String getLastCheckinDate() { return prefs.getString(KEY_LAST_CHECKIN_DATE, ""); }
    public void setLastCheckinDate(String date) { prefs.edit().putString(KEY_LAST_CHECKIN_DATE, date).apply(); }
}
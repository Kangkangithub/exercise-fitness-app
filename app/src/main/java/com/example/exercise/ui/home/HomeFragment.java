package com.example.exercise.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exercise.R;
import com.example.exercise.adapter.CourseAdapter;
import com.example.exercise.data.local.AppDatabase;
import com.example.exercise.data.local.PreferencesHelper;
import com.example.exercise.data.model.FitnessCourse;
import com.example.exercise.ui.courses.CourseDetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView tvStreak, tvSteps, tvCalories, tvMinutes;
    private TextView tvGoalProgress, tvGoalPercent;
    private ProgressBar progressGoal;
    private TextView tvGreeting;
    private RecyclerView rvHomeCourses;
    private CourseAdapter courseAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadData();
        setupRecommendedCourses();
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvStreak = view.findViewById(R.id.tv_streak_home);
        tvSteps = view.findViewById(R.id.tv_home_steps);
        tvCalories = view.findViewById(R.id.tv_home_calories);
        tvMinutes = view.findViewById(R.id.tv_home_minutes);
        tvGoalProgress = view.findViewById(R.id.tv_goal_progress);
        tvGoalPercent = view.findViewById(R.id.tv_goal_percent);
        progressGoal = view.findViewById(R.id.progress_goal);
        rvHomeCourses = view.findViewById(R.id.rv_home_courses);
    }

    private void loadData() {
        PreferencesHelper prefs = PreferencesHelper.getInstance(requireContext());
        String greeting = getTimeGreeting() + "，" + prefs.getNickname() + "！";
        tvGreeting.setText(greeting);
        int streakDays = prefs.getStreakDays();
        tvStreak.setText(streakDays + " 天");
        int todaySteps = prefs.getTodaySteps();
        tvSteps.setText(formatNumber(todaySteps));

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            int totalCalories = db.checkInDao().getTotalCalories();
            int totalMinutes = db.checkInDao().getTotalMinutes();
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            int todayCount = db.checkInDao().getTodayCount(todayDate);

            requireActivity().runOnUiThread(() -> {
                tvCalories.setText(formatNumber(totalCalories));
                tvMinutes.setText(formatNumber(totalMinutes));

                int goalMinutes = prefs.getDailyGoalMinutes();
                int todayMinutes = todayCount * 30;
                if (todayMinutes > goalMinutes) todayMinutes = goalMinutes;
                int percent = goalMinutes > 0 ? (todayMinutes * 100 / goalMinutes) : 0;
                if (percent > 100) percent = 100;
                tvGoalProgress.setText(todayMinutes + "/" + goalMinutes + " 分钟");
                tvGoalPercent.setText(percent + "%");
                progressGoal.setProgress(percent);
            });
        });
    }

    private void setupRecommendedCourses() {
        List<FitnessCourse> courses = new ArrayList<>();
        courses.add(new FitnessCourse("晨间瑜伽", "唤醒身体，提升柔韧性", "初级", 20, 120, R.drawable.bg_splash_gradient, "瑜伽"));
        courses.add(new FitnessCourse("HIIT 燃脂", "高强度间歇训练，快速燃脂", "高级", 25, 350, R.color.gradient_start, "有氧"));
        courses.add(new FitnessCourse("核心力量训练", "强化核心肌群，改善体态", "中级", 30, 250, R.color.secondary, "力量"));
        courses.add(new FitnessCourse("放松拉伸", "缓解肌肉紧张，促进恢复", "初级", 15, 60, R.color.primary_dark, "拉伸"));

        courseAdapter = new CourseAdapter(requireContext(), courses, course -> {
            Intent intent = new Intent(requireContext(), CourseDetailActivity.class);
            intent.putExtra("course_name", course.getName());
            intent.putExtra("course_desc", course.getDescription());
            intent.putExtra("course_difficulty", course.getDifficulty());
            intent.putExtra("course_duration", course.getDurationMinutes());
            intent.putExtra("course_calories", course.getCaloriesBurn());
            startActivity(intent);
        });

        rvHomeCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHomeCourses.setAdapter(courseAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private String getTimeGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 6) return "夜深了";
        if (hour < 9) return "早上好";
        if (hour < 12) return "上午好";
        if (hour < 14) return "中午好";
        if (hour < 18) return "下午好";
        return "晚上好";
    }

    private String formatNumber(int num) {
        if (num >= 10000) {
            return String.format(Locale.getDefault(), "%.1f万", num / 10000.0);
        }
        return String.valueOf(num);
    }
}
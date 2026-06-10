package com.example.exercise.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exercise.R;
import com.example.exercise.adapter.CourseAdapter;
import com.example.exercise.data.local.AppDatabase;
import com.example.exercise.data.local.PreferencesHelper;
import com.example.exercise.data.model.FitnessCourse;
import com.example.exercise.ui.courses.CourseDetailActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView tvStreak, tvGreeting;
    private TextView tvGoalProgress, tvGoalPercent;
    private ProgressBar progressGoal;
    private RecyclerView rvHomeCourses;
    private CourseAdapter courseAdapter;

    // 仪表盘
    private PieChart chartDashboard;
    private TextView tvDashSteps, tvDashGoal;
    private TextView tvDashCalories, tvDashMinutes, tvDashDistance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化控件
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvStreak = view.findViewById(R.id.tv_streak_home);
        tvGoalProgress = view.findViewById(R.id.tv_goal_progress);
        tvGoalPercent = view.findViewById(R.id.tv_goal_percent);
        progressGoal = view.findViewById(R.id.progress_goal);
        rvHomeCourses = view.findViewById(R.id.rv_home_courses);

        chartDashboard = view.findViewById(R.id.chart_dashboard);
        tvDashSteps = view.findViewById(R.id.tv_dashboard_steps);
        tvDashGoal = view.findViewById(R.id.tv_dashboard_goal);
        tvDashCalories = view.findViewById(R.id.tv_dash_calories);
        tvDashMinutes = view.findViewById(R.id.tv_dash_minutes);
        tvDashDistance = view.findViewById(R.id.tv_dash_distance);

        // 初始化环形仪表盘
        setupDashboardChart();

        // 加载数据
        loadData();
        setupRecommendedCourses();
    }

    // ===== 配置环形进度轮盘 =====
    private void setupDashboardChart() {
        // 空心圆环样式
        chartDashboard.setDrawHoleEnabled(true);
        chartDashboard.setHoleRadius(72f);                    // 内孔半径
        chartDashboard.setTransparentCircleRadius(78f);        // 透明环半径
        chartDashboard.setHoleColor(Color.WHITE);
        chartDashboard.setTransparentCircleColor(Color.argb(60, 255, 255, 255));
        chartDashboard.setDrawCenterText(false);               // 不自带中心文字（用FrameLayout叠加）
        chartDashboard.setRotationEnabled(false);
        chartDashboard.setHighlightPerTapEnabled(false);
        chartDashboard.setTouchEnabled(false);
        chartDashboard.setDrawEntryLabels(false);

        // 隐藏图例
        Legend legend = chartDashboard.getLegend();
        legend.setEnabled(false);

        // 隐藏描述
        chartDashboard.getDescription().setEnabled(false);

        // 初始数据：空进度
        updateDashboardRing(0, 8000);
    }

    // ===== 更新环形仪表盘数据 =====
    private void updateDashboardRing(int currentSteps, int stepGoal) {
        // 计算进度百分比（限制在 0~100）
        float progress = stepGoal > 0 ? (float) currentSteps / stepGoal * 100f : 0f;
        if (progress > 100f) progress = 100f;
        float remaining = 100f - progress;

        List<PieEntry> entries = new ArrayList<>();
        // 已完成部分
        entries.add(new PieEntry(progress, "已完成"));
        // 剩余部分
        entries.add(new PieEntry(remaining, "剩余"));

        PieDataSet dataSet = new PieDataSet(entries, "");

        // 已完成 → 主题绿色，剩余 → 浅灰
        int green = ContextCompat.getColor(requireContext(), R.color.primary);
        int gray = ContextCompat.getColor(requireContext(), R.color.chip_bg);
        dataSet.setColors(green, gray);
        dataSet.setDrawValues(false);           // 不显示百分比标签
        dataSet.setSliceSpace(3f);              // 扇区间隙

        PieData data = new PieData(dataSet);
        chartDashboard.setData(data);
        chartDashboard.animateY(600);           // 动画效果
        chartDashboard.invalidate();
    }

    // ===== 加载全部数据 =====
    private void loadData() {
        PreferencesHelper prefs = PreferencesHelper.getInstance(requireContext());

        // 欢迎语
        String greeting = getTimeGreeting() + "，" + prefs.getNickname() + "！";
        tvGreeting.setText(greeting);

        // 连续打卡天数
        int streakDays = prefs.getStreakDays();
        tvStreak.setText(streakDays + " 天");

        // 今日步数
        int todaySteps = prefs.getTodaySteps();
        int stepGoal = prefs.getStepGoal();

        // 更新环形仪表盘
        updateDashboardRing(todaySteps, stepGoal);
        tvDashSteps.setText(formatNumber(todaySteps));
        tvDashGoal.setText("目标 " + formatNumber(stepGoal) + " 步");

        // 从数据库加载累计数据
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            int totalCalories = db.checkInDao().getTotalCalories();
            int totalMinutes = db.checkInDao().getTotalMinutes();

            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            int todayCount = db.checkInDao().getTodayCount(todayDate);

            requireActivity().runOnUiThread(() -> {
                // 仪表盘下方三指标
                tvDashCalories.setText(formatNumber(totalCalories));
                tvDashMinutes.setText(formatNumber(totalMinutes));
                // 距离估算：约1000步=0.7km
                float distance = todaySteps * 0.0007f;
                tvDashDistance.setText(String.format(Locale.getDefault(), "%.1f", distance));

                // 目标进度条
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

    // ===== 推荐课程列表 =====
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
package com.example.exercise.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exercise.R;
import com.example.exercise.data.local.AppDatabase;
import com.example.exercise.data.local.PreferencesHelper;
import com.example.exercise.data.model.CheckInRecord;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName;
    private TextView tvCheckins, tvMinutes, tvStreak;
    private BarChart chartWeekly;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvCheckins = view.findViewById(R.id.tv_profile_checkins);
        tvMinutes = view.findViewById(R.id.tv_profile_minutes);
        tvStreak = view.findViewById(R.id.tv_profile_streak);
        chartWeekly = view.findViewById(R.id.chart_weekly);

        View llEditProfile = view.findViewById(R.id.ll_edit_profile);
        View llSettings = view.findViewById(R.id.ll_settings);
        View llAbout = view.findViewById(R.id.ll_about);

        loadProfileData();
        loadWeeklyChart();

        llEditProfile.setOnClickListener(v -> showEditProfileDialog());
        llSettings.setOnClickListener(v -> showGoalSettingsDialog());
        llAbout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("关于")
                    .setMessage("运动健身打卡 APP\n版本：1.0\n\n大学移动应用开发工程实践期末大作业\n\n技术栈：Java + Android\n数据库：Room (SQLite)\n图表：MPAndroidChart\n动画：Lottie")
                    .setPositiveButton("确定", null).show();
        });
    }

    private void loadProfileData() {
        PreferencesHelper prefs = PreferencesHelper.getInstance(requireContext());
        tvProfileName.setText(prefs.getNickname());
        tvStreak.setText(prefs.getStreakDays() + " 天");

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            int totalCheckins = db.checkInDao().getDistinctDaysCount();
            int totalMinutes = db.checkInDao().getTotalMinutes();
            requireActivity().runOnUiThread(() -> {
                tvCheckins.setText(String.valueOf(totalCheckins));
                tvMinutes.setText(String.valueOf(totalMinutes));
            });
        });
    }

    private void loadWeeklyChart() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            List<String> labels = new ArrayList<>();
            List<BarEntry> entries = new ArrayList<>();

            for (int i = 6; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_YEAR, -i);
                String date = sdf.format(cal.getTime());
                String dayLabel = new SimpleDateFormat("E", Locale.getDefault()).format(cal.getTime());
                labels.add(dayLabel);
                List<CheckInRecord> records = db.checkInDao().getRecordsByDate(date);
                int dayMinutes = 0;
                for (CheckInRecord r : records) { dayMinutes += r.getDurationMinutes(); }
                entries.add(new BarEntry(6 - i, dayMinutes));
            }

            requireActivity().runOnUiThread(() -> {
                BarDataSet dataSet = new BarDataSet(entries, "运动时长(分钟)");
                dataSet.setColor(requireContext().getResources().getColor(R.color.primary, null));
                dataSet.setValueTextSize(10f);
                BarData barData = new BarData(dataSet);
                chartWeekly.setData(barData);
                XAxis xAxis = chartWeekly.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);
                chartWeekly.getAxisLeft().setAxisMinimum(0f);
                chartWeekly.getAxisRight().setEnabled(false);
                chartWeekly.getDescription().setEnabled(false);
                chartWeekly.setFitBars(true);
                chartWeekly.animateY(800);
                chartWeekly.invalidate();
            });
        });
    }

    private void showEditProfileDialog() {
        PreferencesHelper prefs = PreferencesHelper.getInstance(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etNickname = dialogView.findViewById(R.id.et_nickname);
        EditText etWeight = dialogView.findViewById(R.id.et_weight);
        EditText etHeight = dialogView.findViewById(R.id.et_height);
        etNickname.setText(prefs.getNickname());
        etWeight.setText(String.valueOf(prefs.getWeight()));
        etHeight.setText(String.valueOf(prefs.getHeight()));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("编辑资料")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String nickname = etNickname.getText().toString().trim();
                    if (!nickname.isEmpty()) { prefs.setNickname(nickname); }
                    try { prefs.setWeight(Float.parseFloat(etWeight.getText().toString())); } catch (NumberFormatException ignored) {}
                    try { prefs.setHeight(Float.parseFloat(etHeight.getText().toString())); } catch (NumberFormatException ignored) {}
                    loadProfileData();
                    Toast.makeText(requireContext(), "资料已更新", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null).show();
    }

    private void showGoalSettingsDialog() {
        PreferencesHelper prefs = PreferencesHelper.getInstance(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_goal_settings, null);
        EditText etGoalMinutes = dialogView.findViewById(R.id.et_goal_minutes);
        EditText etStepGoal = dialogView.findViewById(R.id.et_step_goal);
        etGoalMinutes.setText(String.valueOf(prefs.getDailyGoalMinutes()));
        etStepGoal.setText(String.valueOf(prefs.getStepGoal()));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("运动目标设置")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    try {
                        prefs.setDailyGoalMinutes(Integer.parseInt(etGoalMinutes.getText().toString()));
                        prefs.setStepGoal(Integer.parseInt(etStepGoal.getText().toString()));
                        Toast.makeText(requireContext(), "目标已更新", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "请输入有效数值", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    @Override
    public void onResume() { super.onResume(); loadProfileData(); }
}
package com.example.exercise.ui.checkin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exercise.R;
import com.example.exercise.data.local.AppDatabase;
import com.example.exercise.data.local.PreferencesHelper;
import com.example.exercise.data.model.CheckInRecord;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CheckInFragment extends Fragment {

    private ImageView ivCheckinStatus;
    private TextView tvCheckinStatus;
    private LinearLayout llExerciseTypes;
    private TextView tvDuration;
    private EditText etSteps, etCalories, etNote;

    private String selectedType = "跑步";
    private int durationMinutes = 30;

    private static final String[] EXERCISE_TYPES = {"跑步", "游泳", "骑行", "瑜伽", "力量训练", "HIIT", "散步", "篮球"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivCheckinStatus = view.findViewById(R.id.iv_checkin_status);
        tvCheckinStatus = view.findViewById(R.id.tv_checkin_status);
        llExerciseTypes = view.findViewById(R.id.ll_exercise_types);
        tvDuration = view.findViewById(R.id.tv_duration);
        etSteps = view.findViewById(R.id.et_steps);
        etCalories = view.findViewById(R.id.et_calories);
        etNote = view.findViewById(R.id.et_note);

        View btnMinus = view.findViewById(R.id.btn_duration_minus);
        View btnPlus = view.findViewById(R.id.btn_duration_plus);
        View btnSubmit = view.findViewById(R.id.btn_checkin_submit);
        View btnHistory = view.findViewById(R.id.btn_view_history);

        // 回填当前步数到输入框
        int savedSteps = PreferencesHelper.getInstance(requireContext()).getTodaySteps();
        if (savedSteps > 0) {
            etSteps.setText(String.valueOf(savedSteps));
        }

        setupExerciseTypeChips();
        checkTodayStatus();

        btnMinus.setOnClickListener(v -> {
            if (durationMinutes > 5) { durationMinutes -= 5; tvDuration.setText(String.valueOf(durationMinutes)); }
        });
        btnPlus.setOnClickListener(v -> {
            if (durationMinutes < 180) { durationMinutes += 5; tvDuration.setText(String.valueOf(durationMinutes)); }
        });
        btnSubmit.setOnClickListener(v -> submitCheckIn());
        btnHistory.setOnClickListener(v -> startActivity(new Intent(requireContext(), CheckInHistoryActivity.class)));
    }

    private void setupExerciseTypeChips() {
        int primaryColor = requireContext().getResources().getColor(R.color.primary, null);
        for (String type : EXERCISE_TYPES) {
            Chip chip = new Chip(requireContext());
            chip.setText(type);
            chip.setCheckable(true);
            chip.setChecked(type.equals(selectedType));
            chip.setChipStrokeColor(ColorStateList.valueOf(primaryColor));
            chip.setChipStrokeWidth(1.5f);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 8, 8);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                selectedType = type;
                for (int i = 0; i < llExerciseTypes.getChildCount(); i++) {
                    View child = llExerciseTypes.getChildAt(i);
                    if (child instanceof Chip) { ((Chip) child).setChecked(child == chip); }
                }
            });
            llExerciseTypes.addView(chip);
        }
    }

    private void checkTodayStatus() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            int todayCount = db.checkInDao().getTodayCount(todayDate);
            requireActivity().runOnUiThread(() -> {
                if (todayCount > 0) {
                    tvCheckinStatus.setText("今天已打卡 " + todayCount + " 次，继续保持！");
                    ivCheckinStatus.setColorFilter(requireContext().getResources().getColor(R.color.primary, null));
                } else {
                    tvCheckinStatus.setText("今天还没有打卡哦~");
                    ivCheckinStatus.clearColorFilter();
                }
            });
        });
    }

    private void submitCheckIn() {
        // 读取步数（选填，不输入则保持原来步数不变）
        String stepsStr = etSteps.getText().toString().trim();
        int newSteps = -1;
        if (!stepsStr.isEmpty()) {
            try { newSteps = Integer.parseInt(stepsStr); }
            catch (NumberFormatException e) { etSteps.setError("请输入有效步数"); return; }
        }

        String caloriesStr = etCalories.getText().toString().trim();
        if (caloriesStr.isEmpty()) { etCalories.setError("请输入消耗卡路里"); return; }
        int calories;
        try { calories = Integer.parseInt(caloriesStr); }
        catch (NumberFormatException e) { etCalories.setError("请输入有效数字"); return; }

        String note = etNote.getText().toString().trim();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        CheckInRecord record = new CheckInRecord(todayDate, selectedType, durationMinutes, calories, note);

        // 捕获步数值用于子线程
        final int finalSteps = newSteps;

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            db.checkInDao().insert(record);

            PreferencesHelper prefs = PreferencesHelper.getInstance(requireContext());

            // 如果用户输入了步数，更新到 SharedPreferences → 首页轮盘自动同步
            if (finalSteps >= 0) {
                prefs.setTodaySteps(finalSteps);
            }

            // 更新连续打卡天数
            String lastDate = prefs.getLastCheckinDate();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            String yesterday = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

            if (lastDate.equals(yesterday)) {
                prefs.setStreakDays(prefs.getStreakDays() + 1);
            } else if (!lastDate.equals(todayDate)) {
                prefs.setStreakDays(1);
            }
            prefs.setLastCheckinDate(todayDate);

            requireActivity().runOnUiThread(() -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("打卡成功！")
                        .setMessage("运动类型：" + selectedType
                                + "\n时长：" + durationMinutes + " 分钟"
                                + "\n卡路里：" + calories + " 千卡"
                                + (finalSteps >= 0 ? "\n步数：" + finalSteps + " 步" : ""))
                        .setPositiveButton("太棒了", (dialog, which) -> {
                            dialog.dismiss();
                            etSteps.setText("");
                            etCalories.setText("");
                            etNote.setText("");
                            checkTodayStatus();
                        }).show();
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 回填最新步数
        int savedSteps = PreferencesHelper.getInstance(requireContext()).getTodaySteps();
        if (savedSteps > 0 && etSteps != null && etSteps.getText().toString().isEmpty()) {
            etSteps.setText(String.valueOf(savedSteps));
        }
        checkTodayStatus();
    }
}
package com.example.exercise.ui.courses;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.exercise.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

import java.util.Locale;

public class CourseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_dark));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true);
        }

        setContentView(R.layout.activity_course_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_detail);
        TextView tvName = findViewById(R.id.tv_course_name);
        TextView tvDesc = findViewById(R.id.tv_course_description);
        TextView tvDuration = findViewById(R.id.tv_course_duration);
        TextView tvCalories = findViewById(R.id.tv_course_calories);
        TextView tvDifficulty = findViewById(R.id.tv_course_difficulty);
        Chip chipDifficulty = findViewById(R.id.chip_difficulty);

        toolbar.setNavigationOnClickListener(v -> finish());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("course_name", "未命名课程");
            String desc = extras.getString("course_desc", "");
            String difficulty = extras.getString("course_difficulty", "初级");
            int duration = extras.getInt("course_duration", 30);
            int calories = extras.getInt("course_calories", 200);

            toolbar.setTitle(name);
            tvName.setText(name);
            tvDesc.setText(desc);
            tvDuration.setText(String.format(Locale.getDefault(), "%d 分钟", duration));
            tvCalories.setText(String.format(Locale.getDefault(), "%d 千卡", calories));
            tvDifficulty.setText(difficulty);
            chipDifficulty.setText(difficulty);
        }

        findViewById(R.id.btn_start_training).setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle("开始训练")
                    .setMessage("训练即将开始，请做好准备！\n完成训练后记得打卡哦~")
                    .setPositiveButton("开始", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }
}
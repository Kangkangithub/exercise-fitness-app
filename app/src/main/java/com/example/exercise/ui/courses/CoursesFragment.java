package com.example.exercise.ui.courses;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exercise.R;
import com.example.exercise.adapter.CourseAdapter;
import com.example.exercise.data.model.FitnessCourse;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {

    private RecyclerView rvCourses;
    private LinearLayout llCategoryChips;
    private CourseAdapter courseAdapter;
    private final List<FitnessCourse> allCourses = new ArrayList<>();
    private List<FitnessCourse> filteredCourses = new ArrayList<>();
    private String selectedCategory = "全部";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_courses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCourses = view.findViewById(R.id.rv_courses);
        llCategoryChips = view.findViewById(R.id.ll_category_chips);
        initCourseData();
        setupCategoryChips();
        setupRecyclerView();
    }

    private void initCourseData() {
        allCourses.clear();
        allCourses.add(new FitnessCourse("晨间瑜伽", "30分钟唤醒身体，提升柔韧性和平衡感", "初级", 30, 150, R.drawable.bg_splash_gradient, "瑜伽"));
        allCourses.add(new FitnessCourse("流瑜伽进阶", "连贯体式流动，增强核心力量", "中级", 45, 280, R.color.gradient_start, "瑜伽"));
        allCourses.add(new FitnessCourse("HIIT 燃脂挑战", "高强度间歇训练，快速燃烧脂肪", "高级", 25, 350, R.color.secondary, "有氧"));
        allCourses.add(new FitnessCourse("跑步入门", "从零开始，科学跑步不伤膝", "初级", 30, 250, R.color.primary_dark, "有氧"));
        allCourses.add(new FitnessCourse("动感单车", "室内骑行，燃脂刷汗", "中级", 40, 400, R.color.accent, "有氧"));
        allCourses.add(new FitnessCourse("核心力量训练", "平板支撑、卷腹等核心训练", "中级", 30, 200, R.color.secondary, "力量"));
        allCourses.add(new FitnessCourse("哑铃全身训练", "哑铃全身塑形训练计划", "高级", 45, 350, R.color.primary, "力量"));
        allCourses.add(new FitnessCourse("自重训练入门", "无需器械，随时随地健身", "初级", 20, 150, R.color.gradient_end, "力量"));
        allCourses.add(new FitnessCourse("全身拉伸放松", "运动后必做拉伸，缓解酸痛", "初级", 15, 60, R.color.primary_dark, "拉伸"));
        allCourses.add(new FitnessCourse("普拉提入门", "核心控制与身体协调训练", "中级", 35, 180, R.color.primary_dark, "拉伸"));
        allCourses.add(new FitnessCourse("Tabata 4分钟", "4分钟极限燃脂训练", "高级", 4, 80, R.color.error, "有氧"));
        allCourses.add(new FitnessCourse("游泳训练计划", "科学游泳训练方案", "中级", 60, 500, R.color.primary, "有氧"));
    }

    private void setupCategoryChips() {
        String[] categories = {"全部", "瑜伽", "有氧", "力量", "拉伸"};
        int primaryColor = requireContext().getResources().getColor(R.color.primary, null);

        for (String category : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setChecked(category.equals(selectedCategory));
            chip.setChipStrokeColor(ColorStateList.valueOf(primaryColor));
            chip.setChipStrokeWidth(1.5f);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 12, 0);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                selectedCategory = category;
                filterCourses();
                for (int i = 0; i < llCategoryChips.getChildCount(); i++) {
                    View child = llCategoryChips.getChildAt(i);
                    if (child instanceof Chip) {
                        ((Chip) child).setChecked(child == chip);
                    }
                }
            });

            llCategoryChips.addView(chip);
        }
        filterCourses();
    }

    private void filterCourses() {
        if ("全部".equals(selectedCategory)) {
            filteredCourses = new ArrayList<>(allCourses);
        } else {
            filteredCourses = new ArrayList<>();
            for (FitnessCourse c : allCourses) {
                if (c.getCategory().equals(selectedCategory)) {
                    filteredCourses.add(c);
                }
            }
        }
        if (courseAdapter != null) {
            courseAdapter.updateData(filteredCourses);
        }
    }

    private void setupRecyclerView() {
        courseAdapter = new CourseAdapter(requireContext(), filteredCourses, course -> {
            Intent intent = new Intent(requireContext(), CourseDetailActivity.class);
            intent.putExtra("course_name", course.getName());
            intent.putExtra("course_desc", course.getDescription());
            intent.putExtra("course_difficulty", course.getDifficulty());
            intent.putExtra("course_duration", course.getDurationMinutes());
            intent.putExtra("course_calories", course.getCaloriesBurn());
            startActivity(intent);
        });
        rvCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCourses.setAdapter(courseAdapter);
    }
}
package com.example.exercise.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exercise.R;
import com.example.exercise.data.model.FitnessCourse;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.Locale;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    public interface OnCourseClickListener { void onCourseClick(FitnessCourse course); }

    private final Context context;
    private List<FitnessCourse> courses;
    private final OnCourseClickListener listener;

    public CourseAdapter(Context context, List<FitnessCourse> courses, OnCourseClickListener listener) {
        this.context = context;
        this.courses = courses;
        this.listener = listener;
    }

    public void updateData(List<FitnessCourse> newCourses) {
        this.courses = newCourses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FitnessCourse course = courses.get(position);
        holder.tvName.setText(course.getName());
        holder.tvDesc.setText(course.getDescription());
        holder.tvDuration.setText(String.format(Locale.getDefault(), "%d 分钟", course.getDurationMinutes()));
        holder.tvCalories.setText(String.format(Locale.getDefault(), "%d 千卡", course.getCaloriesBurn()));
        holder.chipDifficulty.setText(course.getDifficulty());

        int colorRes, bgRes;
        switch (course.getDifficulty()) {
            case "初级": colorRes = R.color.primary; bgRes = R.color.primary_light; break;
            case "中级": colorRes = R.color.secondary; bgRes = R.color.secondary_light; break;
            case "高级": colorRes = R.color.error; bgRes = R.color.orange_light; break;
            default: colorRes = R.color.primary; bgRes = R.color.chip_bg; break;
        }
        holder.chipDifficulty.setTextColor(ContextCompat.getColor(context, colorRes));
        holder.chipDifficulty.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, bgRes)));
        holder.ivIcon.setImageResource(course.getImageResId());
        holder.ivIcon.setBackgroundColor(ContextCompat.getColor(context, getBgForCategory(course.getCategory())));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCourseClick(course);
        });
    }

    private int getBgForCategory(String category) {
        switch (category) {
            case "瑜伽": return R.color.green_light;
            case "有氧": return R.color.orange_light;
            case "力量": return R.color.blue_light;
            case "拉伸": return R.color.primary_light;
            default: return R.color.chip_bg;
        }
    }

    @Override
    public int getItemCount() { return courses != null ? courses.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvDesc, tvDuration, tvCalories;
        Chip chipDifficulty;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_course_icon);
            tvName = itemView.findViewById(R.id.tv_course_name);
            tvDesc = itemView.findViewById(R.id.tv_course_desc);
            tvDuration = itemView.findViewById(R.id.tv_course_duration);
            tvCalories = itemView.findViewById(R.id.tv_course_calories);
            chipDifficulty = itemView.findViewById(R.id.chip_difficulty);
        }
    }
}
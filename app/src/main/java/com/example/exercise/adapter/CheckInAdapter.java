package com.example.exercise.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exercise.R;
import com.example.exercise.data.model.CheckInRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.ViewHolder> {

    private final Context context;
    private List<CheckInRecord> records;

    public CheckInAdapter(Context context) { this.context = context; }

    public void setRecords(List<CheckInRecord> records) { this.records = records; notifyDataSetChanged(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkin_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckInRecord record = records.get(position);
        holder.tvType.setText(record.getExerciseType());

        String displayDate = record.getDate();
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(record.getDate());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM月dd日 EEEE", Locale.getDefault());
            displayDate = outputFormat.format(date);
        } catch (Exception ignored) {}

        holder.tvDate.setText(displayDate);
        holder.tvDuration.setText(String.format(Locale.getDefault(), "%d 分钟", record.getDurationMinutes()));
        holder.tvCalories.setText(String.format(Locale.getDefault(), "%d 千卡", record.getCalories()));
        holder.ivType.setColorFilter(getColorForType(record.getExerciseType()), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private int getColorForType(String type) {
        switch (type) {
            case "跑步": return context.getResources().getColor(R.color.secondary, null);
            case "游泳": return context.getResources().getColor(R.color.primary, null);
            case "骑行": return context.getResources().getColor(R.color.accent, null);
            case "瑜伽": return context.getResources().getColor(R.color.green_light, null);
            case "力量训练": return context.getResources().getColor(R.color.primary_dark, null);
            case "HIIT": return context.getResources().getColor(R.color.error, null);
            default: return context.getResources().getColor(R.color.primary, null);
        }
    }

    @Override
    public int getItemCount() { return records != null ? records.size() : 0; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivType;
        TextView tvType, tvDate, tvDuration, tvCalories;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivType = itemView.findViewById(R.id.iv_record_type);
            tvType = itemView.findViewById(R.id.tv_record_type);
            tvDate = itemView.findViewById(R.id.tv_record_date);
            tvDuration = itemView.findViewById(R.id.tv_record_duration);
            tvCalories = itemView.findViewById(R.id.tv_record_calories);
        }
    }
}
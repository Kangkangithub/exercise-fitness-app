package com.example.exercise.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.exercise.R;
import com.example.exercise.ui.main.MainActivity;

public class WorkoutReceiver extends BroadcastReceiver {

    private static final String TAG = "WorkoutReceiver";
    private static final String CHANNEL_ID = "workout_channel";
    private static final String ACTION_WORKOUT_COMPLETE = "com.example.exercise.WORKOUT_COMPLETE";
    private static final String ACTION_STEPS_UPDATED = "com.example.exercise.STEPS_UPDATED";
    private static int notificationId = 2000;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        Log.d(TAG, "收到广播: " + action);
        createNotificationChannel(context);

        switch (action) {
            case ACTION_WORKOUT_COMPLETE:
                handleWorkoutComplete(context, intent);
                break;
            case ACTION_STEPS_UPDATED:
                handleStepsUpdated(context, intent);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                Log.d(TAG, "设备启动完成");
                break;
        }
    }

    private void handleWorkoutComplete(Context context, Intent intent) {
        String exerciseType = intent.getStringExtra("exercise_type");
        int duration = intent.getIntExtra("duration", 0);
        int calories = intent.getIntExtra("calories", 0);
        if (exerciseType == null) exerciseType = "运动";
        String title = context.getString(R.string.workout_complete);
        String content = exerciseType + " 完成！时长 " + duration + " 分钟，消耗 " + calories + " 千卡";
        showNotification(context, title, content);
    }

    private void handleStepsUpdated(Context context, Intent intent) {
        int steps = intent.getIntExtra("extra_steps", 0);
        int stepGoal = context.getSharedPreferences("exercise_prefs", Context.MODE_PRIVATE).getInt("step_goal", 8000);
        if (steps >= stepGoal && steps > 0) {
            showNotification(context, "\uD83C\uDF89 步数目标达成！", "恭喜！今日已完成 " + steps + " 步，达到每日目标！");
        }
    }

    private void showNotification(Context context, String title, String content) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fire)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat.from(context).notify(notificationId++, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "运动通知", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("运动完成和步数达标通知");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) { manager.createNotificationChannel(channel); }
        }
    }
}
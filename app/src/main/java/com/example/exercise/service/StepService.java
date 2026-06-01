package com.example.exercise.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.example.exercise.R;
import com.example.exercise.data.local.PreferencesHelper;
import com.example.exercise.ui.main.MainActivity;

import java.util.Random;

public class StepService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "step_counter_channel";
    private static final int NOTIFICATION_ID = 1001;
    public static final String ACTION_STEPS_UPDATED = "com.example.exercise.STEPS_UPDATED";
    public static final String EXTRA_STEPS = "extra_steps";

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int stepCount = 0;
    private boolean isRunning = false;
    private Handler handler;
    private Runnable stepRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        stepCount = PreferencesHelper.getInstance(this).getTodaySteps();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
        handler = new Handler(Looper.getMainLooper());
        stepRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    int increment = new Random().nextInt(5) + 1;
                    stepCount += increment;
                    updateNotification();
                    PreferencesHelper.getInstance(StepService.this).setTodaySteps(stepCount);
                    if (stepCount % 100 == 0) {
                        sendStepsBroadcast();
                    }
                    handler.postDelayed(this, 5000);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        if (stepSensor != null && sensorManager != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        startForeground(NOTIFICATION_ID, buildNotification());
        handler.postDelayed(stepRunnable, 5000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        handler.removeCallbacks(stepRunnable);
        if (sensorManager != null) { sensorManager.unregisterListener(this); }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int sensorSteps = (int) event.values[0];
            if (sensorSteps > stepCount) {
                stepCount = sensorSteps;
                PreferencesHelper.getInstance(this).setTodaySteps(stepCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.service_channel_name),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("运动计步服务通知");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) { manager.createNotificationChannel(channel); }
        }
    }

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("运动健身打卡")
                .setContentText("今日步数：" + stepCount)
                .setSmallIcon(R.drawable.ic_checkin)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void updateNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) { manager.notify(NOTIFICATION_ID, buildNotification()); }
    }

    private void sendStepsBroadcast() {
        Intent intent = new Intent(ACTION_STEPS_UPDATED);
        intent.putExtra(EXTRA_STEPS, stepCount);
        sendBroadcast(intent);
    }
}
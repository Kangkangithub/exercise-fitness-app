package com.example.exercise.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exercise.R;
import com.example.exercise.data.local.PreferencesHelper;
import com.example.exercise.ui.login.LoginActivity;
import com.example.exercise.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PreferencesHelper prefs = PreferencesHelper.getInstance(this);
        prefs.setFirstLaunch(false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (prefs.isLoggedIn()) {
                // 已登录，直接进入主页
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // 未登录，跳转注册/登录页
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY_MS);
    }
}

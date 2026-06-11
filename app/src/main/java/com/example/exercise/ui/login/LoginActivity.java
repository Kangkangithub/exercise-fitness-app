package com.example.exercise.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exercise.R;
import com.example.exercise.data.local.PreferencesHelper;
import com.example.exercise.ui.main.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TabLayout tabMode;
    private TextInputEditText etUsername, etPassword, etConfirmPassword;
    private TextInputLayout tilConfirmPassword;
    private MaterialButton btnLogin;

    private boolean isLoginMode = true;
    private PreferencesHelper prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = PreferencesHelper.getInstance(this);

        tabMode = findViewById(R.id.tab_login_mode);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        btnLogin = findViewById(R.id.btn_login);

        // 初始化 Tab：登录 | 注册
        tabMode.addTab(tabMode.newTab().setText("登 录"));
        tabMode.addTab(tabMode.newTab().setText("注 册"));

        tabMode.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isLoginMode = tab.getPosition() == 0;
                tilConfirmPassword.setVisibility(isLoginMode ? View.GONE : View.VISIBLE);
                btnLogin.setText(isLoginMode ? "登 录" : "注 册");
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 默认选中登录
        tabMode.selectTab(tabMode.getTabAt(0));

        btnLogin.setOnClickListener(v -> {
            if (isLoginMode) {
                handleLogin();
            } else {
                handleRegister();
            }
        });
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            return;
        }

        String savedUsername = prefs.getUsername();
        String savedPassword = prefs.getPassword();

        if (TextUtils.isEmpty(savedUsername)) {
            Toast.makeText(this, "该用户不存在，请先注册", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!username.equals(savedUsername) || !password.equals(savedPassword)) {
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.setLoggedIn(true);

        Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
        enterMain();
    }

    private void handleRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入用户名");
            return;
        }
        if (username.length() < 2) {
            etUsername.setError("用户名至少2个字符");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            return;
        }
        if (password.length() < 4) {
            etPassword.setError("密码至少4位");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("两次密码不一致");
            return;
        }

        // 检查是否已注册
        if (!TextUtils.isEmpty(prefs.getUsername())) {
            Toast.makeText(this, "已存在注册用户，请直接登录", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.setUsername(username);
        prefs.setPassword(password);
        prefs.setLoggedIn(true);

        Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
        enterMain();
    }

    private void enterMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}

package com.example.exercise.ui.checkin;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exercise.R;
import com.example.exercise.adapter.CheckInAdapter;
import com.example.exercise.data.local.AppDatabase;
import com.example.exercise.data.model.CheckInRecord;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.concurrent.Executors;

public class CheckInHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private LinearLayout llEmpty;
    private CheckInAdapter adapter;

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

        setContentView(R.layout.activity_checkin_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_history);
        rvHistory = findViewById(R.id.rv_history);
        llEmpty = findViewById(R.id.ll_empty_history);

        toolbar.setNavigationOnClickListener(v -> finish());
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckInAdapter(this);
        rvHistory.setAdapter(adapter);
        loadRecords();
    }

    private void loadRecords() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<CheckInRecord> records = db.checkInDao().getAllRecordsSync();
            runOnUiThread(() -> {
                if (records.isEmpty()) {
                    llEmpty.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                } else {
                    llEmpty.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                    adapter.setRecords(records);
                }
            });
        });
    }

    @Override
    protected void onResume() { super.onResume(); loadRecords(); }
}
package com.example.exercise.ui.main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.exercise.R;
import com.example.exercise.ui.checkin.CheckInFragment;
import com.example.exercise.ui.courses.CoursesFragment;
import com.example.exercise.ui.home.HomeFragment;
import com.example.exercise.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 确保状态栏颜色不透明，内容不延伸到状态栏下方
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_dark));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true);
        }

        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager_main);
        bottomNav = findViewById(R.id.bottom_navigation);

        MainPagerAdapter adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(true);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                viewPager.setCurrentItem(0, true);
            } else if (itemId == R.id.navigation_courses) {
                viewPager.setCurrentItem(1, true);
            } else if (itemId == R.id.navigation_checkin) {
                viewPager.setCurrentItem(2, true);
            } else if (itemId == R.id.navigation_profile) {
                viewPager.setCurrentItem(3, true);
            }
            return true;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: bottomNav.setSelectedItemId(R.id.navigation_home); break;
                    case 1: bottomNav.setSelectedItemId(R.id.navigation_courses); break;
                    case 2: bottomNav.setSelectedItemId(R.id.navigation_checkin); break;
                    case 3: bottomNav.setSelectedItemId(R.id.navigation_profile); break;
                }
            }
        });
    }

    private static class MainPagerAdapter extends FragmentStateAdapter {
        public MainPagerAdapter(@NonNull MainActivity activity) { super(activity); }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new HomeFragment();
                case 1: return new CoursesFragment();
                case 2: return new CheckInFragment();
                case 3: return new ProfileFragment();
                default: return new HomeFragment();
            }
        }

        @Override
        public int getItemCount() { return 4; }
    }
}
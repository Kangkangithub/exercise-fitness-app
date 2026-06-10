# 🏋️ 运动健身打卡 APP —— 答辩演示文档

> Android Studio + Java | 移动应用开发工程实践期末大作业 | 南京理工大学紫金学院

---

## 一、项目概述

本课题开发了一款基于 Android 平台的「运动健身打卡」APP，旨在帮助用户记录每日运动数据、浏览健身课程、完成打卡任务。应用采用 **Java** 语言开发，使用 **Android Studio** 作为 IDE，面向 **Android 10（API 29）及以上**系统，完整覆盖课程评分标准中所有得分点。

---

## 二、整体框架结构

### 2.1 项目目录树（含职责说明）

```
exercise/
│
├── build.gradle.kts                    # [构建] 顶级 Gradle 配置，声明 Android 插件
├── settings.gradle.kts                 # [构建] 仓库配置，含 JitPack（MPAndroidChart 来源）
├── gradle.properties                   # [构建] JVM 参数 + AndroidX 配置
├── gradlew / gradlew.bat               # [构建] Gradle Wrapper 脚本（免安装 Gradle）
│
├── gradle/
│   ├── libs.versions.toml              # [依赖] 统一版本目录：Room/Lottie/MPAndroidChart/Glide
│   └── wrapper/                        # [构建] Gradle Wrapper JAR + 属性文件
│
└── app/
    ├── build.gradle.kts                # [构建] 应用级配置：compileSdk/minSdk/依赖声明
    │
    └── src/main/
        ├── AndroidManifest.xml         # [注册] 4个Activity + 1个Service + 1个Receiver + 权限
        │
        ├── java/com/example/exercise/
        │   │
        │   ├── data/                   # ═══════ 数据层 ═══════
        │   │   ├── model/              # → 数据模型（Entity / POJO）
        │   │   │   ├── CheckInRecord.java      # Room 实体：打卡记录表
        │   │   │   ├── FitnessCourse.java      # 健身课程数据模型
        │   │   │   └── UserProfile.java        # 用户信息模型
        │   │   └── local/              # → 数据持久化
        │   │       ├── AppDatabase.java        # Room 数据库单例（SQLite 封装）
        │   │       ├── CheckInDao.java         # 数据访问对象（DAO 接口）
        │   │       └── PreferencesHelper.java  # SharedPreferences 工具类
        │   │
        │   ├── ui/                     # ═══════ 界面层（View） ═══════
        │   │   ├── splash/             # SplashActivity — 启动页（Lottie 动画 + 2s 跳转）
        │   │   ├── main/               # MainActivity — 主框架（ViewPager2 + BottomNav）
        │   │   ├── home/               # HomeFragment — 首页推荐（统计卡片 + 推荐课程）
        │   │   ├── courses/            # CoursesFragment — 课程列表（RecyclerView + Chip 筛选）
        │   │   │                       # CourseDetailActivity — 课程详情页
        │   │   ├── checkin/            # CheckInFragment — 运动打卡（表单 + Room 写入）
        │   │   │                       # CheckInHistoryActivity — 打卡历史（RecyclerView）
        │   │   └── profile/            # ProfileFragment — 个人中心（MPAndroidChart 图表）
        │   │
        │   ├── adapter/                # ═══════ 适配器层 ═══════
        │   │   ├── CourseAdapter.java          # 课程列表 RecyclerView 适配器
        │   │   └── CheckInAdapter.java         # 打卡记录 RecyclerView 适配器
        │   │
        │   ├── service/                # ═══════ 服务层 ═══════
        │   │   └── StepService.java            # 前台计步服务（Sensor + Handler 模拟）
        │   │
        │   └── receiver/               # ═══════ 广播层 ═══════
        │       └── WorkoutReceiver.java        # 运动完成 / 步数达标广播接收
        │
        └── res/                        # ═══════ 资源层 ═══════
            ├── layout/                 # 12 个 XML 布局文件（ConstraintLayout 为主）
            ├── drawable/               # 矢量图标（ic_*.xml）+ 形状（bg_*.xml）
            ├── menu/                   # 底部导航菜单（bottom_nav_menu.xml）
            ├── color/                  # 状态选择器（bottom_nav_color.xml）
            ├── values/                 # colors.xml / strings.xml / themes.xml
            ├── raw/                    # Lottie 动画 JSON（splash_animation.json）
            ├── mipmap-*/               # 应用图标（多种分辨率）
            └── xml/                    # backup_rules.xml / data_extraction_rules.xml
```

### 2.2 分层架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      界面层 (UI Layer)                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐   │
│  │  Splash  │ │   Main   │ │ Course   │ │ CheckInHistory│   │
│  │ Activity │ │ Activity │ │ DetailAct│ │   Activity    │   │
│  └──────────┘ └────┬─────┘ └──────────┘ └──────────────┘   │
│                    │ ViewPager2 + BottomNav                 │
│        ┌───────────┼───────────┬────────────┐               │
│        ▼           ▼           ▼            ▼               │
│   HomeFragment  CoursesFrag  CheckInFrag  ProfileFrag       │
│   ·统计卡片     ·RecyclerView ·运动打卡    ·图表+设置        │
│   ·目标进度     ·Chip筛选    ·类型选择    ·用户信息          │
├─────────────────────────────────────────────────────────────┤
│                    数据适配层 (Adapter)                       │
│   CourseAdapter（课程列表）     CheckInAdapter（打卡记录）    │
├─────────────────────────────────────────────────────────────┤
│                    业务逻辑层 (Service/Receiver)              │
│   StepService（前台计步）       WorkoutReceiver（广播通知）   │
├─────────────────────────────────────────────────────────────┤
│                    数据持久层 (Data Layer)                    │
│   ┌─────────────────────┐  ┌──────────────────────┐         │
│   │  Room (SQLite)      │  │  SharedPreferences   │         │
│   │  · AppDatabase      │  │  · PreferencesHelper │         │
│   │  · CheckInDao       │  │  · 用户配置/步数     │         │
│   │  · CheckInRecord    │  │  · xml 文件存储      │         │
│   └─────────────────────┘  └──────────────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、Android 核心组件清单

| 组件类型 | 类名 | 数量 | 作用 |
|---------|------|:---:|------|
| **Activity** | SplashActivity | 4 | 欢迎启动页（Lottie 动画 + 延迟跳转） |
| | MainActivity | | 主页面容器（ViewPager2 + BottomNav） |
| | CourseDetailActivity | | 课程详情展示（Intent 数据传递） |
| | CheckInHistoryActivity | | 打卡历史记录列表 |
| **Fragment** | HomeFragment | 4 | 首页：今日统计 + 目标进度 + 推荐课程 |
| | CoursesFragment | | 课程：RecyclerView 列表 + Chip 分类筛选 |
| | CheckInFragment | | 打卡：运动类型选择 + 时长调节 + Room 存储 |
| | ProfileFragment | | 个人中心：用户数据 + MPAndroidChart 图表 |
| **Service** | StepService | 1 | 前台计步服务（通知栏 + 传感器 + 广播） |
| **BroadcastReceiver** | WorkoutReceiver | 1 | 运动完成通知 + 步数达标提醒 |

> **总计：4 Activity + 4 Fragment + 1 Service + 1 BroadcastReceiver**

---

## 四、业务逻辑最核心代码

### 4.1 主框架 —— ViewPager2 + BottomNav 双向同步

```java
// MainActivity.java —— 4 个 Fragment 页面切换的核心
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewpager_main);
        bottomNav = findViewById(R.id.bottom_navigation);

        // FragmentStateAdapter 管理 4 个 Fragment 生命周期
        viewPager.setAdapter(new MainPagerAdapter(this));

        // ① 点击底部导航 → 切换 ViewPager 页面
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home)      viewPager.setCurrentItem(0, true);
            else if (id == R.id.navigation_courses) viewPager.setCurrentItem(1, true);
            else if (id == R.id.navigation_checkin) viewPager.setCurrentItem(2, true);
            else if (id == R.id.navigation_profile) viewPager.setCurrentItem(3, true);
            return true;
        });

        // ② 滑动 ViewPager → 同步底部导航选中状态
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
}
```

> **答辩要点**：通过 `OnItemSelectedListener`（导航→页面）和 `OnPageChangeCallback`（页面→导航）实现双向同步，使用 `FragmentStateAdapter` 管理 Fragment 生命周期，避免内存泄漏。

---

### 4.2 运动打卡 —— 从点击到存储的完整流程

这是本 APP **最核心的业务逻辑**，完整覆盖用户交互 → 数据校验 → Room 持久化 → SharedPreferences 更新 → UI 反馈的闭环：

```java
// CheckInFragment.java —— 打卡提交核心方法
private void submitCheckIn() {

    // ╔══════════════════════════════════════════════╗
    // ║  步骤 1：获取用户输入 + 前端校验              ║
    // ╚══════════════════════════════════════════════╝
    String caloriesStr = etCalories.getText().toString().trim();
    if (caloriesStr.isEmpty()) {
        etCalories.setError("请输入消耗卡路里");    // 空值校验
        return;
    }
    int calories;
    try {
        calories = Integer.parseInt(caloriesStr);    // 格式校验
    } catch (NumberFormatException e) {
        etCalories.setError("请输入有效数字");
        return;
    }

    String note = etNote.getText().toString().trim();
    String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(new Date());                     // 获取当前日期

    // ╔══════════════════════════════════════════════╗
    // ║  步骤 2：构建数据对象                        ║
    // ╚══════════════════════════════════════════════╝
    CheckInRecord record = new CheckInRecord(
            todayDate,          // 打卡日期 (yyyy-MM-dd)
            selectedType,       // 运动类型（跑步/游泳/瑜伽...）
            durationMinutes,    // 运动时长（分钟）
            calories,           // 消耗卡路里
            note                // 备注
    );

    // ╔══════════════════════════════════════════════╗
    // ║  步骤 3：异步写入 Room 数据库（子线程）       ║
    // ╚══════════════════════════════════════════════╝
    Executors.newSingleThreadExecutor().execute(() -> {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        db.checkInDao().insert(record);              // Room INSERT

        // ╔══════════════════════════════════════════════╗
        // ║  步骤 4：更新 SharedPreferences 连续打卡天数 ║
        // ╚══════════════════════════════════════════════╝
        PreferencesHelper prefs = PreferencesHelper.getInstance(requireContext());
        String lastDate = prefs.getLastCheckinDate();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String yesterday = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(cal.getTime());

        if (lastDate.equals(yesterday)) {
            prefs.setStreakDays(prefs.getStreakDays() + 1);  // 连续 → +1
        } else if (!lastDate.equals(todayDate)) {
            prefs.setStreakDays(1);                          // 中断 → 重置为1
        }
        prefs.setLastCheckinDate(todayDate);

        // ╔══════════════════════════════════════════════╗
        // ║  步骤 5：切回主线程 → UI 反馈               ║
        // ╚══════════════════════════════════════════════╝
        requireActivity().runOnUiThread(() -> {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("打卡成功！")
                .setMessage("运动类型：" + selectedType
                    + "\n时长：" + durationMinutes + " 分钟"
                    + "\n卡路里：" + calories + " 千卡")
                .setPositiveButton("太棒了", (dialog, which) -> {
                    dialog.dismiss();
                    etCalories.setText("");              // 清空输入
                    etNote.setText("");
                    checkTodayStatus();                  // 刷新今日状态
                }).show();
        });
    });
}
```

---

### 4.3 数据层 —— Room 数据库三件套

#### Entity（数据表定义）
```java
@Entity(tableName = "check_in_records")
public class CheckInRecord {
    @PrimaryKey(autoGenerate = true)
    private long id;               // 自增主键

    private String date;           // 打卡日期 "2026-06-10"
    private String exerciseType;   // 运动类型
    private int durationMinutes;   // 时长（分钟）
    private int calories;          // 消耗卡路里
    private String note;           // 备注
    private long timestamp;        // Unix 时间戳
}
```

#### DAO（数据访问接口）
```java
@Dao
public interface CheckInDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CheckInRecord record);

    @Query("SELECT * FROM check_in_records ORDER BY timestamp DESC")
    List<CheckInRecord> getAllRecordsSync();

    @Query("SELECT COUNT(*) FROM check_in_records WHERE date = :date")
    int getTodayCount(String date);

    @Query("SELECT SUM(durationMinutes) FROM check_in_records")
    int getTotalMinutes();

    @Query("SELECT SUM(calories) FROM check_in_records")
    int getTotalCalories();
}
```

#### Database（数据库单例）
```java
@Database(entities = {CheckInRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;        // volatile 保证可见性

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {          // DCL 双重检查锁
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "exercise_database"             // 数据库文件名
                    ).fallbackToDestructiveMigration()   // 版本升级策略
                     .build();
                }
            }
        }
        return INSTANCE;
    }
}
```

---

### 4.4 后台服务 —— StepService 前台计步

```java
public class StepService extends Service implements SensorEventListener {

    // 前台通知，持续在通知栏显示步数
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        startForeground(NOTIFICATION_ID, buildNotification());  // 前台服务
        handler.postDelayed(stepRunnable, 5000);                // 每 5s 模拟步数
        return START_STICKY;  // 被系统杀死后自动重启
    }

    // 步数模拟（每 5 秒随机增加 1~5 步）
    private Runnable stepRunnable = new Runnable() {
        @Override public void run() {
            if (isRunning) {
                stepCount += new Random().nextInt(5) + 1;
                updateNotification();                                    // 刷新通知栏
                PreferencesHelper.getInstance(StepService.this)
                    .setTodaySteps(stepCount);                           // 持久化步数
                if (stepCount % 100 == 0) sendStepsBroadcast();          // 每100步广播
                handler.postDelayed(this, 5000);                         // 循环
            }
        }
    };
}
```

### 4.5 广播接收 —— WorkoutReceiver 通知提醒

```java
public class WorkoutReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("com.example.exercise.WORKOUT_COMPLETE".equals(action)) {
            // 运动完成 → 发送系统通知
            String type = intent.getStringExtra("exercise_type");
            showNotification(context, "运动完成！",
                type + " 完成！消耗 " + intent.getIntExtra("calories", 0) + " 千卡");

        } else if ("com.example.exercise.STEPS_UPDATED".equals(action)) {
            // 步数达标 → 发送达标通知
            int steps = intent.getIntExtra("extra_steps", 0);
            int goal = context.getSharedPreferences("exercise_prefs", MODE_PRIVATE)
                .getInt("step_goal", 8000);
            if (steps >= goal && steps > 0) {
                showNotification(context, "🎉 步数目标达成！",
                    "今日已完成 " + steps + " 步！");
            }
        }
    }
}
```

---

## 五、数据流转全流程（点击 → 输入 → 存储）

```
用户操作                          Java 代码流程                       数据存储
────────                        ────────────                       ────────

┌──────────────┐
│ 1. 点击底部   │
│   「打卡」Tab  │
└──────┬───────┘
       │ BottomNav.setOnItemSelectedListener
       ▼
┌──────────────┐
│ 2. ViewPager │
│   切换到      │
│   CheckIn    │
│   Fragment   │
└──────┬───────┘
       │ Fragment.onCreateView() → 加载 fragment_checkin.xml
       │ Fragment.onViewCreated() → setupExerciseTypeChips()
       ▼                             动态创建 8 个 Material Chip
┌──────────────┐
│ 3. 用户选择   │  Chip 点击 → selectedType = "跑步"
│   运动类型    │
│   调节时长    │  btnPlus/btnMinus 点击 → durationMinutes ±= 5
│   输入卡路里  │  etCalories.getText()
│   输入备注    │  etNote.getText()
└──────┬───────┘
       │ btnSubmit 点击 → submitCheckIn()
       ▼
┌──────────────────────────────────────────────────────┐
│ 4. submitCheckIn() 核心流程：                         │
│                                                      │
│  ① 前端校验：卡路里非空 + 数字格式检查                 │
│  ② 构建 CheckInRecord 对象（5 个字段）                │
│  ③ Executors.newSingleThreadExecutor().execute()     │
│     └─ 子线程中执行：                                │
│        a) AppDatabase.getInstance()                  │
│           └─ DCL 单例获取 Room 数据库实例             │
│        b) db.checkInDao().insert(record)              │
│           └─ Room 编译生成的 SQL INSERT              │
│              INSERT INTO check_in_records             │
│              (date, exerciseType, durationMinutes,    │
│               calories, note, timestamp)              │
│              VALUES (?, ?, ?, ?, ?, ?)                │
│        c) PreferencesHelper 更新连续打卡天数          │
│           └─ SharedPreferences.edit().putInt()       │
│  ④ runOnUiThread() 切回主线程                        │
│     └─ MaterialAlertDialogBuilder 弹窗确认            │
│        └─ 清空输入框 → 刷新今日状态                   │
└──────────────────┬───────────────────────────────────┘
                   │
    ┌──────────────┴──────────────┐
    ▼                             ▼
┌─────────────┐           ┌─────────────────┐
│ Room 数据库  │           │ SharedPreferences│
│             │           │                 │
│ /data/data/ │           │ /data/data/     │
│ com.example │           │ com.example     │
│ .exercise/  │           │ .exercise/      │
│ databases/  │           │ shared_prefs/   │
│ exercise_   │           │ exercise_prefs  │
│ database    │           │ .xml            │
│             │           │                 │
│ 存储：      │           │ 存储：           │
│ · 打卡日期  │           │ · 昵称/体重/身高 │
│ · 运动类型  │           │ · 每日运动目标   │
│ · 时长      │           │ · 今日步数       │
│ · 卡路里    │           │ · 连续打卡天数   │
│ · 备注      │           │ · 上次打卡日期   │
│ · 时间戳    │           │                 │
└─────────────┘           └─────────────────┘
```

### 数据流向总结

| 阶段 | 线程 | 操作 | 关键技术 |
|:---:|------|------|----------|
| ① 用户交互 | 主线程 | Chip 选择、按钮点击、文本输入 | Material Design Chip + OnClickListener |
| ② 前端校验 | 主线程 | 空值判断、NumberFormatException 捕获 | EditText.setError() 错误提示 |
| ③ 数据封装 | 主线程 | new CheckInRecord(...) | POJO 对象构造 |
| ④ 异步写入 | 子线程 | Room insert + SharedPreferences 更新 | Executors + Room DAO + PreferencesHelper |
| ⑤ UI 反馈 | 主线程 | MaterialAlertDialog 弹窗 + 状态刷新 | runOnUiThread() 线程切换 |

---

## 六、评分点对应关系

| 评分要求 | 实现方式 | 对应文件 |
|---------|---------|---------|
| **程序完整运行无Bug** | 全链路 Activity→Fragment→Service→Receiver 闭环 | 全部 Java 文件 |
| **≥4 个 Activity** | SplashActivity → MainActivity → CourseDetailActivity → CheckInHistoryActivity | `ui/splash/`, `ui/main/`, `ui/courses/`, `ui/checkin/` |
| **Fragment 多页面切换** | ViewPager2 + FragmentStateAdapter 管理 4 个 Fragment | `ui/main/MainActivity.java` |
| **RecyclerView 列表** | 课程列表（CoursesFragment）+ 打卡记录（CheckInHistoryActivity）+ 首页推荐 | `adapter/CourseAdapter.java`, `adapter/CheckInAdapter.java` |
| **Service 后台服务** | StepService 前台计步，通知栏 + 传感器 + Handler 模拟 | `service/StepService.java` |
| **BroadcastReceiver** | WorkoutReceiver 接收运动完成/步数更新/开机广播 | `receiver/WorkoutReceiver.java` |
| **数据持久化** | Room (SQLite) 存储打卡记录 + SharedPreferences 存储用户配置 | `data/local/AppDatabase.java`, `data/local/PreferencesHelper.java` |
| **≥2 种第三方库** | Lottie（动画）+ MPAndroidChart（图表）+ Glide（图片）+ Room（数据库） | `gradle/libs.versions.toml` |
| **Material Design** | MaterialButton, Chip, CardView, BottomNav, TextInputLayout, MaterialToolbar | `res/layout/*.xml` |
| **ConstraintLayout** | 所有 Activity 根布局使用 ConstraintLayout | `res/layout/activity_*.xml` |

---

## 七、技术亮点

1. **双重检查锁（DCL）单例模式**：`AppDatabase` 使用 `volatile + synchronized` 保证线程安全且高效
2. **主线程/子线程分离**：数据库写入通过 `Executors.newSingleThreadExecutor()` 异步执行，UI 更新通过 `runOnUiThread()` 切回主线程
3. **连续打卡天数算法**：通过比较 `lastCheckinDate` 与 `yesterday` 自动判断是否连续
4. **双向同步模式**：ViewPager2 滑动 ↔ BottomNav 选中状态通过两个监听器相互驱动
5. **前台 Service 生命周期**：`START_STICKY` 确保 Service 被杀后自动恢复
6. **Material Design 主题系统**：统一使用 `Theme.MaterialComponents.Light.NoActionBar` + 自定义颜色体系

---

*答辩准备提示：重点讲解「运动打卡」的完整数据流转过程（第五章），这是最能体现 Android 开发能力的核心业务逻辑。*
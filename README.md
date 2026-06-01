# 🏋️ 运动健身打卡 APP

> Android Studio + Java | 移动应用开发工程实践期末大作业

一款基于 Android 平台的运动健身打卡应用，帮助用户记录每日运动数据、浏览健身课程、完成打卡任务，建立并维持健康运动习惯。

---

## 📱 功能特性

| 模块 | 功能 |
|------|------|
| **欢迎启动页** | Lottie 动画 + 2 秒倒计时跳转 |
| **首页推荐** | 今日步数/卡路里/时长统计、目标进度条、推荐课程列表 |
| **健身课程** | RecyclerView 展示 12 门课程、Material Chip 分类筛选、课程详情页 |
| **运动打卡** | 8 种运动类型选择、时长调节、卡路里/备注输入、Room 持久化存储 |
| **打卡记录** | 历史记录列表、日期格式化展示、运动类型彩色图标 |
| **个人中心** | 用户信息编辑、累计统计、MPAndroidChart 周趋势柱状图、目标设置 |
| **后台计步** | StepService 前台服务、通知栏实时步数、传感器监听 |
| **广播提醒** | WorkoutReceiver 运动完成通知、步数达标提醒 |

---

## 🛠 技术栈

- **语言**：Java 17
- **IDE**：Android Studio
- **最低 SDK**：Android 10 (API 29)
- **目标 SDK**：Android 15 (API 36)

### 核心依赖

| 库 | 版本 | 用途 |
|---|------|------|
| **Room** | 2.7.2 | SQLite ORM 数据库 |
| **Lottie** | 6.6.4 | 启动页动画 |
| **MPAndroidChart** | v3.1.0 | 周运动趋势图表 |
| **Glide** | 4.16.0 | 图片加载与缓存 |
| **Material Components** | 1.13.0 | Material Design UI 控件 |

### Android 组件覆盖

- ✅ **Activity**（4 个）：SplashActivity、MainActivity、CourseDetailActivity、CheckInHistoryActivity
- ✅ **Service**：StepService（前台计步服务）
- ✅ **BroadcastReceiver**：WorkoutReceiver（运动完成/步数达标广播）
- ✅ **Fragment**（4 个）：HomeFragment、CoursesFragment、CheckInFragment、ProfileFragment
- ✅ **RecyclerView**：课程列表、打卡记录列表
- ✅ **Room (SQLite)**：打卡数据持久化
- ✅ **SharedPreferences**：用户配置持久化

---

## 📂 项目结构

```
exercise/
├── build.gradle.kts                    # 顶级构建配置
├── settings.gradle.kts                 # 仓库配置（含 JitPack）
├── gradle/
│   ├── libs.versions.toml              # 依赖版本目录
│   └── wrapper/                        # Gradle Wrapper
└── app/
    ├── build.gradle.kts                # 应用构建配置
    └── src/main/
        ├── AndroidManifest.xml         # 清单文件（权限 + 组件注册）
        ├── java/com/example/exercise/
        │   ├── data/
        │   │   ├── model/              # 数据模型
        │   │   │   ├── CheckInRecord.java      # Room 实体
        │   │   │   ├── FitnessCourse.java      # 课程模型
        │   │   │   └── UserProfile.java        # 用户信息模型
        │   │   └── local/              # 数据持久层
        │   │       ├── AppDatabase.java        # Room 数据库（单例）
        │   │       ├── CheckInDao.java         # 数据访问对象
        │   │       └── PreferencesHelper.java  # SharedPreferences 工具类
        │   ├── ui/
        │   │   ├── splash/             # SplashActivity（启动页）
        │   │   ├── main/               # MainActivity（主页面）
        │   │   ├── home/               # HomeFragment（首页推荐）
        │   │   ├── courses/            # CoursesFragment + CourseDetailActivity
        │   │   ├── checkin/            # CheckInFragment + CheckInHistoryActivity
        │   │   └── profile/            # ProfileFragment（个人中心）
        │   ├── service/                # StepService（后台计步）
        │   ├── receiver/               # WorkoutReceiver（广播接收）
        │   └── adapter/                # RecyclerView 适配器
        │       ├── CourseAdapter.java
        │       └── CheckInAdapter.java
        └── res/
            ├── layout/                 # 12 个 XML 布局文件
            ├── drawable/               # 矢量图标 + 形状资源
            ├── menu/                   # bottom_nav_menu.xml
            ├── color/                  # 状态选择器
            ├── values/                 # 颜色、字符串、主题
            └── raw/                    # Lottie 动画 JSON
```

---

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog (2024.1+) 或更新版本
- JDK 17+
- Gradle 9.2.1（项目已包含 Wrapper，无需单独安装）

### 运行步骤

```bash
# 1. 克隆仓库
git clone https://github.com/YOUR_USERNAME/exercise.git
cd exercise

# 2. 构建项目
./gradlew assembleDebug    # macOS / Linux
gradlew.bat assembleDebug  # Windows

# 3. 安装 APK
# APK 位于: app/build/outputs/apk/debug/app-debug.apk
# 或直接在 Android Studio 中点击 Run ▶️
```

在 Android Studio 中打开项目后，Sync Gradle → Run 即可运行，无需额外配置。

---

## 📸 界面预览

| 启动页 | 首页 | 课程列表 | 运动打卡 |
|:---:|:---:|:---:|:---:|
| Lottie 动画 | 统计卡片 + 推荐 | RecyclerView + 筛选 | Chip 选择 + 提交 |

| 课程详情 | 打卡历史 | 个人中心 | 编辑资料 |
|:---:|:---:|:---:|:---:|
| MaterialToolbar | 日期格式化列表 | 周趋势图表 | MaterialAlertDialog |

---

## 📝 评分点覆盖

| 评分项 | 状态 |
|--------|:----:|
| 程序完整运行，无 Bug | ✅ |
| ≥4 个 Activity | ✅ (4 个) |
| Fragment 多页面切换 | ✅ (ViewPager2 + 4 Fragment) |
| RecyclerView 列表 | ✅ (课程列表 + 打卡记录) |
| Service 后台服务 | ✅ (StepService) |
| BroadcastReceiver 广播 | ✅ (WorkoutReceiver) |
| 数据持久化存储 | ✅ (Room + SharedPreferences) |
| ≥2 种第三方开源库 | ✅ (Lottie + MPAndroidChart + Glide + Room) |
| Material Design 控件 | ✅ (全应用统一使用) |
| ConstraintLayout 布局 | ✅ |

---

## 📄 许可证

本项目为大学课程大作业，仅供学习参考。

---

*南京理工大学紫金学院 · 计算机与人工智能学院 · 移动应用开发工程实践*
# AppSweep — Complete Project Plan & Documentation

> **"Clean & Manage Your Apps"**

---

## Table of Contents

1. [App Inspiration & Origin Story](#1-app-inspiration--origin-story)
2. [App Overview & Core Concept](#2-app-overview--core-concept)
3. [Architecture & Design Patterns](#3-architecture--design-patterns)
4. [Project Structure & File Map](#4-project-structure--file-map)
5. [Data Layer — Models, Repository & State](#5-data-layer--models-repository--state)
6. [Business Logic — ViewModels & Ad System](#6-business-logic--viewmodels--ad-system)
7. [UI Layer — Screens, Components & Theme](#7-ui-layer--screens-components--theme)
8. [Navigation & App Flow](#8-navigation--app-flow)
9. [Frontend Design System](#9-frontend-design-system)
10. [Utility Layer — Helpers & Preferences](#10-utility-layer--helpers--preferences)
11. [Build Configuration & Signing](#11-build-configuration--signing)
12. [CI/CD — GitHub Actions Workflow](#12-cicd--github-actions-workflow)
13. [Permissions & Security](#13-permissions--security)
14. [Monetization — Ad-Reward System](#14-monetization--ad-reward-system)
15. [Full Source Code Reference](#15-full-source-code-reference)
16. [Future Roadmap](#16-future-roadmap)
17. [Learning Notes for Students](#17-learning-notes-for-students)

---

## 1. App Inspiration & Origin Story

### The App That Inspired Us: **Baxa** (`com.tafayor.baxa`)

AppSweep was inspired by **Baxa**, a popular Android app manager developed by Tafayor. Baxa is known for its clean, functional approach to app management — listing installed apps, providing uninstall capabilities, and offering a pro/paid version that unlocks system app management features. The core idea is simple yet powerful: give users a tool to see what's installed on their device and take control of their app ecosystem.

### What We Learned from Baxa

| Baxa Feature | What We Learned | AppSweep Implementation |
|---|---|---|
| App listing with icon, name, size | Users want a quick visual overview of all apps | LazyColumn with Card items showing icon, name, package, size, category |
| System app management (Pro/Paid) | Restricting system apps creates monetization opportunity | Ad-reward system instead of paid Pro — users watch a video ad for 30-min system access |
| Search and sort functionality | Users need to quickly find apps in long lists | Real-time search + 6 sort options + 5 filter chips |
| Batch uninstall | Power users want to remove multiple apps at once | Batch mode with multi-select checkboxes, select all, and batch uninstall |
| App detail view with actions | Users want quick actions (open, settings, store, share) | Detail screen with Open/Store/Settings/Share + full app info cards |
| Clean, lightweight UI | App managers should feel fast and uncluttered | Material 3 light theme with blue-purple palette, rounded cards, smooth animations |

### What Makes AppSweep Different

- **No paid Pro version**: Instead of locking features behind a paywall, we use a rewarded video ad system — users watch a short ad to unlock system app access for 30 minutes. This is more accessible for students and casual users.
- **Modern UI**: Built entirely with Jetpack Compose and Material 3, giving a fresh, animated, and modern feel compared to traditional View-based apps.
- **Open source**: The entire codebase is available on GitHub for learning and contribution.
- **MVVM Architecture**: Clean separation of concerns with StateFlow-based state management, making the code easy to understand and extend.
- **CI/CD Pipeline**: Automated APK building via GitHub Actions, so anyone can download the latest build.

---

## 2. App Overview & Core Concept

### What is AppSweep?

AppSweep is an Android app manager that helps users view, search, filter, sort, and uninstall applications on their device. It provides a beautiful light-themed interface with both individual and batch uninstall capabilities.

### Core Features

1. **App Listing**: Displays all installed apps with icon, name, package name, size, and category (User/System)
2. **Real-time Search**: Instant filtering by app name or package name as you type
3. **5 Filter Options**: All Apps, User Apps, System Apps, Recently Installed, Largest Apps
4. **6 Sort Options**: Name A-Z, Name Z-A, Size (Largest), Size (Smallest), Install Date (Newest/Oldest)
5. **Batch Uninstall Mode**: Multi-select apps with checkboxes, see total selected count and space savings
6. **App Detail Screen**: Full app info (size, dates, SDK versions, paths) with quick actions (Open, Store, Settings, Share)
7. **Ad-Reward System**: Watch a video ad to unlock system app features for 30 minutes
8. **Auto-Refresh**: List updates automatically when apps are installed or uninstalled
9. **Preference Persistence**: Sort and filter selections survive app restarts via SharedPreferences

### App Specs

| Property | Value |
|---|---|
| Package Name | `com.student.appmanager` |
| App Name | AppSweep |
| Minimum SDK | 24 (Android 7.0 Nougat) |
| Target SDK | 34 (Android 14) |
| Language | Kotlin 100% |
| UI Framework | Jetpack Compose + Material 3 |
| Architecture | MVVM + Repository Pattern |
| Build System | Gradle 8.5 with Kotlin DSL |
| Version | 1.1.0 (versionCode 2) |

---

## 3. Architecture & Design Patterns

### MVVM (Model-View-ViewModel)

AppSweep follows the **MVVM architecture pattern**, which separates the app into three layers:

```
┌─────────────────────────────────────────────────────┐
│                    VIEW LAYER                        │
│  (Compose Screens & Components)                      │
│  HomeScreen, DetailScreen, AdDialog, etc.            │
│  Observes StateFlow from ViewModel                   │
├─────────────────────────────────────────────────────┤
│                  VIEWMODEL LAYER                     │
│  (Business Logic & State Management)                 │
│  HomeViewModel, DetailViewModel                      │
│  Transforms data from Repository into UI state       │
│  Survives configuration changes                      │
├─────────────────────────────────────────────────────┤
│                   MODEL LAYER                        │
│  (Data Models + Repository)                          │
│  AppInfo, AppRepository, AdManager, PreferenceManager│
│  Single source of truth for app data                 │
└─────────────────────────────────────────────────────┘
```

### Design Patterns Used

| Pattern | Where Used | Purpose |
|---|---|---|
| **Repository Pattern** | `AppRepository` | Abstracts data source (PackageManager) from ViewModel; single source of truth |
| **Observer Pattern** | `StateFlow` + `collectAsState()` | ViewModel emits state changes; UI reacts automatically |
| **Factory Pattern** | `DetailViewModel.Factory` | Creates ViewModels with parameters (packageName) |
| **Strategy Pattern** | `SortOption` / `FilterOption` enums | Different sort/filter strategies selected at runtime |
| **Singleton Pattern** | `PreferenceManager` | Single shared preferences instance |
| **State Pattern** | `AdRewardState` | Ad access transitions between locked/unlocked/expired states |

### Data Flow Diagram

```
User Action (tap, type, select)
        │
        ▼
Compose Screen (View Layer)
        │ calls ViewModel method
        ▼
ViewModel (Business Logic)
        │ calls Repository
        ▼
Repository (Data Layer)
        │ queries PackageManager
        ▼
Android OS (PackageManager API)
        │
        ▼
Repository returns List<AppInfo>
        │
        ▼
ViewModel updates StateFlow<UiState>
        │
        ▼
Compose recomposes UI with new state
```

---

## 4. Project Structure & File Map

```
AppSweep/
├── .github/
│   └── workflows/
│       └── build.yml                          # CI/CD: Build signed APKs on push
├── .gitignore
├── README.md
├── PROJECT_PLAN.md                            # ← This file
├── appsweep-release.jks                       # Signing keystore
├── build.gradle.kts                           # Root build config
├── keystore.properties                        # Keystore credentials
├── gradle.properties                          # JVM args, AndroidX flags
├── settings.gradle.kts                        # Project name & module includes
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties           # Gradle 8.5
├── gradlew                                    # Gradle wrapper script
└── app/
    ├── build.gradle.kts                        # App module build config
    ├── proguard-rules.pro                      # ProGuard/R8 rules
    └── src/main/
        ├── AndroidManifest.xml                 # Permissions, AdMob, launcher
        ├── java/com/student/appmanager/
        │   ├── AppSweepApp.kt                  # Application class (AdMob init)
        │   ├── MainActivity.kt                 # Single Activity entry point
        │   ├── data/
        │   │   ├── model/
        │   │   │   └── AppInfo.kt              # Data models & UI states
        │   │   └── repository/
        │   │       └── AppRepository.kt        # PackageManager data source
        │   ├── ui/
        │   │   ├── components/
        │   │   │   ├── AdDialog.kt             # Ad reward dialog & simulated ad
        │   │   │   ├── AppListItem.kt          # App card component
        │   │   │   ├── CommonComponents.kt      # Shared UI (Batch bar, Empty, Loading, Count)
        │   │   │   ├── FilterChips.kt          # Filter chips & sort dropdown
        │   │   │   └── SearchBar.kt            # Search input component
        │   │   ├── navigation/
        │   │   │   └── NavGraph.kt             # Compose Navigation setup
        │   │   ├── screens/
        │   │   │   ├── batch/                  # (Planned: dedicated batch screen)
        │   │   │   ├── details/
        │   │   │   │   └── DetailScreen.kt     # App detail view + DetailViewModel
        │   │   │   └── home/
        │   │   │       ├── HomeScreen.kt       # Main app list screen
        │   │   │       └── HomeViewModel.kt    # Home screen state & logic
        │   │   └── theme/
        │   │       ├── Color.kt               # Full color palette
        │   │       ├── Theme.kt               # Material 3 light theme
        │   │       └── Type.kt                # Typography system
        │   └── util/
        │       ├── AdManager.kt               # Ad reward state machine
        │       ├── AppUtils.kt                # Intent helpers
        │       └── PreferenceManager.kt       # SharedPreferences wrapper
        └── res/
            ├── drawable/
            │   ├── ic_launcher_background.xml  # Blue adaptive icon background
            │   └── ic_launcher_foreground.xml  # Broom/sparkle icon foreground
            ├── mipmap-anydpi-v26/
            │   ├── ic_launcher.xml            # Adaptive icon definition
            │   └── ic_launcher_round.xml      # Round adaptive icon
            ├── mipmap-hdpi/ through xxxhdpi/  # PNG icons for all densities
            ├── values/
            │   ├── colors.xml                 # XML color resources
            │   ├── strings.xml                # 28 string resources
            │   └── themes.xml                 # Theme.AppSweep XML theme
            └── xml/                           # (Planned: backup config)
```

---

## 5. Data Layer — Models, Repository & State

### 5.1 AppInfo — Core Data Model

The `AppInfo` data class is the heart of the app. It represents a single installed application and is Parcelable (can be passed between screens via Navigation arguments).

```kotlin
@Parcelize
data class AppInfo(
    val packageName: String,        // Unique identifier: "com.example.app"
    val appName: String,            // User-visible name: "My App"
    val iconPath: String = "",      // Path to icon drawable
    val versionName: String = "",   // Human version: "1.0.0"
    val versionCode: Long = 0,      // Numeric version for updates
    val isSystemApp: Boolean = false, // FLAG_SYSTEM check from PackageManager
    val apkSize: Long = 0,          // File size of APK in bytes
    val installDate: Long = 0,      // firstInstallTime timestamp
    val lastUpdated: Long = 0,      // lastUpdateTime timestamp
    val targetSdk: Int = 0,         // Target SDK version
    val minSdk: Int = 0,            // Minimum SDK version
    val sourceDir: String = "",     // Path: "/data/app/com.example/base.apk"
    val dataDir: String = ""        // Path: "/data/data/com.example"
) : Parcelable
```

**Computed Properties**:
- `category`: Returns `AppCategory.USER` or `AppCategory.SYSTEM` based on `isSystemApp`
- `formattedSize`: Converts byte size to human-readable string (e.g., "12.5 MB")
- `isFrameworkApp`: True if system app AND installed in `/system/` partition

**How AppInfo is populated** (in `AppRepository.mapToAppInfo()`):
1. `packageManager.getInstalledPackages()` returns `List<PackageInfo>`
2. For each `PackageInfo`, extract `ApplicationInfo`
3. `appName` = `applicationInfo.loadLabel(packageManager)`
4. `isSystemApp` = `(applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0`
5. `apkSize` = `File(applicationInfo.sourceDir).length()`
6. `installDate` = `packageInfo.firstInstallTime`
7. `lastUpdated` = `packageInfo.lastUpdateTime`

### 5.2 Enum Models

```kotlin
enum class AppCategory {
    USER,           // User-installed apps
    SYSTEM;         // Pre-installed system apps
    val displayName: String get() = when (this) {
        USER -> "User Apps"
        SYSTEM -> "System Apps"
    }
}

enum class SortOption(val displayName: String) {
    NAME_AZ("Name A-Z"),           // Alphabetical ascending
    NAME_ZA("Name Z-A"),           // Alphabetical descending
    SIZE_LARGEST("Size (Largest)"), // Biggest APK first
    SIZE_SMALLEST("Size (Smallest)"), // Smallest APK first
    DATE_NEWEST("Install Date (Newest)"), // Most recently installed
    DATE_OLDEST("Install Date (Oldest)")  // Longest installed
}

enum class FilterOption(val displayName: String) {
    ALL("All Apps"),                    // No filtering
    USER_ONLY("User Apps"),            // Only user-installed apps
    SYSTEM_ONLY("System Apps"),        // Only system/pre-installed apps
    RECENTLY_INSTALLED("Recently Installed"), // Installed in last 7 days
    LARGEST_FIRST("Largest Apps")      // Sorted by size descending
}
```

### 5.3 UI State Models (MVI-style)

We use **immutable state objects** that represent the entire state of a screen at any point in time. This makes state changes predictable and debuggable.

```kotlin
data class HomeUiState(
    val allApps: List<AppInfo> = emptyList(),       // Full app list from system
    val filteredApps: List<AppInfo> = emptyList(),  // After search+filter+sort
    val searchQuery: String = "",                    // Current search text
    val currentSort: SortOption = SortOption.NAME_AZ,
    val currentFilter: FilterOption = FilterOption.ALL,
    val isLoading: Boolean = true,                   // Loading spinner
    val selectedApps: Set<String> = emptySet(),      // Batch selection (packageNames)
    val isBatchMode: Boolean = false,                // Batch mode toggle
    val adRewardState: AdRewardState = AdRewardState(),
    val error: String? = null                        // Error message
)
```

```kotlin
data class DetailUiState(
    val appInfo: AppInfo? = null,        // The app being viewed
    val isLoading: Boolean = true,
    val isUninstalling: Boolean = false,
    val adRewardState: AdRewardState = AdRewardState(),
    val showAdDialog: Boolean = false,
    val error: String? = null
)
```

```kotlin
data class AdRewardState(
    val hasSystemAccess: Boolean = false,       // Whether system apps are accessible
    val systemAccessExpiry: Long = 0,           // Timestamp when access expires
    val adsWatchedToday: Int = 0,               // Daily ad counter
    val lastAdWatchTime: Long = 0               // Last ad watch timestamp
) {
    val isAccessValid: Boolean
        get() = hasSystemAccess && System.currentTimeMillis() < systemAccessExpiry

    companion object {
        const val ACCESS_DURATION_MS = 30 * 60 * 1000L  // 30 minutes
    }
}
```

### 5.4 AppRepository — Data Access Layer

The `AppRepository` class is the **single source of truth** for all app data. It abstracts the Android `PackageManager` API behind a clean interface.

**Key Methods**:

| Method | Input | Output | Thread | Description |
|---|---|---|---|---|
| `getInstalledApps()` | — | `List<AppInfo>` | IO | All installed apps |
| `getAppByPackageName()` | String | `AppInfo?` | IO | Single app by package name |
| `getUserApps()` | — | `List<AppInfo>` | IO | Only user apps |
| `getSystemApps()` | — | `List<AppInfo>` | IO | Only system apps |
| `searchApps()` | query, List | `List<AppInfo>` | Main | Case-insensitive search by name or package |
| `sortApps()` | List, SortOption | `List<AppInfo>` | Main | Sort by 6 criteria |
| `filterApps()` | List, FilterOption | `List<AppInfo>` | Main | Filter by 5 categories |
| `calculateTotalSize()` | List, Set | Long | Main | Total bytes of selected apps |

**Why Repository Pattern?**
- If we later add a database cache (Room), we only change `AppRepository`, not the ViewModel
- If we add a remote API for app ratings, we add it to `AppRepository`
- The ViewModel doesn't know or care WHERE the data comes from
- Easy to mock for testing — just create a `FakeAppRepository`

---

## 6. Business Logic — ViewModels & Ad System

### 6.1 HomeViewModel — The Brain of the App

The `HomeViewModel` is the most complex class in the app. It manages:

1. **App Loading**: Fetches all installed apps on init and refreshes on package changes
2. **Search**: Real-time text search filtering on every keystroke
3. **Filter Pipeline**: Filter → Search → Sort (applied in order)
4. **Sort**: 6 sort options with persistence via `PreferenceManager`
5. **Batch Mode**: Multi-select toggle, select all, clear selection
6. **Ad Rewards**: Show ad dialog, simulate ad playback, grant 30-min access
7. **Package Change Detection**: BroadcastReceiver for app install/uninstall events

**The Filter Pipeline** (the core algorithm):

```
allApps (full list from PackageManager)
    │
    ▼ Step 1: Filter by category
    repository.filterApps(result, currentFilter)
    │
    ▼ Step 2: Filter by search text
    repository.searchApps(searchQuery, result)
    │
    ▼ Step 3: Sort the filtered results
    repository.sortApps(result, currentSort)
    │
    ▼
filteredApps (displayed in LazyColumn)
```

**State Management Flow**:

```kotlin
// ViewModel holds state
private val _uiState = MutableStateFlow(HomeUiState())
val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

// UI observes state
val uiState by viewModel.uiState.collectAsState()

// ViewModel updates state immutably
_uiState.update { state ->
    state.copy(searchQuery = "new query")
}
```

**BroadcastReceiver for Package Changes**:

```kotlin
private val packageChangeReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED -> refreshApps()
        }
    }
}
```

- On Android 13+ (API 33), uses `RECEIVER_EXPORTED` flag for system broadcasts
- Unregistered in `onCleared()` to prevent memory leaks

### 6.2 DetailViewModel — Single App Manager

The `DetailViewModel` is simpler — it manages state for viewing one app's details. It uses the **Factory Pattern** because ViewModels can't directly receive constructor parameters.

```kotlin
class DetailViewModel(
    private val packageName: String,
    private val repository: AppRepository
) : ViewModel() {

    class Factory(
        private val packageName: String,
        private val repository: AppRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(packageName, repository) as T
        }
    }
}
```

**Usage in Compose**:

```kotlin
viewModel(
    factory = DetailViewModel.Factory(
        packageName = packageName,
        repository = AppRepository(context.applicationContext)
    )
)
```

### 6.3 AdManager — Ad Reward State Machine

The `AdManager` manages the ad-reward lifecycle:

```
┌──────────┐    Watch Ad     ┌──────────┐    30 min pass    ┌──────────┐
│  LOCKED  │ ──────────────► │ UNLOCKED │ ───────────────►  │  LOCKED  │
│  (No     │                 │ (Access  │                    │  (Access │
│  Access) │                 │  Active) │                    │  Expired)│
└──────────┘                 └──────────┘                    └──────────┘
     ▲                              │
     │      Watch Another Ad        │
     └──────────────────────────────┘
         (Extends access by 30 min)
```

**Key Constants**:
- `ACCESS_DURATION_MS = 30 * 60 * 1000L` (30 minutes per ad)
- `MIN_AD_INTERVAL_MS = 5 * 60 * 1000L` (5-minute cooldown between ads)
- `MAX_DAILY_ADS = 20` (Maximum ad watches per day)

---

## 7. UI Layer — Screens, Components & Theme

### 7.1 HomeScreen — Main App List

The HomeScreen is the primary interface users see when they open the app.

```
┌────────────────────────────────────────────┐
│  🗑️ AppSweep              [☑ Batch Mode]  │  ← TopAppBar
├────────────────────────────────────────────┤
│  🔍 Search apps...                    ✕    │  ← SearchBar
├────────────────────────────────────────────┤
│  [All] [User] [System] [Recent] [Large]   │  ← FilterChipsRow
├────────────────────────────────────────────┤
│  Total: 45  User: 28  System: 17  [Sort▼] │  ← CountHeader + SortDropdown
├────────────────────────────────────────────┤
│  ┌──────────────────────────────────────┐  │
│  │ 📱 WhatsApp         [42.5 MB]       │  │
│  │    com.whatsapp     [Remove]         │  │  ← AppListItem (normal mode)
│  └──────────────────────────────────────┘  │
│  ┌──────────────────────────────────────┐  │
│  │ 📱 Chrome          [89.2 MB] [System]│  │
│  │    com.android.chrome  [Remove]      │  │  ← AppListItem (system app)
│  └──────────────────────────────────────┘  │
│  ... more apps ...                         │
├────────────────────────────────────────────┤
│  3 apps selected | Free up 156.7 MB       │  ← BatchActionBar
│  [Uninstall 3 Apps]  [Select All] [Clear] │     (visible in batch mode)
└────────────────────────────────────────────┘
```

**Key Composable Functions in HomeScreen**:

| Function | Description |
|---|---|
| `HomeScreen()` | Main screen with Scaffold, observes ViewModel state |
| `HomeTopBar()` | AppSweep title with batch mode toggle icon |
| `SearchBar()` | OutlinedTextField with search icon and clear button |
| `FilterChipsRow()` | Horizontal scrollable FilterChip row |
| `AppCountHeader()` | Total/User/System count chips |
| `SortDropdown()` | OutlinedButton with DropdownMenu |
| `AppListItem()` | Individual app card in LazyColumn |
| `BatchActionBar()` | Bottom bar in batch mode (animated slide) |
| `AdRewardDialog()` | Dialog for ad-reward system |
| `SimulatedAdScreen()` | Full-screen simulated ad overlay |

### 7.2 DetailScreen — App Details & Actions

```
┌────────────────────────────────────────────┐
│  ← App Details                             │  ← TopAppBar with back button
├────────────────────────────────────────────┤
│              [📱 App Icon]                  │  ← 80dp rounded icon
│              WhatsApp                       │
│           com.whatsapp                      │
│            Version 2.23.24                  │
│           [User App]                        │  ← Category badge
├────────────────────────────────────────────┤
│  App Information                            │
│  ┌──────────────────────────────────────┐  │
│  │ 💾 App Size       42.5 MB           │  │  ← InfoCard
│  ├──────────────────────────────────────┤  │
│  │ 📅 Installed      Jan 15, 2024      │  │
│  ├──────────────────────────────────────┤  │
│  │ 🔄 Last Updated   Mar 20, 2024     │  │
│  ├──────────────────────────────────────┤  │
│  │ 🔧 Target SDK     API 34           │  │
│  ├──────────────────────────────────────┤  │
│  │ 📱 Minimum SDK    API 24           │  │
│  ├──────────────────────────────────────┤  │
│  │ 📁 APK Path       /data/app/...    │  │
│  └──────────────────────────────────────┘  │
├────────────────────────────────────────────┤
│  Actions                                   │
│  [Open] [Store] [Settings] [Share]         │  ← ActionButton row
├────────────────────────────────────────────┤
│  [🗑️ Uninstall WhatsApp]                   │  ← Red uninstall button
└────────────────────────────────────────────┘
```

### 7.3 Reusable UI Components

**AppListItem** — The core list item component:

| Property | Behavior |
|---|---|
| Normal mode | Shows icon, name, package, size badge, system badge, "Remove" button |
| Batch mode | Shows checkbox instead of "Remove" button, blue highlight when selected |
| Click | Navigates to detail (user apps) or shows ad dialog (system apps without access) |

**FilterChipsRow** — Horizontal filter selector:

- 5 `FilterChip` components in a scrollable `Row`
- Selected chip: Blue500 background, white text
- Unselected chip: Gray100 background, gray text
- Pill-shaped with `RoundedCornerShape(20.dp)`

**SortDropdown** — Sort option picker:

- `OutlinedButton` showing current sort
- `DropdownMenu` with 6 options
- Blue checkmark icon next to selected option

**BatchActionBar** — Batch mode bottom bar:

- Slides up with `AnimatedVisibility` + `slideInVertically`
- Shows selected count, total size savings
- "Uninstall N Apps" red button
- "Select All" / "Deselect All" + "Clear" text buttons

**EmptyState** — No results display:

- Centered search icon (64dp, gray)
- Message text (title style)
- Subtitle suggestion (body style)

**LoadingState** — Loading spinner:

- Blue `CircularProgressIndicator` (48dp)
- Loading message below

**AdRewardDialog** — Ad prompt dialog:

- When locked: Shows lock icon, blue surface, "Watch Ad to Unlock" button
- When active: Shows check icon, green surface, remaining time, "Extend Access" button
- Dismissable with "Maybe Later" / "Close" text button

**SimulatedAdScreen** — Placeholder ad:

- Full-screen dark overlay (Gray900)
- Loading spinner + "Loading ad..." text
- In production: replaced by Google AdMob `RewardedAd`

---

## 8. Navigation & App Flow

### Navigation Graph

AppSweep uses **Jetpack Navigation Compose** with two routes:

```kotlin
NavHost(navController, startDestination = "home") {
    composable("home") {
        HomeScreen(onNavigateToDetail = { packageName ->
            navController.navigate("detail/$packageName")
        })
    }
    composable("detail/{packageName}") { backStackEntry ->
        val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
        DetailScreen(packageName = packageName, onBack = { navController.popBackStack() })
    }
}
```

### Complete User Flow

```
App Launch
    │
    ▼
AppSweepApp.onCreate() → Initialize MobileAds SDK
    │
    ▼
MainActivity.onCreate() → Set Compose content with AppSweepTheme
    │
    ▼
AppSweepNavGraph() → Navigate to "home"
    │
    ▼
HomeScreen → HomeViewModel.loadApps()
    │
    ├── User scrolls app list
    ├── User searches → onSearchQueryChanged()
    ├── User filters → onFilterChanged()
    │       └── System apps filter? → Check ad access
    │           ├── No access → Show AdRewardDialog
    │           └── Has access → Apply filter
    ├── User sorts → onSortChanged()
    ├── User taps app →
    │   ├── System app + no access → Show AdRewardDialog
    │   │   └── Watch Ad → SimulatedAdScreen → Grant 30-min access
    │   └── User app (or has access) → Navigate to "detail/{packageName}"
    │       └── DetailScreen → DetailViewModel.loadAppDetails()
    │           ├── Open App → getLaunchIntent()
    │           ├── Play Store → getPlayStoreIntent()
    │           ├── Settings → getAppSettingsIntent()
    │           ├── Share → getShareIntent()
    │           └── Uninstall → getUninstallIntent() → Refresh on return
    ├── User taps batch mode →
    │   ├── Select apps with checkboxes
    │   └── Uninstall All → Start uninstall for each
    └── Package change broadcast → refreshApps()
```

---

## 9. Frontend Design System

### 9.1 Color Palette

AppSweep uses a carefully designed color palette inspired by modern fintech and productivity apps:

**Primary (Blue)**:
| Color | Hex | Usage |
|---|---|---|
| Blue500 | `#3B82F6` | Main brand, buttons, active states, focus |
| Blue600 | `#2563EB` | Pressed button states |
| Blue700 | `#1D4ED8` | Status bar, deep accents |
| Blue100 | `#DBEAFE` | Filled tonal button backgrounds |
| Blue50 | `#EFF6FF` | Selected item background, subtle surfaces |

**Secondary (Violet/Purple)**:
| Color | Hex | Usage |
|---|---|---|
| Violet500 | `#8B5CF6` | System app badges, accent elements |
| Violet100 | `#EDE9FE` | System chip backgrounds |

**Semantic**:
| Color | Hex | Usage |
|---|---|---|
| Green500 | `#22C55E` | Success messages, space savings |
| Green100 | `#DCFCE7` | Active access status background |
| Red500 | `#EF4444` | Uninstall buttons, destructive actions |
| Red100 | `#FEE2E2` | "Remove" button background |
| Orange500 | `#F97316` | Warm CTA accent |
| Yellow500 | `#EAB308` | Warning states |

**Neutrals**:
| Color | Hex | Usage |
|---|---|---|
| Gray900 | `#111827` | Primary text |
| Gray800 | `#1F2937` | Secondary text |
| Gray600 | `#4B5563` | Tertiary text, package names |
| Gray400 | `#9CA3AF` | Disabled states, dividers |
| Gray300 | `#D1D5DB` | Borders, outlines |
| Gray200 | `#E5E7EB` | Light borders |
| Gray100 | `#F3F4F6` | Card backgrounds, chip surfaces |
| Gray50 | `#F9FAFB` | Main screen background |
| White | `#FFFFFF` | Card surfaces, dialogs |

### 9.2 Typography Scale

Full Material 3 type scale with custom adjustments:

| Style | Size | Weight | Usage |
|---|---|---|---|
| displayLarge | 32sp | Bold | Promotional text |
| displayMedium | 28sp | Bold | Large headings |
| displaySmall | 24sp | SemiBold | Section titles |
| headlineLarge | 22sp | Bold | Screen titles |
| headlineMedium | 20sp | SemiBold | AppSweep title |
| headlineSmall | 18sp | SemiBold | Card titles |
| titleLarge | 16sp | SemiBold | App names in list |
| titleMedium | 14sp | Medium | Section labels |
| titleSmall | 12sp | Medium | Small headers |
| bodyLarge | 16sp | Regular | Long body text |
| bodyMedium | 14sp | Regular | Package names, descriptions |
| bodySmall | 12sp | Regular | Version info, dates |
| labelLarge | 14sp | Medium | Buttons, CTAs |
| labelMedium | 12sp | Medium | Chips, badges |
| labelSmall | 10sp | Medium | Size badges, tags |

### 9.3 Shape System

| Element | Shape | Radius |
|---|---|---|
| App list cards | RoundedCornerShape | 16dp |
| Search bar | RoundedCornerShape | 14dp |
| Filter chips | RoundedCornerShape | 20dp (pill) |
| Info cards (detail) | RoundedCornerShape | 12dp |
| Action buttons | RoundedCornerShape | 12dp |
| Sort dropdown | RoundedCornerShape | 10dp |
| Size/System badges | RoundedCornerShape | 6dp |
| Dialog | RoundedCornerShape | 20dp |
| Batch action bar | RoundedCornerShape | 20dp (top only) |

### 9.4 Spacing & Layout

| Measurement | Value | Usage |
|---|---|---|
| Screen padding | 16dp horizontal | Main content margins |
| Card internal padding | 12dp | Inside app list items |
| Card vertical spacing | 4dp | Between list items |
| Section spacing | 8dp | Between search, chips, counts |
| Icon size (list) | 48dp | App icons in list |
| Icon size (detail) | 80dp | Large app icon |
| Component height | 48dp | Touch targets |

### 9.5 Elevation & Shadows

| Element | Default | Selected/Active |
|---|---|---|
| App list card | 1dp | 2dp |
| Batch action bar | 16dp shadow + 4dp tonal | — |
| Dialog | Default M3 elevation | — |

---

## 10. Utility Layer — Helpers & Preferences

### 10.1 AppUtils — Intent Helpers

All methods use Android's standard public Intent system:

| Method | Intent Action | Description |
|---|---|---|
| `getUninstallIntent()` | `ACTION_DELETE` | Opens system uninstall confirmation |
| `getAppSettingsIntent()` | `ACTION_APPLICATION_DETAILS_SETTINGS` | Opens App Info settings |
| `getPlayStoreIntent()` | `ACTION_VIEW` with `market://` URI | Opens Play Store listing |
| `getLaunchIntent()` | `getLaunchIntentForPackage()` | Launches the app |
| `getShareIntent()` | `ACTION_SEND` | Shares app name + Play Store link |
| `isAppInstalled()` | `getPackageInfo()` | Checks if app still exists |

### 10.2 PreferenceManager — Persistent Settings

Uses `SharedPreferences` to persist user choices across app restarts:

| Key | Type | Default | Description |
|---|---|---|---|
| `pref_sort_option` | String | "NAME_AZ" | Last selected sort |
| `pref_filter_option` | String | "ALL" | Last selected filter |
| `pref_system_access_expiry` | Long | 0 | Ad reward expiry timestamp |
| `pref_ads_watched_today` | Int | 0 | Daily ad counter |
| `pref_last_ad_watch_time` | Long | 0 | Timestamp of last ad |
| `pref_show_system_apps` | Boolean | false | Show system apps preference |
| `pref_show_app_sizes` | Boolean | true | Show app sizes preference |
| `pref_grid_size` | Int | 1 | List density (0=compact, 1=normal, 2=large) |

### 10.3 AdManager — Reward State Machine

Tracks and manages the ad-reward lifecycle:

**State Transitions**:
1. **No Access** → User watches ad → **Access Granted (30 min)**
2. **Access Active** → 30 minutes pass → **Access Expired**
3. **Access Active** → User watches another ad → **Access Extended (+30 min)**

**Rate Limiting**:
- Minimum 5 minutes between ad watches (`MIN_AD_INTERVAL_MS`)
- Maximum 20 ads per day (`MAX_DAILY_ADS`)

---

## 11. Build Configuration & Signing

### 11.1 Signing Configuration

APK signing is **mandatory** for Android installation. AppSweep uses a dedicated release keystore:

```kotlin
// keystore.properties
storeFile=appsweep-release.jks
keyAlias=appsweep
keyPassword=appsweep123
storePassword=appsweep123
```

```kotlin
// app/build.gradle.kts
signingConfigs {
    create("release") {
        keyAlias = keystoreProperties["keyAlias"] as String? ?: "appsweep"
        keyPassword = keystoreProperties["keyPassword"] as String? ?: "appsweep123"
        storeFile = rootProject.file(keystoreProperties["storeFile"] as String? ?: "appsweep-release.jks")
        storePassword = keystoreProperties["storePassword"] as String? ?: "appsweep123"
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
    }
    debug {
        // Auto-signed with default debug keystore
    }
}
```

### 11.2 Dependencies

| Category | Dependency | Version | Purpose |
|---|---|---|---|
| Core | `core-ktx` | 1.12.0 | Android KTX extensions |
| Core | `lifecycle-runtime-ktx` | 2.7.0 | Lifecycle-aware components |
| Core | `lifecycle-viewmodel-compose` | 2.7.0 | ViewModel in Compose |
| Core | `activity-compose` | 1.8.2 | Compose Activity |
| Compose | `compose-bom` | 2024.01.00 | BOM for version management |
| Compose | `material3` | (BOM) | Material Design 3 |
| Compose | `material-icons-extended` | (BOM) | Extended icon set |
| Compose | `ui`, `ui-graphics`, `ui-tooling-preview` | (BOM) | Core Compose UI |
| Navigation | `navigation-compose` | 2.7.6 | Screen navigation |
| Coroutines | `kotlinx-coroutines-android` | 1.7.3 | Async operations |
| Images | `coil-compose` | 2.5.0 | Image loading (app icons) |
| Storage | `datastore-preferences` | 1.0.0 | Type-safe preferences |
| Ads | `play-services-ads` | 23.0.0 | Google AdMob |
| UI | `accompanist-systemuicontroller` | 0.34.0 | System bar colors |
| Test | `junit` | 4.13.2 | Unit testing |
| Test | `espresso-core` | 3.5.1 | UI testing |
| Test | `ui-test-junit4` | (BOM) | Compose testing |

### 11.3 ProGuard Rules

```proguard
# Keep Compose
-keep class androidx.compose.** { *; }

# Keep data models (used by Parcelize & Navigation)
-keep class com.student.appmanager.data.model.** { *; }

# Keep Application class
-keep class com.student.appmanager.AppSweepApp { *; }

# Keep Google Ads classes
-keep class com.google.android.gms.ads.** { *; }

# Strip debug logs in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
```

Note: Minification is currently **disabled** (`isMinifyEnabled = false`) for build reliability. Enable in production for smaller APKs.

---

## 12. CI/CD — GitHub Actions Workflow

### Workflow: Build Signed APK

**File**: `.github/workflows/build.yml`

**Triggers**:
- Push to `main` branch
- Pull request to `main` branch
- Manual trigger (`workflow_dispatch`)

**Steps**:

```yaml
1. Checkout code          → actions/checkout@v4
2. Set up JDK 17          → actions/setup-java@v4 (Temurin distribution)
3. Setup Gradle 8.5       → gradle/actions/setup-gradle@v3
4. Generate Gradle Wrapper → gradle wrapper
5. Grant execute perm     → chmod +x gradlew
6. Build Debug APK        → ./gradlew assembleDebug
7. Build Release APK      → ./gradlew assembleRelease (signed with keystore)
8. List APK outputs       → ls -la (verify files exist)
9. Rename APKs            → AppSweep-v1.1.0-debug.apk / AppSweep-v1.1.0-release.apk
10. Verify APK signing    → apksigner verify --print-certs
11. Upload Debug APK      → actions/upload-artifact@v4
12. Upload Release APK    → actions/upload-artifact@v4
```

**Artifacts**:
- `AppSweep-debug-signed` — Debug APK (auto-signed with debug keystore)
- `AppSweep-release-signed` — Release APK (signed with appsweep-release.jks)

---

## 13. Permissions & Security

### Android Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"
    tools:ignore="QueryAllPackagesPermission" />
```

| Permission | Purpose | Required? |
|---|---|---|
| `INTERNET` | Load rewarded video ads from AdMob | Yes (for ads) |
| `ACCESS_NETWORK_STATE` | Check connectivity before ad loading | Yes (for ads) |
| `QUERY_ALL_PACKAGES` | List all installed apps on device | Yes (core feature) |

**About `QUERY_ALL_PACKAGES`**: Starting from Android 11 (API 30), apps can no longer see all installed packages by default. This permission is required for app managers and is allowed by Google Play Policy for apps whose core purpose is listing/managing apps.

### AdMob Configuration

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-3940256099942544~3347511713" />
<meta-data
    android:name="com.google.android.gms.ads.FLAG_MANAGER_APP"
    android:value="true" />
```

**Note**: The current AdMob App ID (`ca-app-pub-3940256099942544~3347511713`) is Google's **test ID**. Replace with a real AdMob App ID before publishing.

---

## 14. Monetization — Ad-Reward System

### How It Works (Instead of Baxa's Paid Pro Version)

Baxa uses a paid Pro version to unlock system app features. AppSweep takes a different approach:

**Baxa Model**: Pay money → Unlock system apps permanently (Pro version)

**AppSweep Model**: Watch video ad → Unlock system apps for 30 minutes → Access expires → Watch another ad

### Ad-Reward Flow

```
1. User tries to access system apps
2. App checks: hasSystemAccess()?
   ├── YES → Allow access, show system apps
   └── NO  → Show AdRewardDialog
3. User taps "Watch Ad to Unlock"
4. SimulatedAdScreen plays (2-second delay)
   └── In production: Google AdMob RewardedAd loads and plays
5. On ad completion:
   ├── adManager.onAdWatched() called
   ├── New AdRewardState created with 30-min expiry
   ├── State persisted in SharedPreferences
   └── Filter/Search re-applied with system apps visible
6. After 30 minutes:
   ├── isAccessValid returns false
   ├── System apps become locked again
   └── User must watch another ad
```

### Production AdMob Integration (TODO)

To replace the simulated ad with real AdMob:

```kotlin
// 1. Replace test App ID with real one in AndroidManifest.xml
// 2. Create a RewardedAd loader:
class AdMobManager(private val context: Context) {
    private var rewardedAd: RewardedAd? = null

    fun loadAd(onAdLoaded: () -> Unit, onAdFailed: () -> Unit) {
        RewardedAd.load(context, "ca-app-pub-XXXXX/YYYYY", AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    onAdLoaded()
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    onAdFailed()
                }
            })
    }

    fun showAd(activity: Activity, onRewardEarned: () -> Unit) {
        rewardedAd?.show(activity) { onRewardEarned() }
    }
}
```

---

## 15. Full Source Code Reference

### 15.1 Application Class (`AppSweepApp.kt`)

```kotlin
package com.student.appmanager

import android.app.Application
import com.google.android.gms.ads.MobileAds

class AppSweepApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            MobileAds.initialize(this) { }
        } catch (e: Exception) { }
    }
}
```

### 15.2 MainActivity (`MainActivity.kt`)

```kotlin
package com.student.appmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.student.appmanager.ui.navigation.AppSweepNavGraph
import com.student.appmanager.ui.theme.AppSweepTheme
import com.student.appmanager.ui.theme.White

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppSweepTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = White) {
                    AppSweepNavGraph()
                }
            }
        }
    }
}
```

### 15.3 Navigation Graph (`NavGraph.kt`)

```kotlin
@Composable
fun AppSweepNavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onNavigateToDetail = { navController.navigate("detail/$it") })
        }
        composable("detail/{packageName}",
            arguments = listOf(navArgument("packageName") { type = NavType.StringType })
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            DetailScreen(packageName = packageName, onBack = { navController.popBackStack() })
        }
    }
}
```

### 15.4 Full File Listing (17 Kotlin source files)

| # | File | Lines | Purpose |
|---|---|---|---|
| 1 | `AppSweepApp.kt` | 12 | Application class, AdMob init |
| 2 | `MainActivity.kt` | 20 | Single Activity, Compose entry |
| 3 | `data/model/AppInfo.kt` | 75 | Data models, enums, UI states |
| 4 | `data/repository/AppRepository.kt` | 203 | PackageManager data source |
| 5 | `ui/components/AdDialog.kt` | 233 | Ad reward dialog + simulated ad |
| 6 | `ui/components/AppListItem.kt` | 199 | App list card component |
| 7 | `ui/components/CommonComponents.kt` | 261 | Batch bar, Empty, Loading, Count |
| 8 | `ui/components/FilterChips.kt` | 163 | Filter chips + sort dropdown |
| 9 | `ui/components/SearchBar.kt` | 93 | Search input component |
| 10 | `ui/navigation/NavGraph.kt` | 61 | Navigation setup |
| 11 | `ui/screens/home/HomeScreen.kt` | 283 | Main screen UI |
| 12 | `ui/screens/home/HomeViewModel.kt` | 367 | Home screen state & logic |
| 13 | `ui/screens/details/DetailScreen.kt` | 509 | Detail screen + DetailViewModel |
| 14 | `ui/theme/Color.kt` | 62 | Color palette definitions |
| 15 | `ui/theme/Theme.kt` | 115 | Material 3 light theme |
| 16 | `ui/theme/Type.kt` | 135 | Typography system |
| 17 | `util/AdManager.kt` | 133 | Ad reward state machine |
| 18 | `util/AppUtils.kt` | 125 | Intent helper utilities |
| 19 | `util/PreferenceManager.kt` | 99 | SharedPreferences wrapper |

---

## 16. Future Roadmap

### Short-term (v1.2.0)

- [ ] Real AdMob integration (replace simulated ad with RewardedAd)
- [ ] App icon loading via Coil (currently showing placeholder icons)
- [ ] Proper adaptive icon with high-quality SVG/PNG assets
- [ ] Settings screen (theme toggle, preferences, about)
- [ ] Unit tests for Repository and ViewModel
- [ ] UI tests for Compose screens

### Medium-term (v1.3.0)

- [ ] Dark mode support (currently light-only)
- [ ] Grid view option (currently list-only)
- [ ] App category labels (Games, Social, Tools, etc.)
- [ ] Sort by last used (UsageStatsManager)
- [ ] Export app list to CSV/PDF
- [ ] Search by app permissions

### Long-term (v2.0.0)

- [ ] App usage statistics (daily/weekly screen time)
- [ ] Duplicate app finder
- [ ] Large file scanner (APK, OBB, cache)
- [ ] App backup/restore functionality
- [ ] Widget for quick uninstall
- [ ] Notification for newly installed apps
- [ ] Hilt dependency injection
- [ ] Room database for caching app data
- [ ] Kotlin Multiplatform (iOS/Desktop)

---

## 17. Learning Notes for Students

### Key Concepts Demonstrated

1. **MVVM Architecture**: Learn how to separate UI, business logic, and data
2. **StateFlow**: Modern reactive state management for Compose
3. **Jetpack Compose**: Declarative UI with composable functions
4. **Material Design 3**: Latest design system with dynamic color support
5. **Navigation Compose**: Type-safe screen navigation with argument passing
6. **Repository Pattern**: Abstracting data sources from business logic
7. **Parcelable**: Efficient data passing between screens
8. **BroadcastReceiver**: Reacting to system events (app install/uninstall)
9. **SharedPreferences**: Simple persistent key-value storage
10. **CI/CD**: Automated builds with GitHub Actions
11. **APK Signing**: Understanding why and how APKs must be signed

### Common Pitfalls & Solutions

| Problem | Cause | Solution |
|---|---|---|
| APK won't install | Unsigned APK | Configure signing config + keystore |
| Compose recomposition loops | Reading state in wrong scope | Use `collectAsState()` with lifecycle awareness |
| ViewModel lost on rotation | Not using ViewModel | Use `viewModel()` from `lifecycle-viewmodel-compose` |
| Memory leak from BroadcastReceiver | Not unregistering | Unregister in `onCleared()` |
| FilterChipDefaults API mismatch | Different Compose BOM versions | Check BOM version compatibility |
| Package visibility on Android 11+ | Missing QUERY_ALL_PACKAGES | Add permission in Manifest |
| Missing Gradle wrapper JAR in CI | `.jar` in `.gitignore` | Generate wrapper in CI workflow |

### Recommended Learning Path

1. **Start with**: `AppInfo.kt` — Understand the data model
2. **Then**: `AppRepository.kt` — See how data comes from the system
3. **Next**: `HomeViewModel.kt` — Learn state management and business logic
4. **Then**: `HomeScreen.kt` — See how UI observes and renders state
5. **Then**: `DetailScreen.kt` — Understand navigation and parameter passing
6. **Then**: `AdManager.kt` — Learn the monetization state machine
7. **Finally**: `build.gradle.kts` + `build.yml` — Understand the build pipeline

---

> **AppSweep** — Built with love for learning. Inspired by Baxa, crafted with modern Android tools.

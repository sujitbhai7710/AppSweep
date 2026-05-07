# AppSweep 🧹

**A modern, beautiful Android app manager — built from scratch for learning.**

AppSweep lets you view, search, sort, filter, and uninstall apps on your Android device. It features a clean light UI with Material Design 3 and uses video ads (instead of a paid "Pro" version) to unlock system app management features.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📋 **App List** | View all installed apps with icon, name, size, and package |
| 🔍 **Search** | Real-time search by app name or package name |
| 📊 **Sort** | Sort by name, size, or install date (ascending/descending) |
| 🏷️ **Filter** | Filter by All, User, System, Recently Installed, or Largest |
| 🗑️ **Single Uninstall** | Uninstall any user app with one tap |
| 📦 **Batch Uninstall** | Select multiple apps and uninstall them all at once |
| 📱 **App Details** | View detailed info: size, version, SDK, dates, paths |
| 🔗 **Quick Actions** | Open app, Play Store, Settings, or Share directly |
| 🎬 **Ad-Rewarded Access** | Watch a video ad to unlock system app features for 30 min |
| 🔄 **Auto-Refresh** | List updates automatically when apps are installed/removed |

---

## 🏗️ Architecture

AppSweep follows **MVVM (Model-View-ViewModel)** architecture with modern Android development practices.

```
┌─────────────────────────────────────────────┐
│                  UI Layer                    │
│  (Jetpack Compose + Material Design 3)      │
│  ┌──────────┐ ┌──────────┐ ┌──────────────┐ │
│  │HomeScreen │ │DetailScr │ │Components    │ │
│  └─────┬─────┘ └────┬─────┘ └──────────────┘ │
│        │             │                         │
│  ┌─────▼─────┐ ┌────▼──────┐                 │
│  │HomeVM     │ │DetailVM   │  ViewModels     │
│  └─────┬─────┘ └────┬──────┘                 │
├────────┼──────────────┼───────────────────────┤
│        │     Data Layer│                       │
│  ┌─────▼──────────────▼──────┐                │
│  │     AppRepository         │                │
│  │  (PackageManager queries) │                │
│  └───────────────────────────┘                │
├──────────────────────────────────────────────┤
│              Utility Layer                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
│  │AppUtils  │ │AdManager │ │PrefManager   │  │
│  └──────────┘ └──────────┘ └──────────────┘  │
└──────────────────────────────────────────────┘
```

### Design Patterns Used

1. **MVVM** — Separates UI from business logic via ViewModels
2. **Repository Pattern** — Single source of truth for app data
3. **Observer Pattern** — StateFlow for reactive UI updates
4. **Singleton** — AdManager and PreferenceManager instances
5. **Factory Pattern** — DetailViewModel.Factory for parameterized VM creation
6. **Strategy Pattern** — Sort/Filter operations as interchangeable strategies

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin 1.9.22 |
| **UI Framework** | Jetpack Compose + Material Design 3 |
| **Architecture** | MVVM + Repository Pattern |
| **Async** | Kotlin Coroutines + Flow |
| **Navigation** | Jetpack Navigation Compose |
| **Image Loading** | Coil |
| **Ads** | Google Mobile Ads SDK (AdMob) |
| **Preferences** | AndroidX Preference / DataStore |
| **Build** | Gradle 8.5 + AGP 8.2.2 |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 34 (Android 14) |

---

## 📁 Project Structure

```
AppSweep/
├── .github/workflows/build.yml      # CI/CD: Auto-build APK
├── app/
│   ├── build.gradle.kts             # App module dependencies
│   ├── proguard-rules.pro           # ProGuard configuration
│   └── src/main/
│       ├── AndroidManifest.xml      # App permissions & components
│       ├── java/com/student/appmanager/
│       │   ├── AppSweepApp.kt       # Application class (AdMob init)
│       │   ├── MainActivity.kt      # Single Activity entry point
│       │   ├── data/
│       │   │   ├── model/
│       │   │   │   └── AppInfo.kt   # Data models + UI states
│       │   │   └── repository/
│       │   │       └── AppRepository.kt  # PackageManager queries
│       │   ├── ui/
│       │   │   ├── theme/
│       │   │   │   ├── Color.kt     # Color palette
│       │   │   │   ├── Type.kt      # Typography system
│       │   │   │   └── Theme.kt     # Material 3 theme
│       │   │   ├── components/
│       │   │   │   ├── AppListItem.kt     # App list card
│       │   │   │   ├── SearchBar.kt       # Search input
│       │   │   │   ├── FilterChips.kt     # Filter & sort chips
│       │   │   │   ├── AdDialog.kt        # Ad reward dialog
│       │   │   │   └── CommonComponents.kt # Shared components
│       │   │   ├── screens/
│       │   │   │   ├── home/
│       │   │   │   │   ├── HomeScreen.kt   # Main app list UI
│       │   │   │   │   └── HomeViewModel.kt # Home screen logic
│       │   │   │   └── details/
│       │   │   │       └── DetailScreen.kt  # App detail UI + VM
│       │   │   └── navigation/
│       │   │       └── NavGraph.kt   # Navigation routes
│       │   └── util/
│       │       ├── AppUtils.kt       # Intent helpers
│       │       ├── AdManager.kt      # Ad reward state
│       │       └── PreferenceManager.kt # User preferences
│       └── res/
│           └── values/
│               ├── strings.xml       # String resources
│               ├── colors.xml        # Color resources
│               └── themes.xml        # XML theme
├── build.gradle.kts                 # Root build config
├── settings.gradle.kts              # Module settings
└── gradle.properties                # Gradle properties
```

---

## 🚀 How It Works (App Flow)

### 1. App Startup
```
User taps app icon
    → AppSweepApp.onCreate() initializes AdMob SDK
    → MainActivity.onCreate() sets up Compose UI
    → AppSweepTheme applies light Material 3 theme
    → AppSweepNavGraph navigates to HomeScreen
    → HomeViewModel.loadApps() queries PackageManager
    → App list appears with all installed apps
```

### 2. App List (Home Screen)
```
HomeScreen displays:
    → SearchBar for real-time filtering
    → FilterChipsRow for category filtering
    → AppCountHeader showing totals
    → SortDropdown for ordering
    → LazyColumn of AppListItems
    → Each item shows: icon, name, package, size, system badge
```

### 3. Search & Filter Pipeline
```
User types in search bar
    → HomeViewModel.onSearchQueryChanged()
    → applyFilters() runs the pipeline:
        1. Filter by category (User/System/All/Recent/Large)
        2. Search by name or package (case-insensitive)
        3. Sort by selected option (Name/Size/Date)
    → StateFlow updates → Compose recomposes UI
```

### 4. Single Uninstall
```
User taps "Remove" on an app
    → AppUtils.getUninstallIntent() creates ACTION_DELETE intent
    → Android system shows uninstall confirmation dialog
    → User confirms → app is uninstalled
    → BroadcastReceiver detects PACKAGE_REMOVED
    → HomeViewModel.refreshApps() updates the list
```

### 5. Batch Uninstall
```
User taps batch mode icon
    → HomeViewModel.toggleBatchMode()
    → Checkboxes appear on each AppListItem
    → User selects multiple apps
    → BatchActionBar appears at bottom
    → Shows count + total size to free
    → User taps "Uninstall N Apps"
    → System uninstall dialog for each selected app
```

### 6. System App Access (Ad Reward)
```
User tries to view/manage system apps
    → HomeViewModel checks adManager.hasSystemAccess()
    → If no access: AdRewardDialog appears
    → User taps "Watch Ad to Unlock"
    → SimulatedAdScreen plays (placeholder for real AdMob ad)
    → On completion: adManager.onAdWatched()
    → Grants 30 minutes of system app access
    → User can now view, search, and uninstall system apps
    → After 30 minutes, access expires → must watch another ad
```

### 7. App Detail Screen
```
User taps on an app in the list
    → Navigation to DetailScreen with packageName argument
    → DetailViewModel loads full app info from repository
    → Shows: icon, name, version, size, dates, SDK levels, paths
    → Action buttons: Open, Play Store, Settings, Share
    → Red "Uninstall" button at bottom
```

---

## 🎬 Ad Monetization Model

Instead of a paid "Pro" version (like Baxa), AppSweep uses **rewarded video ads**:

| Action | Free | After Watching Ad |
|--------|------|-------------------|
| View user apps | ✅ | ✅ |
| Uninstall user apps | ✅ | ✅ |
| Search & sort | ✅ | ✅ |
| Batch uninstall user apps | ✅ | ✅ |
| View system apps | ❌ | ✅ (30 min) |
| Uninstall system apps | ❌ | ✅ (30 min) |
| Batch uninstall system apps | ❌ | ✅ (30 min) |

Each ad watch grants **30 minutes** of system app access. Watching another ad extends the timer.

---

## 🔧 Building the APK

### Local Build
```bash
# Clone the repo
git clone https://github.com/YOUR_USERNAME/AppSweep.git
cd AppSweep

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# APK locations:
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release.apk
```

### GitHub Actions (Automatic)
The repo includes a GitHub Actions workflow that automatically builds the APK on every push to `main`:
1. Push code to GitHub
2. Go to Actions tab → "Build APK" workflow
3. Download the built APK from the artifacts

---

## 📝 Learning Notes

This project is designed as a learning resource for Android development. Key concepts demonstrated:

1. **Jetpack Compose** — Declarative UI framework
2. **MVVM Architecture** — Separation of concerns
3. **StateFlow** — Reactive state management
4. **Kotlin Coroutines** — Asynchronous programming
5. **Repository Pattern** — Data abstraction layer
6. **Navigation Compose** — Type-safe screen navigation
7. **Material Design 3** — Modern theming system
8. **PackageManager API** — Querying installed apps
9. **Intent System** — Launching external activities
10. **BroadcastReceiver** — Reacting to system events

---

## 📄 License

This project is for educational purposes. Built from scratch with original code.

---

Built with ❤️ for learning Android development.

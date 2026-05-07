package com.student.appmanager.data.model

import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents an installed application on the device.
 * This is the core data model used throughout the app.
 *
 * @param packageName - Unique identifier for the app (e.g., "com.student.appmanager")
 * @param appName - User-visible name of the app
 * @param icon - The app's launcher icon drawable
 * @param versionName - Version string shown to users (e.g., "1.0.0")
 * @param versionCode - Numeric version identifier for updates
 * @param isSystemApp - Whether this is a pre-installed system application
 * @param apkSize - Size of the APK file in bytes
 * @param installDate - Timestamp when the app was installed
 * @param lastUpdated - Timestamp when the app was last updated
 * @param targetSdk - Target SDK version the app is compiled for
 * @param minSdk - Minimum SDK version required by the app
 * @param sourceDir - File path to the APK on disk
 * @param dataDir - File path to the app's private data directory
 */
@Parcelize
data class AppInfo(
    val packageName: String,
    val appName: String,
    val iconPath: String = "",  // We store path, load icon lazily
    val versionName: String = "",
    val versionCode: Long = 0,
    val isSystemApp: Boolean = false,
    val apkSize: Long = 0,
    val installDate: Long = 0,
    val lastUpdated: Long = 0,
    val targetSdk: Int = 0,
    val minSdk: Int = 0,
    val sourceDir: String = "",
    val dataDir: String = ""
) : Parcelable {

    /**
     * Category of the app - either user-installed or system
     */
    val category: AppCategory
        get() = if (isSystemApp) AppCategory.SYSTEM else AppCategory.USER

    /**
     * Human-readable size string (e.g., "12.5 MB")
     */
    val formattedSize: String
        get() = formatFileSize(apkSize)

    /**
     * Whether this is a framework app (core system app that can't be disabled)
     */
    val isFrameworkApp: Boolean
        get() = isSystemApp && sourceDir.startsWith("/system/")

    companion object {
        /**
         * Formats file size in bytes to a human-readable string.
         * Uses SI units (1 MB = 1000 KB) for user-facing display.
         */
        fun formatFileSize(size: Long): String {
            if (size <= 0) return "0 B"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
                .coerceAtMost(units.size - 1)
            return String.format(
                "%.1f %s",
                size / Math.pow(1024.0, digitGroups.toDouble()),
                units[digitGroups]
            )
        }
    }
}

/**
 * Enum representing the category of an application.
 * Used for filtering and UI grouping.
 */
enum class AppCategory {
    USER,       // Apps installed by the user from Play Store or sideloading
    SYSTEM;     // Pre-installed system apps

    val displayName: String
        get() = when (this) {
            USER -> "User Apps"
            SYSTEM -> "System Apps"
        }
}

/**
 * Sort options available for the app list.
 * Each option defines the comparison logic used for ordering.
 */
enum class SortOption(val displayName: String) {
    NAME_AZ("Name A-Z"),
    NAME_ZA("Name Z-A"),
    SIZE_LARGEST("Size (Largest)"),
    SIZE_SMALLEST("Size (Smallest)"),
    DATE_NEWEST("Install Date (Newest)"),
    DATE_OLDEST("Install Date (Oldest)")
}

/**
 * Filter options for the app list.
 * Can be combined to show specific subsets of apps.
 */
enum class FilterOption(val displayName: String) {
    ALL("All Apps"),
    USER_ONLY("User Apps"),
    SYSTEM_ONLY("System Apps"),
    RECENTLY_INSTALLED("Recently Installed"),
    LARGEST_FIRST("Largest Apps")
}

/**
 * Represents the current state of the ad reward system.
 * Tracks whether the user has earned access to system app features
 * by watching a rewarded video ad.
 */
data class AdRewardState(
    val hasSystemAccess: Boolean = false,
    val systemAccessExpiry: Long = 0,
    val adsWatchedToday: Int = 0,
    val lastAdWatchTime: Long = 0
) {
    /**
     * Check if the system access from ad reward is still valid.
     * Access expires after 30 minutes per ad watch.
     */
    val isAccessValid: Boolean
        get() = hasSystemAccess && System.currentTimeMillis() < systemAccessExpiry

    companion object {
        const val ACCESS_DURATION_MS = 30 * 60 * 1000L // 30 minutes
    }
}

/**
 * UI state for the home screen.
 * Follows the MVI (Model-View-Intent) pattern for state management.
 */
data class HomeUiState(
    val allApps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val currentSort: SortOption = SortOption.NAME_AZ,
    val currentFilter: FilterOption = FilterOption.ALL,
    val isLoading: Boolean = true,
    val selectedApps: Set<String> = emptySet(),
    val isBatchMode: Boolean = false,
    val adRewardState: AdRewardState = AdRewardState(),
    val error: String? = null
) {
    val isAllSelected: Boolean
        get() = filteredApps.isNotEmpty() && selectedApps.size == filteredApps.size

    val selectedCount: Int
        get() = selectedApps.size
}

/**
 * UI state for the app detail screen.
 */
data class DetailUiState(
    val appInfo: AppInfo? = null,
    val isLoading: Boolean = true,
    val isUninstalling: Boolean = false,
    val adRewardState: AdRewardState = AdRewardState(),
    val showAdDialog: Boolean = false,
    val error: String? = null
)

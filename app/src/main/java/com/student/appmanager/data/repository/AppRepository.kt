package com.student.appmanager.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.student.appmanager.data.model.AppCategory
import com.student.appmanager.data.model.AppInfo
import com.student.appmanager.data.model.FilterOption
import com.student.appmanager.data.model.SortOption

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

/**
 * Repository responsible for querying installed applications from the Android system.
 *
 * This is the single source of truth for all app data in the application.
 * It communicates with Android's PackageManager to retrieve information about
 * all installed packages on the device.
 *
 * Architecture: Part of the Data Layer in MVVM pattern.
 * The repository abstracts the data source from the ViewModel,
 * making it easy to swap data sources or add caching later.
 */
class AppRepository(context: Context) {

    private val packageManager: PackageManager = context.packageManager

    /**
     * Retrieves all installed applications on the device.
     * This includes both user-installed and system apps.
     *
     * The operation runs on Dispatchers.IO since PackageManager queries
     * can be slow on devices with many installed apps.
     *
     * @return List of AppInfo objects representing all installed apps
     */
    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        try {
            val packages = packageManager.getInstalledPackages(
                PackageManager.GET_META_DATA or
                PackageManager.GET_SHARED_LIBRARY_FILES
            )

            packages
                .mapNotNull { packageInfo ->
                    mapToAppInfo(packageInfo)
                }
                .sortedBy { it.appName.lowercase(Locale.getDefault()) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Retrieves detailed information about a specific app by its package name.
     *
     * @param packageName The unique package identifier of the app
     * @return AppInfo object or null if the app is not found
     */
    suspend fun getAppByPackageName(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
        try {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_META_DATA or
                PackageManager.GET_SHARED_LIBRARY_FILES
            )
            mapToAppInfo(packageInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    /**
     * Gets only user-installed applications.
     * Filters out system apps by checking the FLAG_SYSTEM flag.
     */
    suspend fun getUserApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        getInstalledApps().filter { !it.isSystemApp }
    }

    /**
     * Gets only system applications.
     * These are apps pre-installed on the device by the manufacturer or carrier.
     */
    suspend fun getSystemApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        getInstalledApps().filter { it.isSystemApp }
    }

    /**
     * Searches installed apps by name or package name.
     * Uses case-insensitive matching for better user experience.
     *
     * @param query The search string to match against
     * @param apps The full list of apps to search through
     * @return Filtered list of apps matching the query
     */
    fun searchApps(query: String, apps: List<AppInfo>): List<AppInfo> {
        if (query.isBlank()) return apps
        val lowerQuery = query.lowercase(Locale.getDefault())
        return apps.filter { app ->
            app.appName.lowercase(Locale.getDefault()).contains(lowerQuery) ||
            app.packageName.lowercase(Locale.getDefault()).contains(lowerQuery)
        }
    }

    /**
     * Sorts the app list according to the specified sort option.
     *
     * @param apps The list of apps to sort
     * @param sortOption The sorting criteria to apply
     * @return Sorted list of apps
     */
    fun sortApps(apps: List<AppInfo>, sortOption: SortOption): List<AppInfo> {
        return when (sortOption) {
            SortOption.NAME_AZ -> apps.sortedBy { it.appName.lowercase(Locale.getDefault()) }
            SortOption.NAME_ZA -> apps.sortedByDescending { it.appName.lowercase(Locale.getDefault()) }
            SortOption.SIZE_LARGEST -> apps.sortedByDescending { it.apkSize }
            SortOption.SIZE_SMALLEST -> apps.sortedBy { it.apkSize }
            SortOption.DATE_NEWEST -> apps.sortedByDescending { it.installDate }
            SortOption.DATE_OLDEST -> apps.sortedBy { it.installDate }
        }
    }

    /**
     * Filters the app list according to the specified filter option.
     *
     * @param apps The full list of apps to filter
     * @param filterOption The filtering criteria to apply
     * @return Filtered list of apps
     */
    fun filterApps(apps: List<AppInfo>, filterOption: FilterOption): List<AppInfo> {
        return when (filterOption) {
            FilterOption.ALL -> apps
            FilterOption.USER_ONLY -> apps.filter { !it.isSystemApp }
            FilterOption.SYSTEM_ONLY -> apps.filter { it.isSystemApp }
            FilterOption.RECENTLY_INSTALLED -> {
                val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                apps.filter { it.installDate > weekAgo }
            }
            FilterOption.LARGEST_FIRST -> apps.sortedByDescending { it.apkSize }
        }
    }

    /**
     * Calculates the total size of all selected apps.
     * Used in batch uninstall to show total space that can be freed.
     *
     * @param apps List of apps
     * @param selectedPackageNames Set of selected package names
     * @return Total size in bytes
     */
    fun calculateTotalSize(apps: List<AppInfo>, selectedPackageNames: Set<String>): Long {
        return apps
            .filter { it.packageName in selectedPackageNames }
            .sumOf { it.apkSize }
    }

    /**
     * Maps a PackageInfo object from Android's PackageManager to our AppInfo model.
     * This is the core transformation that converts system data into our domain model.
     *
     * The mapping handles:
     * - Determining if an app is a system app via FLAG_SYSTEM
     * - Extracting APK file size from the source directory
     * - Retrieving the first install time and last update time
     * - Getting SDK version information
     */
    private fun mapToAppInfo(packageInfo: PackageInfo): AppInfo? {
        val applicationInfo = packageInfo.applicationInfo ?: return null
        val appName = try {
            applicationInfo.loadLabel(packageManager).toString()
        } catch (e: Exception) {
            packageInfo.packageName
        }

        val isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        val apkSize = try {
            File(applicationInfo.sourceDir).length()
        } catch (e: Exception) {
            0L
        }

        return AppInfo(
            packageName = packageInfo.packageName,
            appName = appName.ifBlank { packageInfo.packageName },
            versionName = packageInfo.versionName ?: "",
            versionCode = packageInfo.longVersionCode,
            isSystemApp = isSystemApp,
            apkSize = apkSize,
            installDate = packageInfo.firstInstallTime,
            lastUpdated = packageInfo.lastUpdateTime,
            targetSdk = applicationInfo.targetSdkVersion,
            minSdk = applicationInfo.minSdkVersion,
            sourceDir = applicationInfo.sourceDir,
            dataDir = applicationInfo.dataDir
        )
    }
}

package com.student.appmanager.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Manages app preferences using Android's SharedPreferences.
 *
 * This class provides type-safe access to all user preferences
 * with default values. It follows the Singleton pattern to ensure
 * a single source of truth for preferences across the app.
 *
 * Preferences stored:
 * - Sort order for the app list
 * - Filter selection
 * - Whether system apps should be shown
 * - Ad reward access token and expiry time
 * - Theme preference (for future dark mode support)
 */
class PreferenceManager(private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    // ---- Sort & Filter Preferences ----

    var selectedSort: String
        get() = prefs.getString(KEY_SORT_OPTION, "NAME_AZ") ?: "NAME_AZ"
        set(value) = prefs.edit().putString(KEY_SORT_OPTION, value).apply()

    var selectedFilter: String
        get() = prefs.getString(KEY_FILTER_OPTION, "ALL") ?: "ALL"
        set(value) = prefs.edit().putString(KEY_FILTER_OPTION, value).apply()

    // ---- System App Access (Ad Reward) ----

    var systemAccessExpiry: Long
        get() = prefs.getLong(KEY_SYSTEM_ACCESS_EXPIRY, 0L)
        set(value) = prefs.edit().putLong(KEY_SYSTEM_ACCESS_EXPIRY, value).apply()

    var adsWatchedToday: Int
        get() = prefs.getInt(KEY_ADS_WATCHED_TODAY, 0)
        set(value) = prefs.edit().putInt(KEY_ADS_WATCHED_TODAY, value).apply()

    var lastAdWatchTime: Long
        get() = prefs.getLong(KEY_LAST_AD_WATCH_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_AD_WATCH_TIME, value).apply()

    // ---- Display Preferences ----

    var showSystemApps: Boolean
        get() = prefs.getBoolean(KEY_SHOW_SYSTEM_APPS, false)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_SYSTEM_APPS, value).apply()

    var showAppSizes: Boolean
        get() = prefs.getBoolean(KEY_SHOW_APP_SIZES, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_APP_SIZES, value).apply()

    var gridSize: Int
        get() = prefs.getInt(KEY_GRID_SIZE, 1) // 0 = compact, 1 = normal, 2 = large
        set(value) = prefs.edit().putInt(KEY_GRID_SIZE, value).apply()

    // ---- Utility Methods ----

    /**
     * Checks if the system app access from ad reward is still valid.
     */
    fun hasValidSystemAccess(): Boolean {
        return systemAccessExpiry > System.currentTimeMillis()
    }

    /**
     * Saves the ad reward state after watching an ad.
     */
    fun saveAdReward(expiryTime: Long) {
        systemAccessExpiry = expiryTime
        adsWatchedToday += 1
        lastAdWatchTime = System.currentTimeMillis()
    }

    /**
     * Clears all preferences. Used for testing or reset.
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_SORT_OPTION = "pref_sort_option"
        private const val KEY_FILTER_OPTION = "pref_filter_option"
        private const val KEY_SYSTEM_ACCESS_EXPIRY = "pref_system_access_expiry"
        private const val KEY_ADS_WATCHED_TODAY = "pref_ads_watched_today"
        private const val KEY_LAST_AD_WATCH_TIME = "pref_last_ad_watch_time"
        private const val KEY_SHOW_SYSTEM_APPS = "pref_show_system_apps"
        private const val KEY_SHOW_APP_SIZES = "pref_show_app_sizes"
        private const val KEY_GRID_SIZE = "pref_grid_size"
    }
}

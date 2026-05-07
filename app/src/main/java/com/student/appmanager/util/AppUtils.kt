package com.student.appmanager.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.student.appmanager.data.model.AppInfo

/**
 * Utility object for performing common app-related operations.
 *
 * This object provides helper methods for:
 * - Uninstalling apps via Android's standard uninstall intent
 * - Opening app detail screens in system settings
 * - Opening apps in the Google Play Store
 * - Launching apps
 * - Sharing app information
 *
 * All methods use Android's public Intent system, ensuring
 * compatibility across all Android versions and devices.
 */
object AppUtils {

    /**
     * Creates an intent to uninstall the specified app.
     * This uses Android's standard uninstall flow which shows
     * a system confirmation dialog before uninstalling.
     *
     * @param packageName The package name of the app to uninstall
     * @return Intent that will launch the uninstall dialog
     */
    fun getUninstallIntent(packageName: String): Intent {
        return Intent(
            Intent.ACTION_DELETE,
            Uri.parse("package:$packageName")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Creates an intent to open the app's detail page in system settings.
     * This shows the "App Info" screen where users can force stop,
     * clear data, manage permissions, etc.
     *
     * @param packageName The package name of the target app
     * @return Intent to open app settings
     */
    fun getAppSettingsIntent(packageName: String): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Creates an intent to open the app's page in the Google Play Store.
     * Falls back to the web version if Play Store is not available.
     *
     * @param packageName The package name of the target app
     * @return Intent to open Play Store
     */
    fun getPlayStoreIntent(packageName: String): Intent {
        return try {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } catch (e: Exception) {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    /**
     * Creates an intent to launch the specified app.
     *
     * @param context Context used to query PackageManager
     * @param packageName The package name of the app to launch
     * @return Intent to launch the app, or null if no launchable activity exists
     */
    fun getLaunchIntent(context: Context, packageName: String): Intent? {
        return context.packageManager.getLaunchIntentForPackage(packageName)
    }

    /**
     * Creates a share intent to share app information with others.
     * Shares the app name and Play Store link.
     *
     * @param appInfo The app information to share
     * @return Intent for sharing (should be started with startActivity chooser)
     */
    fun getShareIntent(appInfo: AppInfo): Intent {
        val shareText = buildString {
            append("Check out ${appInfo.appName}!\n")
            append("https://play.google.com/store/apps/details?id=${appInfo.packageName}")
        }

        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, appInfo.appName)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    }

    /**
     * Checks if a specific app is still installed on the device.
     * Useful for verifying after an uninstall attempt.
     *
     * @param context Context for PackageManager access
     * @param packageName Package name to check
     * @return true if the app is still installed
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}

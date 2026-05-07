package com.student.appmanager

import android.app.Application
import com.google.android.gms.ads.MobileAds

/**
 * Application class for AppSweep.
 *
 * This is the first class initialized when the app process starts.
 * It's used for:
 * 1. Initializing the Google Mobile Ads SDK
 * 2. Setting up global configurations
 * 3. Providing application-wide dependencies
 *
 * Note: In a larger app, this would be replaced by Hilt/Dagger
 * dependency injection, but for learning purposes we keep it simple.
 */
class AppSweepApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Google Mobile Ads SDK on the main thread as recommended by Google.
        // The initialization is async internally, so it won't block the main thread.
        try {
            MobileAds.initialize(this) { initializationStatus ->
                // Ads SDK initialized successfully
                // In production, you'd log the status for debugging
            }
        } catch (e: Exception) {
            // Non-critical failure - ads are optional functionality
        }
    }
}

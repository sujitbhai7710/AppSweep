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

        // Initialize Google Mobile Ads SDK in background
        // This must be done before any ad requests
        Thread {
            try {
                MobileAds.initialize(this) { initializationStatus ->
                    // Ads SDK initialized successfully
                    // In production, you'd log the status for debugging
                }
            } catch (e: Exception) {
                // Non-critical failure - ads are optional functionality
            }
        }.start()
    }
}

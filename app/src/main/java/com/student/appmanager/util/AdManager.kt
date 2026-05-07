package com.student.appmanager.util

import com.student.appmanager.data.model.AdRewardState

/**
 * Manages the ad reward state for unlocking system app features.
 *
 * Business Logic:
 * - System apps are hidden by default (like Baxa's pro feature)
 * - To access system apps, users must watch a rewarded video ad
 * - Each ad watch grants 30 minutes of system app access
 * - The user can watch multiple ads to extend access duration
 * - After the access expires, they must watch another ad
 *
 * This is the "monetization layer" - instead of a paid pro version,
 * we use rewarded video ads. The AdManager tracks:
 * 1. Whether the user currently has active access
 * 2. When the access expires
 * 3. How many ads they've watched today
 *
 * In a production app, this would integrate with Google AdMob's
 * RewardedAd API. For now, we implement the state management
 * and provide hooks for the actual ad SDK integration.
 */
class AdManager {

    private var currentState = AdRewardState()

    /**
     * Gets the current ad reward state.
     * This is used by ViewModels to determine feature access.
     */
    fun getRewardState(): AdRewardState = currentState

    /**
     * Checks if the user currently has valid system app access.
     */
    fun hasSystemAccess(): Boolean = currentState.isAccessValid

    /**
     * Called when a rewarded video ad completes successfully.
     * Grants system app access for the configured duration.
     *
     * @return Updated AdRewardState with new access
     */
    fun onAdWatched(): AdRewardState {
        val now = System.currentTimeMillis()
        val newExpiry = if (currentState.isAccessValid) {
            // Extend existing access
            currentState.systemAccessExpiry + AdRewardState.ACCESS_DURATION_MS
        } else {
            // New access period
            now + AdRewardState.ACCESS_DURATION_MS
        }

        currentState = currentState.copy(
            hasSystemAccess = true,
            systemAccessExpiry = newExpiry,
            adsWatchedToday = currentState.adsWatchedToday + 1,
            lastAdWatchTime = now
        )

        return currentState
    }

    /**
     * Called when an ad fails to load or the user closes it without completing.
     * Does NOT grant access.
     */
    fun onAdFailed() {
        // No state change - user must watch a complete ad
    }

    /**
     * Resets the daily ad counter. Should be called at midnight.
     */
    fun resetDailyCount() {
        currentState = currentState.copy(adsWatchedToday = 0)
    }

    /**
     * Revokes system app access. Used for testing or if fraud is detected.
     */
    fun revokeAccess() {
        currentState = currentState.copy(
            hasSystemAccess = false,
            systemAccessExpiry = 0
        )
    }

    /**
     * Gets the remaining time for current access in milliseconds.
     * Returns 0 if no active access.
     */
    fun getRemainingAccessTime(): Long {
        return if (currentState.isAccessValid) {
            currentState.systemAccessExpiry - System.currentTimeMillis()
        } else {
            0L
        }
    }

    /**
     * Formats the remaining access time as a human-readable string.
     * E.g., "25 min remaining"
     */
    fun getFormattedRemainingTime(): String {
        val remaining = getRemainingAccessTime()
        if (remaining <= 0) return "No active access"

        val minutes = (remaining / (60 * 1000)).toInt()
        return when {
            minutes >= 60 -> "${minutes / 60}h ${minutes % 60}m remaining"
            minutes > 0 -> "$minutes min remaining"
            else -> "Less than 1 min remaining"
        }
    }

    companion object {
        const val TAG = "AdManager"

        /**
         * Minimum time between ad watches (5 minutes) to prevent spam.
         */
        const val MIN_AD_INTERVAL_MS = 5 * 60 * 1000L

        /**
         * Maximum number of ads a user can watch per day.
         */
        const val MAX_DAILY_ADS = 20
    }
}

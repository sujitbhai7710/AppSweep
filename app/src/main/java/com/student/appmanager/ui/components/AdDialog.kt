package com.student.appmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.student.appmanager.ui.theme.*

/**
 * Dialog that prompts the user to watch a video ad
 * to unlock system app features.
 *
 * Business Logic:
 * - System apps are locked by default (like Baxa's pro version)
 * - Instead of paying, users watch a rewarded video ad
 * - Each ad watch grants 30 minutes of system app access
 * - This dialog explains the requirement and provides action buttons
 *
 * Design:
 * - Friendly, non-aggressive tone
 * - Clear explanation of the exchange (watch ad = unlock feature)
 * - Timer showing remaining access if already unlocked
 * - Blue gradient accent for the "Watch Ad" CTA
 * - Dismiss option for users who don't want to watch
 *
 * @param onWatchAd Called when user opts to watch the ad
 * @param onDismiss Called when user dismisses the dialog
 * @param remainingTime Formatted string showing remaining access time
 * @param hasActiveAccess Whether the user currently has system access
 */
@Composable
fun AdRewardDialog(
    onWatchAd: () -> Unit,
    onDismiss: () -> Unit,
    remainingTime: String = "",
    hasActiveAccess: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = White,
        title = {
            Text(
                text = if (hasActiveAccess) "Access Active!" else "Unlock System Apps",
                style = MaterialTheme.typography.headlineSmall,
                color = Gray900
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (hasActiveAccess) {
                    // Show active access status
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Green100,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Green500,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "You have access to system apps!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray800
                            )
                            if (remainingTime.isNotEmpty()) {
                                Text(
                                    text = remainingTime,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Blue500,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Watch another ad to extend your access time. Each ad adds 30 more minutes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray600
                    )
                } else {
                    // Show locked status with explanation
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Blue50,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Blue500,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "System apps are a premium feature",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray800
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "To view and manage system apps, watch a short video ad. " +
                                "Each ad gives you 30 minutes of full access to system app features, " +
                                "including uninstalling, viewing details, and batch operations.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray600
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onWatchAd,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue500,
                    contentColor = White
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (hasActiveAccess) "Extend Access" else "Watch Ad to Unlock",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (hasActiveAccess) "Close" else "Maybe Later",
                    style = MaterialTheme.typography.labelLarge,
                    color = Gray400
                )
            }
        }
    )
}

/**
 * A simulated ad loading/playing screen.
 * In production, this would be replaced with Google AdMob's RewardedAd.
 *
 * This placeholder simulates:
 * 1. A loading state (2 seconds)
 * 2. A "video playing" state (3 seconds)
 * 3. A completion state that triggers the reward
 *
 * @param onComplete Called when the simulated ad finishes
 * @param onError Called if the ad fails to load
 */
@Composable
fun SimulatedAdScreen(
    onComplete: () -> Unit,
    onError: () -> Unit
) {
    // In a real app, this would be:
    // 1. Load a RewardedAd from AdMob
    // 2. Show the ad with MobileAds.initialize()
    // 3. Handle onUserEarnedReward callback
    // 4. Grant system app access

    // For now, we simulate the ad experience
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Gray900
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = Blue500,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading ad...",
                style = MaterialTheme.typography.bodyLarge,
                color = White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This is a placeholder. In production, a real video ad would play here.",
                style = MaterialTheme.typography.bodySmall,
                color = Gray400,
                textAlign = TextAlign.Center
            )
        }
    }
}

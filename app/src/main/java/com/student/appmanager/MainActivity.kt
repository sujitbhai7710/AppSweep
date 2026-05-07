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

/**
 * Main Activity - The entry point of the application.
 *
 * This is the only Activity in the app. It follows the Single Activity
 * architecture pattern recommended by Google for modern Android development.
 *
 * All navigation is handled within Compose using Jetpack Navigation,
 * so we don't need multiple Activities.
 *
 * Lifecycle:
 * 1. onCreate() is called when the app launches
 * 2. enableEdgeToEdge() enables full-screen display
 * 3. setContent() sets up the Compose UI tree
 * 4. AppSweepTheme wraps everything in our custom theme
 * 5. AppSweepNavGraph handles all screen navigation
 *
 * The Activity serves as a thin shell that:
 * - Initializes the Compose UI
 * - Provides the window for rendering
 * - Handles system-level callbacks
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display for modern Android look
        enableEdgeToEdge()

        setContent {
            AppSweepTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = White
                ) {
                    AppSweepNavGraph()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // When returning from uninstall dialog, the list will auto-refresh
        // via the BroadcastReceiver in HomeViewModel
    }
}

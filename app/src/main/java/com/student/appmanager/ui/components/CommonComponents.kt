package com.student.appmanager.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.student.appmanager.data.model.AppInfo
import com.student.appmanager.ui.theme.*

/**
 * Batch mode action bar that appears at the bottom of the screen
 * when the user selects multiple apps for batch uninstallation.
 *
 * Design:
 * - Slides up from the bottom with animation
 * - Shows count of selected apps
 * - Shows total size that will be freed
 * - "Uninstall Selected" button in destructive red
 * - "Select All" toggle button
 *
 * @param selectedCount Number of apps currently selected
 * @param totalSize Total size of selected apps in human-readable format
 * @param onUninstallSelected Called when the uninstall button is tapped
 * @param onSelectAll Called when "Select All" is tapped
 * @param onClearSelection Called when "Clear" is tapped
 * @param isAllSelected Whether all apps are currently selected
 */
@Composable
fun BatchActionBar(
    selectedCount: Int,
    totalSize: String,
    onUninstallSelected: () -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    isAllSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = White,
        shadowElevation = 16.dp,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Selection info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$selectedCount apps selected",
                        style = MaterialTheme.typography.titleMedium,
                        color = Gray900
                    )
                    Text(
                        text = "Free up $totalSize",
                        style = MaterialTheme.typography.bodySmall,
                        color = Green500
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Select All / Deselect All
                    TextButton(onClick = onSelectAll) {
                        Text(
                            text = if (isAllSelected) "Deselect All" else "Select All",
                            style = MaterialTheme.typography.labelLarge,
                            color = Blue500
                        )
                    }

                    // Clear selection
                    TextButton(onClick = onClearSelection) {
                        Text(
                            text = "Clear",
                            style = MaterialTheme.typography.labelLarge,
                            color = Gray400
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Uninstall button
            Button(
                onClick = onUninstallSelected,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red500,
                    contentColor = White
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Uninstall $selectedCount Apps",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

/**
 * Empty state component shown when no apps match the current filter/search.
 *
 * Design:
 * - Centered layout with icon and text
 * - Friendly, encouraging message
 * - Suggestion to change filters
 */
@Composable
fun EmptyState(
    message: String = "No apps found",
    subtitle: String = "Try adjusting your search or filters",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.SearchOff,
            contentDescription = null,
            tint = Gray300,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = Gray600
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray400,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

/**
 * Loading state component with a shimmer-like animation.
 */
@Composable
fun LoadingState(
    message: String = "Loading apps...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = Blue500,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray600
        )
    }
}

/**
 * App count header showing total apps and categories.
 */
@Composable
fun AppCountHeader(
    totalApps: Int,
    userApps: Int,
    systemApps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Total count
        CountChip(
            label = "Total",
            count = totalApps,
            containerColor = Gray100,
            textColor = Gray800
        )
        // User apps count
        CountChip(
            label = "User",
            count = userApps,
            containerColor = Blue50,
            textColor = Blue700
        )
        // System apps count
        CountChip(
            label = "System",
            count = systemApps,
            containerColor = Violet100,
            textColor = ChipSystemText
        )
    }
}

@Composable
private fun CountChip(
    label: String,
    count: Int,
    containerColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }
    }
}

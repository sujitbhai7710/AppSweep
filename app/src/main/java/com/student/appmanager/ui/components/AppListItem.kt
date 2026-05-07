package com.student.appmanager.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.student.appmanager.data.model.AppInfo
import com.student.appmanager.ui.theme.*

/**
 * A single app list item displayed in the main app list.
 *
 * Design:
 * - Rounded card with subtle elevation
 * - App icon on the left (48x48dp)
 * - App name (bold), package name (muted), and size (badge)
 * - System app badge if applicable
 * - Optional checkbox for batch selection mode
 * - Uninstall action button on the right
 *
 * Layout:
 * ┌──────────────────────────────────────────────┐
 * │ [Icon]  App Name              [Size Badge]   │
 * │         com.example.app        [Uninstall]    │
 * │         [System] tag if system                │
 * └──────────────────────────────────────────────┘
 *
 * @param appInfo The app data to display
 * @param onClick Called when the item is tapped (opens detail)
 * @param onUninstallClick Called when uninstall button is tapped
 * @param isSelected Whether this item is selected in batch mode
 * @param isBatchMode Whether batch selection mode is active
 * @param onSelectionToggle Called when checkbox is toggled in batch mode
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListItem(
    appInfo: AppInfo,
    onClick: () -> Unit,
    onUninstallClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isBatchMode: Boolean = false,
    onSelectionToggle: (() -> Unit)? = null,
    iconPainter: Painter? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Blue50 else White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for batch mode
            if (isBatchMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onSelectionToggle?.invoke() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Blue500,
                        uncheckedColor = Gray300
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // App Icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = Gray100
            ) {
                if (iconPainter != null) {
                    androidx.compose.foundation.Image(
                        painter = iconPainter,
                        contentDescription = appInfo.appName,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                    )
                } else {
                    // Fallback icon placeholder
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhoneAndroid,
                            contentDescription = null,
                            tint = Gray400,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // App Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                // App name
                Text(
                    text = appInfo.appName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Gray900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Package name
                Text(
                    text = appInfo.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Tags row
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Size badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Blue50
                    ) {
                        Text(
                            text = appInfo.formattedSize,
                            style = MaterialTheme.typography.labelSmall,
                            color = Blue700,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // System app badge
                    if (appInfo.isSystemApp) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Violet100
                        ) {
                            Text(
                                text = "System",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChipSystemText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Uninstall button
            if (!isBatchMode) {
                FilledTonalButton(
                    onClick = onUninstallClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Red100,
                        contentColor = Red500
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "Remove",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

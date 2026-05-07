package com.student.appmanager.ui.screens.home

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.student.appmanager.data.model.AppInfo
import com.student.appmanager.data.model.FilterOption
import com.student.appmanager.ui.components.*
import com.student.appmanager.ui.theme.*

/**
 * Home Screen - The main screen of AppSweep.
 *
 * This is the primary interface where users can:
 * 1. View all installed apps in a scrollable list
 * 2. Search for specific apps by name or package
 * 3. Filter apps by category (All, User, System, Recent, Large)
 * 4. Sort apps by various criteria
 * 5. Enter batch mode to uninstall multiple apps at once
 * 6. Watch ads to unlock system app features
 *
 * Screen Layout:
 * ┌────────────────────────────────────┐
 * │ TopBar: "AppSweep" + batch toggle  │
 * ├────────────────────────────────────┤
 * │ Search Bar                         │
 * ├────────────────────────────────────┤
 * │ Filter Chips (scrollable)          │
 * ├────────────────────────────────────┤
 * │ Count Header (Total/User/System)   │
 * │ Sort Dropdown                      │
 * ├────────────────────────────────────┤
 * │ App List (scrollable)              │
 * │ ┌──────────────────────────────┐   │
 * │ │ [Icon] App Name     [Remove] │   │
 * │ │        com.example.app       │   │
 * │ │        [12.5 MB] [System]    │   │
 * │ └──────────────────────────────┘   │
 * │ ... more apps ...                  │
 * ├────────────────────────────────────┤
 * │ Batch Action Bar (when selecting)  │
 * └────────────────────────────────────┘
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isSortExpanded by viewModel.isSortExpanded.collectAsState()
    val showAdDialog by viewModel.showAdDialog.collectAsState()
    val isAdPlaying by viewModel.isAdPlaying.collectAsState()
    val context = LocalContext.current

    // Uninstall result launcher
    val uninstallLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        // Refresh the list after uninstall attempt
        viewModel.refreshApps()
    }

    // Shared AdManager instance for UI display (state is managed in ViewModel)
    val adManagerInstance = remember { com.student.appmanager.util.AdManager() }

    // Ad reward dialog
    if (showAdDialog) {
        AdRewardDialog(
            onWatchAd = { viewModel.onWatchAd() },
            onDismiss = { viewModel.onAdDialogDismiss() },
            hasActiveAccess = uiState.adRewardState.isAccessValid,
            remainingTime = adManagerInstance.getFormattedRemainingTime()
        )
    }

    // Simulated ad playing overlay
    if (isAdPlaying) {
        SimulatedAdScreen(
            onComplete = { viewModel.onWatchAd() },
            onError = { viewModel.onAdDialogDismiss() }
        )
        return
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                isBatchMode = uiState.isBatchMode,
                onBatchToggle = { viewModel.toggleBatchMode() }
            )
        },
        bottomBar = {
            // Show batch action bar only when in batch mode with selections
            AnimatedVisibility(
                visible = uiState.isBatchMode && uiState.selectedApps.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BatchActionBar(
                    selectedCount = uiState.selectedCount,
                    totalSize = viewModel.getSelectedTotalSize(),
                    onUninstallSelected = {
                        // Start uninstalling each selected app
                        uiState.selectedApps.forEach { packageName ->
                            val intent = com.student.appmanager.util.AppUtils
                                .getUninstallIntent(packageName)
                            context.startActivity(intent)
                        }
                        viewModel.clearSelection()
                        viewModel.toggleBatchMode()
                    },
                    onSelectAll = { viewModel.toggleSelectAll() },
                    onClearSelection = { viewModel.clearSelection() },
                    isAllSelected = uiState.isAllSelected
                )
            }
        },
        containerColor = Gray50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                onClear = { viewModel.onSearchCleared() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Filter chips
            FilterChipsRow(
                selectedFilter = uiState.currentFilter,
                onFilterSelected = { viewModel.onFilterChanged(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // Count header + Sort row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppCountHeader(
                    totalApps = uiState.filteredApps.size,
                    userApps = uiState.filteredApps.count { !it.isSystemApp },
                    systemApps = uiState.filteredApps.count { it.isSystemApp }
                )

                SortDropdown(
                    selectedSort = uiState.currentSort,
                    onSortSelected = { viewModel.onSortChanged(it) },
                    isExpanded = isSortExpanded,
                    onExpandChange = { viewModel.onSortExpanded(it) }
                )
            }

            // App list or empty state
            if (uiState.isLoading) {
                LoadingState(message = "Scanning installed apps...")
            } else if (uiState.filteredApps.isEmpty()) {
                EmptyState(
                    message = if (uiState.searchQuery.isNotEmpty()) {
                        "No apps match \"${uiState.searchQuery}\""
                    } else {
                        "No apps found"
                    },
                    subtitle = if (uiState.searchQuery.isNotEmpty()) {
                        "Try a different search term"
                    } else {
                        "Try adjusting your filters"
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (uiState.isBatchMode) 80.dp else 0.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(
                        items = uiState.filteredApps,
                        key = { it.packageName }
                    ) { appInfo ->
                        AppListItem(
                            appInfo = appInfo,
                            onClick = {
                                // If it's a system app and no ad access, show ad dialog
                                if (appInfo.isSystemApp && !uiState.adRewardState.isAccessValid) {
                                    viewModel.showAdDialog()
                                } else {
                                    onNavigateToDetail(appInfo.packageName)
                                }
                            },
                            onUninstallClick = {
                                val intent = com.student.appmanager.util.AppUtils
                                    .getUninstallIntent(appInfo.packageName)
                                uninstallLauncher.launch(intent)
                            },
                            isSelected = appInfo.packageName in uiState.selectedApps,
                            isBatchMode = uiState.isBatchMode,
                            onSelectionToggle = {
                                viewModel.toggleAppSelection(appInfo.packageName)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top app bar for the Home screen.
 *
 * Shows:
 * - App name "AppSweep" with a gradient-style title
 * - Batch mode toggle icon (checkbox icon)
 * - Settings icon (for future settings screen)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    isBatchMode: Boolean,
    onBatchToggle: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoDelete,
                    contentDescription = null,
                    tint = Blue500,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AppSweep",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Gray900
                )
            }
        },
        actions = {
            // Batch mode toggle
            IconButton(onClick = onBatchToggle) {
                Icon(
                    imageVector = if (isBatchMode) Icons.Default.Close else Icons.Default.Checklist,
                    contentDescription = if (isBatchMode) "Exit batch mode" else "Batch uninstall",
                    tint = if (isBatchMode) Red500 else Gray600
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White
        )
    )
}

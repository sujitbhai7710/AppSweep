package com.student.appmanager.ui.screens.home

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.student.appmanager.data.model.*
import com.student.appmanager.data.repository.AppRepository
import com.student.appmanager.util.AdManager
import com.student.appmanager.util.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home Screen - the main screen of the app.
 *
 * Architecture: MVVM (Model-View-ViewModel)
 *
 * This ViewModel is the bridge between the UI (Compose) and the data layer (Repository).
 * It manages all the state for the home screen and handles user interactions.
 *
 * State Management:
 * - Uses StateFlow (not LiveData) for better Compose integration
 * - Single source of truth via HomeUiState
 * - All state changes go through updateUiState() for consistency
 *
 * Key Responsibilities:
 * 1. Loading all installed apps from the repository
 * 2. Applying search, sort, and filter operations
 * 3. Managing batch selection mode
 * 4. Handling ad reward logic for system app access
 * 5. Reacting to app install/uninstall events
 *
 * Lifecycle:
 * - Created when the HomeScreen enters composition
 * - Survives configuration changes (screen rotation)
 * - Cleared when the HomeScreen is permanently removed
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val adManager = AdManager()
    private val prefs = PreferenceManager(application)

    // UI state - the single source of truth for the home screen
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Sort dropdown state
    private val _isSortExpanded = MutableStateFlow(false)
    val isSortExpanded: StateFlow<Boolean> = _isSortExpanded.asStateFlow()

    // Ad dialog state
    private val _showAdDialog = MutableStateFlow(false)
    val showAdDialog: StateFlow<Boolean> = _showAdDialog.asStateFlow()

    // Simulated ad playing state
    private val _isAdPlaying = MutableStateFlow(false)
    val isAdPlaying: StateFlow<Boolean> = _isAdPlaying.asStateFlow()

    /**
     * Broadcast receiver that listens for app install/uninstall events.
     * When an app is added or removed, we refresh the app list to stay current.
     */
    private val packageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_PACKAGE_ADDED,
                Intent.ACTION_PACKAGE_REMOVED,
                Intent.ACTION_PACKAGE_REPLACED -> {
                    refreshApps()
                }
            }
        }
    }

    init {
        // Load apps when ViewModel is created
        loadApps()

        // Restore preferences
        _uiState.update { state ->
            state.copy(
                currentSort = try {
                    SortOption.valueOf(prefs.selectedSort)
                } catch (e: Exception) {
                    SortOption.NAME_AZ
                },
                currentFilter = try {
                    FilterOption.valueOf(prefs.selectedFilter)
                } catch (e: Exception) {
                    FilterOption.ALL
                },
                adRewardState = AdRewardState(
                    hasSystemAccess = prefs.hasValidSystemAccess(),
                    systemAccessExpiry = prefs.systemAccessExpiry,
                    adsWatchedToday = prefs.adsWatchedToday,
                    lastAdWatchTime = prefs.lastAdWatchTime
                )
            )
        }

        // Register for package change broadcasts
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        application.registerReceiver(packageChangeReceiver, filter)
    }

    /**
     * Loads all installed apps from the repository.
     * This is a suspending operation that runs on Dispatchers.IO.
     */
    fun loadApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val apps = repository.getInstalledApps()
                _uiState.update { state ->
                    state.copy(
                        allApps = apps,
                        isLoading = false,
                        error = null
                    )
                }
                applyFilters()
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Failed to load apps: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Refreshes the app list (used after install/uninstall events).
     */
    fun refreshApps() {
        viewModelScope.launch {
            try {
                val apps = repository.getInstalledApps()
                _uiState.update { state -> state.copy(allApps = apps) }
                applyFilters()
            } catch (e: Exception) {
                // Silently fail on refresh - don't disrupt the user
            }
        }
    }

    /**
     * Updates the search query and re-applies filters.
     * Called on every keystroke in the search bar for real-time filtering.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    /**
     * Clears the search query.
     */
    fun onSearchCleared() {
        _uiState.update { it.copy(searchQuery = "") }
        applyFilters()
    }

    /**
     * Changes the sort option and re-sorts the list.
     */
    fun onSortChanged(sort: SortOption) {
        prefs.selectedSort = sort.name
        _uiState.update { it.copy(currentSort = sort) }
        applyFilters()
    }

    /**
     * Changes the filter option and re-filters the list.
     * If the user selects "System Only" without active ad access,
     * show the ad dialog instead of applying the filter.
     */
    fun onFilterChanged(filter: FilterOption) {
        // Check if trying to access system apps without ad reward
        if ((filter == FilterOption.SYSTEM_ONLY) && !adManager.hasSystemAccess()) {
            _showAdDialog.value = true
            return
        }

        prefs.selectedFilter = filter.name
        _uiState.update { it.copy(currentFilter = filter) }
        applyFilters()
    }

    /**
     * Toggles batch selection mode.
     * When entering batch mode, the selection is cleared.
     * When leaving, all selections are cleared.
     */
    fun toggleBatchMode() {
        _uiState.update { state ->
            if (state.isBatchMode) {
                state.copy(isBatchMode = false, selectedApps = emptySet())
            } else {
                state.copy(isBatchMode = true, selectedApps = emptySet())
            }
        }
    }

    /**
     * Toggles selection of an app in batch mode.
     */
    fun toggleAppSelection(packageName: String) {
        _uiState.update { state ->
            val newSelection = if (packageName in state.selectedApps) {
                state.selectedApps - packageName
            } else {
                state.selectedApps + packageName
            }
            state.copy(selectedApps = newSelection)
        }
    }

    /**
     * Selects or deselects all currently filtered apps.
     */
    fun toggleSelectAll() {
        _uiState.update { state ->
            if (state.isAllSelected) {
                state.copy(selectedApps = emptySet())
            } else {
                state.copy(selectedApps = state.filteredApps.map { it.packageName }.toSet())
            }
        }
    }

    /**
     * Clears the current selection.
     */
    fun clearSelection() {
        _uiState.update { it.copy(selectedApps = emptySet()) }
    }

    /**
     * Called when the user requests to uninstall a single app.
     * Returns the intent needed to trigger Android's uninstall dialog.
     */
    fun onUninstallApp(packageName: String): Intent {
        return com.student.appmanager.util.AppUtils.getUninstallIntent(packageName)
    }

    /**
     * Called when the user wants to watch an ad to unlock system apps.
     */
    fun onWatchAd() {
        _showAdDialog.value = false
        _isAdPlaying.value = true

        // Simulate ad playback (in production, this would use Google AdMob)
        viewModelScope.launch {
            // Simulate loading and watching an ad
            kotlinx.coroutines.delay(2000) // Simulated ad duration

            // Grant reward
            val newState = adManager.onAdWatched()
            prefs.saveAdReward(newState.systemAccessExpiry)

            _uiState.update { it.copy(adRewardState = newState) }
            _isAdPlaying.value = false

            // If user was trying to filter system apps, apply it now
            if (_uiState.value.currentFilter == FilterOption.ALL || adManager.hasSystemAccess()) {
                applyFilters()
            }
        }
    }

    /**
     * Dismisses the ad dialog without watching.
     */
    fun onAdDialogDismiss() {
        _showAdDialog.value = false
    }

    /**
     * Shows the ad dialog (e.g., when user taps on a system app).
     */
    fun showAdDialog() {
        _showAdDialog.value = true
    }

    fun onSortExpanded(expanded: Boolean) {
        _isSortExpanded.value = expanded
    }

    /**
     * Applies the current search query, sort, and filter to the full app list.
     * This is the central "pipeline" that produces the displayed list.
     *
     * Pipeline:
     * 1. Start with all apps
     * 2. Apply filter (category-based subset)
     * 3. Apply search (text matching)
     * 4. Apply sort (ordering)
     */
    private fun applyFilters() {
        _uiState.update { state ->
            var result = state.allApps

            // Step 1: Filter by category
            result = repository.filterApps(result, state.currentFilter)

            // Step 2: Filter system apps if no ad access
            if (!adManager.hasSystemAccess() && state.currentFilter != FilterOption.SYSTEM_ONLY) {
                // By default, hide system apps unless user has ad access
                // But we still show them in "All" - they just show a lock icon
            }

            // Step 3: Apply search query
            result = repository.searchApps(state.searchQuery, result)

            // Step 4: Sort
            result = repository.sortApps(result, state.currentSort)

            state.copy(filteredApps = result)
        }
    }

    /**
     * Calculates total size of selected apps for batch uninstall info.
     */
    fun getSelectedTotalSize(): String {
        val total = repository.calculateTotalSize(
            _uiState.value.allApps,
            _uiState.value.selectedApps
        )
        return AppInfo.formatFileSize(total)
    }

    override fun onCleared() {
        super.onCleared()
        try {
            getApplication<Application>().unregisterReceiver(packageChangeReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered
        }
    }
}

package com.student.appmanager.ui.screens.details

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.student.appmanager.data.model.AppInfo
import com.student.appmanager.data.model.DetailUiState
import com.student.appmanager.data.repository.AppRepository
import com.student.appmanager.ui.components.AdRewardDialog
import com.student.appmanager.ui.theme.*
import com.student.appmanager.util.AdManager
import com.student.appmanager.util.AppUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for the App Detail Screen.
 *
 * Manages the state for viewing detailed information about a single app,
 * including uninstall actions, ad reward checks, and app management actions.
 */
class DetailViewModel(
    private val packageName: String,
    private val repository: AppRepository
) : ViewModel() {

    private val adManager = AdManager()

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _showAdDialog = MutableStateFlow(false)
    val showAdDialog: StateFlow<Boolean> = _showAdDialog.asStateFlow()

    init {
        loadAppDetails()
    }

    private fun loadAppDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val appInfo = repository.getAppByPackageName(packageName)
            _uiState.update {
                it.copy(
                    appInfo = appInfo,
                    isLoading = false,
                    showAdDialog = appInfo?.isSystemApp == true && !adManager.hasSystemAccess()
                )
            }
        }
    }

    fun onWatchAd() {
        _showAdDialog.value = false
        val newState = adManager.onAdWatched()
        _uiState.update { it.copy(adRewardState = newState, showAdDialog = false) }
    }

    fun onAdDialogDismiss() {
        _showAdDialog.value = false
    }

    fun refreshApp() {
        loadAppDetails()
    }

    /**
     * Factory for creating DetailViewModel with package name parameter.
     */
    class Factory(
        private val packageName: String,
        private val repository: AppRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailViewModel(packageName, repository) as T
        }
    }
}

/**
 * Detail Screen - Shows comprehensive information about a single app.
 *
 * Layout:
 * ┌────────────────────────────────────┐
 * │ ← Back    App Icon    [Actions ▼]  │
 * ├────────────────────────────────────┤
 * │         [Large App Icon]           │
 * │         App Name                   │
 * │         com.example.app            │
 * │         v1.0.0                     │
 * ├────────────────────────────────────┤
 * │ Info Card:                         │
 * │  Size: 12.5 MB                     │
 * │  Installed: Jan 15, 2024           │
 * │  Updated: Mar 20, 2024             │
 * │  Target SDK: 34                    │
 * │  Min SDK: 24                       │
 * ├────────────────────────────────────┤
 * │ Actions:                           │
 * │  [Open App] [Play Store] [Settings]│
 * │  [Share]                            │
 * ├────────────────────────────────────┤
 * │ [UNINSTALL] (red button)           │
 * └────────────────────────────────────┘
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    packageName: String,
    onBack: () -> Unit,
    viewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Factory(
            packageName = packageName,
            repository = AppRepository(LocalContext.current)
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val showAdDialog by viewModel.showAdDialog.collectAsState()
    val context = LocalContext.current

    val uninstallLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _: ActivityResult ->
        // Check if app was actually uninstalled
        if (!AppUtils.isAppInstalled(context, packageName)) {
            onBack()
        } else {
            viewModel.refreshApp()
        }
    }

    val adManager = remember { AdManager() }

    // Ad dialog for system apps
    if (showAdDialog) {
        AdRewardDialog(
            onWatchAd = { viewModel.onWatchAd() },
            onDismiss = { viewModel.onAdDialogDismiss() },
            hasActiveAccess = uiState.adRewardState.isAccessValid,
            remainingTime = adManager.getFormattedRemainingTime()
        )
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Blue500)
        }
        return
    }

    val appInfo = uiState.appInfo ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = Gray50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App header section
            AppHeader(appInfo)

            Spacer(modifier = Modifier.height(16.dp))

            // Info cards
            AppInfoCards(appInfo)

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            AppActionButtons(
                appInfo = appInfo,
                onOpenApp = {
                    AppUtils.getLaunchIntent(context, appInfo.packageName)?.let {
                        context.startActivity(it)
                    }
                },
                onOpenPlayStore = {
                    context.startActivity(AppUtils.getPlayStoreIntent(appInfo.packageName))
                },
                onOpenSettings = {
                    context.startActivity(AppUtils.getAppSettingsIntent(appInfo.packageName))
                },
                onShare = {
                    val shareIntent = AppUtils.getShareIntent(appInfo)
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Uninstall button
            Button(
                onClick = {
                    val intent = AppUtils.getUninstallIntent(appInfo.packageName)
                    uninstallLauncher.launch(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red500,
                    contentColor = White
                ),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Uninstall ${appInfo.appName}",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AppHeader(appInfo: AppInfo) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = White
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large app icon
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                color = Gray100
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = null,
                        tint = Gray400,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App name
            Text(
                text = appInfo.appName,
                style = MaterialTheme.typography.headlineMedium,
                color = Gray900
            )

            // Package name
            Text(
                text = appInfo.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = Gray600
            )

            // Version
            if (appInfo.versionName.isNotEmpty()) {
                Text(
                    text = "Version ${appInfo.versionName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Category badge
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (appInfo.isSystemApp) Violet100 else Blue50
            ) {
                Text(
                    text = if (appInfo.isSystemApp) "System App" else "User App",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (appInfo.isSystemApp) ChipSystemText else Blue700,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AppInfoCards(appInfo: AppInfo) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "App Information",
            style = MaterialTheme.typography.titleMedium,
            color = Gray900,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        InfoCard(
            icon = Icons.Default.Storage,
            title = "App Size",
            value = appInfo.formattedSize
        )
        InfoCard(
            icon = Icons.Default.CalendarToday,
            title = "Installed",
            value = formatDate(appInfo.installDate)
        )
        InfoCard(
            icon = Icons.Default.Update,
            title = "Last Updated",
            value = formatDate(appInfo.lastUpdated)
        )
        if (appInfo.targetSdk > 0) {
            InfoCard(
                icon = Icons.Default.Build,
                title = "Target SDK",
                value = "API ${appInfo.targetSdk}"
            )
        }
        if (appInfo.minSdk > 0) {
            InfoCard(
                icon = Icons.Default.PhoneAndroid,
                title = "Minimum SDK",
                value = "API ${appInfo.minSdk}"
            )
        }
        InfoCard(
            icon = Icons.Default.Folder,
            title = "APK Path",
            value = appInfo.sourceDir
        )
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Blue500,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray600
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray900
                )
            }
        }
    }
}

@Composable
private fun AppActionButtons(
    appInfo: AppInfo,
    onOpenApp: () -> Unit,
    onOpenPlayStore: () -> Unit,
    onOpenSettings: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Actions",
            style = MaterialTheme.typography.titleMedium,
            color = Gray900,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                icon = Icons.Default.OpenInNew,
                label = "Open",
                onClick = onOpenApp,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = Icons.Default.Shop,
                label = "Store",
                onClick = onOpenPlayStore,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = Icons.Default.Settings,
                label = "Settings",
                onClick = onOpenSettings,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                onClick = onShare,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = White,
            contentColor = Gray800
        ),
        contentPadding = PaddingValues(vertical = 10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp <= 0) return "Unknown"
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

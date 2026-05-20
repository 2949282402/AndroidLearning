package hejulian.ai.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import hejulian.ai.myapplication.core.model.AppDestination
import hejulian.ai.myapplication.core.model.ServerProfile
import hejulian.ai.myapplication.core.model.demoServers
import hejulian.ai.myapplication.ui.AIPage.HomeRoute
import hejulian.ai.myapplication.ui.LogsPage.LogsScreen
import hejulian.ai.myapplication.ui.LogsPage.LogsViewModel
import hejulian.ai.myapplication.ui.LogsPage.LogsViewModelFactory
import hejulian.ai.myapplication.ui.MonitorPage.MonitorScreen
import hejulian.ai.myapplication.ui.ServersPage.ServerEditorScreen
import hejulian.ai.myapplication.ui.ServersPage.ServersScreen
import hejulian.ai.myapplication.ui.SftpPage.SftpScreen
import hejulian.ai.myapplication.ui.startup.StartupScreen

@Composable
fun SshMobileApp() {
    var current by rememberSaveable { mutableStateOf(AppDestination.Servers) }
    var showPowerGuide by rememberSaveable { mutableStateOf(true) }
    var editingServer by remember { mutableStateOf<ServerProfile?>(null) }
    var showEditor by rememberSaveable { mutableStateOf(false) }
    val servers = remember { mutableStateListOf(*demoServers().toTypedArray()) }
    val selectedServerIds = remember { mutableStateListOf<String>() }
    val appLogger = AppDependencies.logger
    val logsViewModel: LogsViewModel = viewModel(factory = LogsViewModelFactory(appLogger))

    LaunchedEffect(Unit) {
        appLogger.service("App launched", "startDestination=${current.route}")
    }

    if (showPowerGuide) {
        StartupScreen(
            onContinue = {
                showPowerGuide = false
                appLogger.service("Power guide dismissed")
            },
        )
        return
    }

    if (showEditor) {
        ServerEditorScreen(
            initialServer = editingServer,
            onDismiss = { showEditor = false },
            onSave = { saved ->
                val existingIndex = servers.indexOfFirst { it.id == saved.id }
                if (existingIndex >= 0) {
                    servers[existingIndex] = saved
                    appLogger.info("Server updated", "server=${saved.name}")
                } else {
                    servers.add(0, saved)
                    appLogger.info("Server added", "server=${saved.name}")
                }
                showEditor = false
            },
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = current == destination,
                        onClick = {
                            if (current != destination) {
                                appLogger.info(
                                    message = "Navigation changed",
                                    details = "from=${current.route}, to=${destination.route}",
                                )
                            }
                            current = destination
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
        floatingActionButton = {
            if (current == AppDestination.Servers) {
                FloatingActionButton(
                    onClick = {
                        editingServer = null
                        showEditor = true
                    },
                ) {
                    Text("+")
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                .padding(padding),
        ) {
            when (current) {
                AppDestination.Ai -> HomeRoute(
                    logger = appLogger,
                    servers = servers,
                    modifier = Modifier.fillMaxSize(),
                )

                AppDestination.Servers -> ServersScreen(
                    servers = servers,
                    selectedIds = selectedServerIds.toSet(),
                    onToggleSelection = { id ->
                        if (selectedServerIds.contains(id)) {
                            selectedServerIds.remove(id)
                        } else {
                            selectedServerIds.add(id)
                        }
                    },
                    onClearSelection = { selectedServerIds.clear() },
                    onDeleteSelected = {
                        val ids = selectedServerIds.toSet()
                        servers.removeAll { it.id in ids }
                        selectedServerIds.clear()
                        appLogger.warning("Servers deleted", "count=${ids.size}")
                    },
                    onEditServer = { server ->
                        editingServer = server
                        showEditor = true
                    },
                    logger = appLogger,
                )

                AppDestination.Sftp -> SftpScreen(servers = servers)
                AppDestination.Monitor -> MonitorScreen(servers = servers)
                AppDestination.Logs -> LogsScreen(viewModel = logsViewModel)
            }
        }
    }
}

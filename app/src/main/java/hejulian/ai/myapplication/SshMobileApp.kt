package hejulian.ai.myapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import hejulian.ai.myapplication.core.model.AppDestination
import hejulian.ai.myapplication.ui.AIPage.HomeRoute
import hejulian.ai.myapplication.ui.LogsPage.LogsScreen
import hejulian.ai.myapplication.ui.LogsPage.LogsViewModel
import hejulian.ai.myapplication.ui.LogsPage.LogsViewModelFactory

@Composable
fun SshMobileApp(){
    var current by rememberSaveable{ mutableStateOf(AppDestination.Servers) }
    val appLogger = AppDependencies.logger
    val logsViewModel: LogsViewModel = viewModel(
        factory = LogsViewModelFactory(appLogger)
    )

    LaunchedEffect(Unit) {
        appLogger.service("App launched", "startDestination=${current.route}")
    }

    Scaffold(
        bottomBar = {
            NavigationBar{
                AppDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = current == destination,
                        onClick = {
                            if (current != destination) {
                                appLogger.info(
                                    message = "Navigation changed",
                                    details = "from=${current.route}, to=${destination.route}"
                                )
                            }
                            current = destination
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)){
            when(current){
                AppDestination.Ai       ->  HomeRoute(logger = appLogger)//PlaceholderScreen("AI")
                AppDestination.Servers  ->  PlaceholderScreen("Servers")
                AppDestination.Sftp     ->  PlaceholderScreen("Sftp")
                AppDestination.Monitor  ->  PlaceholderScreen("Monitor")
                AppDestination.Logs     ->  LogsScreen(viewModel = logsViewModel)//PlaceholderScreen("Logs")
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(title)
    }
}


package hejulian.ai.myapplication.ui.AIPage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import hejulian.ai.myapplication.core.logging.AppLogger
import hejulian.ai.myapplication.core.model.ServerProfile

@Composable
fun HomeRoute(
    logger: AppLogger,
    servers: List<ServerProfile>,
    modifier: Modifier = Modifier,
) {
    HomeScreen(
        modifier = modifier,
        logger = logger,
        servers = servers,
    )
}

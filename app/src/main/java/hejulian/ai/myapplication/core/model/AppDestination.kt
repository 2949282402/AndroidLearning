package hejulian.ai.myapplication.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
){
    Ai("ai",            "AI",       Icons.Outlined.SmartToy),
    Servers("servers",  "Servers",  Icons.Outlined.Storage),
    Sftp("sftp",        "SFTP",     Icons.Outlined.Folder),
    Monitor("monitor",  "Monitor",  Icons.Outlined.Memory),
    Logs("logs",        "Logs",     Icons.Outlined.Terminal)
}

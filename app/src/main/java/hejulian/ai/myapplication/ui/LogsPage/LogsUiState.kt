package hejulian.ai.myapplication.ui.LogsPage

import hejulian.ai.myapplication.core.logging.AppLog
import hejulian.ai.myapplication.core.logging.LogLevel

data class LogsUiState(
    val logs: List<AppLog> = emptyList(),
    val selectedLevel: LogLevel? = null
)
package hejulian.ai.myapplication.ui.LogsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hejulian.ai.myapplication.core.logging.AppLogger
import hejulian.ai.myapplication.core.logging.LogLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class LogsViewModel(
    private val logger: AppLogger,
) : ViewModel() {
    private val selectedLevel = MutableStateFlow<LogLevel?>(null)
    private val selectedIds = MutableStateFlow<Set<String>>(emptySet())

    val uiState: StateFlow<LogsUiState> =
        combine(logger.logs, selectedLevel, selectedIds) { logs, level, ids ->
            val filteredLogs = if (level == null) logs else logs.filter { it.level == level }
            val validIds = ids.filterTo(mutableSetOf()) { id -> logs.any { it.id == id } }
            LogsUiState(
                logs = filteredLogs,
                selectedLevel = level,
                selectedIds = validIds,
                levelCounts = buildMap {
                    put(null, logs.size)
                    LogLevel.entries.forEach { logLevel ->
                        put(logLevel, logs.count { it.level == logLevel })
                    }
                },
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            LogsUiState(),
        )

    fun selectLevel(level: LogLevel?) {
        selectedLevel.value = level
    }

    fun toggleSelection(id: String) {
        selectedIds.value = selectedIds.value.toMutableSet().apply {
            if (!add(id)) remove(id)
        }
    }

    fun clearSelection() {
        selectedIds.value = emptySet()
    }

    fun deleteSelected() {
        logger.deleteByIds(selectedIds.value)
        selectedIds.value = emptySet()
    }

    fun clearLogs() {
        logger.clear()
        selectedIds.value = emptySet()
        selectedLevel.value = null
    }
}

class LogsViewModelFactory(
    private val logger: AppLogger,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogsViewModel::class.java)) {
            return LogsViewModel(logger) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

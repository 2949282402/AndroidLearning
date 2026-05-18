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
    private val logger: AppLogger
) : ViewModel() {
    private val selectedLevel = MutableStateFlow<LogLevel?>(null)

    val uiState: StateFlow<LogsUiState> =
        combine(logger.logs, selectedLevel) { logs, level ->
            LogsUiState(
                logs = if (level == null) logs else logs.filter { it.level == level },
                selectedLevel = level
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            LogsUiState()
        )

    fun selectLevel(level: LogLevel?) {
        selectedLevel.value = level
    }
}

class LogsViewModelFactory(
    private val logger: AppLogger
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogsViewModel::class.java)) {
            return LogsViewModel(logger) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

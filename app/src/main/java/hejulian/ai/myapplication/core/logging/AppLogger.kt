package hejulian.ai.myapplication.core.logging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

interface AppLogger{
    val logs: StateFlow<List<AppLog>>

    fun debug(message: String, details: String? = null)
    fun info(message: String, details: String? = null)
    fun warning(message: String, details: String? = null)
    fun error(message: String, throwable: Throwable? = null, details: String? = null)
    fun service(message: String, details: String? = null)
    fun tool(message: String, details: String? = null)
}

class InMemoryAppLogger: AppLogger{
    private val _logs = MutableStateFlow<List<AppLog>>(emptyList())
    override val logs: StateFlow<List<AppLog>> = _logs.asStateFlow()

    override fun debug(message: String, details: String?) =
        add(LogLevel.Debug, message, details)

    override fun info(message: String, details: String?) =
        add(LogLevel.Info, message, details)

    override fun warning(message: String, details: String?) =
        add(LogLevel.Warning, message, details)

    override fun error(message: String, throwable: Throwable?, details: String?) {
        add(LogLevel.Error, message, buildString {
            if (!details.isNullOrBlank()) append(details)
            if (throwable != null) append("\n").append(throwable.stackTraceToString())
        }.trim().ifBlank { null })
    }

    override fun service(message: String, details: String?) =
        add(LogLevel.Service, message, details)

    override fun tool(message: String, details: String?) =
        add(LogLevel.Tool, message, details)

    private fun add(level: LogLevel, message: String, details: String?) {
        val item = AppLog(
            id = UUID.randomUUID().toString(),
            timeMillis = System.currentTimeMillis(),
            level = level,
            message = message,
            details = details
        )
        _logs.update { old -> (listOf(item) + old).take(500) }
    }
}

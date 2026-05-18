package hejulian.ai.myapplication.core.logging

data class AppLog(
    val id: String,
    val timeMillis: Long,
    val level: LogLevel,
    val message: String,
    val details: String? = null
)

enum class LogLevel{
    Debug,
    Info,
    Warning,
    Error,
    Service,
    Tool
}
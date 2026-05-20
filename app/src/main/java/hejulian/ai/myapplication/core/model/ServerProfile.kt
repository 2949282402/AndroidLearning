package hejulian.ai.myapplication.core.model

data class ServerProfile(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val username: String,
    val osLabel: String,
    val tag: String,
    val healthSummary: String,
    val healthScore: Int,
    val terminalWindows: List<TerminalWindowSummary>,
)

data class TerminalWindowSummary(
    val id: String,
    val title: String,
    val status: String,
)

fun demoServers(): List<ServerProfile> = listOf(
    ServerProfile(
        id = "prod-web-1",
        name = "prod-web-1",
        host = "10.0.1.12",
        port = 22,
        username = "root",
        osLabel = "Linux",
        tag = "Production",
        healthSummary = "Healthy · CPU 28% · Memory 51%",
        healthScore = 92,
        terminalWindows = listOf(
            TerminalWindowSummary("w1", "ops-shell", "Connected"),
            TerminalWindowSummary("w2", "deploy-watch", "Connected"),
        ),
    ),
    ServerProfile(
        id = "staging-api",
        name = "staging-api",
        host = "10.0.2.34",
        port = 22,
        username = "ubuntu",
        osLabel = "Linux",
        tag = "Staging",
        healthSummary = "Warning · Disk 81% · Network spike",
        healthScore = 72,
        terminalWindows = listOf(
            TerminalWindowSummary("w3", "api-debug", "Idle"),
        ),
    ),
    ServerProfile(
        id = "win-jumphost",
        name = "win-jumphost",
        host = "10.0.9.9",
        port = 22,
        username = "Administrator",
        osLabel = "Windows",
        tag = "Jump Host",
        healthSummary = "Healthy · Services stable",
        healthScore = 88,
        terminalWindows = listOf(
            TerminalWindowSummary("w4", "powershell", "Connected"),
            TerminalWindowSummary("w5", "event-viewer", "Idle"),
        ),
    ),
)

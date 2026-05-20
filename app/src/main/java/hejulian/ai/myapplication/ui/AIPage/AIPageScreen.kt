package hejulian.ai.myapplication.ui.AIPage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hejulian.ai.myapplication.core.logging.AppLogger
import hejulian.ai.myapplication.core.model.ServerProfile

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: AIPageViewModel = viewModel(),
    logger: AppLogger,
    servers: List<ServerProfile>,
) {
    val inputText by viewModel.inputText.collectAsState()
    val chats = remember {
        mutableStateListOf(
            AiChat(
                id = "chat-1",
                title = "Production health report",
                messages = mutableStateListOf(
                    AiMessage("assistant", "Ask me about your servers, logs, status, or a remote file."),
                    AiMessage("user", "Summarize the health of prod-web-1."),
                    AiMessage("assistant", "prod-web-1 looks healthy overall. CPU and memory are stable, and the most recent monitor sample stayed in the healthy band."),
                ),
            ),
            AiChat(
                id = "chat-2",
                title = "Deploy checklist",
                messages = mutableStateListOf(
                    AiMessage("assistant", "Use the tools bar below to bind a server, a skill, or the embedded WebView context."),
                ),
            ),
        )
    }
    var activeChatId by rememberSaveable { mutableStateOf("chat-1") }
    var historyVisible by rememberSaveable { mutableStateOf(false) }
    var toolsExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedServerId by rememberSaveable { mutableStateOf(servers.firstOrNull()?.id) }
    val activeChat = chats.firstOrNull { it.id == activeChatId } ?: chats.first()
    val selectedServer = servers.firstOrNull { it.id == selectedServerId }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { historyVisible = true }) {
                        Icon(Icons.Outlined.Menu, contentDescription = null)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            activeChat.title,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                        Text(
                            "Context 12k / 259k · ${selectedServer?.name ?: "No default server"}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    IconButton(onClick = {
                        val nextId = "chat-${chats.size + 1}"
                        chats.add(
                            0,
                            AiChat(
                                id = nextId,
                                title = "New chat",
                                messages = mutableStateListOf(
                                    AiMessage("assistant", "New chat created. Ask about a server, a log, or a remote file."),
                                ),
                            ),
                        )
                        activeChatId = nextId
                    }) {
                        Icon(Icons.Outlined.AddComment, contentDescription = null)
                    }
                    IconButton(onClick = { logger.info("AI app settings opened") }) {
                        Icon(Icons.Outlined.Settings, contentDescription = null)
                    }
                    IconButton(onClick = { logger.info("AI local settings opened") }) {
                        Icon(Icons.Outlined.Tune, contentDescription = null)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(activeChat.messages) { message ->
                    MessageBubble(message = message)
                }
            }

            Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = inputText,
                            onValueChange = viewModel::onInputTextChange,
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(18.dp),
                                )
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { toolsExpanded = !toolsExpanded }) {
                            Icon(Icons.Outlined.Add, contentDescription = null)
                        }
                        IconButton(
                            onClick = {
                                val prompt = inputText.trim()
                                if (prompt.isBlank()) {
                                    logger.warning("AI prompt send ignored", "reason=empty_input")
                                    return@IconButton
                                }
                                activeChat.messages.add(AiMessage("user", prompt))
                                activeChat.messages.add(
                                    AiMessage(
                                        "assistant",
                                        "Draft answer for \"$prompt\".\n\nThis Compose screen mirrors the Flutter chat layout: toolbar, history panel, tools strip, message stream, and composer.",
                                    ),
                                )
                                if (activeChat.title == "New chat") {
                                    val newTitle = prompt.take(22)
                                    val index = chats.indexOfFirst { it.id == activeChat.id }
                                    chats[index] = activeChat.copy(title = newTitle)
                                }
                                logger.info("AI prompt submit requested", "length=${prompt.length}")
                                viewModel.clearInputText()
                            },
                        ) {
                            Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null)
                        }
                    }
                    AnimatedVisibility(visible = toolsExpanded) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ToolChip("Server", selectedServer?.name ?: "None")
                            ToolChip("Skills", "2 enabled")
                            ToolChip("WebView", "Detached")
                        }
                    }
                }
            }
        }

        if (historyVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.35f))
                    .clickable { historyVisible = false },
            )
            Surface(
                modifier = Modifier
                    .fillMaxSize(0.84f)
                    .align(Alignment.CenterStart),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Text("Chat history", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    chats.forEach { chat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    activeChatId = chat.id
                                    historyVisible = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (chat.id == activeChatId) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                },
                            ),
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(chat.title, fontWeight = FontWeight.Bold)
                                Text(
                                    "${chat.messages.size} messages",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: AiMessage) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.82f else 0.92f),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
            ),
            shape = RoundedCornerShape(22.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isUser) "User" else "Assistant",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(message.text)
            }
        }
    }
}

@Composable
private fun ToolChip(title: String, value: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("$title: ", fontWeight = FontWeight.SemiBold)
            Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private data class AiChat(
    val id: String,
    val title: String,
    val messages: androidx.compose.runtime.snapshots.SnapshotStateList<AiMessage>,
)

private data class AiMessage(
    val role: String,
    val text: String,
)

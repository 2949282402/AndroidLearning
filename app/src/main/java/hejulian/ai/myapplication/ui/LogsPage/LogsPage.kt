package hejulian.ai.myapplication.ui.LogsPage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hejulian.ai.myapplication.core.logging.AppLog
import hejulian.ai.myapplication.core.logging.LogLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogsScreen(viewModel: LogsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Toolbar(
            state = state,
            onClearSelection = viewModel::clearSelection,
            onDeleteSelected = viewModel::deleteSelected,
            onClearLogs = viewModel::clearLogs,
            onCopy = {
                val logs = if (state.selectionMode) {
                    state.logs.filter { it.id in state.selectedIds }
                } else {
                    state.logs
                }
                copyLogs(context, logs)
            },
        )
        LazyRow(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = state.selectedLevel == null,
                    onClick = { viewModel.selectLevel(null) },
                    label = { Text("All ${state.levelCounts[null] ?: 0}") },
                )
            }
            items(LogLevel.entries) { level ->
                FilterChip(
                    selected = state.selectedLevel == level,
                    onClick = { viewModel.selectLevel(level) },
                    label = { Text("${level.name} ${state.levelCounts[level] ?: 0}") },
                )
            }
        }
        if (state.logs.isEmpty()) {
            EmptyLogs()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.logs, key = { it.id }) { log ->
                    LogCard(
                        log = log,
                        selected = log.id in state.selectedIds,
                        selectionMode = state.selectionMode,
                        onTap = { if (state.selectionMode) viewModel.toggleSelection(log.id) },
                        onLongPress = { viewModel.toggleSelection(log.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun Toolbar(
    state: LogsUiState,
    onClearSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    onClearLogs: () -> Unit,
    onCopy: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (state.selectionMode) {
                        "${state.selectedIds.size} logs selected"
                    } else {
                        "Developer logs"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (state.selectionMode) {
                    IconButton(onClick = onClearSelection) {
                        Icon(Icons.Outlined.Close, contentDescription = null)
                    }
                }
                IconButton(onClick = onCopy) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                }
                IconButton(onClick = if (state.selectionMode) onDeleteSelected else onClearLogs) {
                    Icon(Icons.Outlined.Delete, contentDescription = null)
                }
            }
            Text(
                text = "This page follows the Flutter log screen structure: selection mode, batch actions, level filters, and monospace log cards.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LogCard(
    log: AppLog,
    selected: Boolean,
    selectionMode: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onTap, onLongClick = onLongPress),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectionMode) {
                    Icon(
                        imageVector = if (selected) {
                            Icons.Outlined.TaskAlt
                        } else {
                            Icons.Outlined.RadioButtonUnchecked
                        },
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                LevelPill(level = log.level)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatTimestamp(log.timeMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = log.message,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            if (!log.details.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = log.details,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LevelPill(level: LogLevel) {
    val color = when (level) {
        LogLevel.Error -> MaterialTheme.colorScheme.error
        LogLevel.Warning -> MaterialTheme.colorScheme.tertiary
        LogLevel.Service -> MaterialTheme.colorScheme.secondary
        LogLevel.Tool -> MaterialTheme.colorScheme.primary
        LogLevel.Debug -> MaterialTheme.colorScheme.outline
        LogLevel.Info -> MaterialTheme.colorScheme.primary
    }
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = level.name,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun EmptyLogs() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(shape = RoundedCornerShape(26.dp)) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(36.dp))
                Text("No logs", fontWeight = FontWeight.Bold)
                Text(
                    "Interact with the other pages to generate app, service, and tool logs.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun copyLogs(context: Context, logs: List<AppLog>) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val payload = logs.joinToString(separator = "\n\n") { log ->
        buildString {
            append("[")
            append(log.level.name)
            append("] ")
            append(formatTimestamp(log.timeMillis))
            append("\n")
            append(log.message)
            if (!log.details.isNullOrBlank()) {
                append("\n")
                append(log.details)
            }
        }
    }
    clipboard.setPrimaryClip(ClipData.newPlainText("logs", payload))
}

private fun formatTimestamp(timeMillis: Long): String {
    return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timeMillis))
}

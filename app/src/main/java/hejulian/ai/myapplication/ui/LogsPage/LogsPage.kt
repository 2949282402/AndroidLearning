package hejulian.ai.myapplication.ui.LogsPage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hejulian.ai.myapplication.core.logging.LogLevel

@Composable
fun LogsScreen(viewModel: LogsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        LazyRow(modifier = Modifier.padding(8.dp)) {
            item {
                FilterChip(
                    selected = state.selectedLevel == null,
                    onClick = { viewModel.selectLevel(null) },
                    label = { Text("All") }
                )
            }
            items(LogLevel.entries) { level ->
                FilterChip(
                    selected = state.selectedLevel == level,
                    onClick = { viewModel.selectLevel(level) },
                    label = { Text(level.name) }
                )
            }
        }

        LazyColumn {
            items(state.logs, key = { it.id }) { log ->
                ListItem(
                    headlineContent = { Text(log.message) },
                    supportingContent = {
                        if (log.details != null) Text(log.details, maxLines = 3)
                    },
                    overlineContent = { Text(log.level.name) }
                )
                HorizontalDivider()
            }
        }
    }
}
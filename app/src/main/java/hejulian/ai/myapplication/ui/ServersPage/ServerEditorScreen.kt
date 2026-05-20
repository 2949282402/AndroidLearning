package hejulian.ai.myapplication.ui.ServersPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hejulian.ai.myapplication.core.model.ServerProfile
import hejulian.ai.myapplication.core.model.TerminalWindowSummary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerEditorScreen(
    initialServer: ServerProfile?,
    onDismiss: () -> Unit,
    onSave: (ServerProfile) -> Unit,
) {
    var name by remember(initialServer) { mutableStateOf(initialServer?.name.orEmpty()) }
    var host by remember(initialServer) { mutableStateOf(initialServer?.host.orEmpty()) }
    var port by remember(initialServer) { mutableStateOf((initialServer?.port ?: 22).toString()) }
    var username by remember(initialServer) { mutableStateOf(initialServer?.username.orEmpty()) }
    var osLabel by remember(initialServer) { mutableStateOf(initialServer?.osLabel ?: "Linux") }
    var tag by remember(initialServer) { mutableStateOf(initialServer?.tag ?: "Production") }
    var jumpHost by remember { mutableStateOf("") }
    var authMode by remember { mutableStateOf("Password") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (initialServer == null) "Add connection" else "Edit connection")
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val profile = ServerProfile(
                                id = initialServer?.id ?: UUID.randomUUID().toString(),
                                name = name.ifBlank { "new-server" },
                                host = host.ifBlank { "0.0.0.0" },
                                port = port.toIntOrNull() ?: 22,
                                username = username.ifBlank { "root" },
                                osLabel = osLabel.ifBlank { "Linux" },
                                tag = tag.ifBlank { "Custom" },
                                healthSummary = initialServer?.healthSummary ?: "No samples yet",
                                healthScore = initialServer?.healthScore ?: 0,
                                terminalWindows = initialServer?.terminalWindows ?: listOf(
                                    TerminalWindowSummary("draft", "shell", "Idle")
                                ),
                            )
                            onSave(profile)
                        },
                    ) {
                        Text("Save")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                EditorSection(title = "Basic") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Name") },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tag,
                        onValueChange = { tag = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Environment tag") },
                    )
                }
            }
            item {
                EditorSection(title = "Connection") {
                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Host") },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Username") },
                        )
                        OutlinedTextField(
                            value = port,
                            onValueChange = { port = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Port") },
                        )
                    }
                }
            }
            item {
                EditorSection(title = "Authentication") {
                    OutlinedTextField(
                        value = authMode,
                        onValueChange = { authMode = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Auth mode") },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = osLabel,
                        onValueChange = { osLabel = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Operating system") },
                    )
                }
            }
            item {
                EditorSection(title = "Advanced") {
                    OutlinedTextField(
                        value = jumpHost,
                        onValueChange = { jumpHost = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Jump host (optional)") },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "This form follows the same section order as the Flutter editor: basic info, connection, authentication, and advanced routing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item {
                Button(
                    onClick = {
                        val profile = ServerProfile(
                            id = initialServer?.id ?: UUID.randomUUID().toString(),
                            name = name.ifBlank { "new-server" },
                            host = host.ifBlank { "0.0.0.0" },
                            port = port.toIntOrNull() ?: 22,
                            username = username.ifBlank { "root" },
                            osLabel = osLabel.ifBlank { "Linux" },
                            tag = tag.ifBlank { "Custom" },
                            healthSummary = initialServer?.healthSummary ?: "No samples yet",
                            healthScore = initialServer?.healthScore ?: 0,
                            terminalWindows = initialServer?.terminalWindows ?: listOf(
                                TerminalWindowSummary("draft", "shell", "Idle")
                            ),
                        )
                        onSave(profile)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(if (initialServer == null) "Create connection" else "Update connection")
                }
            }
        }
    }
}

@Composable
private fun EditorSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

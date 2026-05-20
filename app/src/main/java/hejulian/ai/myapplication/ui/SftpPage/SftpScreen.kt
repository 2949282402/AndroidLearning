package hejulian.ai.myapplication.ui.SftpPage

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.LinkOff
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hejulian.ai.myapplication.core.model.ServerProfile

@Composable
fun SftpScreen(
    servers: List<ServerProfile>,
) {
    var serversCollapsed by rememberSaveable { mutableStateOf(false) }
    var selectedServerId by rememberSaveable { mutableStateOf(servers.firstOrNull()?.id) }
    var mode by rememberSaveable { mutableStateOf(SftpMode.Browser) }
    var openedFile by rememberSaveable { mutableStateOf<SftpEntry?>(null) }
    var editorText by rememberSaveable {
        mutableStateOf(
            """
            server {
              listen 443 ssl;
              server_name example.internal;
              location /health {
                return 200 "ok";
              }
            }
            """.trimIndent()
        )
    }
    var wrapLines by rememberSaveable { mutableStateOf(true) }
    val entries = remember { mutableStateListOf(*demoEntries().toTypedArray()) }
    val selectedServer = servers.firstOrNull { it.id == selectedServerId } ?: servers.firstOrNull()

    when (mode) {
        SftpMode.Browser -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                if (serversCollapsed) {
                    CollapsedServerBar(
                        server = selectedServer,
                        onExpand = { serversCollapsed = false },
                    )
                } else {
                    ExpandedServerStrip(
                        servers = servers,
                        selectedServerId = selectedServerId,
                        onCollapse = { serversCollapsed = true },
                        onSelectServer = { selectedServerId = it },
                    )
                }
                BrowserTopBar()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(entries, key = { it.path }) { entry ->
                        EntryCard(
                            entry = entry,
                            onPreview = {
                                openedFile = entry
                                mode = SftpMode.Preview
                            },
                            onEdit = {
                                openedFile = entry
                                mode = SftpMode.Edit
                            },
                        )
                    }
                }
            }
        }

        SftpMode.Preview -> FilePreviewScreen(
            file = openedFile,
            onBack = { mode = SftpMode.Browser },
        )

        SftpMode.Edit -> SftpEditorScreen(
            file = openedFile,
            text = editorText,
            wrapLines = wrapLines,
            onBack = { mode = SftpMode.Browser },
            onTextChange = { editorText = it },
            onWrapLinesChange = { wrapLines = it },
        )
    }
}

@Composable
private fun ExpandedServerStrip(
    servers: List<ServerProfile>,
    selectedServerId: String?,
    onCollapse: () -> Unit,
    onSelectServer: (String) -> Unit,
) {
    Column {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.size(width = 48.dp, height = 72.dp),
                ) {
                    IconButton(onClick = onCollapse) {
                        Icon(Icons.Outlined.KeyboardDoubleArrowUp, contentDescription = null)
                    }
                }
            }
            items(servers, key = { it.id }) { server ->
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .height(72.dp)
                        .clickable { onSelectServer(server.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (server.id == selectedServerId) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                    ),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Outlined.Folder, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(server.name, fontWeight = FontWeight.Bold)
                            Text(
                                "${server.username}@${server.host}",
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

@Composable
private fun CollapsedServerBar(
    server: ServerProfile?,
    onExpand: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onExpand) {
                Icon(Icons.Outlined.KeyboardDoubleArrowDown, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(server?.name ?: "SFTP servers", fontWeight = FontWeight.Bold)
                Text(
                    server?.let { "${it.username}@${it.host}" } ?: "Choose a connection",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun BrowserTopBar() {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                ) {
                    Text(
                        text = "/srv/projects/ssh-mobile",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = {}) { Icon(Icons.Outlined.Refresh, contentDescription = null) }
                IconButton(onClick = {}) { Icon(Icons.Outlined.UploadFile, contentDescription = null) }
                IconButton(onClick = {}) { Icon(Icons.Outlined.CreateNewFolder, contentDescription = null) }
                IconButton(onClick = {}) { Icon(Icons.Outlined.LinkOff, contentDescription = null) }
            }
        }
    }
}

@Composable
private fun EntryCard(
    entry: SftpEntry,
    onPreview: () -> Unit,
    onEdit: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (entry.isDirectory) Icons.Outlined.Folder else Icons.Outlined.Description,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.name, fontWeight = FontWeight.Bold)
                Text(
                    entry.meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (!entry.isDirectory) {
                IconButton(onClick = onPreview) {
                    Icon(Icons.Outlined.Preview, contentDescription = null)
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = null)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.Download, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun FilePreviewScreen(
    file: SftpEntry?,
    onBack: () -> Unit,
) {
    ScaffoldLikePage(
        title = file?.name ?: "Preview",
        actionLabel = "Back",
        onAction = onBack,
    ) {
        Text(
            text = """
                # ${file?.name ?: "README.md"}

                This preview page mirrors the Flutter version’s full-screen reader.

                - Opened from the SFTP browser
                - Focused on readable content
                - Kept separate from the editor workflow
            """.trimIndent(),
            modifier = Modifier.padding(16.dp),
            lineHeight = 22.sp,
        )
    }
}

@Composable
private fun SftpEditorScreen(
    file: SftpEntry?,
    text: String,
    wrapLines: Boolean,
    onBack: () -> Unit,
    onTextChange: (String) -> Unit,
    onWrapLinesChange: (Boolean) -> Unit,
) {
    val scrollState = rememberScrollState()
    ScaffoldLikePage(
        title = file?.name ?: "Editor",
        actionLabel = if (wrapLines) "Wrap on" else "Wrap off",
        onAction = { onWrapLinesChange(!wrapLines) },
        navigationLabel = "Back",
        onNavigation = onBack,
        trailingLabel = "Save",
        onTrailing = { onBack() },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(16.dp),
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                ),
            )
        }
    }
}

@Composable
private fun ScaffoldLikePage(
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    navigationLabel: String? = null,
    onNavigation: (() -> Unit)? = null,
    trailingLabel: String? = null,
    onTrailing: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (navigationLabel != null && onNavigation != null) {
                IconButton(onClick = onNavigation) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = navigationLabel)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(
                    actionLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = actionLabel,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable { onAction() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            if (trailingLabel != null && onTrailing != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = trailingLabel,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable { onTrailing() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

private enum class SftpMode {
    Browser,
    Preview,
    Edit,
}

private data class SftpEntry(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val meta: String,
)

private fun demoEntries(): List<SftpEntry> = listOf(
    SftpEntry("nginx", "/etc/nginx", true, "Directory · modified 2h ago"),
    SftpEntry("docker-compose.yml", "/srv/projects/ssh-mobile/docker-compose.yml", false, "4.2 KB · modified 8m ago"),
    SftpEntry("README.md", "/srv/projects/ssh-mobile/README.md", false, "12.7 KB · modified yesterday"),
    SftpEntry("logs", "/srv/projects/ssh-mobile/logs", true, "Directory · modified 30m ago"),
    SftpEntry("deploy.sh", "/srv/projects/ssh-mobile/deploy.sh", false, "1.1 KB · modified 5m ago"),
)

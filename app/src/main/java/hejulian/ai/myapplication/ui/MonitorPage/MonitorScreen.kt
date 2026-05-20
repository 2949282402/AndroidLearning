package hejulian.ai.myapplication.ui.MonitorPage

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import hejulian.ai.myapplication.core.model.ServerProfile
import kotlin.math.max

@Composable
fun MonitorScreen(
    servers: List<ServerProfile>,
) {
    var tabIndex by rememberSaveable { mutableStateOf(0) }
    var collapsed by rememberSaveable { mutableStateOf(false) }
    var selectedServerId by rememberSaveable { mutableStateOf(servers.firstOrNull()?.id) }
    val selectedServer = servers.firstOrNull { it.id == selectedServerId } ?: servers.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        PrimaryTabRow(selectedTabIndex = tabIndex) {
            listOf("Performance", "Ports", "Applications").forEachIndexed { index, title ->
                Tab(selected = tabIndex == index, onClick = { tabIndex = index }, text = { Text(title) })
            }
        }
        if (collapsed) {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { collapsed = false }) {
                        Icon(Icons.Outlined.KeyboardDoubleArrowDown, contentDescription = null)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(selectedServer?.name ?: "Monitor servers", fontWeight = FontWeight.Bold)
                        Text(
                            selectedServer?.healthSummary ?: "Select one or more servers",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        } else {
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
                        IconButton(onClick = { collapsed = true }) {
                            Icon(Icons.Outlined.KeyboardDoubleArrowUp, contentDescription = null)
                        }
                    }
                }
                items(servers, key = { it.id }) { server ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .height(72.dp)
                            .clickable { selectedServerId = server.id },
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
                            Icon(Icons.Outlined.MonitorHeart, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(server.name, fontWeight = FontWeight.Bold)
                                Text(
                                    server.healthSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }

        when (tabIndex) {
            0 -> PerformanceTab(server = selectedServer)
            1 -> PortsTab(server = selectedServer)
            else -> ApplicationsTab(server = selectedServer)
        }
    }
}

@Composable
private fun PerformanceTab(server: ServerProfile?) {
    val charts = remember { demoCharts() }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(server?.name ?: "No server selected", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        if (server == null) {
                            "Select one or more servers, then start monitoring."
                        } else {
                            "Monitoring ${server.username}@${server.host} · Interval 5s · Range 15m"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        items(charts, key = { it.title }) { chart ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(chart.title, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(chart.value, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    MiniLineChart(values = chart.points)
                }
            }
        }
    }
}

@Composable
private fun PortsTab(server: ServerProfile?) {
    val ports = remember { demoPorts() }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            SectionHeader(
                title = "Ports snapshot",
                subtitle = server?.name ?: "Select a server for ports",
            )
        }
        items(ports, key = { "${it.port}-${it.process}" }) { item ->
            var expanded by remember { mutableStateOf(false) }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = item.port,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.process, fontWeight = FontWeight.Bold)
                            Text(
                                "${item.protocol} ${item.state} · ${item.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Icon(Icons.Outlined.Refresh, contentDescription = null)
                    }
                    if (expanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Address: ${item.address}")
                        Text("Protocol: ${item.protocol}")
                        Text("State: ${item.state}")
                        Text("Process: ${item.process}")
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicationsTab(server: ServerProfile?) {
    val apps = remember { demoApplications() }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            SectionHeader(
                title = "Applications",
                subtitle = server?.name ?: "Select a server for process view",
            )
        }
        items(apps, key = { it.name }) { app ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(app.name, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(app.cpu)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "${app.memory} · ${app.status}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MiniLineChart(values: List<Float>) {
    val maxValue = max(values.maxOrNull() ?: 1f, 1f)
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
    ) {
        if (values.isEmpty()) return@Canvas
        val path = Path()
        values.forEachIndexed { index, value ->
            val x = size.width * index / (values.size - 1).coerceAtLeast(1)
            val y = size.height - (value / maxValue) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = primary,
            style = Stroke(width = 6f, cap = StrokeCap.Round),
        )
        repeat(3) { step ->
            val y = size.height * step / 2f
            drawLine(
                color = outline,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
        }
    }
}

private data class MetricChart(val title: String, val value: String, val points: List<Float>)
private data class PortSnapshot(val port: String, val process: String, val protocol: String, val state: String, val address: String)
private data class AppSnapshot(val name: String, val cpu: String, val memory: String, val status: String)

private fun demoCharts(): List<MetricChart> = listOf(
    MetricChart("CPU", "28%", listOf(16f, 20f, 26f, 31f, 24f, 28f, 33f)),
    MetricChart("Memory", "51%", listOf(42f, 44f, 47f, 48f, 50f, 51f, 52f)),
    MetricChart("Disk IO", "12 MB/s", listOf(4f, 6f, 10f, 8f, 12f, 9f, 11f)),
    MetricChart("Network", "240 KB/s", listOf(60f, 90f, 120f, 180f, 150f, 210f, 240f)),
)

private fun demoPorts(): List<PortSnapshot> = listOf(
    PortSnapshot("22", "sshd", "TCP", "LISTEN", "0.0.0.0:22"),
    PortSnapshot("443", "nginx", "TCP", "LISTEN", "0.0.0.0:443"),
    PortSnapshot("5432", "postgres", "TCP", "LISTEN", "127.0.0.1:5432"),
)

private fun demoApplications(): List<AppSnapshot> = listOf(
    AppSnapshot("nginx.service", "2.3%", "110 MB", "running"),
    AppSnapshot("docker.service", "3.8%", "482 MB", "running"),
    AppSnapshot("postgresql", "7.4%", "822 MB", "running"),
)

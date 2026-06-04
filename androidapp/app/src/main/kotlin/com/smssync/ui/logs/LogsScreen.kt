package com.smssync.ui.logs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smssync.data.db.SmsEntity
import java.text.SimpleDateFormat
import java.util.*

@file:OptIn(ExperimentalMaterial3Api::class)

@Composable
fun LogsScreen(
    viewModel: LogsViewModel,
    onSettingsClick: () -> Unit
) {
    val smsList = viewModel.smsList.collectAsState(initial = emptyList())
    val syncStats = viewModel.syncStats.collectAsState()
    val isSyncing = viewModel.isSyncing.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        TopAppBar(
            title = { Text("SMS Sync Logs") },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Settings"
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Button(
                    onClick = { viewModel.masterSync() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !isSyncing.value
                ) {
                    if (isSyncing.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Syncing...", modifier = Modifier.padding(start = 8.dp))
                    } else {
                        Text("Master Sync")
                    }
                }
            }

            if (syncStats.value != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            val stats = syncStats.value!!
                            Text("Sync Results:", style = MaterialTheme.typography.titleSmall)
                            Text("Total Scanned: ${stats.totalScanned}", style = MaterialTheme.typography.bodySmall)
                            Text("Imported: ${stats.imported}", style = MaterialTheme.typography.bodySmall)
                            Text("Synced: ${stats.synced}", style = MaterialTheme.typography.bodySmall)
                            Text("Failed: ${stats.failed}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            items(smsList.value, key = { it.id }) { sms ->
                SmsListItem(sms = sms, onSync = { viewModel.syncSms(sms) })
            }
        }
    }
}

@Composable
fun SmsListItem(sms: SmsEntity, onSync: () -> Unit) {
    val isExpanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded.value = !isExpanded.value }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "From: ${sms.sender}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        formatTimestamp(sms.timestamp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (sms.isBankSms) {
                        FilterChip(
                            selected = true,
                            onClick = {},
                            label = { Text("Bank SMS") },
                            enabled = false
                        )
                    }

                    FilterChip(
                        selected = sms.synced,
                        onClick = {},
                        label = { Text(if (sms.synced) "Synced" else "Not Synced") },
                        enabled = false
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded.value,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Divider()
                    Text(
                        "Message:",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        sms.body,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (sms.errorMessage != null) {
                        Text(
                            "Error: ${sms.errorMessage}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = onSync,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sync")
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

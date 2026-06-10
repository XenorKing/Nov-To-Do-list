package com.novaroject.novtodolist.tasks.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.novaroject.novtodolist.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val priorityColor = when (task.priority) {
        2 -> Color(0xFFFF5252)
        1 -> Color(0xFFFFAB40)
        else -> Color(0xFF69F0AE)
    }
    val cardColor by animateColorAsState(
        if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.surface, label = "card"
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить задачу?") },
            text  = { Text("«${task.title}» будет удалена без возможности восстановления.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            Box(
                Modifier
                    .width(4.dp)
                    .height(48.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = priorityColor),
                    shape = MaterialTheme.shapes.small
                ) {}
            }
            Spacer(Modifier.width(12.dp))

            // Checkbox
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { if (!task.isCompleted) onComplete() }
            )
            Spacer(Modifier.width(8.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.description.isNotBlank()) {
                    Text(
                        task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (task.dueDate != null) {
                    Text(
                        SimpleDateFormat("d MMM, HH:mm", Locale("ru")).format(task.dueDate.toDate()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                if (task.isCompleted && task.completedByName != null) {
                    Text(
                        "✓ ${task.completedByName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Delete button
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.DeleteOutline, "Удалить",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}

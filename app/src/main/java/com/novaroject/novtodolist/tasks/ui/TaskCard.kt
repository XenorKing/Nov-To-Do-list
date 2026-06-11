package com.novaroject.novtodolist.tasks.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novaroject.novtodolist.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskCard(task: Task, onComplete: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }

    val priorityColor = when (task.priority) {
        2 -> Color(0xFFFF2D78)
        1 -> Color(0xFFFFAB40)
        else -> Color(0xFF39FF14)
    }
    val glowColor = priorityColor.copy(alpha = if (task.isCompleted) 0.1f else 0.25f)

    // ─── Delete dialog ───
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF100D20),
            title = { Text("Удалить задачу?", color = Color.White) },
            text = { Text("Задача будет удалена без возможности восстановления.", color = Color(0xFF8888AA)) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Удалить", color = Color(0xFFFF2D78))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена", color = Color(0xFF8888AA)) }
            }
        )
    }

    // ─── Detail dialog (Fix #2) ───
    if (showDetailDialog) {
        val fmt = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("ru"))
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            containerColor = Color(0xFF100D20),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.width(4.dp).height(20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(priorityColor)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(task.title, color = Color.White, fontWeight = FontWeight.Bold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (task.description.isNotBlank()) {
                        Text(task.description, color = Color(0xFFBBBBDD), fontSize = 14.sp, lineHeight = 20.sp)
                        HorizontalDivider(color = Color(0xFF221F3A))
                    }
                    // Date
                    if (task.dueDate != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, null, Modifier.size(15.dp), tint = priorityColor)
                            Spacer(Modifier.width(8.dp))
                            Text(fmt.format(task.dueDate.toDate()), color = priorityColor, fontSize = 13.sp)
                        }
                    }
                    // Category
                    if (task.category.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Label, null, Modifier.size(15.dp), tint = Color(0xFF8888AA))
                            Spacer(Modifier.width(8.dp))
                            Text(categoryLabel(task.category), color = Color(0xFFAAAAAA), fontSize = 13.sp)
                        }
                    }
                    // Repeat
                    if (task.repeatType != "none") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Repeat, null, Modifier.size(15.dp), tint = Color(0xFF8888AA))
                            Spacer(Modifier.width(8.dp))
                            Text(repeatLabel(task.repeatType), color = Color(0xFFAAAAAA), fontSize = 13.sp)
                        }
                    }
                    // Reminder
                    if (task.dueDate != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, null, Modifier.size(15.dp), tint = Color(0xFF8888AA))
                            Spacer(Modifier.width(8.dp))
                            Text(reminderLabel(task.reminderOffset), color = Color(0xFFAAAAAA), fontSize = 13.sp)
                        }
                    }
                    // Priority
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Flag, null, Modifier.size(15.dp), tint = priorityColor)
                        Spacer(Modifier.width(8.dp))
                        Text(priorityLabel(task.priority), color = priorityColor, fontSize = 13.sp)
                    }
                    // Created at
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, Modifier.size(15.dp), tint = Color(0xFF5555AA))
                        Spacer(Modifier.width(8.dp))
                        Text("Создана: ${fmt.format(task.createdAt.toDate())}",
                            color = Color(0xFF6666AA), fontSize = 12.sp)
                    }
                    // Completed by
                    if (task.isCompleted && task.completedByName != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, Modifier.size(15.dp), tint = Color(0xFF39FF14))
                            Spacer(Modifier.width(8.dp))
                            Text("Выполнено: ${task.completedByName}", color = Color(0xFF39FF14), fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                if (!task.isCompleted) {
                    TextButton(onClick = { onComplete(); showDetailDialog = false }) {
                        Text("✓ Выполнить", color = Color(0xFF39FF14), fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(onClick = { showDetailDialog = false }) {
                        Text("Закрыть", color = Color(0xFF8888AA))
                    }
                }
            },
            dismissButton = {
                if (!task.isCompleted) {
                    TextButton(onClick = { showDetailDialog = false }) {
                        Text("Закрыть", color = Color(0xFF8888AA))
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        asFrameworkPaint().apply {
                            isAntiAlias = true
                            color = android.graphics.Color.TRANSPARENT
                            setShadowLayer(12f, 0f, 0f, glowColor.copy(alpha = 0.6f).toArgb())
                        }
                    }
                    canvas.drawRoundRect(
                        left = 0f, top = 0f, right = size.width, bottom = size.height,
                        radiusX = 16.dp.toPx(), radiusY = 16.dp.toPx(), paint = paint
                    )
                }
            }
            .clip(RoundedCornerShape(16.dp))
            .background(if (task.isCompleted) Color(0xFF0A0818) else Color(0xFF100D20))
            .clickable { showDetailDialog = true }  // Fix #2 — открывает детали
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Priority neon strip
            Box(
                Modifier
                    .width(3.dp).height(52.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor.copy(alpha = if (task.isCompleted) 0.3f else 0.9f))
            )
            Spacer(Modifier.width(12.dp))

            // Fix #1 — Checkbox работает: помечаем выполненным (onComplete вызывается при клике)
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = {
                    if (!task.isCompleted) onComplete()  // только в одну сторону
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = priorityColor,
                    uncheckedColor = Color(0xFF5555AA),
                    checkmarkColor = Color.Black
                )
            )
            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    color = if (task.isCompleted) Color(0xFF6666AA) else Color(0xFFE8E8FF),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                if (task.description.isNotBlank()) {
                    Text(task.description, color = Color(0xFF7878AA), fontSize = 12.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (task.dueDate != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, null, Modifier.size(11.dp),
                            tint = priorityColor.copy(alpha = 0.8f))
                        Spacer(Modifier.width(3.dp))
                        Text(
                            SimpleDateFormat("d MMM, HH:mm", Locale("ru")).format(task.dueDate.toDate()),
                            color = priorityColor.copy(alpha = 0.8f), fontSize = 11.sp
                        )
                    }
                }
                if (task.isCompleted && task.completedByName != null) {
                    Text("✓ " + task.completedByName, color = Color(0xFF39FF14).copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }

            // Кнопка удаления
            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.DeleteOutline, "Удалить", Modifier.size(18.dp),
                    tint = Color(0xFFFF2D78).copy(alpha = 0.5f))
            }
        }
    }
}

private fun categoryLabel(cat: String) = when (cat) {
    "work"     -> "Работа"
    "personal" -> "Личное"
    "health"   -> "Здоровье"
    "study"    -> "Учёба"
    "other"    -> "Другое"
    else       -> cat
}

private fun repeatLabel(r: String) = when (r) {
    "daily"   -> "Повторять каждый день"
    "weekly"  -> "Повторять каждую неделю"
    "monthly" -> "Повторять каждый месяц"
    else      -> "Не повторять"
}

private fun reminderLabel(offset: Int) = when (offset) {
    0    -> "Напоминание в момент"
    5    -> "Напоминание за 5 минут"
    15   -> "Напоминание за 15 минут"
    30   -> "Напоминание за 30 минут"
    60   -> "Напоминание за 1 час"
    1440 -> "Напоминание за 1 день"
    else -> "Напоминание за $offset мин"
}

private fun priorityLabel(p: Int) = when (p) {
    2 -> "Высокий приоритет"
    1 -> "Средний приоритет"
    else -> "Низкий приоритет"
}

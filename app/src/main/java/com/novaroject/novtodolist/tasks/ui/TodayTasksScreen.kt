package com.novaroject.novtodolist.tasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novaroject.novtodolist.tasks.TaskViewModel
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPurple
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TodayTasksScreen(
    onAddTask: () -> Unit,
    onOpenProfile: () -> Unit,
    vm: TaskViewModel = hiltViewModel()
) {
    var search by remember { mutableStateOf("") }
    val state  by vm.todayState.collectAsState()
    val tasks  = vm.filteredToday(search)
    val today  = SimpleDateFormat("d MMMM, EEEE", Locale("ru")).format(Date())
    val done   = tasks.count { it.isCompleted }
    val total  = tasks.size

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg)
    ) {
        // ── Header ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0818))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Сегодня", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold,
                        color = Color.White)
                    Text(today, fontSize = 12.sp, color = Color(0xFF7878AA))
                }
                IconButton(onClick = onOpenProfile) {
                    Box(
                        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(50))
                            .background(NeonPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountCircle, "Профиль", tint = NeonPurple,
                            modifier = Modifier.size(26.dp))
                    }
                }
            }
        }

        // ── Progress bar ──
        if (total > 0) {
            Box(
                modifier = Modifier.fillMaxWidth().height(3.dp).background(Color(0xFF221F3A))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = if (total > 0) done.toFloat() / total else 0f)
                        .fillMaxHeight()
                        .background(NeonCyan)
                )
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$done / $total выполнено", fontSize = 11.sp, color = Color(0xFF7878AA))
                if (total > 0 && done == total) Text("Всё готово! ✓", fontSize = 11.sp, color = NeonCyan)
            }
        }

        // ── Search ──
        TextField(
            value = search, onValueChange = { search = it },
            placeholder = { Text("Поиск задач...", color = Color(0xFF5555AA), fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF5555AA), modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (search.isNotEmpty()) IconButton({ search = "" }) {
                    Icon(Icons.Default.Clear, null, tint = Color(0xFF5555AA), modifier = Modifier.size(18.dp))
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(14.dp)),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0E0C1C), unfocusedContainerColor = Color(0xFF0A0818),
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFCCCCDD),
                cursorColor = NeonCyan
            )
        )

        // ── Task list ──
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan, strokeWidth = 2.dp)
            }
            tasks.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✦", fontSize = 48.sp, color = NeonCyan.copy(alpha = 0.3f))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (search.isBlank()) "На сегодня задач нет" else "Ничего не найдено",
                        color = Color(0xFF7878AA), fontSize = 16.sp
                    )
                    if (search.isBlank()) {
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onAddTask,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                        ) { Text("+ Добавить задачу") }
                    }
                }
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(task = task, onComplete = { vm.completeTask(task.id) }, onDelete = { vm.deleteTask(task.id) })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

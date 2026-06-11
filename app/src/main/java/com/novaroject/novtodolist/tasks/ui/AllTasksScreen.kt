package com.novaroject.novtodolist.tasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novaroject.novtodolist.tasks.TaskViewModel
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPurple

@Composable
fun AllTasksScreen(onAddTask: () -> Unit, vm: TaskViewModel = hiltViewModel()) {
    var search          by remember { mutableStateOf("") }
    var filterCompleted by remember { mutableStateOf<Boolean?>(null) }
    var menuOpen        by remember { mutableStateOf(false) }
    val state = vm.allState.collectAsState().value
    val base  = vm.filteredAll(search)
    val tasks = when (filterCompleted) {
        true  -> base.filter { it.isCompleted }
        false -> base.filter { !it.isCompleted }
        null  -> base
    }
    val totalAll = state.tasks.size
    val doneAll  = state.tasks.count { it.isCompleted }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {

        // ─── Cyberpunk Header ───
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFF100D28), Color(0xFF0C0A1E), DarkBg)))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 12.dp, top = 20.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text("Все задачи", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold,
                            color = Color.White, letterSpacing = (-0.5).sp)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            Box(Modifier.size(7.dp).clip(CircleShape).background(NeonPurple))
                            Spacer(Modifier.width(6.dp))
                            Text("$totalAll задач · $doneAll выполнено", color = NeonPurple.copy(0.85f),
                                fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Box {
                        IconButton(onClick = { menuOpen = true }) {
                            Box(
                                Modifier.size(44.dp).clip(CircleShape)
                                    .background(
                                        if (filterCompleted != null) NeonCyan.copy(0.2f)
                                        else Color(0xFF1A1630)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FilterList, "Фильтр",
                                    tint = if (filterCompleted != null) NeonCyan else Color(0xFF7878AA),
                                    modifier = Modifier.size(22.dp))
                            }
                        }
                        DropdownMenu(
                            expanded = menuOpen, onDismissRequest = { menuOpen = false },
                            modifier = Modifier.background(Color(0xFF100D20))
                        ) {
                            listOf(null to "Все", true to "Выполненные", false to "Невыполненные")
                                .forEach { (v, lbl) ->
                                    DropdownMenuItem(
                                        text = { Text(lbl, color = if (filterCompleted == v) NeonCyan else Color(0xFFCCCCDD)) },
                                        onClick = { filterCompleted = v; menuOpen = false }
                                    )
                                }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Box(
                    Modifier.fillMaxWidth().height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, NeonPurple.copy(0.25f),
                                    NeonCyan.copy(0.25f), Color.Transparent)
                            )
                        )
                )
            }
        }

        // ─── Error Banner ───
        if (state.error != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2A0A14)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error, null,
                        tint = Color(0xFFFF2D78), modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        state.error ?: "",
                        color = Color(0xFFFF2D78), fontSize = 12.sp,
                        modifier = Modifier.weight(1f), lineHeight = 16.sp
                    )
                }
            }
        }

        // ─── Search ───
        TextField(
            value = search, onValueChange = { search = it },
            placeholder = { Text("Поиск...", color = Color(0xFF5555AA), fontSize = 14.sp) },
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
                focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFCCCCDD), cursorColor = NeonCyan
            )
        )

        filterCompleted?.let { fc ->
            FilterChip(
                selected = true, onClick = { filterCompleted = null },
                label = { Text(if (fc) "Выполненные" else "Невыполненные", fontSize = 12.sp) },
                trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(14.dp)) },
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonPurple.copy(0.2f), selectedLabelColor = NeonPurple,
                    selectedTrailingIconColor = NeonPurple
                )
            )
        }

        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan, strokeWidth = 2.dp)
            }
            tasks.isEmpty() && state.error == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✦", fontSize = 48.sp, color = NeonPurple.copy(alpha = 0.3f))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (search.isBlank() && filterCompleted == null) "Задач пока нет" else "Ничего не найдено",
                        color = Color(0xFF7878AA), fontSize = 16.sp
                    )
                }
            }
            tasks.isNotEmpty() -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(task = task, onComplete = { vm.completeTask(task.id) },
                        onDelete = { vm.deleteTask(task.id) })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✦", fontSize = 48.sp, color = Color(0xFFFF2D78).copy(alpha = 0.3f))
                    Spacer(Modifier.height(12.dp))
                    Text("Проверьте подключение к интернету", color = Color(0xFF7878AA), fontSize = 14.sp)
                }
            }
        }
    }
}

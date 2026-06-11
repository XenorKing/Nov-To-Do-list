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
    val done   = state.tasks.count { it.isCompleted }
    val total  = state.tasks.size

    // ─── Ошибка при выполнении задачи ───
    val completeError by vm.completeError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(completeError) {
        if (completeError != null) {
            snackbarHostState.showSnackbar(
                message = completeError ?: "Ошибка",
                duration = SnackbarDuration.Short
            )
            vm.clearCompleteError()
        }
    }

    Scaffold(
        containerColor = DarkBg,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF2A0A14),
                    contentColor = Color(0xFFFF2D78),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    ) { scaffoldPad ->
        Column(modifier = Modifier.fillMaxSize().background(DarkBg).padding(scaffoldPad)) {

            // ─── Cyberpunk Header ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                            Text(
                                "Сегодня",
                                fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                today,
                                fontSize = 12.sp, color = NeonPurple.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        IconButton(onClick = onOpenProfile) {
                            Box(
                                Modifier.size(44.dp).clip(CircleShape)
                                    .background(NeonPurple.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountCircle, "Профиль",
                                    tint = NeonPurple, modifier = Modifier.size(30.dp))
                            }
                        }
                    }

                    if (total > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(NeonCyan))
                                Spacer(Modifier.width(8.dp))
                                Text("$done из $total выполнено", color = NeonCyan,
                                    fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            if (done == total && total > 0) {
                                Text("Всё готово! ✓", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text(
                                    "${if (total > 0) ((done.toFloat() / total) * 100).toInt() else 0}%",
                                    color = Color(0xFF7878AA), fontSize = 12.sp
                                )
                            }
                        }
                        Box(
                            Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                                .height(3.dp).clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF1A1730))
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(if (total > 0) done.toFloat() / total else 0f)
                                    .fillMaxHeight()
                                    .background(Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)))
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier.fillMaxWidth().height(1.dp)
                            .background(Brush.horizontalGradient(
                                listOf(Color.Transparent, NeonPurple.copy(0.25f),
                                    NeonCyan.copy(0.25f), Color.Transparent)
                            ))
                    )
                }
            }

            // ─── Error Banner (Firestore/сеть) ───
            if (state.error != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2A0A14)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, null, tint = Color(0xFFFF2D78), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(state.error ?: "", color = Color(0xFFFF2D78), fontSize = 12.sp,
                            modifier = Modifier.weight(1f), lineHeight = 16.sp)
                    }
                }
            }

            // ─── Search ───
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
                    focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFCCCCDD), cursorColor = NeonCyan
                )
            )

            // ─── Content ───
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan, strokeWidth = 2.dp)
                }
                tasks.isEmpty() && state.error == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                                onClick = onAddTask, shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                            ) { Text("+ Добавить задачу") }
                        }
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
}

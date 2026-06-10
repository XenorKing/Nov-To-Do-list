package com.novaroject.novtodolist.tasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novaroject.novtodolist.tasks.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayTasksScreen(
    onAddTask: () -> Unit,
    onOpenProfile: () -> Unit,
    vm: TaskViewModel = hiltViewModel()
) {
    var search by remember { mutableStateOf("") }
    val state by vm.todayState.collectAsState()
    val tasks = vm.filteredToday(search)
    val today = SimpleDateFormat("d MMMM, EEEE", Locale("ru")).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column {
                    Text("Сегодня", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(today, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }},
                navigationIcon = {
                    IconButton(onClick = onOpenProfile) {
                        Icon(Icons.Default.AccountCircle, "Профиль",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = onAddTask) {
                        Icon(Icons.Default.Add, "Добавить задачу",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad)) {
            // Search bar
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                placeholder = { Text("Поиск задач...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = { if (search.isNotEmpty()) IconButton({ search = "" }) { Icon(Icons.Default.Clear, null) } },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true, shape = MaterialTheme.shapes.large
            )

            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                tasks.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                        Spacer(Modifier.height(12.dp))
                        Text(if (search.isBlank()) "На сегодня задач нет" else "Ничего не найдено",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface)
                        if (search.isBlank()) {
                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = onAddTask) { Text("+ Добавить задачу") }
                        }
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onComplete = { vm.completeTask(task.id) },
                            onDelete   = { vm.deleteTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}

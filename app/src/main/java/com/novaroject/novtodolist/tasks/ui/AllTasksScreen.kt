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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTasksScreen(
    onAddTask: () -> Unit,
    vm: TaskViewModel = hiltViewModel()
) {
    var search by remember { mutableStateOf("") }
    var filterCompleted by remember { mutableStateOf<Boolean?>(null) }
    val state by vm.allState.collectAsState()
    val base  = vm.filteredAll(search)
    val tasks = when (filterCompleted) {
        true  -> base.filter { it.isCompleted }
        false -> base.filter { !it.isCompleted }
        null  -> base
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Все задачи", fontWeight = FontWeight.Bold) },
                actions = {
                    // Filter chip
                    var menuOpen by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { menuOpen = true }) { Icon(Icons.Default.FilterList, "Фильтр") }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(text = { Text("Все") },            onClick = { filterCompleted = null;  menuOpen = false })
                            DropdownMenuItem(text = { Text("Выполненные") },    onClick = { filterCompleted = true;  menuOpen = false })
                            DropdownMenuItem(text = { Text("Невыполненные") },  onClick = { filterCompleted = false; menuOpen = false })
                        }
                    }
                    IconButton(onClick = onAddTask) { Icon(Icons.Default.Add, "Добавить") }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                placeholder = { Text("Поиск...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = { if (search.isNotEmpty()) IconButton({ search = "" }) { Icon(Icons.Default.Clear, null) } },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true, shape = MaterialTheme.shapes.large
            )

            filterCompleted?.let {
                FilterChip(
                    selected = true,
                    onClick = { filterCompleted = null },
                    label = { Text(if (it) "Выполненные" else "Невыполненные") },
                    trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) },
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                )
            }

            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                tasks.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.List, null, Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                        Spacer(Modifier.height(12.dp))
                        Text(if (search.isBlank() && filterCompleted == null) "Задач пока нет"
                             else "Ничего не найдено",
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
                            task      = task,
                            onComplete= { vm.completeTask(task.id) },
                            onDelete  = { vm.deleteTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}

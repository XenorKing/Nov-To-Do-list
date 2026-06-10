package com.novaroject.novtodolist.tasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.novaroject.novtodolist.tasks.TaskViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(onBack: () -> Unit, vm: TaskViewModel = hiltViewModel()) {
    var title       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority    by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedMillis by remember { mutableStateOf<Long?>(null) }
    var titleError by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedMillis = state.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) { DatePicker(state = state) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая задача", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Close, null) } },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isBlank()) { titleError = true; return@TextButton }
                            val due = selectedMillis?.let { Timestamp(Date(it)) }
                            vm.addTask(title, description, due, priority)
                            onBack()
                        }
                    ) {
                        Text("Сохранить", fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(pad)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it; titleError = false },
                label = { Text("Название *") },
                leadingIcon = { Icon(Icons.Default.Title, null) },
                isError = titleError,
                supportingText = { if (titleError) Text("Введите название задачи") },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.large
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Описание (необязательно)") },
                leadingIcon = { Icon(Icons.Default.Notes, null) },
                modifier = Modifier.fillMaxWidth().height(120.dp), shape = MaterialTheme.shapes.large,
                maxLines = 4
            )

            // Date picker button
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.CalendarToday, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (selectedMillis != null)
                    java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale("ru"))
                        .format(java.util.Date(selectedMillis!!))
                else "Выбрать дату и время")
            }

            // Priority
            Text("Приоритет", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Низкий" to 0, "Средний" to 1, "Высокий" to 2).forEach { (label, value) ->
                    FilterChip(
                        selected = priority == value,
                        onClick  = { priority = value },
                        label    = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

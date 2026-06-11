package com.novaroject.novtodolist.tasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.Timestamp
import com.novaroject.novtodolist.tasks.TaskViewModel
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.DarkCard
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPurple
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(onBack: () -> Unit, vm: TaskViewModel = hiltViewModel()) {
    var title       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority    by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate   by remember { mutableStateOf<Long?>(null) }
    var selectedHour   by remember { mutableIntStateOf(9) }
    var selectedMin    by remember { mutableIntStateOf(0) }
    var hasTime        by remember { mutableStateOf(false) }
    var titleError     by remember { mutableStateOf(false) }

    val addResult by vm.addResult.collectAsState()

    LaunchedEffect(addResult) {
        if (addResult == "OK") { vm.clearAddResult(); onBack() }
    }

    // Date picker dialog
    if (showDatePicker) {
        val dpState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { selectedDate = dpState.selectedDateMillis; showDatePicker = false }) {
                    Text("OK", color = NeonCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            },
            colors = DatePickerDefaults.colors(containerColor = DarkCard)
        ) { DatePicker(state = dpState) }
    }

    // Time picker dialog
    if (showTimePicker) {
        val tpState = rememberTimePickerState(initialHour = selectedHour, initialMinute = selectedMin)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = DarkCard,
            title = { Text("Выбрать время", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    TimeInput(state = tpState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color(0xFF14112A),
                            selectorColor  = NeonCyan,
                            timeSelectorSelectedContainerColor = NeonPurple.copy(alpha = 0.3f),
                            timeSelectorUnselectedContainerColor = Color(0xFF14112A),
                        ))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = tpState.hour
                    selectedMin  = tpState.minute
                    hasTime = true
                    showTimePicker = false
                }) { Text("OK", color = NeonCyan) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Отмена") }
            }
        )
    }

    // Build Timestamp
    val dueTimestamp: Timestamp? = selectedDate?.let { dateMs ->
        val cal = Calendar.getInstance()
        cal.timeInMillis = dateMs
        cal.set(Calendar.HOUR_OF_DAY, if (hasTime) selectedHour else 9)
        cal.set(Calendar.MINUTE,      if (hasTime) selectedMin  else 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        Timestamp(cal.time)
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Новая задача", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Close, null, tint = Color(0xFF8888AA)) } },
                actions = {
                    val isLoading = addResult == null && title.isNotBlank()
                    TextButton(
                        onClick = {
                            if (title.isBlank()) { titleError = true; return@TextButton }
                            vm.addTask(title, description, dueTimestamp, priority)
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Сохранить", fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(pad).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Error from save
            if (addResult != null && addResult != "OK") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2A0A14)
                ) {
                    Text(addResult!!, color = Color(0xFFFF2D78),
                        modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            // Title
            TextField(
                value = title, onValueChange = { title = it; titleError = false },
                placeholder = { Text("Название задачи *", color = Color(0xFF6666AA)) },
                leadingIcon = { Icon(Icons.Default.Title, null, tint = if (titleError) Color(0xFFFF2D78) else NeonCyan) },
                isError = titleError,
                supportingText = { if (titleError) Text("Введите название задачи", color = Color(0xFFFF2D78)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF14112A), unfocusedContainerColor = Color(0xFF0E0C1C),
                    focusedIndicatorColor = NeonCyan, unfocusedIndicatorColor = Color(0xFF221F3A),
                    focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFDDDDEE),
                    cursorColor = NeonCyan, errorContainerColor = Color(0xFF1A0812),
                    errorIndicatorColor = Color(0xFFFF2D78)
                )
            )

            // Description
            TextField(
                value = description, onValueChange = { description = it },
                placeholder = { Text("Описание (необязательно)", color = Color(0xFF6666AA)) },
                leadingIcon = { Icon(Icons.Default.Notes, null, tint = NeonPurple.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF14112A), unfocusedContainerColor = Color(0xFF0E0C1C),
                    focusedIndicatorColor = NeonPurple, unfocusedIndicatorColor = Color(0xFF221F3A),
                    focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFDDDDEE),
                    cursorColor = NeonPurple
                )
            )

            // Date + Time row
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF221F3A))
                ) {
                    Icon(Icons.Default.CalendarToday, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (selectedDate != null)
                            java.text.SimpleDateFormat("d MMM", java.util.Locale("ru"))
                                .format(java.util.Date(selectedDate!!))
                        else "Дата",
                        fontSize = 13.sp
                    )
                }
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedButtonDefaults.outlinedButtonColors(
                        contentColor = if (hasTime) NeonPurple else Color(0xFF6666AA)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF221F3A))
                ) {
                    Icon(Icons.Default.AccessTime, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (hasTime) String.format("%02d:%02d", selectedHour, selectedMin) else "Время",
                        fontSize = 13.sp
                    )
                }
            }

            // Priority
            Text("Приоритет", fontWeight = FontWeight.Medium, color = Color.White, fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                data class Prio(val label: String, val value: Int, val color: Color)
                listOf(
                    Prio("Низкий", 0, Color(0xFF39FF14)),
                    Prio("Средний", 1, Color(0xFFFFAB40)),
                    Prio("Высокий", 2, Color(0xFFFF2D78))
                ).forEach { p ->
                    FilterChip(
                        selected = priority == p.value,
                        onClick = { priority = p.value },
                        label = { Text(p.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = p.color.copy(alpha = 0.2f),
                            selectedLabelColor = p.color,
                            containerColor = Color(0xFF0E0C1C),
                            labelColor = Color(0xFF8888AA)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = priority == p.value,
                            selectedBorderColor = p.color.copy(alpha = 0.5f),
                            borderColor = Color(0xFF221F3A)
                        )
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

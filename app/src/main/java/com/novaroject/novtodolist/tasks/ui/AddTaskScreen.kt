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
    var title          by remember { mutableStateOf("") }
    var description    by remember { mutableStateOf("") }
    var priority       by remember { mutableIntStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Fix #3 — по умолчанию дата = сегодня
    var selectedDate by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var selectedHour by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var selectedMin  by remember { mutableIntStateOf(0) }
    var hasTime      by remember { mutableStateOf(false) }
    var titleError   by remember { mutableStateOf(false) }

    // Новые параметры (Fix #7)
    var category       by remember { mutableStateOf("") }
    var reminderOffset by remember { mutableIntStateOf(30) }
    var repeatType     by remember { mutableStateOf("none") }

    val addResult by vm.addResult.collectAsState()

    LaunchedEffect(addResult) {
        if (addResult == "OK") { vm.clearAddResult(); onBack() }
    }

    // Date picker dialog
    if (showDatePicker) {
        val dpState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis()
        )
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
                            selectorColor = NeonCyan,
                            timeSelectorSelectedContainerColor = NeonPurple.copy(alpha = 0.3f),
                            timeSelectorUnselectedContainerColor = Color(0xFF14112A),
                        ))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = tpState.hour; selectedMin = tpState.minute
                    hasTime = true; showTimePicker = false
                }) { Text("OK", color = NeonCyan) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Отмена") }
            }
        )
    }

    // Build Timestamp — если дата не указана, используем сегодня (Fix #3)
    val dueTimestamp: Timestamp? = run {
        val dateMs = selectedDate ?: System.currentTimeMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = dateMs
        cal.set(Calendar.HOUR_OF_DAY, if (hasTime) selectedHour else 9)
        cal.set(Calendar.MINUTE, if (hasTime) selectedMin else 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        Timestamp(cal.time)
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Новая задача", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, null, tint = Color(0xFF8888AA)) }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isBlank()) { titleError = true; return@TextButton }
                            vm.addTask(title, description, dueTimestamp, priority, category, reminderOffset, repeatType)
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
            modifier = Modifier
                .fillMaxSize().verticalScroll(rememberScrollState())
                .padding(pad).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Error banner
            if (addResult != null && addResult != "OK") {
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFF2A0A14)) {
                    Text(addResult!!, color = Color(0xFFFF2D78), modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            // ─── Название ───
            TextField(
                value = title, onValueChange = { title = it; titleError = false },
                placeholder = { Text("Название задачи *", color = Color(0xFF6666AA)) },
                leadingIcon = { Icon(Icons.Default.Title, null, tint = if (titleError) Color(0xFFFF2D78) else NeonCyan) },
                isError = titleError,
                supportingText = { if (titleError) Text("Введите название задачи", color = Color(0xFFFF2D78)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                colors = cyberTextFieldColors(NeonCyan)
            )

            // ─── Описание ───
            TextField(
                value = description, onValueChange = { description = it },
                placeholder = { Text("Описание (необязательно)", color = Color(0xFF6666AA)) },
                leadingIcon = { Icon(Icons.Default.Notes, null, tint = NeonPurple.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 4,
                colors = cyberTextFieldColors(NeonPurple)
            )

            // ─── Дата + Время ───
            SectionLabel("Дата и время")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(0.4f))
                ) {
                    Icon(Icons.Default.CalendarToday, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        selectedDate?.let {
                            java.text.SimpleDateFormat("d MMM", java.util.Locale("ru")).format(java.util.Date(it))
                        } ?: "Сегодня",
                        fontSize = 13.sp
                    )
                }
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (hasTime) NeonPurple else Color(0xFF6666AA)),
                    border = androidx.compose.foundation.BorderStroke(1.dp,
                        if (hasTime) NeonPurple.copy(0.5f) else Color(0xFF221F3A))
                ) {
                    Icon(Icons.Default.AccessTime, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (hasTime) String.format("%02d:%02d", selectedHour, selectedMin) else "Время",
                        fontSize = 13.sp
                    )
                }
            }

            // ─── Приоритет ───
            SectionLabel("Приоритет")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                data class Prio(val label: String, val value: Int, val color: Color)
                listOf(
                    Prio("Низкий", 0, Color(0xFF39FF14)),
                    Prio("Средний", 1, Color(0xFFFFAB40)),
                    Prio("Высокий", 2, Color(0xFFFF2D78))
                ).forEach { p ->
                    FilterChip(
                        selected = priority == p.value, onClick = { priority = p.value },
                        label = { Text(p.label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = p.color.copy(alpha = 0.2f),
                            selectedLabelColor = p.color,
                            containerColor = Color(0xFF0E0C1C), labelColor = Color(0xFF8888AA)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = priority == p.value,
                            selectedBorderColor = p.color.copy(alpha = 0.5f), borderColor = Color(0xFF221F3A)
                        )
                    )
                }
            }

            // ─── Категория (Fix #7) ───
            SectionLabel("Категория")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                data class Cat(val label: String, val value: String)
                val cats = listOf(Cat("Работа","work"), Cat("Личное","personal"),
                    Cat("Здоровье","health"), Cat("Учёба","study"), Cat("Другое","other"))
                cats.forEach { c ->
                    FilterChip(
                        selected = category == c.value, onClick = { category = if (category == c.value) "" else c.value },
                        label = { Text(c.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurple.copy(0.2f), selectedLabelColor = NeonPurple,
                            containerColor = Color(0xFF0E0C1C), labelColor = Color(0xFF8888AA)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = category == c.value,
                            selectedBorderColor = NeonPurple.copy(0.5f), borderColor = Color(0xFF221F3A)
                        )
                    )
                }
            }

            // ─── Напоминание (Fix #7) ───
            SectionLabel("Напоминание")
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                data class Remind(val label: String, val offset: Int)
                val options = listOf(
                    Remind("В момент задачи", 0),
                    Remind("За 5 минут", 5),
                    Remind("За 15 минут", 15),
                    Remind("За 30 минут", 30),
                    Remind("За 1 час", 60),
                    Remind("За 1 день", 1440)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    options.take(3).forEach { r ->
                        FilterChip(
                            selected = reminderOffset == r.offset,
                            onClick = { reminderOffset = r.offset },
                            label = { Text(r.label, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(0.15f), selectedLabelColor = NeonCyan,
                                containerColor = Color(0xFF0E0C1C), labelColor = Color(0xFF8888AA)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = reminderOffset == r.offset,
                                selectedBorderColor = NeonCyan.copy(0.5f), borderColor = Color(0xFF221F3A)
                            )
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    options.drop(3).forEach { r ->
                        FilterChip(
                            selected = reminderOffset == r.offset,
                            onClick = { reminderOffset = r.offset },
                            label = { Text(r.label, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(0.15f), selectedLabelColor = NeonCyan,
                                containerColor = Color(0xFF0E0C1C), labelColor = Color(0xFF8888AA)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = reminderOffset == r.offset,
                                selectedBorderColor = NeonCyan.copy(0.5f), borderColor = Color(0xFF221F3A)
                            )
                        )
                    }
                }
            }

            // ─── Повторение (Fix #7) ───
            SectionLabel("Повторение")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                data class Rep(val label: String, val value: String)
                listOf(
                    Rep("Нет", "none"), Rep("Ежедневно", "daily"),
                    Rep("Еженедельно", "weekly"), Rep("Ежемесячно", "monthly")
                ).forEach { r ->
                    FilterChip(
                        selected = repeatType == r.value, onClick = { repeatType = r.value },
                        label = { Text(r.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFAB40).copy(0.2f),
                            selectedLabelColor = Color(0xFFFFAB40),
                            containerColor = Color(0xFF0E0C1C), labelColor = Color(0xFF8888AA)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = repeatType == r.value,
                            selectedBorderColor = Color(0xFFFFAB40).copy(0.5f), borderColor = Color(0xFF221F3A)
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontWeight = FontWeight.Medium, color = Color.White, fontSize = 14.sp)
}

@Composable
private fun cyberTextFieldColors(accent: Color) = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF14112A), unfocusedContainerColor = Color(0xFF0E0C1C),
    focusedIndicatorColor = accent, unfocusedIndicatorColor = Color(0xFF221F3A),
    focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFDDDDEE),
    cursorColor = accent, errorContainerColor = Color(0xFF1A0812),
    errorIndicatorColor = Color(0xFFFF2D78)
)

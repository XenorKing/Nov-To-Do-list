package com.novaroject.novtodolist.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.auth.FirebaseAuth
import com.novaroject.novtodolist.auth.AuthViewModel
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPink
import com.novaroject.novtodolist.ui.theme.NeonPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onAbout: () -> Unit,
    onBack: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val user    = FirebaseAuth.getInstance().currentUser
    var name    by remember { mutableStateOf(user?.displayName?.takeIf { it.isNotBlank() } ?: "Пользователь") }
    val email   = user?.email ?: ""
    val initial = name.firstOrNull()?.uppercase() ?: "N"

    var showLogoutDialog   by remember { mutableStateOf(false) }
    var showEditNickDialog by remember { mutableStateOf(false) }  // Fix #6
    var notifEnabled       by remember { mutableStateOf(true) }
    var editNick           by remember { mutableStateOf(name) }

    val updateNickResult by vm.updateNickResult.collectAsState()

    // Обрабатываем результат обновления ника
    LaunchedEffect(updateNickResult) {
        if (updateNickResult == "OK") {
            name = editNick
            vm.clearUpdateNickResult()
            showEditNickDialog = false
        }
    }

    // ─── Logout dialog ───
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color(0xFF100D20),
            title = { Text("Выйти из аккаунта?", color = Color.White) },
            confirmButton = {
                TextButton(onClick = { FirebaseAuth.getInstance().signOut(); onLogout() }) {
                    Text("Выйти", color = NeonPink)
                }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Отмена") } }
        )
    }

    // ─── Edit Nickname dialog (Fix #6) ───
    if (showEditNickDialog) {
        AlertDialog(
            onDismissRequest = { showEditNickDialog = false; vm.clearUpdateNickResult() },
            containerColor = Color(0xFF100D20),
            title = { Text("Сменить никнейм", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editNick,
                        onValueChange = { editNick = it },
                        placeholder = { Text("Введите никнейм", color = Color(0xFF6666AA)) },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, null, tint = NeonPurple) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple, unfocusedBorderColor = Color(0xFF221F3A),
                            focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFDDDDEE),
                            cursorColor = NeonPurple, focusedContainerColor = Color(0xFF14112A),
                            unfocusedContainerColor = Color(0xFF0E0C1C)
                        )
                    )
                    // Ошибка обновления
                    if (updateNickResult != null && updateNickResult != "OK" && updateNickResult != "loading") {
                        Text(updateNickResult!!, color = Color(0xFFFF2D78), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { vm.updateNickname(editNick) },
                    enabled = editNick.isNotBlank() && updateNickResult != "loading"
                ) {
                    if (updateNickResult == "loading")
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = NeonPurple)
                    else
                        Text("Сохранить", color = NeonPurple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNickDialog = false; vm.clearUpdateNickResult() }) {
                    Text("Отмена", color = Color(0xFF8888AA))
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // ─── Top bar ───
        Box(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0818))
                .statusBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = Color(0xFF8888AA))
            }
            Text("Профиль", modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // ─── Avatar ───
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.size(96.dp)
                        .background(
                            Brush.radialGradient(listOf(NeonPurple.copy(alpha = 0.4f), Color.Transparent)),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF1E1040), Color(0xFF2A1060)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initial, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = NeonPurple)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Имя + кнопка редактирования (Fix #6)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = { editNick = name; showEditNickDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Edit, "Редактировать ник",
                        Modifier.size(16.dp), tint = NeonPurple.copy(alpha = 0.7f))
                }
            }
            Text(email, fontSize = 13.sp, color = Color(0xFF7878AA))

            Spacer(Modifier.height(32.dp))

            // ─── Settings card ───
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color(0xFF100D20)) {
                Column {
                    ListItem(
                        headlineContent = { Text("Уведомления", fontWeight = FontWeight.Medium, color = Color.White) },
                        leadingContent = {
                            Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                                .background(NeonCyan.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Notifications, null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                        },
                        trailingContent = {
                            Switch(checked = notifEnabled, onCheckedChange = { notifEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black, checkedTrackColor = NeonPurple,
                                    uncheckedTrackColor = Color(0xFF221F3A)))
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    HorizontalDivider(color = Color(0xFF221F3A), modifier = Modifier.padding(horizontal = 16.dp))
                    // Fix #6 — кнопка смены ника
                    ListItem(
                        headlineContent = { Text("Сменить никнейм", fontWeight = FontWeight.Medium, color = Color.White) },
                        supportingContent = { Text(name, color = Color(0xFF7878AA), fontSize = 12.sp) },
                        leadingContent = {
                            Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                                .background(NeonPurple.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AlternateEmail, null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                            }
                        },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF5555AA)) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable { editNick = name; showEditNickDialog = true }
                    )
                    HorizontalDivider(color = Color(0xFF221F3A), modifier = Modifier.padding(horizontal = 16.dp))
                    ListItem(
                        headlineContent = { Text("Синхронизация", fontWeight = FontWeight.Medium, color = Color.White) },
                        supportingContent = { Text("Изменения видны на всех устройствах", color = Color(0xFF7878AA), fontSize = 12.sp) },
                        leadingContent = {
                            Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                                .background(NeonCyan.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Sync, null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ─── О приложении ───
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                color = Color(0xFF100D20), onClick = onAbout) {
                ListItem(
                    headlineContent = { Text("О приложении", fontWeight = FontWeight.Medium, color = Color.White) },
                    leadingContent = {
                        Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                            .background(NeonPurple.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Info, null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                        }
                    },
                    trailingContent = { Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF5555AA)) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(24.dp))

            // ─── Выйти ───
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPink.copy(alpha = 0.15f), contentColor = NeonPink)
            ) {
                Icon(Icons.Default.Logout, null, Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text("Выйти из аккаунта", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}


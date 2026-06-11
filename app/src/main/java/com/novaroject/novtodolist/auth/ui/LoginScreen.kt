package com.novaroject.novtodolist.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novaroject.novtodolist.auth.AuthViewModel
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPurple

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit,
    onNavigateReset: () -> Unit = {},
    onPrivacyPolicy: () -> Unit = {},
    vm: AuthViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    var email      by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var pwVisible  by remember { mutableStateOf(false) }
    val state      by vm.state.collectAsState()

    // ─── Диалог сброса пароля (inline, как в novAnime) ───
    var showForgotDialog by remember { mutableStateOf(false) }
    var resetEmail       by remember { mutableStateOf("") }

    LaunchedEffect(state.success) { if (state.success) onLoginSuccess() }
    LaunchedEffect(Unit) { vm.clearError() }

    if (showForgotDialog) {
        val blockedDomains = listOf("mail.ru", "bk.ru", "inbox.ru", "list.ru", "yandex.ru", "ya.ru", "rambler.ru")
        val resetDomain = resetEmail.substringAfterLast("@", "").lowercase()

        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            containerColor = Color(0xFF100D20),
            title = {
                Text("Восстановить пароль", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Введите email — пришлём ссылку для смены пароля.",
                        color = Color(0xFF8888AA),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    CyberField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        placeholder = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(20.dp)) },
                        keyboardType = KeyboardType.Email
                    )
                    AnimatedVisibility(
                        visible = blockedDomains.any { resetDomain == it },
                        enter = fadeIn(), exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFF6B35).copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Письма на $resetDomain часто попадают в Спам — проверьте папку Спам после отправки.",
                                color = Color(0xFFFF6B35),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { vm.resetPassword(resetEmail); showForgotDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    enabled = resetEmail.isNotBlank()
                ) { Text("Отправить") }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Отмена", color = Color(0xFF8888AA))
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF060412), DarkBg, Color(0xFF0A0520))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            // ─── Logo ───
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF0D0B20), Color(0xFF1A1040)))),
                contentAlignment = Alignment.Center
            ) {
                Text("N", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = NeonCyan)
                Box(
                    Modifier.size(86.dp).clip(RoundedCornerShape(22.dp))
                        .background(NeonCyan.copy(alpha = 0.04f))
                )
            }

            Spacer(Modifier.height(22.dp))
            Text("novTo-Do List", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text("Войдите в свой аккаунт", fontSize = 14.sp, color = Color(0xFF8888AA))
            Spacer(Modifier.height(44.dp))

            // ─── Поля ───
            CyberField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(20.dp)) },
                keyboardType = KeyboardType.Email,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )
            Spacer(Modifier.height(14.dp))

            CyberField(
                value = password, onValueChange = { password = it },
                placeholder = { Text("Пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(
                            if (pwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null, modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password,
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (email.isNotBlank() && password.isNotBlank()) vm.login(email, password)
                })
            )

            // Забыли пароль
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(
                    onClick = { resetEmail = email; showForgotDialog = true },
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("Забыли пароль?", color = NeonPurple, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            // ─── Ошибка / сообщение ───
            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn(), exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (state.error?.startsWith("Письмо") == true)
                        Color(0xFF39FF14).copy(alpha = 0.1f)
                    else
                        Color(0xFFFF2D78).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (state.error?.startsWith("Письмо") == true) Icons.Default.MarkEmailRead
                            else Icons.Default.ErrorOutline,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = if (state.error?.startsWith("Письмо") == true)
                                Color(0xFF39FF14) else Color(0xFFFF2D78)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            state.error ?: "",
                            color = if (state.error?.startsWith("Письмо") == true)
                                Color(0xFF39FF14) else Color(0xFFFF2D78),
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f),
                            lineHeight = 18.sp
                        )
                        IconButton(
                            onClick = { vm.clearError() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, null, Modifier.size(14.dp),
                                tint = Color(0xFF8888AA))
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ─── Кнопка Войти ───
            Button(
                onClick = { vm.login(email, password) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !state.loading && email.isNotBlank() && password.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple,
                    disabledContainerColor = Color(0xFF2A1A45)
                )
            ) {
                if (state.loading)
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                else
                    Text("Войти", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }

            Spacer(Modifier.height(24.dp))

            // ─── Нет аккаунта? ───
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Нет аккаунта?", color = Color(0xFF8888AA), fontSize = 14.sp)
                TextButton(onClick = onNavigateRegister, contentPadding = PaddingValues(start = 4.dp)) {
                    Text("Зарегистрироваться", color = NeonPurple, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onPrivacyPolicy) {
                Text(
                    "Политика конфиденциальности",
                    color = Color(0xFF5A5A8A),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

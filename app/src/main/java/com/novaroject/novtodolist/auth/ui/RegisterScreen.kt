package com.novaroject.novtodolist.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novaroject.novtodolist.auth.AuthViewModel
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPurple

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    onPrivacyPolicy: () -> Unit = {},
    vm: AuthViewModel = hiltViewModel()
) {
    var nickname  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirm   by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    var cfVisible by remember { mutableStateOf(false) }
    var agreed    by remember { mutableStateOf(false) }
    val state     by vm.state.collectAsState()
    val pwMatch   = password == confirm || confirm.isEmpty()

    val blockedDomains = listOf("mail.ru", "bk.ru", "inbox.ru", "list.ru", "yandex.ru", "ya.ru", "rambler.ru")
    val emailDomain    = email.substringAfterLast("@", "").lowercase()
    val isBlockedDomain = emailDomain.isNotBlank() && blockedDomains.any { emailDomain == it }

    LaunchedEffect(state.success) { if (state.success) onRegistered() }
    LaunchedEffect(Unit) { vm.clearError() }

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
            Spacer(Modifier.height(56.dp))

            // ─── Logo ───
            Box(
                modifier = Modifier.size(86.dp).clip(RoundedCornerShape(22.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF0D0B20), Color(0xFF1A1040)))),
                contentAlignment = Alignment.Center
            ) {
                Text("N", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = NeonCyan)
            }
            Spacer(Modifier.height(20.dp))
            Text("Создать аккаунт", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text("Начните организовывать задачи", fontSize = 14.sp, color = Color(0xFF8888AA))
            Spacer(Modifier.height(36.dp))

            // ─── Никнейм ───
            CyberField(
                value = nickname, onValueChange = { nickname = it },
                placeholder = { Text("Никнейм (отображается в задачах)") },
                leadingIcon = { Icon(Icons.Default.AlternateEmail, null, modifier = Modifier.size(20.dp)) }
            )
            Spacer(Modifier.height(14.dp))

            CyberField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(20.dp)) },
                keyboardType = KeyboardType.Email
            )

            // ─── Предупреждение домена (как в novAnime) ───
            AnimatedVisibility(
                visible = isBlockedDomain,
                enter = fadeIn(), exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFF6B35).copy(alpha = 0.12f)
                ) {
                    Text(
                        "Письма Firebase могут не приходить на $emailDomain. Рекомендуем использовать Gmail.",
                        color = Color(0xFFFF6B35),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            CyberField(
                value = password, onValueChange = { password = it },
                placeholder = { Text("Пароль (мин. 6 символов)") },
                leadingIcon = { Icon(Icons.Default.Lock, null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(if (pwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null, modifier = Modifier.size(20.dp))
                    }
                },
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password
            )
            Spacer(Modifier.height(14.dp))

            CyberField(
                value = confirm, onValueChange = { confirm = it },
                placeholder = { Text("Повторите пароль") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, null, modifier = Modifier.size(20.dp),
                        tint = if (pwMatch) Color(0xFF6666AA) else Color(0xFFFF2D78))
                },
                trailingIcon = {
                    IconButton(onClick = { cfVisible = !cfVisible }) {
                        Icon(if (cfVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null, modifier = Modifier.size(20.dp))
                    }
                },
                visualTransformation = if (cfVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password
            )

            if (!pwMatch) {
                Text(
                    "Пароли не совпадают", color = Color(0xFFFF2D78), fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // ─── Ошибка ───
            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn(), exit = fadeOut()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFF2D78).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, null, Modifier.size(16.dp), tint = Color(0xFFFF2D78))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            state.error ?: "",
                            color = Color(0xFFFF2D78),
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f),
                            lineHeight = 18.sp
                        )
                        IconButton(onClick = { vm.clearError() }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, null, Modifier.size(14.dp), tint = Color(0xFF8888AA))
                        }
                    }
                }
            }

            // ─── Политика конфиденциальности ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0E0C1C))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreed, onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = NeonPurple, uncheckedColor = Color(0xFF6666AA), checkmarkColor = Color.White
                    )
                )
                val annotated = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF8888AA), fontSize = 13.sp)) {
                        append("Я согласен с ")
                    }
                    withStyle(SpanStyle(
                        color = NeonPurple, fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, textDecoration = TextDecoration.Underline
                    )) {
                        append("Политикой конфиденциальности")
                    }
                }
                TextButton(onClick = onPrivacyPolicy, contentPadding = PaddingValues(0.dp)) {
                    Text(annotated)
                }
            }

            AnimatedVisibility(
                visible = nickname.isNotBlank() && email.isNotBlank() &&
                        password.length >= 6 && password == confirm && !agreed,
                enter = fadeIn(), exit = fadeOut()
            ) {
                Text(
                    "Отметьте согласие с политикой конфиденциальности",
                    color = NeonPurple.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(18.dp))

            // ─── Кнопка Зарегистрироваться ───
            Button(
                onClick = { vm.register(nickname, email, password) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !state.loading && nickname.isNotBlank() && email.isNotBlank()
                        && password.length >= 6 && password == confirm && agreed,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple, disabledContainerColor = Color(0xFF2A1A45))
            ) {
                if (state.loading)
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                else
                    Text("Зарегистрироваться", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }
            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Уже есть аккаунт?", color = Color(0xFF8888AA), fontSize = 14.sp)
                TextButton(onClick = onBack, contentPadding = PaddingValues(start = 4.dp)) {
                    Text("Войти", color = NeonPurple, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

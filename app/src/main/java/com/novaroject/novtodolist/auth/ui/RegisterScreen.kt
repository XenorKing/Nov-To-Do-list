package com.novaroject.novtodolist.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novaroject.novtodolist.auth.AuthViewModel

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val Purple = Color(0xFF7B5CF5)
    val Cyan   = Color(0xFF00D4E8)

    var name      by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirm   by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    var cfVisible by remember { mutableStateOf(false) }
    var agreed    by remember { mutableStateOf(false) }
    val state     by vm.state.collectAsState()

    val pwMatch = password == confirm || confirm.isEmpty()

    LaunchedEffect(state.success) { if (state.success) onRegistered() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF12101E))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        // ── Logo ──
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF1C1830)),
            contentAlignment = Alignment.Center
        ) {
            Text("N", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Cyan)
        }

        Spacer(Modifier.height(20.dp))
        Text("Создать аккаунт", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(6.dp))
        Text("Начните организовывать задачи", fontSize = 14.sp, color = Color(0xFF8A8A9A))

        Spacer(Modifier.height(36.dp))

        // ── Name ──
        NovTextField(
            value = name, onValueChange = { name = it },
            placeholder = "Имя",
            leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF8A8A9A), modifier = Modifier.size(20.dp)) }
        )
        Spacer(Modifier.height(12.dp))

        // ── Email ──
        NovTextField(
            value = email, onValueChange = { email = it },
            placeholder = "Email",
            leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF8A8A9A), modifier = Modifier.size(20.dp)) },
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(12.dp))

        // ── Password ──
        NovTextField(
            value = password, onValueChange = { password = it },
            placeholder = "Пароль (мин. 6 символов)",
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF8A8A9A), modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                IconButton(onClick = { pwVisible = !pwVisible }) {
                    Icon(if (pwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null,
                        tint = Color(0xFF8A8A9A), modifier = Modifier.size(20.dp))
                }
            },
            visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password
        )
        Spacer(Modifier.height(12.dp))

        // ── Confirm password ──
        NovTextField(
            value = confirm, onValueChange = { confirm = it },
            placeholder = "Повторите пароль",
            leadingIcon = { Icon(Icons.Default.Lock, null,
                tint = if (pwMatch) Color(0xFF8A8A9A) else Color(0xFFFF5C5C),
                modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                IconButton(onClick = { cfVisible = !cfVisible }) {
                    Icon(if (cfVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null,
                        tint = Color(0xFF8A8A9A), modifier = Modifier.size(20.dp))
                }
            },
            visualTransformation = if (cfVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password
        )
        if (!pwMatch) {
            Text("Пароли не совпадают", color = Color(0xFFFF5C5C), fontSize = 11.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, top = 4.dp))
        }
        Spacer(Modifier.height(16.dp))

        // ── Privacy checkbox ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1A1728))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = agreed, onCheckedChange = { agreed = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Purple, uncheckedColor = Color(0xFF6A6A7A),
                    checkmarkColor = Color.White
                )
            )
            Text(
                text = "Я согласен с ",
                color = Color(0xFF8A8A9A), fontSize = 13.sp
            )
            TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                Text("Политикой конфиденциальности",
                    color = Purple, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        // ── Error ──
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp, textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp))
        }

        Spacer(Modifier.height(16.dp))

        // ── Register button ──
        Button(
            onClick = { vm.register(name, email, password) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !state.loading && name.isNotBlank() && email.isNotBlank()
                    && password.length >= 6 && password == confirm && agreed,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple,
                disabledContainerColor = Color(0xFF2D2845)
            )
        ) {
            if (state.loading)
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
            else
                Text("Зарегистрироваться", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
        }

        Spacer(Modifier.height(20.dp))

        // ── Login row ──
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Уже есть аккаунт?", color = Color(0xFF8A8A9A), fontSize = 14.sp)
            TextButton(onClick = onBack, contentPadding = PaddingValues(start = 4.dp)) {
                Text("Войти", color = Purple, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

package com.novaroject.novtodolist.auth.ui

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
    onNavigateReset: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    val state     by vm.state.collectAsState()

    LaunchedEffect(state.success) { if (state.success) onLoginSuccess() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF060412), DarkBg, Color(0xFF0A0520)))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0E0C1C)),
                contentAlignment = Alignment.Center
            ) {
                Text("N", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = NeonCyan)
            }

            Spacer(Modifier.height(22.dp))
            Text("novTo-Do List", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text("Войдите в свой аккаунт", fontSize = 14.sp, color = Color(0xFF8888AA))

            Spacer(Modifier.height(44.dp))

            CyberField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(20.dp)) },
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(14.dp))

            CyberField(
                value = password, onValueChange = { password = it },
                placeholder = { Text("Пароль") },
                leadingIcon = { Icon(Icons.Default.Lock, null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(if (pwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null,
                            modifier = Modifier.size(20.dp))
                    }
                },
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardType = KeyboardType.Password
            )

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = onNavigateReset) {
                    Text("Забыли пароль?", color = NeonPurple, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            state.error?.let {
                Text(it, color = Color(0xFFFF2D78), fontSize = 12.sp,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(Modifier.height(8.dp))

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
                if (state.loading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                else Text("Войти", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }

            Spacer(Modifier.height(28.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Нет аккаунта?", color = Color(0xFF8888AA), fontSize = 14.sp)
                TextButton(onClick = onNavigateRegister, contentPadding = PaddingValues(start = 4.dp)) {
                    Text("Зарегистрироваться", color = NeonPurple, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

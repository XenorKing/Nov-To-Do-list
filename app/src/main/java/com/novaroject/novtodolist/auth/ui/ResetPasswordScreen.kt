package com.novaroject.novtodolist.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novaroject.novtodolist.auth.AuthViewModel
import com.novaroject.novtodolist.ui.theme.DarkBg
import com.novaroject.novtodolist.ui.theme.NeonCyan
import com.novaroject.novtodolist.ui.theme.NeonPurple

@Composable
fun ResetPasswordScreen(onBack: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    val state by vm.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF060412), DarkBg)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.success) {
                Box(modifier = Modifier.size(86.dp).clip(RoundedCornerShape(22.dp)).background(Color(0xFF0E0C1C)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MarkEmailRead, null, tint = NeonCyan, modifier = Modifier.size(44.dp))
                }
                Spacer(Modifier.height(24.dp))
                Text("Письмо отправлено!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(10.dp))
                Text("Проверьте почту и перейдите\nпо ссылке для сброса пароля.",
                    color = Color(0xFF8888AA), fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)) {
                    Text("Назад", fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else {
                Box(modifier = Modifier.size(86.dp).clip(RoundedCornerShape(22.dp)).background(Color(0xFF0E0C1C)),
                    contentAlignment = Alignment.Center) {
                    Text("N", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = NeonCyan)
                }
                Spacer(Modifier.height(20.dp))
                Text("Сброс пароля", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text("Введите email — отправим ссылку\nдля восстановления доступа",
                    color = Color(0xFF8888AA), fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(36.dp))

                CyberField(value = email, onValueChange = { email = it },
                    placeholder = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(20.dp)) },
                    keyboardType = KeyboardType.Email)

                state.error?.let {
                    Text(it, color = Color(0xFFFF2D78), fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                }
                Spacer(Modifier.height(20.dp))
                Button(onClick = { vm.resetPassword(email) }, modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !state.loading && email.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple,
                        disabledContainerColor = Color(0xFF2A1A45))) {
                    if (state.loading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = Color.White)
                    else Text("Отправить", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onBack) {
                    Text("← Назад к входу", color = NeonPurple, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

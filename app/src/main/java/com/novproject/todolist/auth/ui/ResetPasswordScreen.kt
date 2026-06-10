package com.novproject.todolist.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novproject.todolist.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }
    val state by vm.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) sent = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сброс пароля") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (sent) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Письмо отправлено!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Проверьте почту $email и следуйте инструкции для сброса пароля.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = onBack, shape = MaterialTheme.shapes.large) {
                    Text("Назад")
                }
            } else {
                Text(
                    "Введите email для получения ссылки сброса пароля",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { vm.resetPassword(email) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !state.loading && email.isNotBlank(),
                    shape = MaterialTheme.shapes.large
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Отправить", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

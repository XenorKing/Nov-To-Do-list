package com.novaroject.novtodolist.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novaroject.novtodolist.auth.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateRegister: () -> Unit,
    onNavigateReset: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    val state by vm.state.collectAsState()

    LaunchedEffect(state.success) { if (state.success) onLoginSuccess() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(60.dp))

        Surface(
            modifier = Modifier.size(88.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "N",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("novTo-Do List", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Войдите в свой аккаунт", style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(36.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = MaterialTheme.shapes.large
        )
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Пароль") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            trailingIcon = {
                IconButton(onClick = { pwVisible = !pwVisible }) {
                    Icon(if (pwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = MaterialTheme.shapes.large
        )
        TextButton(onClick = onNavigateReset, modifier = Modifier.align(Alignment.End)) {
            Text("Забыли пароль?", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 6.dp))
        }

        Button(
            onClick = { vm.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !state.loading && email.isNotBlank() && password.isNotBlank(),
            shape = MaterialTheme.shapes.large
        ) {
            if (state.loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            else Text("Войти", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Нет аккаунта?", color = MaterialTheme.colorScheme.onSurface)
            TextButton(onClick = onNavigateRegister) {
                Text("Зарегистрироваться", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(60.dp))
    }
}

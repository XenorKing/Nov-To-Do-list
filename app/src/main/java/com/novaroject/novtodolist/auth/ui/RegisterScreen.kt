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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pwVisible by remember { mutableStateOf(false) }
    val state by vm.state.collectAsState()

    LaunchedEffect(state.success) { if (state.success) onRegistered() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Регистрация") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize().verticalScroll(rememberScrollState())
                .padding(pad).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it },
                label = { Text("Имя") }, leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.large)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = email, onValueChange = { email = it },
                label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.large)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Пароль (мин. 6 символов)") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(if (pwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                    }
                },
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = MaterialTheme.shapes.large
            )
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp))
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { vm.register(name, email, password) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !state.loading && name.isNotBlank() && email.isNotBlank() && password.length >= 6,
                shape = MaterialTheme.shapes.large
            ) {
                if (state.loading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Создать аккаунт", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

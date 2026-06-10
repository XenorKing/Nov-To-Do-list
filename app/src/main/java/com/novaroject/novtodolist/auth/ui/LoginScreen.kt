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

private val Purple = Color(0xFF7B5CF5)
private val Cyan   = Color(0xFF00D4E8)
private val FieldBg = Color(0xFF1E1A2E)

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF12101E))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(72.dp))

        // ── Logo ──
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF1C1830)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "N",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Cyan
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "novTo-Do List",
            fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Войдите в свой аккаунт",
            fontSize = 14.sp, color = Color(0xFF8A8A9A)
        )

        Spacer(Modifier.height(40.dp))

        // ── Email field ──
        NovTextField(
            value = email, onValueChange = { email = it },
            placeholder = "Email",
            leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF8A8A9A), modifier = Modifier.size(20.dp)) },
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(12.dp))

        // ── Password field ──
        NovTextField(
            value = password, onValueChange = { password = it },
            placeholder = "Пароль",
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF8A8A9A), modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                IconButton(onClick = { pwVisible = !pwVisible }) {
                    Icon(
                        if (pwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        null, tint = Color(0xFF8A8A9A), modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password
        )

        // ── Forgot password ──
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = onNavigateReset) {
                Text("Забыли пароль?", color = Purple, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
        }

        // ── Error ──
        state.error?.let {
            Text(
                it, color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp, textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Login button ──
        Button(
            onClick = { vm.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !state.loading && email.isNotBlank() && password.isNotBlank(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple,
                disabledContainerColor = Color(0xFF2D2845)
            )
        ) {
            if (state.loading)
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
            else
                Text("Войти", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
        }

        Spacer(Modifier.height(28.dp))

        // ── Sign up row ──
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Нет аккаунта?", color = Color(0xFF8A8A9A), fontSize = 14.sp)
            TextButton(onClick = onNavigateRegister, contentPadding = PaddingValues(start = 4.dp)) {
                Text("Зарегистрироваться", color = Purple, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(60.dp))
    }
}

@Composable
internal fun NovTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val Purple = Color(0xFF7B5CF5)
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF6A6A7A), fontSize = 14.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor   = Color(0xFF1E1A2E),
            unfocusedContainerColor = Color(0xFF1A1728),
            focusedIndicatorColor   = Purple,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor        = Color.White,
            unfocusedTextColor      = Color.White,
            cursorColor             = Purple
        )
    )
}
